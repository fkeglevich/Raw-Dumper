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

package com.fkeglevich.rawdumper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fkeglevich.rawdumper.ui.ISOInterface;
import com.fkeglevich.rawdumper.ui.ModesInterface;
import com.fkeglevich.rawdumper.ui.ShutterSpeedInterface;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private static final String SAVE_DIR_NAME = "RawDumper";
    private static final String PARTIAL_DIR_NAME = ".partial";
    private static final String BKP_DIR_NAME = ".bkp";

    private static final boolean DEBUG_MODE = false;
    private static final boolean DEBUG_LOGS = true;
    private static void log(String message)
    {
        if (DEBUG_LOGS)
            Log.v("RawDumper", message);
    }

    private ModesInterface modesInterface;
    private TextureView textureView;
    private ImageButton captureBt;
    private ImageButton flashBt;
    private ImageButton camSwitchButton;
    private ProgressBar progressBar;
    private Toast currentToast;
    private ISOInterface isoInterface;
    private ShutterSpeedInterface shutterSpeedInterface;
    private Button isoBt;
    private ImageButton shutterSpeedBt;
    //private CameraAccess cameraAccess = null;

    private boolean flashIsOn = false;

    private File saveDir;
    private File partialDir;
    private File bkpDir;

    private int currentCamera = 0;
    private int numCameras;

    private String lastISO = null;
    private String lastSS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        log("Starting app");
        setContentView(R.layout.activity_main);

        numCameras = Camera.getNumberOfCameras();

        currentToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        modesInterface = new ModesInterface(this);
        isoInterface = new ISOInterface(this);
        shutterSpeedInterface = new ShutterSpeedInterface(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        captureBt = (ImageButton) findViewById(R.id.captureButton);
        captureBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //if (cameraAccess != null)
                //{
                    //ExposureInfo exposureInfo = cameraAccess.getDeviceInfo().getCameras()[currentCamera].getExposure();

                    //lastISO = cameraAccess.getParameter(exposureInfo.getIsoParameter());
                    //lastSS = cameraAccess.getParameter(exposureInfo.getShutterSpeedParameter());
                    /*cameraAccess.takeRawPictureAsync(partialDir.getAbsolutePath(),
                            saveDir.getAbsolutePath(), getApplicationContext(), new IRawCaptureCallback()
                            {
                                @Override
                                public void onPictureTaken(boolean success)
                                {
                                    enableCaptureButton();

                                    if (!success)
                                        showTextToast("Error taking picture!");
                                    else
                                        showTextToast("DNG file created successfully!");
                                }
                            });*/

                    disableCaptureButton();
                //}
            }
        });

        flashBt = (ImageButton) findViewById(R.id.flashButton);
        flashBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //if (cameraAccess != null)
                //{
                    if (!flashIsOn)
                        flashBt.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_flash_on_black_24dp));
                    else
                        flashBt.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_flash_off_black_24dp));

                    flashIsOn = !flashIsOn;
                //    cameraAccess.getCameraFlash().setFlashValue(flashIsOn);
                //}
            }
        });

        camSwitchButton = (ImageButton) findViewById(R.id.camSwitchButton);
        /*camSwitchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (cameraAccess != null)
                {
                    currentCamera++;
                    if (currentCamera >= numCameras)
                        currentCamera = 0;

                    CameraThread.getInstance().closeCamera();
                    //openCamera();
                }
            }
        });*/

        textureView = (TextureView)findViewById(R.id.textureView);
        textureView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP/* && cameraAccess != null*/)
                {
                   /* cameraAccess.getCameraFocus().touchFocusAsync((int) event.getY(),
                            (int) event.getY(), textureView.getWidth(), textureView.getHeight(), new IAutoFocusCallback()
                            {
                                @Override
                                public void onAutoFocus(boolean success)
                                {
                                    //no-op
                                }
                            });*/
                }
                return true;
            }
        });

        final AppCompatActivity thiz = this;
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
        {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {

                        ActivityCompat.requestPermissions(thiz, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    else
                    {
                        permissionsGranted();
                    }
                }
                else
                {
                    permissionsGranted();
                }

                /*else
                {
                    showDeviceIncompatibleAlert();
                }*/
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
            {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
            {
                //CameraThread.getInstance().closeCamera();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface)
            {

            }
        });

        isoBt = (Button)findViewById(R.id.isoBt);
        isoBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                shutterSpeedInterface.forceHide();
                isoInterface.toggleVisibility();
            }
        });
        shutterSpeedBt = (ImageButton)findViewById(R.id.shutterSpeedBt);
        shutterSpeedBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isoInterface.forceHide();
                shutterSpeedInterface.toggleVisibility();
            }
        });


    }

    @Override
    public void onBackPressed()
    {
        if (modesInterface.isVisible())
            modesInterface.setIsVisible(false);
        else
            super.onBackPressed();
    }

    private void permissionsGranted()
    {
        saveDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), SAVE_DIR_NAME);
        saveDir.mkdirs();
        //LogFile.initLogFile(saveDir);
        partialDir = new File(saveDir, PARTIAL_DIR_NAME);
        partialDir.mkdirs();
        //requestRoot();

        if (DEBUG_MODE)
        {
            bkpDir = new File(saveDir, BKP_DIR_NAME);
            bkpDir.mkdirs();
        }

        /*if (partialDir.listFiles().length > 0)
        {
            showTextToast("Found partial pictures! Converting them to DNG");
            convertToDng();
        }*/
    }

    private void openCamera(final boolean reopen)
    {
        //hide();

        /*CameraThread.getInstance().openCamera(currentCamera, getApplicationContext(), new AsyncOperation<OpenCameraResult>()
        {
            @Override
            protected void execute(OpenCameraResult argument)
            {
                CameraAccess access = argument.getAccess();
                //CameraOpenError openError = argument.getOpenError();

                if (access != null && openError.equals(CameraOpenError.NONE))
                {
                    cameraAccess = access;
                    CameraConfig cameraConfig = access.getCameraConfig();

                    cameraConfig.setupCamera();
                    cameraConfig.setupPreviewTexture(textureView);

                    if (reopen)
                    {
                        ExposureInfo exposureInfo = cameraAccess.getDeviceInfo().getCameras()[currentCamera].getExposure();
                        if (lastISO != null)
                            cameraAccess.setParameter(exposureInfo.getIsoParameter(), lastISO);

                        if (lastSS != null)
                            cameraAccess.setParameter(exposureInfo.getShutterSpeedParameter(), lastSS);
                    }

                    if (currentCamera == 1)
                    {
                        access.setReopenCallback(new IReopenCameraCallback()
                        {
                            @Override
                            public void onReopen()
                            {
                                openCamera(true);
                                access.resaveDngFiles(partialDir.getAbsolutePath(),
                                        saveDir.getAbsolutePath(), getApplicationContext(), new IRawCaptureCallback()
                                        {
                                            @Override
                                            public void onPictureTaken(boolean success)
                                            {
                                                enableCaptureButton();

                                                if (!success)
                                                    showTextToast("Error taking picture!");
                                                else
                                                    showTextToast("DNG file created successfully!");
                                            }
                                        });
                            }
                        });
                    }//
                    LogFile.writeLine("Build.MODEL: " + Build.MODEL);
                    LogFile.writeLine("Build.MANUFACTURER: " + Build.MANUFACTURER);
                    LogFile.writeLine("Has Intel Camera available: " + cameraAccess.hasIntelFeatures());
                    LogFile.writeLine("Camera Parameters: " + cameraConfig.dumpParameters());

                    Log.i("PARS", cameraConfig.dumpParameters());

                    DeviceInfo deviceInfo = access.getDeviceInfo();
                    isoInterface.updateISOValues(deviceInfo.getCameras()[currentCamera].getExposure(), access);
                    shutterSpeedInterface.updateSSValues(deviceInfo.getCameras()[currentCamera].getExposure(), access);
                    flashBt.setVisibility(access.hasFlash() ? View.VISIBLE : View.INVISIBLE);

                    access.startPreview();
                    //access.startPreviewAsync(new IStartPreviewCallback()
                    {
                        @Override
                        public void previewStarted()
                        {
                            //no-op
                        }
                    });//
                }
                else
                {
                    throw new RuntimeException("Error opening camera!");
                }
            }
        });*/
    }

    private void showTextToast(String text)
    {
        currentToast.setText(text);
        currentToast.show();
    }

    private void disableCaptureButton()
    {
        captureBt.setClickable(false);
        captureBt.setEnabled(false);

        flashBt.setClickable(false);
        flashBt.setEnabled(false);

        camSwitchButton.setClickable(false);
        camSwitchButton.setEnabled(false);

        isoInterface.forceHide();
        shutterSpeedInterface.forceHide();

        isoBt.setClickable(false);
        isoBt.setEnabled(false);

        shutterSpeedBt.setClickable(false);
        shutterSpeedBt.setEnabled(false);

        captureBt.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void enableCaptureButton()
    {
        captureBt.setClickable(true);
        captureBt.setEnabled(true);

        flashBt.setClickable(true);
        flashBt.setEnabled(true);

        camSwitchButton.setClickable(true);
        camSwitchButton.setEnabled(true);

        isoBt.setClickable(true);
        isoBt.setEnabled(true);

        shutterSpeedBt.setClickable(true);
        shutterSpeedBt.setEnabled(true);

        captureBt.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
