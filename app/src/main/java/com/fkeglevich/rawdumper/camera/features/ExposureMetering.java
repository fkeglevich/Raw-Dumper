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
import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.helper.ExposureHelper;

/**
 * Class for real time ISO and exposure time metering.
 *
 * Created by Flávio Keglevich on 21/08/2017.
 */

public class ExposureMetering extends AFeature
{
    ExposureMetering(SharedCameraGetter sharedCameraGetter)
    {
        super(sharedCameraGetter);
    }

    public int getCurrentIso()
    {
        synchronized (sharedCamera.getLock())
        {
            return sharedCamera.get().getParameters().getInt(AsusParameters.ASUS_XENON_ISO);
        }
    }

    public double getCurrentExposureTime()
    {
        synchronized (sharedCamera.getLock())
        {
            int value = sharedCamera.get().getParameters().getInt(AsusParameters.ASUS_XENON_EXPOSURE_TIME);
            return ExposureHelper.decodeIntegerExposureTime(value);
        }
    }

    @Override
    public boolean isAvailable()
    {
        synchronized (sharedCamera.getLock())
        {
            Camera.Parameters parameters = sharedCamera.get().getCamera().getParameters();
            String iso          = parameters.get(AsusParameters.ASUS_XENON_ISO);
            String exposureTime = parameters.get(AsusParameters.ASUS_XENON_EXPOSURE_TIME);

            return (iso != null) && (exposureTime != null);
        }
    }
}
