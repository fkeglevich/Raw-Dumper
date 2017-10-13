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
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/10/17.
 */

public class FeatureRecyclerFactory
{
    private final List<Feature> registeredFeatures = new ArrayList<>();
    private final CameraContext cameraContext;
    private final ParameterCollection parameterCollection;

    public FeatureRecyclerFactory(CameraContext cameraContext, ParameterCollection parameterCollection)
    {
        this.cameraContext = cameraContext;
        this.parameterCollection = parameterCollection;
    }

    private void registerFeature(Feature feature)
    {
        registeredFeatures.add(feature);
    }

    public void cleanUpAllFeatures()
    {
        for (Feature feature : registeredFeatures)
            Feature.clearEventDispatchers(feature);

        registeredFeatures.clear();
    }

    public WritableFeature<Iso, List<Iso>> createIsoFeature()
    {
        IsoFeature result = IsoFeature.create(cameraContext.getCameraInfo().getExposure(), parameterCollection);
        registerFeature(result);
        return result;
    }

    public WritableFeature<ShutterSpeed, List<ShutterSpeed>> createShutterSpeedFeature()
    {
        ShutterSpeedFeature result = ShutterSpeedFeature.create(cameraContext.getCameraInfo().getExposure(), parameterCollection);
        registerFeature(result);
        return result;
    }

    public WritableFeature<Ev, List<Ev>> createEVFeature()
    {
        EvFeature result = EvFeature.create(parameterCollection);
        registerFeature(result);
        return result;
    }

    public WritableFeature<Flash, List<Flash>> createFlashFeature()
    {
        FlashFeature result = new FlashFeature(parameterCollection);
        registerFeature(result);
        return result;
    }

    public Feature<CaptureSize> createPreviewFeature()
    {
        PreviewFeature result = new PreviewFeature(parameterCollection);
        registerFeature(result);
        return result;
    }
}
