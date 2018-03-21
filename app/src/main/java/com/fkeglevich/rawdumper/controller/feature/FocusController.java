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

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.feature.FocusFeature;
import com.fkeglevich.rawdumper.controller.animation.ButtonDisabledStateController;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flavio on 21/11/17.
 */

public class FocusController extends FeatureController implements Dismissible
{
    private final OnClickNotifier clickNotifier;
    private final Map<FocusMode, View> focusButtonMap;
    private final View focusButton;
    private final View chooser;
    private final Fade fadeTransition;
    private final ButtonDisabledStateController buttonDisabledStateController;

    private FocusFeature focusFeature;
    private View lastToggled = null;

    FocusController(
            ActivityReference reference,
            OnClickNotifier clickNotifier)
    {
        this.clickNotifier = clickNotifier;
        focusButtonMap = new HashMap<>();
        focusButton = reference.weaklyGet().findViewById(R.id.focusBt);
        chooser = reference.weaklyGet().findViewById(R.id.focusChooser);
        fadeTransition = new Fade();
        fadeTransition.setDuration(300L);
        buttonDisabledStateController = new ButtonDisabledStateController(focusButton, false);
        hideChooser();

        initializeMaps(reference);
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        focusFeature = camera.getFocusFeature();
        if (!focusFeature.isAvailable())
        {
            reset();
            return;
        }

        disableAllButtons();
        List<FocusMode> focusModes = focusFeature.getAvailableValues();
        View button;
        for (final FocusMode mode : focusModes)
        {
            if (mode.equals(FocusMode.CONTINUOUS_VIDEO))
                continue;

            button = focusButtonMap.get(mode);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(button1 ->
            {
                focusFeature.setValue(mode);
                unToggleLastButton();
                toggleButton(button1);
            });
        }

        focusButton.setOnClickListener(v ->
        {
            if (chooser.getVisibility() == View.VISIBLE)
                hideChooser();
            else
            {
                clickNotifier.notifyOnClick();
                showChooser();
            }
        });

        toggleButton(focusButtonMap.get(focusFeature.getValue()));
        buttonDisabledStateController.enableAnimated();
    }

    private void hideChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) chooser.getRootView(), fadeTransition);
        chooser.setVisibility(View.INVISIBLE);
    }

    private void showChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) chooser.getRootView(), fadeTransition);
        chooser.setVisibility(View.VISIBLE);
    }

    private void toggleButton(View button)
    {
        changeButtonBackground(button, R.drawable.round_button_toggled);
        lastToggled = button;
    }

    private void unToggleLastButton()
    {
        if (lastToggled != null)
            changeButtonBackground(lastToggled, R.drawable.round_button_default);
    }

    private void changeButtonBackground(View button, @DrawableRes int id)
    {
        ViewCompat.setBackground(button, ContextCompat.getDrawable(button.getContext(), id));
        button.invalidate();
    }

    @Override
    protected void reset()
    {
        unToggleLastButton();
        disableAllButtons();
        buttonDisabledStateController.disableAnimated();
        hideChooser();
    }

    @Override
    protected void disable()
    {
        buttonDisabledStateController.disable();
    }

    @Override
    protected void enable()
    {
        buttonDisabledStateController.enable();
    }

    private void initializeMaps(ActivityReference reference)
    {
        putFocusButtonMap(reference, FocusMode.AUTO,                 R.id.autoFocusBt);
        putFocusButtonMap(reference, FocusMode.CONTINUOUS_PICTURE,   R.id.continuousFocusBt);
        putFocusButtonMap(reference, FocusMode.CONTINUOUS_VIDEO,     R.id.continuousFocusBt);
        putFocusButtonMap(reference, FocusMode.MACRO,                R.id.macroFocusBt);
        putFocusButtonMap(reference, FocusMode.INFINITY,             R.id.infinityFocusBt);
        putFocusButtonMap(reference, FocusMode.FIXED,                R.id.fixedFocusBt);
        putFocusButtonMap(reference, FocusMode.EDOF,                 R.id.edofFocusBt);
    }

    private void putFocusButtonMap(ActivityReference reference, FocusMode mode, @IdRes int id)
    {
        focusButtonMap.put(mode, reference.weaklyGet().findViewById(id));
    }

    private void disableAllButtons()
    {
        for (View button : focusButtonMap.values())
            button.setVisibility(View.GONE);
    }

    @Override
    public void dismiss()
    {
        hideChooser();
    }
}
