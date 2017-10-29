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

import com.fkeglevich.rawdumper.camera.async.CameraManager;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.controller.animation.ButtonDisabledStateController;
import com.fkeglevich.rawdumper.ui.CameraPreviewTexture;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class SwitchButtonController extends FeatureController
{
    private final ButtonDisabledStateController disabledStateController;
    private final CameraManager cameraManager;

    public SwitchButtonController(View switchButton, final CameraManager cameraManager, final CameraPreviewTexture previewTexture)
    {
        switchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                previewTexture.startCloseCameraAnimation();
                cameraManager.switchCamera();
            }
        });

        this.disabledStateController = new ButtonDisabledStateController(switchButton, false);
        this.cameraManager = cameraManager;
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        if (!cameraManager.canSwitchCamera())
        {
            reset();
            return;
        }
        disabledStateController.enableAnimated();
    }

    @Override
    protected void reset()
    {
        disabledStateController.disableAnimated();
    }
}
