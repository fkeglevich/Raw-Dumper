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

import android.hardware.Camera;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 03/10/17.
 */

class LowLevelParameterInterfaceImpl implements LowLevelParameterInterface
{
    private final Camera innerCamera;
    private final Object lock;

    LowLevelParameterInterfaceImpl(Camera innerCamera, Object lock)
    {
        this.innerCamera = innerCamera;
        this.lock = lock;
    }

    @Override
    public void remove(final String key)
    {
        update(new UpdateFunction()
        {
            @Override
            public void update(Camera.Parameters parameters)
            {
                parameters.remove(key);
            }
        });
    }

    @Override
    public String get(String key)
    {
        synchronized (lock)
        {
            return innerCamera.getParameters().get(key);
        }
    }

    @Override
    public void set(final String key, final String value)
    {
        update(new UpdateFunction()
        {
            @Override
            public void update(Camera.Parameters parameters)
            {
                parameters.set(key, value);
            }
        });
    }

    @Override
    public boolean has(String key)
    {
        return get(key) != null;
    }

    private void update(UpdateFunction function)
    {
        synchronized (lock)
        {
            Camera.Parameters parameters = innerCamera.getParameters();
            function.update(parameters);
            innerCamera.setParameters(parameters);
        }
    }
}
