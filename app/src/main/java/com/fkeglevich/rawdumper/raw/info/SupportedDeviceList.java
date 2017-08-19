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

package com.fkeglevich.rawdumper.raw.info;

import android.os.Build;
import android.support.annotation.Keep;

import java.io.IOException;

/**
 * Represents the list of known devices.
 *
 * Created by Flávio Keglevich on 15/06/2017.
 */

@Keep
@SuppressWarnings("unused")
class SupportedDeviceList
{
    private static final String DEVICE_FILE_EXTENSION = ".json";

    private SupportedDevice[] supportedDevices;

    private SupportedDeviceList()
    {   }

    String findDeviceInfoFile() throws IOException
    {
        for (SupportedDevice sd : supportedDevices)
            if (sd.deviceModel.equals(Build.MODEL))
                return sd.deviceInfoFile + DEVICE_FILE_EXTENSION;

        throw new IOException("Couldn't find the device info file! Build.MODEL: " + Build.MODEL);
    }
}
