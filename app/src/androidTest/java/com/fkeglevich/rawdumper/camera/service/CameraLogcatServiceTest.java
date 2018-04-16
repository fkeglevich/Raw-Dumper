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

package com.fkeglevich.rawdumper.camera.service;

import android.util.Log;

import com.fkeglevich.rawdumper.camera.service.available.WhiteBalanceService;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class CameraLogcatServiceTest
{
    @Test
    public void testPackageName()
    {
        assertEquals( CameraLogcatService.class.getPackage().getName() + ".available", "com.fkeglevich.rawdumper.camera.service.available");
        try
        {
            Class<?> aClass = Class.forName(CameraLogcatService.class.getPackage().getName() + ".available" + "." + "WhiteBalanceService");
            Method method = aClass.getMethod("getInstance");
            LogcatFeatureService service = (LogcatFeatureService)method.invoke(null);
            service.setAvailable(true);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
}