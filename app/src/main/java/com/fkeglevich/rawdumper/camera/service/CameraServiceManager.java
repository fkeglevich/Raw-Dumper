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

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.service.available.CoarseIntegrationTimeMeteringService;
import com.fkeglevich.rawdumper.camera.service.available.SensorGainMeteringService;
import com.fkeglevich.rawdumper.camera.service.available.WhiteBalanceService;

import java.util.List;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 01/05/18.
 */
public class CameraServiceManager
{
    private static final LogcatFeatureService[] AVAILABLE_SERVICES = new LogcatFeatureService[]
            {WhiteBalanceService.getInstance(), SensorGainMeteringService.getInstance(), CoarseIntegrationTimeMeteringService.getInstance()};

    private static final CameraServiceManager instance = new CameraServiceManager();

    public static CameraServiceManager getInstance()
    {
        return instance;
    }

    private LogcatService logcatService;

    private CameraServiceManager()
    {   }

    public synchronized void prepare()
    {
        if (logcatService == null) logcatService = new LogcatService(createMatchArray());
    }

    public synchronized void enableFeatures(CameraContext cameraContext)
    {
        if (logcatService == null) return;
        List<String> serviceNames = getServiceNamesFromContext(cameraContext);
        for (String name : serviceNames)
            enableServiceFromName(name);

        logcatService.enterHalDebug();
    }

    public synchronized void disableFeatures()
    {
        if (logcatService == null) return;
        for (LogcatFeatureService featureService : AVAILABLE_SERVICES)
            featureService.setAvailable(false);
        logcatService.exitHalDebug();
    }

    private List<String> getServiceNamesFromContext(CameraContext cameraContext)
    {
        return cameraContext.getCameraInfo().getLogcatServices();
    }

    private LogcatMatch[] createMatchArray()
    {
        LogcatMatch[] result = new LogcatMatch[AVAILABLE_SERVICES.length];
        for (int i = 0; i < result.length; i++)
            result[i] = AVAILABLE_SERVICES[i].match;

        return result;
    }

    private void enableServiceFromName(String name)
    {
        for (LogcatFeatureService featureService : AVAILABLE_SERVICES)
            if (featureService.getClass().getSimpleName().equals(name))
            {
                featureService.setAvailable(true);
                return;
            }
    }
}
