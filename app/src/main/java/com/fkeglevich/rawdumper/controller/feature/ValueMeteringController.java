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

public class ValueMeteringController<T extends Displayable> extends FeatureController
{
    public static final int AUTO_VALUE_TEXT_COLOR      = 0xFFFFFFFF;
    public static final int MANUAL_VALUE_TEXT_COLOR    = 0xFFFFFF00;
    public static final int METERING_DELAY_MILLIS      = 200;

    private final EventListener<ParameterChangeEvent<T>> fallbackChangedListener = eventData -> updateViewFromMetering();

    private final Runnable meteringRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (enabled)
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
    private boolean enabled = true;

    ValueMeteringController(TextView meteringView, T defaultValue)
    {
        this.handler = new Handler(Looper.getMainLooper());
        this.meteringView = meteringView;
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

        updateViewFromMetering();
    }

    @Override
    protected void disable()
    {
        enabled = false;
    }

    @Override
    protected void enable()
    {
        enabled = true;
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
            meteringView.setAlpha(0.25f);
            setViewText(defaultValue, true);
            return;
        }

        meteringView.setAlpha(1f);
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
            setViewText(fallbackReadings, false);
    }

    private Nullable<T> getMeteringReadings()
    {
        if (meteringFeature == null)
            return Nullable.of(null);

        return meteringFeature.getValue();
    }

    @SuppressWarnings("unchecked")
    private Feature<T> getFallbackFeature(TurboCamera turboCamera)
    {
        return turboCamera.getListFeature((Class<T>) defaultValue.getClass());
    }

    @SuppressWarnings("unchecked")
    private Feature<Nullable<T>> getMeteringFeature(TurboCamera turboCamera)
    {
        return turboCamera.getMeteringFeature((Class<T>) defaultValue.getClass());
    }
}
