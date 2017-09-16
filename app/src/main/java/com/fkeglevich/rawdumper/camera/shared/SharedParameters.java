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

package com.fkeglevich.rawdumper.camera.shared;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.helper.ParameterHelper;

import java.util.List;

/**
 * Created by Flávio Keglevich on 17/08/2017.
 * TODO: Add a class header comment!
 */

public class SharedParameters
{
    private final Camera camera;

    SharedParameters(Camera camera)
    {
        this.camera = camera;
    }

    public String get(String key)
    {
        return camera.getParameters().get(key);
    }

    public void set(String key, String value, Camera.Parameters parameters)
    {
        parameters.set(key, value);
    }

    public void setAndUpdate(String key, String value)
    {
        Camera.Parameters parameters = camera.getParameters();
        parameters.set(key, value);
        camera.setParameters(parameters);
    }

    public int getInt(String key)
    {
        return camera.getParameters().getInt(key);
    }

    public void set(String key, int value, Camera.Parameters parameters)
    {
        parameters.set(key, value);
    }

    public void setAndUpdate(String key, int value)
    {
        Camera.Parameters parameters = camera.getParameters();
        parameters.set(key, value);
        camera.setParameters(parameters);
    }

    public List<String> getValues(String key)
    {
        return ParameterHelper.splitValues(get(key));
    }

    public List<Integer> getIntValues(String key)
    {
        return ParameterHelper.splitIntValues(get(key));
    }

    public Camera.Parameters getRawParameters()
    {
        return camera.getParameters();
    }

    public void setRawParameters(Camera.Parameters parameters)
    {
        camera.setParameters(parameters);
    }
}
