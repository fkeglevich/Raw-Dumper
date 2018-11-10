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
import com.fkeglevich.rawdumper.camera.data.PreviewArea;
import com.fkeglevich.rawdumper.camera.service.available.WhiteBalanceService;
import com.fkeglevich.rawdumper.dng.dngsdk.DngNegative;
import com.fkeglevich.rawdumper.raw.gain.BayerGainMap;
import com.fkeglevich.rawdumper.raw.gain.ShadingIlluminant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private String gainMapFile;

    private boolean hasKnownMakernote;
    private boolean retryOnError;
    private int     retryPipelineDelay;
    private boolean canBePatched;

    private transient int facing;
    private transient int orientation;
    private transient boolean canDisableShutterSound;
    private transient Map<ShadingIlluminant, BayerGainMap> gainMapCollection;

    private ExtraCameraInfo()
    {   }

    void runtimeInit()
    {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(getId(), cameraInfo);
        facing                  = cameraInfo.facing;
        orientation             = cameraInfo.orientation;
        canDisableShutterSound  = cameraInfo.canDisableShutterSound;
        gainMapCollection       = GainMapAssetLoader.load(gainMapFile);
    }

    public void writeInfoTo(DngNegative negative)
    {
        negative.setModel(String.format(Locale.US, uniqueCameraModel, Build.MODEL));
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

    public PreviewArea.FlipType getFlipType()
    {
        return getFacing() == Camera.CameraInfo.CAMERA_FACING_FRONT ? PreviewArea.FlipType.HORIZONTAL : PreviewArea.FlipType.NONE;
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

    public String getModel()
    {
        return String.format(Locale.US, model, Build.MODEL);
    }

    public List<String> getLogcatServices()
    {
        return logcatServices != null ? Arrays.asList(logcatServices) : Collections.emptyList();
    }

    public Map<ShadingIlluminant, BayerGainMap> getGainMapCollection()
    {
        return gainMapCollection;
    }

    void fixId(int newId)
    {
        this.id = newId;
    }

    public void removeUnessentialLogcatServices()
    {
        List<String> serviceList = new ArrayList<>();
        for (String serviceName : logcatServices)
        {
            if (serviceName.equals(WhiteBalanceService.class.getSimpleName()))
                serviceList.add(serviceName);
        }
        logcatServices = serviceList.toArray(new String[0]);
    }
}
