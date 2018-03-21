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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.animation.ButtonDisabledStateController;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 27/10/17.
 */

public class FlashController extends FeatureController
{
    private WritableFeature<Flash, List<Flash>> flashFeature;
    private final ImageButton flashButton;
    private final Map<Flash, Integer> flashIconMap;
    private final ButtonDisabledStateController buttonDisabledStateController;

    private List<Flash> flashList;
    private int selectedFlashIndex;

    public FlashController(ImageButton flashButton)
    {
        this.flashFeature = null;
        this.flashButton = flashButton;
        this.flashIconMap = new HashMap<>();
        this.buttonDisabledStateController = new ButtonDisabledStateController(flashButton, false);
        initializeFlashIcons();
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        flashFeature = camera.getFlashFeature();
        if (!flashFeature.isAvailable())
        {
            reset();
            return;
        }

        flashList = createOrderedFlashList();
        initFlashIndex();
        updateButtonUi();

        buttonDisabledStateController.enableAnimated();

        flashButton.setOnClickListener(v ->
        {
            selectedFlashIndex++;
            if (selectedFlashIndex >= flashList.size())
                selectedFlashIndex = 0;

            flashFeature.setValue(flashList.get(selectedFlashIndex));
            updateButtonUi();
        });
    }

    private void initializeFlashIcons()
    {
        flashIconMap.put(Flash.OFF, R.drawable.ic_flash_off_black_24dp);
        flashIconMap.put(Flash.ON, R.drawable.ic_flash_on_black_24dp);
        flashIconMap.put(Flash.AUTO, R.drawable.ic_flash_auto_black_24dp);
        flashIconMap.put(Flash.TORCH, R.drawable.ic_highlight_black_24dp);
        flashIconMap.put(Flash.RED_EYE, R.drawable.ic_remove_red_eye_black_24dp);
    }

    private List<Flash> createOrderedFlashList()
    {
        List<Flash> valueList = new ArrayList<>();
        valueList.addAll(flashFeature.getAvailableValues());

        /*  Currently, we are disabling the torch and red-eye flash modes
            The reason for that is a pure cosmetic one, since having too much
            flash states in a single flash button can be irritating to the user*/
        valueList.remove(Flash.TORCH);
        valueList.remove(Flash.RED_EYE);

        Assert.assertTrue(valueList.contains(Flash.OFF));
        valueList.remove(Flash.OFF);
        valueList.add(0, Flash.OFF);

        return valueList;
    }

    private void initFlashIndex()
    {
        Flash current = flashFeature.getValue();
        if (current == Flash.TORCH)
        {
            flashFeature.setValue(Flash.OFF);
            current = Flash.OFF;
        }

        selectedFlashIndex = flashList.indexOf(current);
    }

    private void updateButtonUi()
    {
        Flash current = flashList.get(selectedFlashIndex);
        setIconFromFlash(current);
    }

    private void setIconFromFlash(Flash current)
    {
        Context context = flashButton.getContext();
        Drawable icon = ContextCompat.getDrawable(context, flashIconMap.get(current));
        flashButton.setImageDrawable(icon);
    }

    @Override
    protected void reset()
    {
        setIconFromFlash(Flash.OFF);
        if (flashFeature != null && flashFeature.isAvailable())
            buttonDisabledStateController.disableAnimated();
        else
            buttonDisabledStateController.disable();

        flashFeature = null;
    }

    @Override
    protected void disable()
    {
        buttonDisabledStateController.disableAnimated();
    }

    @Override
    protected void enable()
    {
        buttonDisabledStateController.enableAnimated();
    }
}
