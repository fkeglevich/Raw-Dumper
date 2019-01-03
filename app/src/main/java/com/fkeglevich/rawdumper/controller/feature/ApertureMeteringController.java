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

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.Aperture;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.util.Nullable;

import static com.fkeglevich.rawdumper.controller.feature.ValueMeteringController.AUTO_VALUE_TEXT_COLOR;

public class ApertureMeteringController extends FeatureController
{
    private static final int APERTURE_DELAY_MILLIS = 500;

    private final Runnable meteringRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (enabled)
                updateViewFromMetering();
            handler.postDelayed(meteringRunnable, APERTURE_DELAY_MILLIS);
        }
    };

    private final Handler handler;
    private final TextView meteringView;

    private Feature<Nullable<Aperture>> meteringFeature = null;
    private boolean enabled = true;

    ApertureMeteringController(TextView meteringView)
    {
        this.handler = new Handler(Looper.getMainLooper());
        this.meteringView = meteringView;
        updateViewFromMetering();
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        meteringFeature = camera.getMeteringFeature(Aperture.class);
        if (meteringFeature != null && meteringFeature.isAvailable())
        {
            handler.post(meteringRunnable);
            updateViewFromMetering();
        }
        else
            reset();
    }

    @Override
    protected void reset()
    {
        meteringFeature = null;
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

    private void setViewText(Aperture value)
    {
        meteringView.setTextColor(AUTO_VALUE_TEXT_COLOR);
        meteringView.setText(value.displayValue());
    }

    private void updateViewFromMetering()
    {
        Nullable<Aperture> meteringReadings = getMeteringReadings();
        if (meteringReadings.isPresent())
            setViewText(meteringReadings.get());
        else
            setViewText(Aperture.AUTO);
    }

    private Nullable<Aperture> getMeteringReadings()
    {
        if (meteringFeature == null)
            return Nullable.of(null);

        return meteringFeature.getValue();
    }
}
