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

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CameraPreview;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.controller.feature.preset.ManualController;

/**
 * Created by flavio on 22/11/17.
 */

class ManualFocusController extends ManualController<FocusMode, ManualFocus>
{
    private final CameraPreview cameraPreview;

    ManualFocusController(ActivityReference reference)
    {
        super(reference.findViewById(R.id.manualFocusBt),
                reference.findViewById(R.id.manualFocusBackBt),
                reference.findViewById(R.id.manualFocusChooser),
                reference.findViewById(R.id.manualFocusBar),
                reference.findViewById(R.id.stdFocusChooser));
        this.cameraPreview = reference.findViewById(R.id.cameraSurfaceView);
    }

    @Override
    protected FocusMode getDefaultPresetValue()
    {
        return FocusMode.AUTO;
    }

    @Override
    protected ManualFocus getDisabledManualValue()
    {
        return ManualFocus.DISABLED;
    }

    @Override
    protected void setupCameraOnManualButtonClick(TurboCamera camera)
    {
        //camera.getFocusFeature().cancelAutoFocus();
    }

    @Override
    protected void onHideChooser()
    {
        cameraPreview.stopFocusPeaking();
    }

    @Override
    protected void onShowChooser()
    {
        cameraPreview.startFocusPeaking();
    }
}
