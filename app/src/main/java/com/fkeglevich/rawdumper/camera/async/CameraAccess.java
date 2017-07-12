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

package com.fkeglevich.rawdumper.camera.async;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.camera.async.callbacks.IAutoFocusCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IOpenCameraCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IPictureCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IRawCaptureCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IReopenCameraCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IStartPreviewCallback;
import com.fkeglevich.rawdumper.i3av4.I3av4ToDngConverter;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfoLoader;
import com.fkeglevich.rawdumper.su.ShellManager;
import com.intel.camera.extensions.IntelCamera;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Flávio Keglevich on 25/06/2017.
 * TODO: Add a class header comment!
 */

public class CameraAccess
{
    //Main thread fields
    private final Handler threadHandler;
    private DeviceInfo mainDeviceInfo;
    private final CameraConfig cameraConfig;
    private final CameraFocus cameraFocus;
    private final CameraFlash cameraFlash;

    //Camera thread fields
    private final CameraUICallbacks uiCallbacks;
    private DeviceInfo threadDeviceInfo;

    //Shared fields
    final CameraLock cameraLock;

    CameraAccess(Looper looper)
    {
        threadHandler = new Handler(looper);
        cameraConfig = new CameraConfig(this);
        cameraFocus = new CameraFocus(this);
        cameraFlash = new CameraFlash(this);
        uiCallbacks = new CameraUICallbacks();
        cameraLock = new CameraLock();
    }

    public DeviceInfo getDeviceInfo()
    {
        return mainDeviceInfo;
    }

    public CameraConfig getCameraConfig()
    {
        return cameraConfig;
    }

    public CameraFocus getCameraFocus()
    {
        return cameraFocus;
    }

    public CameraFlash getCameraFlash()
    {
        return cameraFlash;
    }

