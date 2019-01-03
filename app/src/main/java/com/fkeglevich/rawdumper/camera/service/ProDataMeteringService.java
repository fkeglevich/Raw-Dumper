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

package com.fkeglevich.rawdumper.camera.service;

import com.asus.camera.extensions.AsusCameraExtension;

import androidx.annotation.Nullable;

public class ProDataMeteringService
{
    private static final ProDataMeteringService instance = new ProDataMeteringService();
    private static final int DEFAULT_INTERVAL = 200;

    public static ProDataMeteringService getInstance()
    {
        return instance;
    }

    private AsusCameraExtension.ProfessionalData latestData = new AsusCameraExtension.ProfessionalData();
    private boolean dataIsAvailable = false;

    private ProDataMeteringService()
    {   }

    public void bind(AsusCameraExtension cameraExtension)
    {
        cameraExtension.getOnGotProfessionalData().addListener(eventData ->
        {
            if (eventData != null)
            {
                latestData.copyFrom(eventData);
                dataIsAvailable = true;
            }
        });
        cameraExtension.startQueryData(DEFAULT_INTERVAL);
    }

    public void unbind()
    {
        dataIsAvailable = false;
    }

    @Nullable
    public AsusCameraExtension.ProfessionalData getLatestData()
    {
        if (!AsusCameraExtension.isAvailable() || !dataIsAvailable) return null;
        return latestData;
    }

    public boolean isAvailable()
    {
        return dataIsAvailable;
    }
}
