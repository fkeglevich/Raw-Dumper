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
import android.widget.SeekBar;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.feature.ManualFocusFeature;

/**
 * Created by flavio on 22/11/17.
 */

public class ManualFocusController extends FeatureController
{
    private final View manualButton;
    private final View backButton;
    private final SeekBar focusSlider;
    private ManualFocusFeature manualFocusFeature;

    public ManualFocusController(View manualButton, View backButton, SeekBar focusSlider)
    {
        this.manualButton = manualButton;
        this.backButton = backButton;
        this.focusSlider = focusSlider;
        manualButton.setVisibility(View.GONE);
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        manualFocusFeature = camera.getManualFocusFeature();
        if (!manualFocusFeature.isAvailable())
        {
            reset();
            return;
        }

        manualButton.setVisibility(View.VISIBLE);
        manualButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }

    @Override
    protected void reset()
    {

    }

    @Override
    protected void disable()
    {

    }

    @Override
    protected void enable()
    {

    }
}