    void openCameraAsync(final int cameraId, final Context applicationContext, final IOpenCameraCallback openCameraCallback)
    {
        final CameraAccess camAccess = this;
        threadHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                DeviceInfo[] deviceInfoArray;
                IntelCamera intelCamera;
                boolean couldOpen;
                boolean errorWhileGettingCamInfo;
                CameraOpenError openError;

                synchronized (cameraLock)
                {
                    deviceInfoArray = new DeviceInfoLoader().loadCopiesOfDeviceInfo(applicationContext, 2, R.raw.z00ad);
                    if (deviceInfoArray != null)
                    {
                        mainDeviceInfo = deviceInfoArray[1];
                        threadDeviceInfo = deviceInfoArray[0];
                        converter = new I3av4ToDngConverter(threadDeviceInfo);

                        errorWhileGettingCamInfo = false;
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        try
                        {
                            Camera.getCameraInfo(cameraId, cameraInfo);
                            cameraLock.setCameraInfo(cameraInfo);
                        } catch (RuntimeException re)
                        {
                            errorWhileGettingCamInfo = true;
                        }

                        if (!errorWhileGettingCamInfo)
                        {
                            try
                            {
                                intelCamera = new IntelCamera(cameraId);
                            } catch (RuntimeException re)
                            {
                                intelCamera = null;
                            }
                        }
                        else
                        {
                            intelCamera = null;
                        }

                        couldOpen = intelCamera != null && intelCamera.getCameraDevice() != null;
                        openError = couldOpen ? CameraOpenError.NONE : CameraOpenError.UNKNOWN_ERROR;
                        cameraLock.setCamera(intelCamera);
                    }
                    else
                    {
                        couldOpen = false;
                        openError = CameraOpenError.INCOMPATIBLE_DEVICE;
                    }

                    uiCallbacks.postCameraOpenedCallback(couldOpen, openCameraCallback, openError, camAccess);
                }
            }
        });
    }

    public void close()
    {
        boolean success = false;
        synchronized (cameraLock)
        {
            if (cameraLock.getCamera() != null)
            {
                try
                {
                    cameraLock.getCamera().getCameraDevice().stopPreview();
                }
                finally
                {
                    uiCallbacks.removeAllCallbacks();
                    cameraLock.getCamera().release();
                    cameraLock.setCamera(null);
                    success = true;
                }
            }
        }
        if (!success)
            throw new RuntimeException("close() exception: camera wasn't opened!");
    }

    public void startPreview()
    {
        synchronized (cameraLock)
        {
            if (cameraLock.getCamera() != null)
                cameraLock.getCamera().getCameraDevice().startPreview();
            else
                throw new RuntimeException("startPreview() exception: camera wasn't opened!");
        }
    }

    public void startPreviewAsync(final IStartPreviewCallback startPreviewCallback)
    {
        threadHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (cameraLock)
                {
                    if (cameraLock.getCamera() != null)
                        cameraLock.getCamera().getCameraDevice().startPreview();
                    else
                        throw new RuntimeException("startPreviewAsync() exception: camera wasn't opened!");

                    uiCallbacks.postStartPreviewCallback(startPreviewCallback);
                }
            }
        });
    }

    void autoFocusAsync(final IAutoFocusCallback autoFocusCallback)
    {
        threadHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (cameraLock)
                {
                    if (cameraLock.getCamera() != null)
                        cameraLock.getCamera().getCameraDevice().autoFocus(new Camera.AutoFocusCallback()
                        {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera)
                            {
                                uiCallbacks.postAutoFocusCallback(success, autoFocusCallback);
                            }
                        });
                    else
                        throw new RuntimeException("autoFocusAsync() exception: camera wasn't opened!");
                }
            }
        });
    }

    void takePictureAsync(final IPictureCallback pictureCallback)
    {
        threadHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (cameraLock)
                {
                    if (cameraLock.getCamera() != null)
                        cameraLock.getCamera().getCameraDevice().takePicture(null, null, new Camera.PictureCallback()
                        {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera)
                            {
                                uiCallbacks.postTakePictureCallbacks(data, pictureCallback);
                            }
                        });
                    else
                        throw new RuntimeException("takePictureAsync() exception: camera wasn't opened!");
                }
            }
        });
    }

    public void takeRawPictureAsync(final String partialDirPath,
                             final String saveDirPath, final Context applicationContext,
                             final IRawCaptureCallback rawCaptureCallback)
    {
        threadHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (cameraLock)
                {
                    if (cameraLock.getCamera() != null)
                        cameraLock.getCamera().getCameraDevice().takePicture(null, null, new Camera.PictureCallback()
                        {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera)
                            {
                                camera.startPreview();
                                saveDngFiles(partialDirPath, saveDirPath, applicationContext, rawCaptureCallback);
                            }
                        });
                    else
                        throw new RuntimeException("takeRawPictureAsync() exception: camera wasn't opened!");
                }
            }
        });
    }

    public void resaveDngFiles(final String partialDirPath,
                               final String saveDirPath, final Context applicationContext,
                               final IRawCaptureCallback rawCaptureCallback)
    {
        synchronized (cameraLock)
        {
            saveDngFiles(partialDirPath, saveDirPath, applicationContext, rawCaptureCallback);
        }
    }

    private void saveDngFiles(final String partialDirPath,
                              final String saveDirPath, final Context applicationContext,
                              final IRawCaptureCallback rawCaptureCallback)
    {
        ShellManager.getInstance().addSingleCommand("mv " + threadDeviceInfo.getDumpDirectoryLocation() + "/* " + partialDirPath, new Shell.OnCommandLineListener() {
            @Override
            public void onCommandResult(int commandCode, int exitCode) {
                boolean success;

                if (exitCode < 0)
                {
                    success = false;
                    //enableCaptureButton();
                    //showTextToast("Error taking picture!");
                }
                else
                {
                    success = !convertToDng(partialDirPath, saveDirPath, applicationContext);
                                            /*
                                            showTextToast("Picture taken, converting to DNG...");
                                            if (!convertToDng(partialDirPath, saveDirPath, applicationContext))
                                                showTextToast("DNG file created successfully!");
                                            else
                                                showTextToast("Error creating DNG file!");
                                            enableCaptureButton();*/
                }
                uiCallbacks.postTakeRawPictureCallback(success, rawCaptureCallback);
            }

            @Override
            public void onLine(String line) {   }
        });
    }

    private volatile boolean isShellRunning = false;

    boolean isShellRunning()
    {
        return isShellRunning;
    }

    void openShell(final Shell.OnCommandResultListener callback)
    {
        threadHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                ShellManager.getInstance().open(new Shell.OnCommandResultListener()
                {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode, List<String> output)
                    {
                        if (exitCode == Shell.OnCommandResultListener.SHELL_RUNNING)
                            isShellRunning = true;

                        uiCallbacks.postOpenShellCallback(commandCode, callback, exitCode, output);
                    }
                });
            }
        });
    }

    public boolean hasFlash()
    {
        synchronized (cameraLock)
        {
            Camera.Parameters parameters = cameraLock.getCamera().getCameraDevice().getParameters();
            List<String> flashModes = parameters.getSupportedFlashModes();
            return flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_ON);
        }
    }

    public void setReopenCallback(final IReopenCameraCallback reopenCameraCallback)
    {
        synchronized (cameraLock)
        {
            cameraLock.getCamera().getCameraDevice().setErrorCallback(new Camera.ErrorCallback()
            {
                @Override
                public void onError(int error, Camera camera)
                {
                    try
                    {
                        Thread.sleep(2000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    close();
                    uiCallbacks.postReopenCameraCallback(reopenCameraCallback);
                }
            });
        }
    }

    public void setParameter(String key, String value)
    {
        synchronized (cameraLock)
        {
            if (cameraLock.getCamera() != null)
            {
                Camera.Parameters parameters = cameraLock.getCamera().getCameraDevice().getParameters();
                parameters.set(key, value);
                cameraLock.getCamera().getCameraDevice().setParameters(parameters);
            }
        }
    }

    public String getParameter(String key)
    {
        synchronized (cameraLock)
        {
            Camera.Parameters parameters = cameraLock.getCamera().getCameraDevice().getParameters();
            return parameters.get(key);
        }
    }

    private I3av4ToDngConverter converter;

    private boolean convertToDng(String partialDirPath, String saveDirPath, Context applicationContext)
    {
        File partialDir = new File(partialDirPath);
        File saveDir = new File(saveDirPath);

        String[] files = partialDir.list();
        File input, output;
        boolean error = false;
        for (String filePath : files)
        {
            if (filePath.endsWith(".i3av4"))
            {
                input = new File(partialDir, filePath);
                output = new File(saveDir, input.getName() + ".dng");
                try
                {
                    converter.convert(input.getAbsolutePath(), output.getAbsolutePath(), applicationContext);
                    input.delete();
                }
                catch (IOException e)
                {
                    error = true;
                    e.printStackTrace();
                }
            }
        }
        return error;
    }
}
