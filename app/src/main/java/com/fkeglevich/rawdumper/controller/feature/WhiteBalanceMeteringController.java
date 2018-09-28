/*
 * Copyright 2018, Flávio Keglevich
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

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.DataRange;
import com.fkeglevich.rawdumper.camera.data.ManualTemperature;
import com.fkeglevich.rawdumper.camera.data.ManualTemperatureRange;
import com.fkeglevich.rawdumper.camera.data.WhiteBalancePreset;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.feature.preset.PresetMeteringController;

import java.util.List;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class WhiteBalanceMeteringController extends PresetMeteringController<WhiteBalancePreset, ManualTemperature>
{
    WhiteBalanceMeteringController(TextView textView)
    {
        super(textView);
    }

    @Override
    protected WhiteBalancePreset getUnavailableValue()
    {
        return WhiteBalancePreset.AUTO;
    }

    @Override
    protected WhiteBalancePreset getDefaultValue()
    {
        return WhiteBalancePreset.AUTO;
    }

    @Override
    protected String getManualText(WritableFeature<ManualTemperature, DataRange<ManualTemperature>> manualFeature)
    {
        return manualFeature.getValue().displayValue();
    }

    @Override
    protected ManualTemperature getDisabledManualValue()
    {
        return ManualTemperature.DISABLED;
    }
}
