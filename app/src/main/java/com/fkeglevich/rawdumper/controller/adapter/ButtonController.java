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

package com.fkeglevich.rawdumper.controller.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.camera.data.Displayable;
import com.fkeglevich.rawdumper.controller.animation.ButtonDisabledStateController;
import com.fkeglevich.rawdumper.controller.feature.Dismissible;
import com.fkeglevich.rawdumper.controller.feature.DisplayableFeatureUi;
import com.fkeglevich.rawdumper.controller.feature.ExternalNotificationController;
import com.fkeglevich.rawdumper.controller.feature.OnClickNotifier;
import com.fkeglevich.rawdumper.ui.listener.ItemSelectedListener;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;

import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 23/10/17.
 */

public class ButtonController implements DisplayableFeatureUi, Dismissible
{
    private final View button;
    private final DisplayableFeatureUi adapter;
    private final View chooser;
    private final ExternalNotificationController notificationController;
    private final OnClickNotifier clickNotifier;
    private final Fade fadeTransition;
    private final ButtonDisabledStateController buttonDisabledStateController;

    public static void changeButtonBackground(View button, @DrawableRes int id)
    {
        ViewCompat.setBackground(button, ContextCompat.getDrawable(button.getContext(), id));
        button.invalidate();
    }

    public ButtonController(View button, DisplayableFeatureUi adapter, View chooser,
                            ExternalNotificationController notificationController,
                            OnClickNotifier clickNotifier)
    {
        this.button = button;
        button.setOnClickListener(v -> toggleChooser());

        this.adapter = adapter;
        this.chooser = chooser;
        this.notificationController = notificationController;
        this.clickNotifier = clickNotifier;
        this.fadeTransition = new Fade();
        fadeTransition.setDuration(300L);

        this.buttonDisabledStateController = new ButtonDisabledStateController(button, false);
        hideChooser();
    }

    @Override
    public void setItems(List<String> items)
    {
        adapter.setItems(items);
    }

    @Override
    public void setSelectedIndex(int index)
    {
        adapter.setSelectedIndex(index);
    }

    @Override
    public void displayExternalChangeNotification(Displayable newValue)
    {
        adapter.displayExternalChangeNotification(newValue);
        notificationController.showNotification(newValue);
    }

    @Override
    public void setListener(ItemSelectedListener listener)
    {
        adapter.setListener(listener);
    }

    @Override
    public void enable()
    {
        adapter.enable();
        buttonDisabledStateController.enableAnimated();
    }

    @Override
    public void disable()
    {
        adapter.disable();
        buttonDisabledStateController.disableAnimated();
        hideChooser();
    }

    @Override
    public void dismiss()
    {
        hideChooser();
    }

    private void hideChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) chooser.getRootView(), fadeTransition);
        chooser.setVisibility(View.INVISIBLE);
        changeButtonBackground(button, R.drawable.round_button_default);
    }

    private void showChooser()
    {
        TransitionManager.beginDelayedTransition((ViewGroup) chooser.getRootView(), fadeTransition);
        chooser.setVisibility(View.VISIBLE);
        changeButtonBackground(button, R.drawable.round_button_toggled);
    }

    private void toggleChooser()
    {
        if (chooser.getVisibility() == View.VISIBLE)
            hideChooser();
        else
        {
            clickNotifier.notifyOnClick();
            showChooser();
        }
    }
}
