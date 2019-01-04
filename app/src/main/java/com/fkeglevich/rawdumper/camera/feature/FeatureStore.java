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

import android.content.Context;
import android.content.SharedPreferences;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.controller.context.ContextManager;

import java.util.List;

class FeatureStore
{
    private static final String FEATURE_STORE_PREFIX = "feature-data-";
    private static final String RAW_SETTINGS_STORE_PREFIX = "raw-settings-";

    private static final FeatureStore instance = new FeatureStore();

    static FeatureStore getInstance()
    {
        return instance;
    }

    void storeData(CameraContext cameraContext, List<Feature> features)
    {
        storeFeaturesData(cameraContext, features);
        storeRawSettingsData(cameraContext);
    }

    private void storeFeaturesData(CameraContext cameraContext, List<Feature> features)
    {
        SharedPreferences preferences = getSharedPreferences(FEATURE_STORE_PREFIX, cameraContext);
        SharedPreferences.Editor editor = preferences.edit();
        for (Feature feature : features)
        {
            if (feature.isAvailable())
                feature.storeValue(editor);
        }
        editor.apply();
    }

    private void storeRawSettingsData(CameraContext cameraContext)
    {
        SharedPreferences preferences = getSharedPreferences(RAW_SETTINGS_STORE_PREFIX, cameraContext);
        SharedPreferences.Editor editor = preferences.edit();
        cameraContext.getRawSettings().storeValues(editor);
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    void loadData(CameraContext cameraContext, List<Feature> features)
    {
        SharedPreferences featurePrefs = getSharedPreferences(FEATURE_STORE_PREFIX, cameraContext);
        for (Feature feature : features)
        {
            if (feature.isAvailable() && feature instanceof WritableFeature)
                feature.loadValue(featurePrefs);
        }

        SharedPreferences preferences = getSharedPreferences(RAW_SETTINGS_STORE_PREFIX, cameraContext);
        cameraContext.getRawSettings().loadValues(preferences);
    }

    private SharedPreferences getSharedPreferences(String prefix, CameraContext cameraContext)
    {
        int cameraId = cameraContext.getCameraInfo().getId();
        String prefsName = prefix + cameraId;

        return ContextManager.getApplicationContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }
}
