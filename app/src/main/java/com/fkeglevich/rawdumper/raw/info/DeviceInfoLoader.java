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
import android.util.Log;

import com.fkeglevich.rawdumper.raw.data.BayerPattern;
import com.fkeglevich.rawdumper.raw.data.CalibrationIlluminant;
import com.fkeglevich.rawdumper.raw.data.ExifFlash;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.util.AssetUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for loading and creating DeviceInfo objects.
 *
 * Created by Flávio Keglevich on 15/06/2017.
 */

public class DeviceInfoLoader
{
    private static final String SUPPORTED_DEVICES_FILENAME = "supported_devices.json";
    private static final String DEVICE_FILE_EXTENSION = ".json";

    private Moshi moshi;

    public DeviceInfoLoader()
    {
        CalibrationIlluminant.values();
        BayerPattern.values();
        ExifFlash.values();
        ImageOrientation.values();

        moshi = new Moshi.Builder().build();
    }

    public DeviceInfo loadDeviceInfo() throws DeviceLoadException
    {
        return loadDeviceInfo(Build.MODEL);
    }

    public DeviceInfo loadDeviceInfo(String deviceModel) throws DeviceLoadException
    {
        try
        {
            return loadDeviceInfoFromFileName(getDeviceInfoFileName(deviceModel));
        }
        catch (IOException ioe)
        {
            return throwDeviceLoadException(ioe);
        }
    }

    public List<DeviceInfo> loadAllDeviceInfos() throws DeviceLoadException
    {
        try
        {
            List<DeviceInfo> result = new ArrayList<>();
            SupportedDeviceList deviceList = loadDeviceList();
            List<String> deviceInfoFiles = deviceList.listDeviceInfoFiles();
            for (String fileName : deviceInfoFiles)
                result.add(loadDeviceInfoFromFileName(fileName));

            return result;
        }
        catch (IOException ioe)
        {
            return throwDeviceLoadException(ioe);
        }
    }

    private <T> T throwDeviceLoadException(Exception exception) throws DeviceLoadException
    {
        Log.e("DeviceInfoLoader", exception.getClass().getSimpleName() + ": " + exception.getMessage());
        throw new DeviceLoadException();
    }

    private DeviceInfo loadDeviceInfoFromFileName(String deviceInfoFileName) throws IOException
    {
        try
        {
            String deviceInfoJson = AssetUtil.getAssetAsString(deviceInfoFileName + DEVICE_FILE_EXTENSION);
            DeviceInfo deviceInfo = moshi.adapter(DeviceInfo.class).fromJson(deviceInfoJson);
            if (deviceInfo != null) deviceInfo.runtimeInit(deviceInfoFileName);
            return deviceInfo;
        }
        catch (IllegalArgumentException iae)
        {
            Log.e("DeviceInfoLoader", "IllegalArgumentException: " + iae.getMessage());
            throw new RuntimeException();
        }
    }

    private String getDeviceInfoFileName(String deviceModel) throws IOException
    {
        SupportedDeviceList deviceList = loadDeviceList();
        return deviceList.findDeviceInfoFile(deviceModel);
    }

    private SupportedDeviceList loadDeviceList() throws IOException
    {
        String deviceListJson = AssetUtil.getAssetAsString(SUPPORTED_DEVICES_FILENAME);
        JsonAdapter<SupportedDeviceList> adapter = moshi.adapter(SupportedDeviceList.class);
        return adapter.fromJson(deviceListJson);
    }
}
