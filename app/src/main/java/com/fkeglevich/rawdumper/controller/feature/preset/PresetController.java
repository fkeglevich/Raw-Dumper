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

import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.adapter.ButtonController;
import com.fkeglevich.rawdumper.controller.animation.ButtonDisabledStateController;
import com.fkeglevich.rawdumper.controller.feature.Dismissible;
import com.fkeglevich.rawdumper.controller.feature.FeatureController;
import com.fkeglevich.rawdumper.controller.feature.OnClickNotifier;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public abstract class PresetController<T> extends FeatureController implements Dismissible
{
    private final OnClickNotifier clickNotifier;
    private final Map<T, View> iconMap;
    private final View mainButton;
    private final View chooser;
    private final View manualChooser;
    private final Fade fadeTransition;
    private final ButtonDisabledStateController buttonDisabledStateController;
    private WritableFeature<T, List<T>> feature;
    private View lastToggled = null;

    protected PresetController(ActivityReference reference, OnClickNotifier clickNotifier)
    {
        mainButton = reference.findViewById(getMainButtonId());
        buttonDisabledStateController = new ButtonDisabledStateController(mainButton, false);
        fadeTransition = new Fade();
        iconMap = new HashMap<>();
        manualChooser = reference.findViewById(getManualChooserId());
        this.clickNotifier = clickNotifier;
        chooser = reference.findViewById(getMainChooser());

        fadeTransition.setDuration(300L);
        hideChooser();
        initializeIconMap(reference);
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        feature = selectFeature(camera);
        if (!feature.isAvailable())
        {
            reset();
            return;
        }

        disableAllButtons();
        List<T> values = feature.getAvailableValues();
        View button;
        for (final T mode : values)
        {
            if (shouldIgnore(mode))
                continue;

            button = iconMap.get(mode);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(button1 ->
            {
                feature.setValue(mode);
                unToggleLastButton();
                toggleButton(button1);
            });
        }

        mainButton.setOnClickListener(v ->
        {
            if (chooser.getVisibility() == View.VISIBLE)
            {
                hideChooser();
                if (manualChooser.getVisibility() == View.VISIBLE)
                    onHidingChooserManualState();
            }
            else
            {
                clickNotifier.notifyOnClick();
                showChooser();
                if (manualChooser.getVisibility() == View.VISIBLE)
                    onShowingChooserManualState();
            }
        });

        toggleButton(iconMap.get(feature.getValue()));
        buttonDisabledStateController.enableAnimated();
    }

    private void hideChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) chooser.getRootView(), fadeTransition);
        chooser.setVisibility(View.INVISIBLE);
        ButtonController.changeButtonBackground(mainButton, R.drawable.round_button_default);
    }

    private void showChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) chooser.getRootView(), fadeTransition);
        chooser.setVisibility(View.VISIBLE);
        ButtonController.changeButtonBackground(mainButton, R.drawable.round_button_toggled);
    }

    private void toggleButton(View button)
    {
        ButtonController.changeButtonBackground(button, R.drawable.round_button_toggled);
        lastToggled = button;
    }

    private void unToggleLastButton()
    {
        if (lastToggled != null)
            ButtonController.changeButtonBackground(lastToggled, R.drawable.round_button_default);
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

    protected void putIconMap(ActivityReference reference, T value, @IdRes int id)
    {
        iconMap.put(value, reference.findViewById(id));
    }

    private void disableAllButtons()
    {
        for (View button : iconMap.values())
            button.setVisibility(View.GONE);
    }

    @Override
    public void dismiss()
    {
        hideChooser();
    }

    protected abstract void initializeIconMap(ActivityReference reference);
    protected abstract WritableFeature<T, List<T>> selectFeature(TurboCamera camera);
    protected abstract int getManualChooserId();
    protected abstract int getMainButtonId();
    protected abstract int getMainChooser();

    protected void onHidingChooserManualState()
    { }

    protected void onShowingChooserManualState()
    { }

    protected boolean shouldIgnore(T mode)
    {
        return false;
    }
}
