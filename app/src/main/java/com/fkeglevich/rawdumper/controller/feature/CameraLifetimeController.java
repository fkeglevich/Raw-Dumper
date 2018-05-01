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

package com.fkeglevich.rawdumper.controller.feature;

import android.view.View;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.CameraManager;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CameraPreview;
import com.fkeglevich.rawdumper.camera.exception.CameraPatchRequiredException;
import com.fkeglevich.rawdumper.camera.exception.RawIsUnavailableException;
import com.fkeglevich.rawdumper.ui.activity.FullscreenManager;
import com.fkeglevich.rawdumper.ui.dialog.FatalErrorDialog;
import com.fkeglevich.rawdumper.ui.dialog.OkDialog;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class CameraLifetimeController
{
    private final EventListener<Void> pauseListener = new EventListener<Void>()
    {
        @Override
        public void onEvent(Void eventData)
        {
            cameraManager.closeCamera();
            cameraPreview.clearCamera();
        }
    };

    private final ActivityReference reference;
    private final FullscreenManager fullscreenManager;
    private final FeatureControllerManager featureControllerManager;
    private final CameraPreview cameraPreview;
    private final CameraManager cameraManager;
    private final SwitchButtonController switchButtonController;

    public CameraLifetimeController(ActivityReference reference)
    {
        this.reference = reference;
        setupActivityListeners();
        this.fullscreenManager          = new FullscreenManager(reference);
        this.featureControllerManager   = new FeatureControllerManager();
        featureControllerManager.createControllers(reference);
        this.cameraPreview = getTextureView(reference);
        this.cameraManager              = new CameraManager(reference, cameraPreview);
        setupCameraManager();
        this.switchButtonController     = createSwitchButtonController();
    }

    private void setupActivityListeners()
    {
        reference.onResume.addListener(eventData -> cameraManager.openCamera());
    }

    private void setupCameraManager()
    {
        cameraManager.onCameraOpened.addListener(this::onCameraOpened);
        cameraManager.onCameraException.addListener(eventData ->
        {
            if (eventData instanceof RawIsUnavailableException || eventData instanceof CameraPatchRequiredException)
                OkDialog.show(reference, eventData,
                        (dialog, which) ->
                        {
                            cameraManager.selectNextCamera();
                            cameraManager.openCamera();
                        });
            else
                FatalErrorDialog.show(reference, eventData);
        });
    }

    private void onCameraOpened(TurboCamera turboCamera)
    {
        fullscreenManager.goToFullscreenMode();

        reference.onPause.removeListener(pauseListener);
        reference.onPause.addListener(pauseListener);

        cameraPreview.setupCamera(turboCamera);

        featureControllerManager.setupControllers(turboCamera, cameraManager.onCameraClosed);
        switchButtonController.setupFeature(turboCamera, cameraManager.onCameraClosed);
    }

    private CameraPreview getTextureView(ActivityReference reference)
    {
        return (CameraPreview) reference.weaklyGet().findViewById(R.id.cameraSurfaceView);
    }

    private SwitchButtonController createSwitchButtonController()
    {
        View switchButton = reference.weaklyGet().findViewById(R.id.camSwitchButton);
        CameraPreview textureView = (CameraPreview) reference.weaklyGet().findViewById(R.id.cameraSurfaceView);
        return new SwitchButtonController(switchButton, cameraManager, textureView);
    }
}
