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

import android.hardware.Camera;
import android.util.Log;

import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExposureInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.util.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

class Zen2LLWorkaround implements DeviceWorkaround
{
    private enum LibPatch
    {
        NONE(""),
        V1("601cffed1dd2c6129587fc7a21f0b4c4"),
        V2("de8e7213d408170cb606c026e06200d4");

        public final String md5;

        LibPatch (String md5)
        {
            this.md5 = md5;
        }
    }

    private static final String TAG = "Zen2LLWorkaround";
    private static final File M10MO_SO_FILE = new File("/system/lib/hw/m10mo/camera.m10mo.so");
    private static final String CAMERA_LIB_PATH  = "/system/lib/hw/camera.vendor.mofd_v1.so";

    @Override
    public void applyWorkaroundIfNeeded(DeviceInfo deviceInfo)
    {
        if (M10MO_SO_FILE.exists())
            apply(deviceInfo);
    }

    private void apply(DeviceInfo deviceInfo)
    {
        LibPatch libPatch = getCameraLibPatch();
        Log.i(Zen2LLWorkaround.class.getSimpleName(), libPatch.name());
        if (libPatch.equals(LibPatch.V2))
            deviceInfo.disableDebugCmd();

        boolean isLibNotPatched = LibPatch.NONE.equals(libPatch);

        ExposureInfo exposureInfo;
        ExtraCameraInfo[] extraCameraInfos = deviceInfo.getCameras();
        for (ExtraCameraInfo cameraInfo : extraCameraInfos)
        {
            cameraInfo.setRetryOnError(false);
            exposureInfo = cameraInfo.getExposure();
            removeLongExposureValues(exposureInfo.getShutterSpeedValues());
            disableRawIfNeeded(cameraInfo, isLibNotPatched);
            if (libPatch.equals(LibPatch.V2))
                cameraInfo.removeUnessentialLogcatServices();
        }
    }

    private void disableRawIfNeeded(ExtraCameraInfo cameraInfo, boolean isLibNotPatched)
    {
        boolean isRearCamera = cameraInfo.getFacing() == Camera.CameraInfo.CAMERA_FACING_BACK;
        if (isRearCamera && isLibNotPatched)
            cameraInfo.getSensor().disableRaw();
    }

    private void removeLongExposureValues(List<String> exposureList)
    {
        String value;
        int i = 0;
        while(i < exposureList.size())
        {
            value = exposureList.get(i);
            if (value.endsWith("s"))
                exposureList.remove(i);
            else
                i++;
        }
    }

    private LibPatch getCameraLibPatch()
    {
        FileInputStream fi = null;
        try
        {
            fi = new FileInputStream(CAMERA_LIB_PATH);
            String base16 = MD5.calculateAsBase16(fi);

            for (LibPatch libPatch : LibPatch.values())
                if (libPatch.md5.equals(base16))
                    return libPatch;

        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "FileNotFoundException: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        finally
        {
            if (fi != null)
            {
                try
                {
                    fi.close();
                }
                catch (IOException e)
                {
                    Log.e(TAG, "IOException during close: " + e.getMessage());
                }
            }
        }
        return LibPatch.NONE;
    }
}
