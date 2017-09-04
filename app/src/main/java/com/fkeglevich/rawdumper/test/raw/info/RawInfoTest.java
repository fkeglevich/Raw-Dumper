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

package com.fkeglevich.rawdumper.test.raw.info;

import android.util.Log;

import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfoLoader;
import com.fkeglevich.rawdumper.test.Test;
import com.fkeglevich.rawdumper.util.AssetUtil;

/**
 * Created by Flávio Keglevich on 04/09/2017.
 * TODO: Add a class header comment!
 */

public class RawInfoTest extends Test
{
    @Override
    protected void executeTest() throws Exception
    {
        DeviceInfoLoader deviceInfoLoader = new DeviceInfoLoader();
        String deviceInfoJson = AssetUtil.getAssetAsString("z00x.json");
        DeviceInfo deviceInfo = deviceInfoLoader.loadDeviceInfo(deviceInfoJson);
        Log.i(getTag(), "Lens aperture: " + deviceInfo.getCameras()[0].getLens().getAperture());
    }
}
