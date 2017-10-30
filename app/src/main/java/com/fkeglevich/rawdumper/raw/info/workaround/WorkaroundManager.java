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

package com.fkeglevich.rawdumper.raw.info.workaround;

import com.fkeglevich.rawdumper.raw.info.DeviceInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public class WorkaroundManager
{
    private final Map<String, DeviceWorkaround> workaroundMap = new HashMap<>();

    public WorkaroundManager()
    {
        workaroundMap.put("z00x", new Zen2LLWorkaround());
    }

    public void applyWorkaroundIfNeeded(DeviceInfo deviceInfo)
    {
        String fileName = deviceInfo.getDeviceFileName();
        if (workaroundMap.containsKey(fileName))
            workaroundMap.get(fileName).applyWorkaroundIfNeeded(deviceInfo);
    }
}
