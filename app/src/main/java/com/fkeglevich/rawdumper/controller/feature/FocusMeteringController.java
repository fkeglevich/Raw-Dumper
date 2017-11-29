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
import com.fkeglevich.rawdumper.camera.feature.FocusFeature;
import com.fkeglevich.rawdumper.camera.feature.ManualFocusFeature;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.util.event.EventListener;

import static com.fkeglevich.rawdumper.controller.feature.ValueMeteringController.AUTO_VALUE_TEXT_COLOR;
import static com.fkeglevich.rawdumper.controller.feature.ValueMeteringController.MANUAL_VALUE_TEXT_COLOR;

/**
 * Created by flavio on 22/11/17.
 */

public class FocusMeteringController extends FeatureController
{
    private final TextView focusText;
    private FocusFeature focusFeature;
    private ManualFocusFeature manualFocusFeature;

    private EventListener<ParameterChangeEvent<ManualFocus>> manualFocusListener = new EventListener<ParameterChangeEvent<ManualFocus>>()
    {
        @Override
        public void onEvent(ParameterChangeEvent<ManualFocus> eventData)
        {
            updateText(false);
        }
    };

    private EventListener<ParameterChangeEvent<FocusMode>> focusListener = new EventListener<ParameterChangeEvent<FocusMode>>()
    {
        @Override
        public void onEvent(ParameterChangeEvent<FocusMode> eventData)
        {
            updateText(false);
        }
    };

    public FocusMeteringController(TextView focusText)
    {
        this.focusText = focusText;
        updateText(false);
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        focusFeature = camera.getFocusFeature();
        if (!focusFeature.isAvailable())
        {
            reset();
            updateText(true);
            return;
        }

        focusFeature.getOnChanged().addListener(focusListener);

        manualFocusFeature = camera.getManualFocusFeature();
        if (manualFocusFeature.isAvailable())
            manualFocusFeature.getOnChanged().addListener(manualFocusListener);
        else
            manualFocusFeature = null;

        updateText(false);
    }

    @Override
    protected void reset()
    {
        if (focusFeature != null)
            focusFeature.getOnChanged().removeListener(focusListener);
        focusFeature = null;

        if (manualFocusFeature != null)
            manualFocusFeature.getOnChanged().removeListener(manualFocusListener);
        manualFocusFeature = null;

        updateText(false);
    }

    @Override
    protected void disable()
    {

    }

    @Override
    protected void enable()
    {

    }

    private void updateText(boolean showFixedFocusValue)
    {
        if (showFixedFocusValue)
        {
            focusText.setAlpha(0.25f);
            focusText.setTextColor(AUTO_VALUE_TEXT_COLOR);
            focusText.setText(FocusMode.FIXED.displayValue());
            return;
        }

        if (manualFocusFeature != null)
        {
            if (!manualFocusFeature.getValue().equals(ManualFocus.DISABLED))
            {
                focusText.setAlpha(1f);
                focusText.setTextColor(MANUAL_VALUE_TEXT_COLOR);
                focusText.setText(R.string.focus_manual);
                return;
            }
        }

        if (focusFeature != null)
        {
            focusText.setAlpha(1f);
            focusText.setTextColor(AUTO_VALUE_TEXT_COLOR);
            focusText.setText(focusFeature.getValue().displayValue());
        }
        else
        {
            focusText.setAlpha(0.25f);
            focusText.setTextColor(AUTO_VALUE_TEXT_COLOR);
            focusText.setText(FocusMode.AUTO.displayValue());
        }
    }
}
