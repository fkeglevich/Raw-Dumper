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

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.RadioGroup;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.util.event.EventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.CompoundButtonCompat;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 24/05/18.
 */
public abstract class RadioGroupController<T> extends FeatureController
{
    private final ActivityReference reference;
    private final RadioGroup radioGroup;
    private final ColorStateList stateList;
    private WritableFeature<T, List<T>> feature;
    private final Map<View, T> viewValueMap = new HashMap<>();
    private final EventListener<Void> validatorListener = eventData -> updateSizeList();

    private boolean isUpdating = false;

    RadioGroupController(ActivityReference reference, RadioGroup radioGroup)
    {
        int accentColor = ResourcesCompat.getColor(reference.weaklyGet().getResources(), R.color.colorAccent, null);

        this.reference = reference;
        this.radioGroup = radioGroup;
        this.stateList = new ColorStateList(new int[][]
                {
                        new int[]{-android.R.attr.state_checked},
                        new int[]{ android.R.attr.state_checked}
                },
                new int[]
                        {
                                Color.WHITE
                                , accentColor
                        });
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
        feature.onValidatorChanged.addListener(validatorListener);
        radioGroup.setOnCheckedChangeListener((group, checkedId) ->
        {
            if (!isUpdating)
                feature.setValue(viewValueMap.get(group.findViewById(checkedId)));
        });
        updateSizeList();
    }

    @Override
    protected void reset()
    {
        feature.onValidatorChanged.removeListener(validatorListener);
        disable();
    }

    @Override
    protected void disable()
    {
        radioGroup.setEnabled(false);
    }

    @Override
    protected void enable()
    {
        radioGroup.setEnabled(true);
    }

    private void updateSizeList()
    {
        isUpdating = true;
        AppCompatRadioButton button;
        T value, current = feature.getValue();
        radioGroup.removeAllViews();
        viewValueMap.clear();
        for(int i = 0; i < feature.getAvailableValues().size(); i++)
        {
            button = new AppCompatRadioButton(reference.weaklyGet());
            value = feature.getAvailableValues().get(i);

            button.setText(displayValue(value));
            button.setTextColor(0xFFFFFFFF);
            CompoundButtonCompat.setButtonTintList(button, stateList);
            button.setId(View.generateViewId());
            viewValueMap.put(button, value);
            if (value.equals(current))
                button.setChecked(true);
            radioGroup.addView(button);
        }
        radioGroup.invalidate();
        isUpdating = false;
    }

    protected abstract WritableFeature<T, List<T>> selectFeature(TurboCamera camera);
    protected abstract String displayValue(T value);
}
