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
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.feature.ManualFocusFeature;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.Visibility;

/**
 * Created by flavio on 22/11/17.
 */

public class ManualFocusController extends FeatureController
{
    private final View manualButton;
    private final View backButton;
    private final SeekBar focusSlider;
    private final View manualFocusChooser;
    private final View stdFocusChooser;
    private final Visibility manualChooserTransition;
    private final Visibility stdChooserTransition;
    private ManualFocusFeature manualFocusFeature;

    public ManualFocusController(View manualButton,
                                 View backButton,
                                 SeekBar focusSlider,
                                 View manualFocusChooser,
                                 View stdFocusChooser)
    {
        this.manualButton = manualButton;
        this.backButton = backButton;
        this.focusSlider = focusSlider;
        this.manualFocusChooser = manualFocusChooser;
        this.stdFocusChooser = stdFocusChooser;

        manualChooserTransition = new Fade();//new Slide(Gravity.END);

        manualChooserTransition.setDuration(300L);

        stdChooserTransition = new Fade();//new Slide(Gravity.START);
        stdChooserTransition.setDuration(300L);

        disable();
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

        enable();
        manualButton.setOnClickListener(v -> showChooser());
        backButton.setOnClickListener(v -> hideChooser());

        focusSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updateManualFocus(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    private void updateManualFocus(int progress)
    {
        double proportion = progress / (double)focusSlider.getMax();
        manualFocusFeature.setValueAsProportion(proportion);
    }

    @Override
    protected void reset()
    {
        hideChooser();
    }

    @Override
    protected void disable()
    {
        manualButton.setVisibility(View.GONE);
    }

    @Override
    protected void enable()
    {
        manualButton.setVisibility(View.VISIBLE);
    }

    private void hideChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) manualFocusChooser, manualChooserTransition);
        manualFocusChooser.setVisibility(View.INVISIBLE);
        TransitionManager.beginDelayedTransition((ViewGroup) stdFocusChooser, stdChooserTransition);
        stdFocusChooser.setVisibility(View.VISIBLE);

        if (manualFocusFeature != null && manualFocusFeature.isAvailable())
            manualFocusFeature.setValue(ManualFocus.DISABLED);
    }

    private void showChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) manualFocusChooser, manualChooserTransition);
        manualFocusChooser.setVisibility(View.VISIBLE);
        TransitionManager.beginDelayedTransition((ViewGroup) stdFocusChooser, stdChooserTransition);
        stdFocusChooser.setVisibility(View.INVISIBLE);

        updateManualFocus(focusSlider.getProgress());
    }
}
