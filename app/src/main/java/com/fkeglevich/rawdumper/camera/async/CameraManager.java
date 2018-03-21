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

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.async.impl.CameraSelectorImpl;
import com.fkeglevich.rawdumper.camera.data.CameraPreview;
import com.fkeglevich.rawdumper.camera.setup.CameraSetup;
import com.fkeglevich.rawdumper.controller.orientation.OrientationModule;
import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionModule;
import com.fkeglevich.rawdumper.controller.permission.MandatoryRootModule;
import com.fkeglevich.rawdumper.debug.DebugFlag;

import java.lang.Void;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.EventListener;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import junit.framework.Assert;

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
    public final EventDispatcher<Void> onCameraClosed;
    public final EventDispatcher<MessageException> onCameraException;

    private boolean postponedClose = false;

    private State currentState = State.IDLE;

    public CameraManager(ActivityReference activityReference, CameraPreview textureSource)
    {
        orientationModule = new OrientationModule(activityReference);
        permissionModule = DebugFlag.isDisableMandatoryRoot()
                            ? new MandatoryPermissionModule(activityReference)
                            : new MandatoryRootModule(activityReference);

        cameraSelector = new CameraSelectorImpl();
        cameraSetup = new CameraSetup(textureSource,
                activityReference, permissionModule.getPermissionManager(), cameraSelector);

        onCameraOpened = new SimpleDispatcher<>();
        onCameraClosed = new SimpleDispatcher<>();
        onCameraException = cameraSetup.onException;

        cameraSetup.onComplete.addListener(eventData ->
        {
            currentState = State.CAMERA_READY;
            if (postponedClose)
            {
                closeCamera();
                postponedClose = false;
            }
            else
                onCameraOpened.dispatchEvent(eventData);
        });

        cameraSetup.onException.addListener(eventData -> currentState = State.IDLE);
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

        Assert.assertEquals(State.IDLE, currentState);
        cameraSetup.setupCamera();
        currentState = State.PERFORMING_OPERATION;
    }

    public void switchCamera()
    {
        Assert.assertEquals(State.CAMERA_READY, currentState);
        Assert.assertTrue(canSwitchCamera());
        dispatchCameraClose();
        CameraThread.getInstance().closeCameraAsync(new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
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
            Assert.assertEquals(State.CAMERA_READY, currentState);
            CameraThread.getInstance().closeCamera();
            dispatchCameraClose();
            currentState = State.IDLE;
        }
    }

    public boolean canSwitchCamera()
    {
        return cameraSelector.hasMultipleCameras();
    }

    public void selectNextCamera()
    {
        cameraSelector.selectNextCamera();
    }

    private void dispatchCameraClose()
    {
        onCameraClosed.dispatchEvent(null);
        onCameraClosed.removeAllListeners();
    }
}
