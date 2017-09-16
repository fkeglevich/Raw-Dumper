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

import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.helper.ExposureHelper;
import com.fkeglevich.rawdumper.camera.shared.SharedParameters;

/**
 * Class for real time ISO and exposure time metering.
 *
 * Created by Flávio Keglevich on 21/08/2017.
 */

public class CurrentExposureMetering extends AFeature
{
    CurrentExposureMetering(SharedParameters sharedParameters, Object lock)
    {
        super(sharedParameters, lock);
    }

    public int getCurrentIso()
    {
        synchronized (lock)
        {
            checkFeatureAvailability();
            return sharedParameters.getInt(AsusParameters.ASUS_XENON_ISO);
        }
    }

    public double getCurrentExposureTime()
    {
        synchronized (lock)
        {
            checkFeatureAvailability();
            int value = sharedParameters.getInt(AsusParameters.ASUS_XENON_EXPOSURE_TIME);
            return ExposureHelper.decodeIntegerExposureTime(value);
        }
    }

    @Override
    public boolean isAvailable()
    {
        synchronized (lock)
        {
            String iso          = sharedParameters.get(AsusParameters.ASUS_XENON_ISO);
            String exposureTime = sharedParameters.get(AsusParameters.ASUS_XENON_EXPOSURE_TIME);

            return (iso != null) && (exposureTime != null);
        }
    }
}
