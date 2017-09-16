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

package com.fkeglevich.rawdumper.camera.features;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.async.SharedCameraGetter;
import com.fkeglevich.rawdumper.camera.extension.IntelParameters;
import com.fkeglevich.rawdumper.camera.shared.SharedParameters;

import static com.fkeglevich.rawdumper.camera.extension.IntelParameters.KEY_RAW_DATA_FORMAT;
import static com.fkeglevich.rawdumper.camera.extension.IntelParameters.RAW_DATA_FORMAT_BAYER;
import static com.fkeglevich.rawdumper.camera.extension.IntelParameters.RAW_DATA_FORMAT_NONE;

/**
 * Created by Flávio Keglevich on 16/08/2017.
 * TODO: Add a class header comment!
 */

public class RawCapture extends AFeature
{
    private Boolean isAvailableCache = null;

    RawCapture(SharedParameters sharedParameters, Object lock)
    {
        super(sharedParameters, lock);
    }

    public void enable()
    {
        setValue(true);
    }

    public void disable()
    {
        setValue(false);
    }

    public boolean isEnabled()
    {
        synchronized (lock)
        {
            String value = sharedParameters.get(KEY_RAW_DATA_FORMAT);
            return RAW_DATA_FORMAT_BAYER.equals(value);
        }
    }

    public boolean isAvailable()
    {
        synchronized (lock)
        {
            if (isAvailableCache != null) return isAvailableCache;

            boolean result = true;
            Camera.Parameters parameters = sharedParameters.getRawParameters();
            {
                String old = parameters.get(KEY_RAW_DATA_FORMAT);
                {
                    parameters.set(KEY_RAW_DATA_FORMAT, RAW_DATA_FORMAT_BAYER);

                    try { sharedParameters.setRawParameters(parameters); }

                    catch (RuntimeException re) { result = false; }

                    parameters.remove(KEY_RAW_DATA_FORMAT);
                }
                if (old != null) parameters.set(KEY_RAW_DATA_FORMAT, old);
            }
            sharedParameters.setRawParameters(parameters);
            isAvailableCache = result;
            return result;
        }
    }

    private void setValue(boolean value)
    {
        synchronized (lock)
        {
            checkFeatureAvailability();
            String strValue = value ? RAW_DATA_FORMAT_BAYER : RAW_DATA_FORMAT_NONE;
            sharedParameters.setAndUpdate(KEY_RAW_DATA_FORMAT, strValue);
        }
    }
}
