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

package com.fkeglevich.rawdumper.raw.info;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeviceInfoLoaderTest
{
    @Test
    public void testAllDeviceInfoFiles() throws DeviceLoadException
    {
        DeviceInfoLoader deviceInfoLoader = new DeviceInfoLoader();
        List<DeviceInfo> deviceInfos = deviceInfoLoader.loadAllDeviceInfos();
        assertTrue(deviceInfos.size() > 0);
    }

    @Test
    public void testDeviceInfoFailure()
    {
        DeviceInfoLoader deviceInfoLoader = new DeviceInfoLoader();
        try
        {
            deviceInfoLoader.loadDeviceInfo(null);
        }
        catch (DeviceLoadException e)
        {
            return;
        }
        assertTrue(false);
    }
}