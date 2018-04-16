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

import android.support.annotation.Nullable;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.su.ShellManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraLogcatService
{
    private static final String ENTER_HAL_DEBUG_MODE = "setprop \"camera.hal.debug\" 2";
    private static final String EXIT_HAL_DEBUG_MODE = "setprop \"camera.hal.debug\" 0";
    private static final String[] INIT_COMMANDS = {ENTER_HAL_DEBUG_MODE};
    private static final String METHOD_NAME = "getInstance";
    private static final String PACKAGE_NAME = ".available.";

    private LogcatServiceThread logcatServiceThread = new LogcatServiceThread();
    private List<LogcatFeatureService> activeServices = new ArrayList<>();

    private static final CameraLogcatService instance = new CameraLogcatService();

    public static CameraLogcatService getInstance()
    {
        return instance;
    }

    private CameraLogcatService()
    {   }

    public void startService(CameraContext cameraContext)
    {
        Log.i(getClass().getSimpleName(), "Starting CameraLogcatService");
        if (DebugFlag.isDisableMandatoryRoot()) return;
        List<String> serviceNames = getServiceNamesFromContext(cameraContext);
        if (serviceNames.size() == 0) return;

        activeServices.clear();
        List<LogcatMatch> matchList = new ArrayList<>();

        for (String name : serviceNames)
        {
            LogcatFeatureService service = getServiceFromName(name);
            if (service != null)
            {
                service.setAvailable(true);
                matchList.add(service.getMatch());
                activeServices.add(service);
            }
        }

        logcatServiceThread.startShell(INIT_COMMANDS, matchList, serviceIsRunning -> Log.i(CameraLogcatService.class.getSimpleName(), "Service is running: " + serviceIsRunning));
    }

    public void stopService()
    {
        Log.i(getClass().getSimpleName(), "Stopping CameraLogcatService");
        for (LogcatFeatureService service : activeServices)
            service.setAvailable(false);

        activeServices.clear();
        logcatServiceThread.closeShell();
    }

    public void disableHalDebugMode()
    {
        Log.i(getClass().getSimpleName(), "Disabling camera hal debug mode");
        ShellManager.getInstance().addSingleCommand(EXIT_HAL_DEBUG_MODE, null);
    }

    private List<String> getServiceNamesFromContext(CameraContext cameraContext)
    {
        return cameraContext.getCameraInfo().getLogcatServices();
    }

    @Nullable
    private LogcatFeatureService getServiceFromName(String name)
    {
        try
        {
            Class<?> aClass = Class.forName(CameraLogcatService.class.getPackage().getName() + PACKAGE_NAME + name);
            Method method = aClass.getMethod(METHOD_NAME);
            return (LogcatFeatureService)method.invoke(null);
        }
        catch (Exception e)
        {
            Log.i(getClass().getSimpleName(), "Exception on getServiceFromName: " + e.getMessage());
            return null;
        }
    }
}
