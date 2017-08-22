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

import com.fkeglevich.rawdumper.controller.context.ContextManager;

import java.io.File;

import dalvik.system.DexClassLoader;

import static android.content.Context.MODE_PRIVATE;

/**
 * This class tries to load the Intel Camera Extension Library, if available in the system,
 * and returns a dummy, but functional camera extension proxy when the actual library is not present.
 *
 * The class tries to locate the .jar library in the system and loads it from there. The biggest
 * advantage in this method is that the app can still use the library despite minor modifications
 * in its actual implementation. For example: the camera library can use 32 bits pointers in ABC
 * devices and 64 bits pointers in XYZ devices; using IntelCameraExtensionLoader permits handling
 * both cases gracefully.
 *
 * Created by Flávio Keglevich on 30/07/2017.
 */

public class IntelCameraExtensionLoader
{
    private static final String INTEL_CAMERA_PATH = "/system/framework/com.intel.camera.extensions.jar";
    private static final String INTEL_CAMERA_CLASS_NAME = "com.intel.camera.extensions.IntelCamera";
    private static final String DEX_CACHE_DIR_NAME = "dex";

    private static Class<Object> intelCameraClassCache = null;

    @SuppressWarnings("unchecked")
    public static ICameraExtension extendedOpenCamera(int cameraId)
    {
        if (intelCameraClassCache != null)
            return IntelCameraProxy.createNew(intelCameraClassCache, cameraId);

        File intelCameraJar = new File(INTEL_CAMERA_PATH);
        if (!intelCameraJar.exists())
            return DummyCameraProxy.createNew(cameraId);

        try
        {
            File dexOutputDir = getDexCacheDir();
            DexClassLoader classloader = new DexClassLoader(intelCameraJar.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, IntelCameraExtensionLoader.class.getClassLoader());
            intelCameraClassCache = (Class<Object>) classloader.loadClass(INTEL_CAMERA_CLASS_NAME);
            return IntelCameraProxy.createNew(intelCameraClassCache, cameraId);
        }
        catch (Exception e)
        {
            return DummyCameraProxy.createNew(cameraId);
        }
    }

    private static File getDexCacheDir()
    {
        synchronized (ContextManager.getApplicationContext().getLock())
        {
            return ContextManager.getApplicationContext().get().getDir(DEX_CACHE_DIR_NAME, MODE_PRIVATE);
        }
    }
}
