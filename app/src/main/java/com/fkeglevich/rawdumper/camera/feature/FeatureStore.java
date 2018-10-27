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

package com.fkeglevich.rawdumper.camera.feature;

import android.util.SparseArray;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.raw.capture.RawSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FeatureStore
{
    private static final FeatureStore instance = new FeatureStore();

    private SparseArray<Map<String, Object>> store = new SparseArray<>();
    private SparseArray<RawSettings> rawSettingsStore = new SparseArray<>();
    
    static FeatureStore getInstance()
    {
        return instance;
    }

    public void storeFeaturesData(CameraContext cameraContext, List<Feature> features)
    {
        Map<String, Object> map = getMapForCamera(cameraContext);
        for (Feature feature : features)
        {
            if (feature.isAvailable())
                map.put(feature.parameter.getKey(), feature.getValue());
        }

        RawSettings rawSettings = getRawSettingsForCamera(cameraContext);
        rawSettings.getDataFrom(cameraContext.getRawSettings());
    }

    @SuppressWarnings("unchecked")
    public void loadFeaturesData(CameraContext cameraContext, List<Feature> features)
    {
        Map<String, Object> map = getMapForCamera(cameraContext);
        for (Feature feature : features)
        {
            Object value = map.get(feature.parameter.getKey());
            if (value != null && feature.isAvailable() && feature instanceof WritableFeature)
            {
                WritableFeature writableFeature = (WritableFeature) feature;
                if (writableFeature.getValidator().isValid(value))
                    writableFeature.setValue(value);
            }
        }

        RawSettings rawSettings = getRawSettingsForCamera(cameraContext);
        cameraContext.getRawSettings().getDataFrom(rawSettings);
    }

    private Map<String, Object> getMapForCamera(CameraContext cameraContext)
    {
        int cameraId = cameraContext.getCameraInfo().getId();
        if (store.get(cameraId) == null)
            store.put(cameraId, new HashMap<>());

        return store.get(cameraId);
    }

    private RawSettings getRawSettingsForCamera(CameraContext cameraContext)
    {
        int cameraId = cameraContext.getCameraInfo().getId();
        if (rawSettingsStore.get(cameraId) == null)
            rawSettingsStore.put(cameraId, new RawSettings());

        return rawSettingsStore.get(cameraId);
    }
}
