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
import com.fkeglevich.rawdumper.camera.exception.UnavailableFeatureException;
import com.fkeglevich.rawdumper.camera.extension.IntelParameters;

/**
 * Created by Flávio Keglevich on 16/08/2017.
 * TODO: Add a class header comment!
 */

public class RawCapture extends AFeature
{
    private Boolean isAvailableCache = null;

    RawCapture(SharedCameraGetter sharedCameraGetter)
    {
        super(sharedCameraGetter);
    }

    public void enable()
    {
        if (!isAvailable()) throw new UnavailableFeatureException();
        setValue(true);
    }

    public void disable()
    {
        setValue(false);
    }

    public boolean isEnabled()
    {
        synchronized (sharedCamera.getLock())
        {
            String value = sharedCamera.get().getCamera().getParameters().get(IntelParameters.KEY_RAW_DATA_FORMAT);
            return IntelParameters.RAW_DATA_FORMAT_BAYER.equals(value);
        }
    }

    public boolean isAvailable()
    {
        synchronized (sharedCamera.getLock())
        {
            if (isAvailableCache != null) return isAvailableCache;

            boolean result = true;
            Camera.Parameters parameters = sharedCamera.get().getCamera().getParameters();
            {
                String old = parameters.get(IntelParameters.KEY_RAW_DATA_FORMAT);
                {
                    parameters.set(IntelParameters.KEY_RAW_DATA_FORMAT, IntelParameters.RAW_DATA_FORMAT_BAYER);

                    try { sharedCamera.get().getCamera().setParameters(parameters); }

                    catch (RuntimeException re) { result = false; }

                    parameters.remove(IntelParameters.KEY_RAW_DATA_FORMAT);
                }
                if (old != null) parameters.set(IntelParameters.KEY_RAW_DATA_FORMAT, old);
            }
            sharedCamera.get().getCamera().setParameters(parameters);
            isAvailableCache = result;
            return result;
        }
    }

    private void setValue(boolean value)
    {
        synchronized (sharedCamera.getLock())
        {
            Camera.Parameters parameters = sharedCamera.get().getCamera().getParameters();
            String strValue = value ? IntelParameters.RAW_DATA_FORMAT_BAYER : IntelParameters.RAW_DATA_FORMAT_NONE;
            parameters.set(IntelParameters.KEY_RAW_DATA_FORMAT, strValue);
            sharedCamera.get().getCamera().setParameters(parameters);
        }
    }
}
