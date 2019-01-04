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

package com.fkeglevich.rawdumper.camera.feature;

import com.fkeglevich.rawdumper.camera.async.CameraContext;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

class FeatureRecyclerFactoryBase
{
    private final List<Feature> registeredFeatures = new ArrayList<>();

    <T extends Feature> T register(T feature)
    {
        registeredFeatures.add(feature);
        return feature;
    }

    @SuppressWarnings("unchecked")
    public void storeAndCleanupFeatures(CameraContext cameraContext)
    {
        FeatureStore.getInstance().storeData(cameraContext, registeredFeatures);
        for (Feature feature : registeredFeatures)
            Feature.clearEventDispatchers(feature);

        registeredFeatures.clear();
    }

    public void loadFeaturesData(CameraContext cameraContext)
    {
        FeatureStore.getInstance().loadData(cameraContext, registeredFeatures);
    }
}
