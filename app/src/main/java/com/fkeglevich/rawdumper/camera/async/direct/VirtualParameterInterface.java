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

package com.fkeglevich.rawdumper.camera.async.direct;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class VirtualParameterInterface implements LowLevelParameterInterface
{
    private final Object lock;

    public VirtualParameterInterface(Object lock)
    {
        this.lock = lock;
    }

    private final Map<String, String> innerMap = new HashMap<>();

    @Override
    public String get(String key)
    {
        synchronized (lock)
        {
            return innerMap.get(key);
        }
    }

    @Override
    public void set(String key, String value)
    {
        synchronized (lock)
        {
            innerMap.put(key, value);
        }
    }

    @Override
    public boolean has(String key)
    {
        synchronized (lock)
        {
            return innerMap.containsKey(key);
        }
    }

    @Override
    public void remove(String key)
    {
        synchronized (lock)
        {
            innerMap.remove(key);
        }
    }
}