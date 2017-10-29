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

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.Displayable;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.util.Nullable;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public abstract class ValueMeteringController<T extends Displayable> extends FeatureController
{
    private static final int AUTO_VALUE_TEXT_COLOR      = 0xFFFFFFFF;
    private static final int MANUAL_VALUE_TEXT_COLOR    = 0xFFFFFF00;
    private static final int METERING_DELAY_MILLIS      = 100;

    private final EventListener<ParameterChangeEvent<T>> fallbackChangedListener = new EventListener<ParameterChangeEvent<T>>()
    {
        @Override
        public void onEvent(ParameterChangeEvent<T> eventData)
        {
            updateViewFromMetering();
        }
    };

    private final Runnable meteringRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            updateViewFromMetering();
            if (fallbackFeature != null)
                handler.postDelayed(meteringRunnable, METERING_DELAY_MILLIS);
        }
    };

    private final Handler handler;
    private final TextView meteringView;
    private final T defaultValue;

    private Feature<Nullable<T>> meteringFeature = null;
    private Feature<T> fallbackFeature = null;

    ValueMeteringController(TextView meteringView, T defaultValue)
    {
        this.handler = new Handler(Looper.getMainLooper());
        this.meteringView = meteringView;
        meteringView.setAlpha(0.25f);
        this.defaultValue = defaultValue;
        updateViewFromMetering();
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        fallbackFeature = getFallbackFeature(camera);
        if (!fallbackFeature.isAvailable())
        {
            reset();
            return;
        }

        meteringFeature = getMeteringFeature(camera);
        if (meteringFeature != null && !meteringFeature.isAvailable())
            meteringFeature = null;

        meteringView.setAlpha(1f);
        fallbackFeature.getOnChanged().addListener(fallbackChangedListener);
        handler.post(meteringRunnable);
        updateViewFromMetering();
    }

    @Override
    protected void reset()
    {
        meteringFeature = null;

        if (fallbackFeature != null)
            fallbackFeature.getOnChanged().removeListener(fallbackChangedListener);
        fallbackFeature = null;

        meteringView.setAlpha(0.25f);
        updateViewFromMetering();
    }

    private void setViewText(T value, boolean isAuto)
    {
        meteringView.setTextColor(isAuto ? AUTO_VALUE_TEXT_COLOR : MANUAL_VALUE_TEXT_COLOR);
        meteringView.setText(value.displayValue());
    }

    private void updateViewFromMetering()
    {
        if (fallbackFeature == null)
        {
            setViewText(defaultValue, true);
            return;
        }

        T fallbackReadings = fallbackFeature.getValue();
        if (defaultValue.equals(fallbackReadings))
        {
            Nullable<T> meteringReadings = getMeteringReadings();
            if (meteringReadings.isPresent())
                setViewText(meteringReadings.get(), true);
            else
                setViewText(defaultValue, true);
        }
        else
        {
            setViewText(fallbackReadings, false);
        }
    }

    private Nullable<T> getMeteringReadings()
    {
        if (meteringFeature == null)
            return Nullable.of(null);

        return meteringFeature.getValue();
    }

    protected abstract Feature<Nullable<T>> getMeteringFeature(TurboCamera turboCamera);
    protected abstract Feature<T> getFallbackFeature(TurboCamera turboCamera);
}
