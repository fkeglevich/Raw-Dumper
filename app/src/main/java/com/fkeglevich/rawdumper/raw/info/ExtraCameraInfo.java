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

import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.Keep;

import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Simple immutable class that stores specific information about
 * a single camera of the device.
 *
 * Created by Flávio Keglevich on 11/06/2017.
 */

@Keep
@SuppressWarnings("unused")
public class ExtraCameraInfo
{
    private int id;
    private String model;
    private String uniqueCameraModel;

    private SensorInfo sensor;
    private LensInfo lens;
    private ColorInfo color;
    private ExposureInfo exposure;
    private OpcodeListInfo[] opcodes;
    private NoiseInfo noise;
    private CaptureSize[] binningSizes;
    private String[] logcatServices;

    private boolean hasKnownMakernote;
    private boolean retryOnError;
    private int     retryPipelineDelay;
    private boolean canBePatched;

    private transient int facing;
    private transient int orientation;
    private transient boolean canDisableShutterSound;

    private ExtraCameraInfo()
    {   }

    void runtimeInit()
    {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(getId(), cameraInfo);
        facing                  = cameraInfo.facing;
        orientation             = cameraInfo.orientation;
        canDisableShutterSound  = cameraInfo.canDisableShutterSound;
    }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_MODEL, String.format(Locale.US, model, Build.MODEL));
        tiffWriter.setField(TiffTag.TIFFTAG_UNIQUECAMERAMODEL, String.format(Locale.US, uniqueCameraModel, Build.MODEL));
    }

    public int getId()
    {
        return id;
    }

    public SensorInfo getSensor()
    {
        return sensor;
    }

    public LensInfo getLens()
    {
        return lens;
    }

    public ColorInfo getColor()
    {
        return color;
    }

    public ExposureInfo getExposure()
    {
        return exposure;
    }

    public OpcodeListInfo[] getOpcodes()
    {
        return opcodes;
    }

    public NoiseInfo getNoise()
    {
        return noise;
    }

    public boolean hasKnownMakernote()
    {
        return hasKnownMakernote;
    }

    public synchronized int getFacing()
    {
        return facing;
    }

    public synchronized int getOrientation()
    {
        return orientation;
    }

    public synchronized boolean canDisableShutterSound()
    {
        return canDisableShutterSound;
    }

    public boolean isRetryOnError()
    {
        return retryOnError;
    }

    public int getRetryPipelineDelay()
    {
        return retryPipelineDelay;
    }

    public void setRetryOnError(boolean retryOnError)
    {
        this.retryOnError = retryOnError;
    }

    public CaptureSize[] getBinningSizes()
    {
        return binningSizes;
    }

    public boolean isCanBePatched()
    {
        return canBePatched;
    }

    public List<String> getLogcatServices()
    {
        return logcatServices != null ? Arrays.asList(logcatServices) : Collections.emptyList();
    }

    void fixId(int newId)
    {
        this.id = newId;
    }
}
