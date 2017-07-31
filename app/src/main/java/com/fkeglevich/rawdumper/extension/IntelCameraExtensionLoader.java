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

package com.fkeglevich.rawdumper.extension;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Flávio Keglevich on 30/07/2017.
 * TODO: Add a class header comment!
 */

public class IntelCameraExtensionLoader
{
    private static final String INTEL_CAMERA_PATH = "/system/framework/com.intel.camera.extensions.jar";
    private static final String INTEL_CAMERA_CLASS_NAME = "com.intel.camera.extensions.IntelCamera";
    private static final String DEX_CACHE_DIR_NAME = "dex";

    private static Class<Object> intelCameraClassCache = null;

    @SuppressWarnings("unchecked")
    public static ICameraExtension extendedOpenCamera(Context applicationContext, int cameraId)
    {
        if (intelCameraClassCache != null)
            return IntelCameraProxy.createNew(intelCameraClassCache, cameraId);

        File intelCameraJar = new File(INTEL_CAMERA_PATH);
        if (!intelCameraJar.exists())
            return DummyCameraProxy.createNew(cameraId);

        try
        {
            File dexOutputDir = applicationContext.getDir(DEX_CACHE_DIR_NAME, MODE_PRIVATE);
            DexClassLoader classloader = new DexClassLoader(intelCameraJar.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, IntelCameraExtensionLoader.class.getClassLoader());
            intelCameraClassCache = (Class<Object>) classloader.loadClass(INTEL_CAMERA_CLASS_NAME);
            return IntelCameraProxy.createNew(intelCameraClassCache, cameraId);
        }
        catch (Exception e)
        {
            return DummyCameraProxy.createNew(cameraId);
        }
    }
}
