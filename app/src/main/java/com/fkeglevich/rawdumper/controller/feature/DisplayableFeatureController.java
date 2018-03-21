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

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.Displayable;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.ui.listener.ItemSelectedListener;
import com.fkeglevich.rawdumper.util.event.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 19/10/17.
 */

public abstract class DisplayableFeatureController<T extends Displayable> extends FeatureController
{
    private final DisplayableFeatureUi featureUi;
    private WritableFeature<T, List<T>> feature;

    private boolean ignoreOnChangedListener = false;
    private EventListener<ParameterChangeEvent<T>> featureChangedListener = new EventListener<ParameterChangeEvent<T>>()
    {
        @Override
        public void onEvent(ParameterChangeEvent<T> eventData)
        {
            if (!ignoreOnChangedListener)
            {
                updateUiFromFeatureValue();
                featureUi.displayExternalChangeNotification(feature.getValue());
            }
            ignoreOnChangedListener = false;
        }
    };

    DisplayableFeatureController(DisplayableFeatureUi featureUi)
    {
        this.featureUi = featureUi;
        this.feature = null;
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

        List<T> valueList = feature.getAvailableValues();
        List<String> displayableValueList = new ArrayList<>();

        for (T value : valueList)
            displayableValueList.add(value.displayValue());

        setupFeatureUi(displayableValueList);
    }

    @Override
    protected void reset()
    {
        if (feature != null)
            feature.getOnChanged().removeListener(featureChangedListener);
        feature = null;
        featureUi.setListener(null);
        featureUi.disable();
    }

    @Override
    protected void disable()
    {
        featureUi.disable();
    }

    @Override
    protected void enable()
    {
        featureUi.enable();
    }

    private void setupFeatureUi(List<String> displayableValueList)
    {
        feature.getOnChanged().addListener(featureChangedListener);
        featureUi.setItems(displayableValueList);
        featureUi.setListener(index ->
        {
            ignoreOnChangedListener = true;
            updateFeatureValueFromUi(index);
        });
        updateUiFromFeatureValue();
        featureUi.enable();
    }

    private void updateFeatureValueFromUi(int index)
    {
        feature.setValue(feature.getAvailableValues().get(index));
    }

    private void updateUiFromFeatureValue()
    {
        featureUi.setSelectedIndex(feature.getAvailableValues().indexOf(feature.getValue()));
    }

    protected abstract WritableFeature<T, List<T>> selectFeature(TurboCamera camera);
}
