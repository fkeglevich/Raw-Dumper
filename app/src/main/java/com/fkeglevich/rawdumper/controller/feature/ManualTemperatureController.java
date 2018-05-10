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

import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.ManualTemperature;
import com.fkeglevich.rawdumper.camera.data.WhiteBalancePreset;
import com.fkeglevich.rawdumper.camera.feature.ProportionFeature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.feature.preset.ManualController;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class ManualTemperatureController extends ManualController<WhiteBalancePreset, ManualTemperature>
{
    ManualTemperatureController(ActivityReference reference)
    {
        super(reference.findViewById(R.id.manualWbBt),
                reference.findViewById(R.id.manualWbBackBt),
                reference.findViewById(R.id.manualWbChooser),
                reference.findViewById(R.id.manualWbBar),
                reference.findViewById(R.id.stdWbChooser));
        SeekBar seekBar = reference.findViewById(R.id.manualWbBar);
        View decreaseBt = reference.findViewById(R.id.manualWbTungstenIcon);
        decreaseBt.setOnClickListener(v ->
        {
            double value = seekBar.getProgress() / (double) seekBar.getMax();
            value -= 0.005;
            if (value < 0d) value = 0d;
            seekBar.setProgress((int)Math.round(value * seekBar.getMax()));
        });
        View increaseBt = reference.findViewById(R.id.manualWbCloudyIcon);
        increaseBt.setOnClickListener(v ->
        {
            double value = seekBar.getProgress() / (double) seekBar.getMax();
            value += 0.005;
            if (value > 1d) value = 1d;
            seekBar.setProgress((int)Math.round(value * seekBar.getMax()));
        });
    }

    @Override
    protected WhiteBalancePreset getDefaultPresetValue()
    {
        return WhiteBalancePreset.AUTO;
    }

    @Override
    protected ManualTemperature getDisabledManualValue()
    {
        return ManualTemperature.DISABLED;
    }

    @Override
    protected ProportionFeature<ManualTemperature, ?> getManualFeature(TurboCamera camera)
    {
        return camera.getManualTemperatureFeature();
    }

    @Override
    protected WritableFeature<WhiteBalancePreset, ?> getPresetFeature(TurboCamera camera)
    {
        return camera.getWhiteBalancePresetFeature();
    }
}
