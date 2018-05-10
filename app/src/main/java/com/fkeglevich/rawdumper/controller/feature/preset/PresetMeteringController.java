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

package com.fkeglevich.rawdumper.controller.feature.preset;

import android.widget.TextView;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.Displayable;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.controller.feature.FeatureController;
import com.fkeglevich.rawdumper.util.event.EventListener;

import java.util.List;

import static com.fkeglevich.rawdumper.controller.feature.ValueMeteringController.AUTO_VALUE_TEXT_COLOR;
import static com.fkeglevich.rawdumper.controller.feature.ValueMeteringController.MANUAL_VALUE_TEXT_COLOR;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public abstract class PresetMeteringController<P extends Displayable, M, MR> extends FeatureController
{
    private final TextView textView;
    private WritableFeature<P, List<P>> presetFeature;
    private WritableFeature<M, MR> manualFeature;
    private EventListener<ParameterChangeEvent<P>> presetListener = eventData -> updateText(false);
    private EventListener<ParameterChangeEvent<M>> manualListener = eventData -> updateText(false);

    public PresetMeteringController(TextView textView)
    {
        this.textView = textView;
        updateText(false);
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        presetFeature = getPresetFeature(camera);
        if (!presetFeature.isAvailable())
        {
            reset();
            updateText(true);
            return;
        }

        presetFeature.getOnChanged().addListener(presetListener);

        manualFeature = getManualFeature(camera);
        if (manualFeature.isAvailable())
            manualFeature.getOnChanged().addListener(manualListener);
        else
            manualFeature = null;

        updateText(false);
    }

    @Override
    protected void reset()
    {
        if (presetFeature != null)
            presetFeature.getOnChanged().removeListener(presetListener);
        presetFeature = null;

        if (manualFeature != null)
            manualFeature.getOnChanged().removeListener(manualListener);
        manualFeature = null;

        updateText(false);
    }

    @Override
    protected void disable()
    { }

    @Override
    protected void enable()
    { }

    private void updateText(boolean showUnavailableValue)
    {
        if (showUnavailableValue)
        {
            textView.setAlpha(0.25f);
            textView.setTextColor(AUTO_VALUE_TEXT_COLOR);
            textView.setText(getUnavailableValue().displayValue());
            return;
        }

        if (manualFeature != null)
        {
            if (!manualFeature.getValue().equals(getDisabledManualValue()))
            {
                textView.setAlpha(1f);
                textView.setTextColor(MANUAL_VALUE_TEXT_COLOR);
                textView.setText(getManualText(manualFeature));
                return;
            }
        }

        if (presetFeature != null)
        {
            textView.setAlpha(1f);
            textView.setTextColor(AUTO_VALUE_TEXT_COLOR);
            textView.setText(presetFeature.getValue().displayValue());
        }
        else
        {
            textView.setAlpha(0.25f);
            textView.setTextColor(AUTO_VALUE_TEXT_COLOR);
            textView.setText(getDefaultValue().displayValue());
        }
    }

    protected abstract WritableFeature<P, List<P>> getPresetFeature(TurboCamera camera);
    protected abstract WritableFeature<M, MR> getManualFeature(TurboCamera camera);

    protected abstract P getUnavailableValue();
    protected abstract P getDefaultValue();
    protected abstract String getManualText(WritableFeature<M, MR> manualFeature);
    protected abstract M getDisabledManualValue();
}
