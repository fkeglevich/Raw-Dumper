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

import com.fkeglevich.rawdumper.camera.async.SharedCameraGetter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Flávio Keglevich on 16/08/2017.
 * TODO: Add a class header comment!
 */

public class Iso extends AFeature
{
    private final String isoKey;

    Iso(SharedCameraGetter sharedCameraGetter)
    {
        super(sharedCameraGetter);
        synchronized (sharedCamera.getLock())
        {
            isoKey = sharedCamera.get().getExtraCameraInfo().getExposure().getIsoParameter();
        }
    }

    public String getValue()
    {
        synchronized (sharedCamera.getLock())
        {
            checkFeatureAvailability();
            return sharedCamera.get().getParameters().get(isoKey);
        }
    }

    public void setValue(String value)
    {
        synchronized (sharedCamera.getLock())
        {
            checkFeatureAvailability();
            sharedCamera.get().getParameters().setAndUpdate(isoKey, value);
        }
    }

    public List<String> getValidValues()
    {
        synchronized (sharedCamera.getLock())
        {
            checkFeatureAvailability();
            return Arrays.asList(sharedCamera.get().getExtraCameraInfo().getExposure().getIsoValues().clone());
        }
    }

    public String displayValue(String value)
    {
        return value.toUpperCase();
    }

    @Override
    public boolean isAvailable()
    {
        synchronized (sharedCamera.getLock())
        {
            return isoKey != null;
        }
    }
}
