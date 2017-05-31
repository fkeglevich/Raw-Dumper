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

package com.fkeglevich.rawdumper.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;

import com.fkeglevich.rawdumper.camera.mode.ACameraMode;
import com.fkeglevich.rawdumper.camera.mode.CameraModeFactory;
import com.intel.camera.extensions.IntelCamera;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Flávio Keglevich on 22/04/2017.
 * TODO: Add a class header comment!
 */

public class TurboCamera
{
    private static ModeInfo[] DEFAULT_MODES = new ModeInfo[] {ModeInfo.SINGLE_JPEG};

    private static final String THREAD_NAME = "TurboCameraThread";
    private static HandlerThread cameraThread;
    private static Handler threadHandler;

    public static TurboCamera open(int cameraId)
    {
        IntelCamera intelCamera = new IntelCamera(cameraId);
        return new TurboCamera(intelCamera);
    }

    public static void openAsync(final int cameraId, final IAsyncCamOpenCallback callback)
    {
        if (cameraThread == null)
        {
            cameraThread = new HandlerThread(THREAD_NAME);
            cameraThread.start();
            threadHandler = new Handler(cameraThread.getLooper());
        }
        threadHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                TurboCamera turboCamera = new TurboCamera(new IntelCamera(cameraId));
                synchronized (this)
                {
                    callback.cameraOpened(turboCamera);
                }
            }
        });
    }

    private IntelCamera intelCamera;
    private Map<ModeInfo, ACameraMode> modes;
    private List<ModeInfo> modeInfos;
    private ACameraMode currentMode;

    private TurboCamera(IntelCamera intelCamera)
    {
        this.intelCamera = intelCamera;
        this.currentMode = null;
        this.modes = new EnumMap<>(ModeInfo.class);
        this.modeInfos = Collections.unmodifiableList(Arrays.asList(DEFAULT_MODES));

        for (ModeInfo mode : DEFAULT_MODES)
            modes.put(mode, CameraModeFactory.createMode(mode));

    }

    public void close()
    {
        intelCamera.release();
    }

    public List<ModeInfo> getCameraModes()
    {
        return modeInfos;
    }

    public void setCameraMode(ModeInfo modeInfo)
    {
        if (currentMode != null && (currentMode.getModeInfo().equals(modeInfo)))
            return;

        ACameraMode nextMode = modes.get(modeInfo);
        if (nextMode == null)
            throw new RuntimeException("Invalid Camera Mode!");

        if (currentMode != null)
            currentMode.resetMode(intelCamera);

        nextMode.setupMode(intelCamera);
        currentMode = nextMode;
    }

    public ModeInfo getCameraMode()
    {
        if (currentMode == null) return null;
        return currentMode.getModeInfo();
    }

    public CaptureConfig getCaptureConfig()
    {
        if (currentMode == null) return null;
        return currentMode.getCaptureConfig();
    }

    public void setPreviewTexture(SurfaceTexture surfaceTexture) throws IOException
    {
        intelCamera.getCameraDevice().setPreviewTexture(surfaceTexture);
    }

    public void setDisplayOrientation(int degrees)
    {
        intelCamera.getCameraDevice().setDisplayOrientation(degrees);
    }

    public void startPreview()
    {
        intelCamera.getCameraDevice().startPreview();
    }

    public void stopPreview()
    {
        intelCamera.getCameraDevice().stopPreview();
    }

    public void setContinuousFocus()
    {
        Camera.Parameters parameters = intelCamera.getCameraDevice().getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        intelCamera.getCameraDevice().setParameters(parameters);
    }

    public void setFlash(boolean flashIsOn)
    {
        Camera.Parameters parameters = intelCamera.getCameraDevice().getParameters();
        parameters.setFlashMode(flashIsOn ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
        intelCamera.getCameraDevice().setParameters(parameters);
    }

    public void enableRaw()
    {
        Camera.Parameters parameters = intelCamera.getCameraDevice().getParameters();
        parameters.set("raw-data-format", "bayer");
        parameters.set("mode", "PRO");
        intelCamera.getCameraDevice().setParameters(parameters);
    }

    public void takePicture(Camera.PictureCallback jpegCallback)
    {
        intelCamera.getCameraDevice().takePicture(null, null, jpegCallback);
    }
}
