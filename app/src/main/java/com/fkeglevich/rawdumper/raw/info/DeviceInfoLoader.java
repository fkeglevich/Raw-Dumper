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

import android.content.Context;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.util.ByteArrayUtil;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Flávio Keglevich on 15/06/2017.
 * TODO: Add a class header comment!
 */

public class DeviceInfoLoader
{
    private Moshi moshi;

    public DeviceInfoLoader()
    {
        moshi = new Moshi.Builder().build();
    }

    public DeviceInfo loadDeviceInfo(Context context)
    {
        byte[] supportedDevicesBytes;
        byte[] deviceInfoBytes;
        SupportedDeviceList deviceList;
        String deviceInfoFile;
        DeviceInfo result;

        try
        {
            deviceInfoBytes = ByteArrayUtil.getRawResource(context, "z00ad");
            result = moshi.adapter(DeviceInfo.class).fromJson(new String(deviceInfoBytes, Charset.defaultCharset()));
            return result;
        }
        catch (IOException e)
        {
            return null;
        }

        /*try
        {
            supportedDevicesBytes = ByteArrayUtil.getRawResource(context, R.raw.supported_devices);
            deviceList = moshi.adapter(SupportedDeviceList.class).fromJson(new String(supportedDevicesBytes, Charset.defaultCharset()));
            deviceInfoFile = deviceList.findDeviceInfoFile();
            if (deviceInfoFile == null)
                return null;
            deviceInfoBytes = ByteArrayUtil.getRawResource(context, deviceInfoFile);
            result = moshi.adapter(DeviceInfo.class).fromJson(new String(deviceInfoBytes, Charset.defaultCharset()));
            return result;
        }
        catch (IOException ioe)
        {   return null;    }*/
    }
}
