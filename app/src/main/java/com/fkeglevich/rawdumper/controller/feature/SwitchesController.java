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

import android.support.annotation.IdRes;
import android.widget.Switch;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.raw.capture.RawSettings;

class SwitchesController extends FeatureController
{
    private static final int[] IDS = new int[]{R.id.klvSwitch, R.id.cmpSwitch, R.id.safSwitch};

    private final ActivityReference reference;

    SwitchesController(ActivityReference reference)
    {
        this.reference = reference;
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        RawSettings rawSettings = camera.getRawSettings();

        setupSwitch(R.id.klvSwitch, rawSettings.keepLensVignetting, rawSettings);
        setupSwitch(R.id.cmpSwitch, rawSettings.compressRawFiles, rawSettings);
        setupSwitch(R.id.safSwitch, rawSettings.shouldInvertFrontCameraRows, rawSettings);

        enable();
    }

    @Override
    protected void reset()
    {
        disable();
    }

    @Override
    protected void disable()
    {
        for (int id : IDS)
            reference.findViewById(id).setEnabled(false);
    }

    @Override
    protected void enable()
    {
        for (int id : IDS)
            reference.findViewById(id).setEnabled(true);
    }

    private void setupSwitch(@IdRes int id, boolean initValue, RawSettings rawSettings)
    {
        Switch widget = reference.findViewById(id);
        widget.setChecked(initValue);
        widget.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            switch (id)
            {
                case R.id.klvSwitch:
                    rawSettings.keepLensVignetting = isChecked;
                    break;

                case R.id.cmpSwitch:
                    rawSettings.compressRawFiles = isChecked;
                    break;

                case R.id.safSwitch:
                    rawSettings.shouldInvertFrontCameraRows = isChecked;
                    break;
            }
        });
    }
}
