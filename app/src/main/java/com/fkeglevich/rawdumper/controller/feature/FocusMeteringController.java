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

import android.widget.TextView;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.data.ManualFocusRange;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.context.ContextManager;
import com.fkeglevich.rawdumper.controller.feature.preset.PresetMeteringController;

import java.util.List;

/**
 * Created by flavio on 22/11/17.
 */

class FocusMeteringController extends PresetMeteringController<FocusMode, ManualFocus, ManualFocusRange>
{
    FocusMeteringController(TextView focusText)
    {
        super(focusText);
    }

    @Override
    protected WritableFeature<FocusMode, List<FocusMode>> getPresetFeature(TurboCamera camera)
    {
        return camera.getFocusFeature();
    }

    @Override
    protected WritableFeature<ManualFocus, ManualFocusRange> getManualFeature(TurboCamera camera)
    {
        return camera.getManualFocusFeature();
    }

    @Override
    protected FocusMode getUnavailableValue()
    {
        return FocusMode.FIXED;
    }

    @Override
    protected FocusMode getDefaultValue()
    {
        return FocusMode.AUTO;
    }

    @Override
    protected String getManualText(WritableFeature<ManualFocus, ManualFocusRange> manualFeature)
    {
        return ContextManager.getApplicationContext().getString(R.string.focus_manual);
    }
}
