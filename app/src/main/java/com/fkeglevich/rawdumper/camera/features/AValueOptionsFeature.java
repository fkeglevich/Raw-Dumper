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

import java.util.List;

/**
 * Created by Flávio Keglevich on 21/08/2017.
 * TODO: Add a class header comment!
 */

abstract class AValueOptionsFeature extends AFeature
{
    private String featureParamKey;

    AValueOptionsFeature(SharedCameraGetter sharedCameraGetter)
    {
        super(sharedCameraGetter);
    }

    void initializeFeatureParamKey(String featureParamKey)
    {
        this.featureParamKey = featureParamKey;
    }

    public String getValue()
    {
        synchronized (sharedCamera.getLock())
        {
            checkFeatureAvailability();
            return sharedCamera.get().getParameters().get(featureParamKey);
        }
    }

    public void setValue(String value)
    {
        synchronized (sharedCamera.getLock())
        {
            checkFeatureAvailability();
            sharedCamera.get().getParameters().setAndUpdate(featureParamKey, value);
        }
    }

    @Override
    public boolean isAvailable()
    {
        synchronized (sharedCamera.getLock())
        {
            return featureParamKey != null;
        }
    }

    public abstract List<String> getValidValues();
}
