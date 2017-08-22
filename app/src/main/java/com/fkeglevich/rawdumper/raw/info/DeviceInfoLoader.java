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

import com.fkeglevich.rawdumper.util.AssetUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

/**
 * This class is used for loading and creating DeviceInfo objects.
 *
 * Created by Flávio Keglevich on 15/06/2017.
 */

public class DeviceInfoLoader
{
    private static final String SUPPORTED_DEVICES_FILENAME = "supported_devices.json";

    private Moshi moshi;

    public DeviceInfoLoader()
    {
        moshi = new Moshi.Builder().build();
    }

    public String loadDeviceInfoJson() throws IOException
    {
        SupportedDeviceList deviceList = loadDeviceList();
        return AssetUtil.getAssetAsString(deviceList.findDeviceInfoFile());
    }

    public DeviceInfo loadDeviceInfo(String deviceInfoJson) throws IOException
    {
        return moshi.adapter(DeviceInfo.class).fromJson(deviceInfoJson);
    }

    private SupportedDeviceList loadDeviceList() throws IOException
    {
        String deviceListJson = AssetUtil.getAssetAsString(SUPPORTED_DEVICES_FILENAME);
        JsonAdapter<SupportedDeviceList> adapter = moshi.adapter(SupportedDeviceList.class);
        return adapter.fromJson(deviceListJson);
    }
}
