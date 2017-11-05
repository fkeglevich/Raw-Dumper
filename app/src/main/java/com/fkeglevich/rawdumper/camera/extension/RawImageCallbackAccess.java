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

package com.fkeglevich.rawdumper.camera.extension;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.exception.RawCallbackBufferException;
import com.fkeglevich.rawdumper.camera.exception.RawCallbackBufferMethodException;

import java.lang.reflect.Method;

/**
 * Created by Flávio Keglevich on 15/08/2017.
 * TODO: Add a class header comment!
 */

public class RawImageCallbackAccess
{
    private static final String METHOD_NAME = "addRawImageCallbackBuffer";

    private static Method findMethod()
    {
        Method[] methods = Camera.class.getMethods();
        for (Method method : methods)
            if (method.getName().equals(METHOD_NAME))
                return method;

        throw new RawCallbackBufferMethodException();
    }

    private static final Method addRawBufferMethod = findMethod();

    public static void addRawImageCallbackBuffer(Camera camera, byte[] buffer)
    {
        try
        {
            addRawBufferMethod.invoke(camera, new Object[]{buffer});
        }
        catch (Exception e)
        {
            throw new RawCallbackBufferException(e);
        }
    }
}
