/*
 * Copyright 2017, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fkeglevich.rawdumper.camera.async;

import android.view.TextureView;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.async.impl.CameraSelectorImpl;
import com.fkeglevich.rawdumper.camera.setup.CameraSetup;
import com.fkeglevich.rawdumper.controller.orientation.OrientationModule;
import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionModule;
import com.fkeglevich.rawdumper.controller.permission.MandatoryRootModule;
import com.fkeglevich.rawdumper.debug.DebugFlags;
import com.fkeglevich.rawdumper.util.Assert;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.EventListener;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 19/10/17.
 */

public class CameraManager
{
    private enum State
    {
        IDLE,
        PERFORMING_OPERATION,
        CAMERA_READY;
    }

    private final OrientationModule orientationModule;
    private final MandatoryPermissionModule permissionModule;
    private final CameraSelector cameraSelector;
    private final CameraSetup cameraSetup;

    public final EventDispatcher<TurboCamera> onCameraOpened;
    public final EventDispatcher<Nothing> onCameraClosed;
    public final EventDispatcher<MessageException> onCameraException;

    private boolean postponedClose = false;

    private State currentState = State.IDLE;

    public CameraManager(ActivityReference activityReference, TextureView textureView)
    {
        orientationModule = new OrientationModule(activityReference);
        permissionModule = DebugFlags.isDisableMandatoryRoot()
                            ? new MandatoryPermissionModule(activityReference)
                            : new MandatoryRootModule(activityReference);

        cameraSelector = new CameraSelectorImpl();
        cameraSetup = new CameraSetup(textureView,
                activityReference, permissionModule.getPermissionManager(), cameraSelector);

        onCameraOpened = new SimpleDispatcher<>();
        onCameraClosed = new SimpleDispatcher<>();
        onCameraException = cameraSetup.onException;

        cameraSetup.onComplete.addListener(new EventListener<TurboCamera>()
        {
            @Override
            public void onEvent(TurboCamera eventData)
            {
                currentState = State.CAMERA_READY;
                if (postponedClose)
                {
                    closeCamera();
                    postponedClose = false;
                }
                else
                    onCameraOpened.dispatchEvent(eventData);
            }
        });
    }

    public void openCamera()
    {
        if (currentState == State.PERFORMING_OPERATION)
        {
            if (postponedClose)
                postponedClose = false;
            return;
        }

        if (currentState == State.CAMERA_READY)
            return;

        Assert.state(currentState, State.IDLE);
        cameraSetup.setupCamera();
        currentState = State.PERFORMING_OPERATION;
    }

    public void switchCamera()
    {
        Assert.state(currentState, State.CAMERA_READY);
        Assert.isTrue(canSwitchCamera());
        dispatchCameraClose();
        CameraThread.getInstance().closeCameraAsync(new AsyncOperation<Nothing>()
        {
            @Override
            protected void execute(Nothing argument)
            {
                cameraSelector.selectNextCamera();
                cameraSetup.setupCamera();
            }
        });
        currentState = State.PERFORMING_OPERATION;
    }

    public void closeCamera()
    {
        if (currentState == State.PERFORMING_OPERATION)
            postponedClose = true;
        else
        {
            Assert.state(currentState, State.CAMERA_READY);
            CameraThread.getInstance().closeCamera();
            dispatchCameraClose();
            currentState = State.IDLE;
        }
    }

    public boolean canSwitchCamera()
    {
        return cameraSelector.hasMultipleCameras();
    }

    private void dispatchCameraClose()
    {
        onCameraClosed.dispatchEvent(Nothing.NOTHING);
        onCameraClosed.removeAllListeners();
    }
}
