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

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.mode.ACameraMode;
import com.fkeglevich.rawdumper.camera.mode.CameraModeFactory;
import com.intel.camera.extensions.IntelCamera;

import java.io.IOException;
import java.util.ArrayList;
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
    private static Thread cameraThread2;
    private static Handler threadHandler;

    public static TurboCamera open(int cameraId)
    {
        IntelCamera intelCamera = new IntelCamera(cameraId);
        return new TurboCamera(intelCamera);
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

    public void setAutoFocus()
    {
        Camera.Parameters parameters = intelCamera.getCameraDevice().getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
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

    public void setParameter(String key, String value)
    {
        Camera.Parameters parameters = intelCamera.getCameraDevice().getParameters();
        parameters.set(key, value);
        intelCamera.getCameraDevice().setParameters(parameters);
    }

    public void takePicture(Camera.PictureCallback jpegCallback)
    {
        intelCamera.getCameraDevice().takePicture(null, null, jpegCallback);
    }

    public void touchFocus(int x, int y, int viewWidth, int viewHeight)
    {
        Rect touchRect = new Rect(x - 100, y - 100, x + 100, y + 100);
        final Rect targetFocusRect = new Rect(
                touchRect.left * 2000/viewWidth - 1000,
                touchRect.top * 2000/viewHeight - 1000,
                touchRect.right * 2000/viewWidth - 1000,
                touchRect.bottom * 2000/viewHeight - 1000);

        if (targetFocusRect.left < -1000)
        {
            int dif = targetFocusRect.left + 1000;
            targetFocusRect.left = -1000;
            targetFocusRect.right += dif;
        }
        if (targetFocusRect.right > 1000)
        {
            int dif = targetFocusRect.right - 1000;
            targetFocusRect.right = 1000;
            targetFocusRect.left -= dif;
        }
        if (targetFocusRect.top < -1000)
        {
            int dif = targetFocusRect.top + 1000;
            targetFocusRect.top = -1000;
            targetFocusRect.bottom += dif;
        }
        if (targetFocusRect.bottom > 1000)
        {
            int dif = targetFocusRect.bottom -1000;
            targetFocusRect.bottom = 1000;
            targetFocusRect.top -=dif;
        }

        Camera camera = intelCamera.getCameraDevice();

        try {
            List<Camera.Area> focusList = new ArrayList<>();
            Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
            focusList.add(focusArea);

            Camera.Parameters parameters = camera.getParameters();

            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO))
            {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                parameters.setFocusAreas(focusList);
                parameters.setMeteringAreas(focusList);
                camera.setParameters(parameters);

                camera.autoFocus(new Camera.AutoFocusCallback()
                {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera)
                    {
                        if (success)
                        {
                            Log.i("SUCESS", "sucess focus");
                            Camera.Parameters parameters = camera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                            camera.setParameters(parameters);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TurboCamera", "Unable to autofocus");
        }
    }
}
