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
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.feature.preset.PresetController;

import java.util.List;

/**
 * Created by flavio on 21/11/17.
 */

public class FocusController extends PresetController<FocusMode>
{
    private final CameraPreview cameraPreview;

    FocusController(ActivityReference reference, OnClickNotifier clickNotifier, CameraPreview cameraPreview)
    {
        super(reference, clickNotifier);
        this.cameraPreview = cameraPreview;
    }

    @Override
    protected void initializeIconMap(ActivityReference reference)
    {
        putIconMap(reference, FocusMode.AUTO,                 R.id.autoFocusBt);
        putIconMap(reference, FocusMode.CONTINUOUS_PICTURE,   R.id.continuousFocusBt);
        putIconMap(reference, FocusMode.CONTINUOUS_VIDEO,     R.id.continuousFocusBt);
        putIconMap(reference, FocusMode.MACRO,                R.id.macroFocusBt);
        putIconMap(reference, FocusMode.INFINITY,             R.id.infinityFocusBt);
        putIconMap(reference, FocusMode.FIXED,                R.id.fixedFocusBt);
        putIconMap(reference, FocusMode.EDOF,                 R.id.edofFocusBt);
    }

    @Override
    protected WritableFeature<FocusMode, List<FocusMode>> selectFeature(TurboCamera camera)
    {
        return camera.getFocusFeature();
    }

    @Override
    protected int getManualChooserId()
    {
        return R.id.manualFocusChooser;
    }

    @Override
    protected int getMainButtonId()
    {
        return R.id.focusBt;
    }

    @Override
    protected int getMainChooser()
    {
        return R.id.focusChooser;
    }

    @Override
    protected void onHidingChooserManualState()
    {
        cameraPreview.stopFocusPeaking();
    }

    @Override
    protected void onShowingChooserManualState()
    {
        cameraPreview.startFocusPeaking();
    }

    @Override
    protected boolean shouldIgnore(FocusMode mode)
    {
        return mode.equals(FocusMode.CONTINUOUS_VIDEO);
    }
}
