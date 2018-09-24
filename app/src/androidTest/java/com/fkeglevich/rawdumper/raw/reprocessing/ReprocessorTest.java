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

package com.fkeglevich.rawdumper.raw.reprocessing;

import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfoLoader;
import com.fkeglevich.rawdumper.raw.info.DeviceLoadException;

import org.junit.Test;

import java.io.File;

public class ReprocessorTest
{
    @Test
    public void reprocessI3av4File() throws DeviceLoadException
    {
        DeviceInfoLoader deviceInfoLoader = new DeviceInfoLoader();
        DeviceInfo deviceInfo = deviceInfoLoader.loadDeviceInfo();
        Reprocessor reprocessor = new Reprocessor();
        reprocessor.reprocessI3av4File(deviceInfo, new File("/sdcard/new.i3av4"));
    }
}