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

package com.fkeglevich.rawdumper.raw.info;

import android.support.annotation.Keep;

import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

/**
 * Contains device-specific information used to create DNG files.
 *
 * Created by Flávio Keglevich on 11/06/2017.
 */

@Keep
@SuppressWarnings("unused")
public class DeviceInfo
{
    private String manufacturer;
    private ExtraCameraInfo[] cameras;
    private String dumpDirectoryLocation;
    private int alignWidth;

    private transient String deviceFileName;
    private transient boolean needHalDebugCommandFlag = true;

    private DeviceInfo()
    {   }

    void runtimeInit(String deviceFileName)
    {
        this.deviceFileName = deviceFileName;
        cameras = new ExtraCameraInfoInitializer().initializeCameras(cameras);
    }

    public ExtraCameraInfo[] getCameras()
    {
        return cameras;
    }

    public String getDumpDirectoryLocation()
    {
        return dumpDirectoryLocation;
    }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_MAKE, manufacturer);
    }

    public String getDeviceFileName()
    {
        return deviceFileName;
    }

    public boolean needsLogcatServices()
    {
        if (DebugFlag.isDisableLogcatService())
            return false;

        for (ExtraCameraInfo cameraInfo : cameras)
            if (!cameraInfo.getLogcatServices().isEmpty())
                return true;

        return false;
    }

    public void disableDebugCmd()
    {
        needHalDebugCommandFlag = false;
    }

    public boolean needsHalDebugCommandFlag()
    {
        return needHalDebugCommandFlag;
    }
}
