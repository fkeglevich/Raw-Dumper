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

package com.fkeglevich.rawdumper.camera.shared;

import android.content.Context;

import com.fkeglevich.rawdumper.camera.exception.DeviceLoadException;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfoLoader;

import java.io.IOException;

/**
 * Created by Flávio Keglevich on 09/08/2017.
 * TODO: Add a class header comment!
 */

public class SharedDataLoader
{
    private final Context applicationContext;
    private final DeviceInfoLoader deviceInfoLoader;

    public SharedDataLoader(Context applicationContext)
    {
        this.applicationContext = applicationContext;
        this.deviceInfoLoader   = new DeviceInfoLoader();
    }

    public SharedData load() throws DeviceLoadException
    {
        try
        {
            String deviceInfoJson = deviceInfoLoader.loadDeviceInfoJson(applicationContext);
            DeviceInfo deviceInfo = deviceInfoLoader.loadDeviceInfo(deviceInfoJson);
            return new SharedData(applicationContext, deviceInfo);
        }
        catch (IOException ioe)
        {
            throw new DeviceLoadException();
        }
    }
}
