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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fkeglevich.rawdumper.camera.async.CameraAccess;
import com.fkeglevich.rawdumper.camera.async.CameraConfig;
import com.fkeglevich.rawdumper.camera.async.CameraOpenError;
import com.fkeglevich.rawdumper.camera.async.CameraThread;
import com.fkeglevich.rawdumper.camera.async.callbacks.IAutoFocusCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IOpenCameraCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IRawCaptureCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IReopenCameraCallback;
import com.fkeglevich.rawdumper.log.LogFile;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExposureInfo;
import com.fkeglevich.rawdumper.ui.ISOInterface;
import com.fkeglevich.rawdumper.ui.ModesInterface;
import com.fkeglevich.rawdumper.ui.ShutterSpeedInterface;
import com.fkeglevich.rawdumper.ui.UiUtils;
import com.intel.camera.extensions.IntelCamera;

import java.io.File;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

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
    private ImageButton infoBt;
    private ImageButton camSwitchButton;
    private ProgressBar progressBar;
    private Toast currentToast;
    private ISOInterface isoInterface;
    private ShutterSpeedInterface shutterSpeedInterface;
    private Button isoBt;
    private ImageButton shutterSpeedBt;
    private CameraAccess cameraAccess = null;

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
                if (cameraAccess != null)
                {
                    ExposureInfo exposureInfo = cameraAccess.getDeviceInfo().getCameras()[currentCamera].getExposure();

                    lastISO = cameraAccess.getParameter(exposureInfo.getIsoParameter());
                    lastSS = cameraAccess.getParameter(exposureInfo.getShutterSpeedParameter());
                    cameraAccess.takeRawPictureAsync(partialDir.getAbsolutePath(),
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

                    disableCaptureButton();
                }
            }
        });

        flashBt = (ImageButton) findViewById(R.id.flashButton);
        flashBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (cameraAccess != null)
                {
                    if (!flashIsOn)
                        flashBt.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_flash_on_black_24dp));
                    else
                        flashBt.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_flash_off_black_24dp));

                    flashIsOn = !flashIsOn;
                    cameraAccess.getCameraFlash().setFlashValue(flashIsOn);
                }
            }
        });

        camSwitchButton = (ImageButton) findViewById(R.id.camSwitchButton);
        camSwitchButton.setOnClickListener(new View.OnClickListener()
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
                    openCamera();
                }
            }
        });

        infoBt = (ImageButton) findViewById(R.id.infoButton);
        infoBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showAboutScreen();
            }
        });

        textureView = (TextureView)findViewById(R.id.textureView);
        textureView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP && cameraAccess != null)
                {
                    cameraAccess.getCameraFocus().touchFocusAsync((int) event.getY(),
                            (int) event.getY(), textureView.getWidth(), textureView.getHeight(), new IAutoFocusCallback()
                            {
                                @Override
                                public void onAutoFocus(boolean success)
                                {
                                    //no-op
                                }
                            });
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
                CameraThread.getInstance().closeCamera();
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        hide();
    }

    /**
     * Hides the action bar and the system UI.
     */
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        View decorView = getWindow().getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        boolean everyGranted = true;

        hide();

        for (int value : grantResults)
            if (value != PackageManager.PERMISSION_GRANTED)
            {
                everyGranted = false;
                break;
            }

        if (!everyGranted)
            showNeedsPermissionsAlert();
        else
            permissionsGranted();
    }

    private void requestRoot()
    {
        //if (ShellManager.getInstance().isRunning())
        if (CameraThread.getInstance().isShellRunning())
        {
            openCamera();
        }
        else
        {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Please wait");
            dialog.setMessage("Requesting root privilege...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            UiUtils.showDialogInImmersiveMode(dialog, this);

            CameraThread.getInstance().openShell(new Shell.OnCommandResultListener()
            {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output)
                {
                    dialog.dismiss();
                    if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING)
                        showNeedsRootPermissionsAlert();
                    else
                        openCamera();
                }
            });
        }
    }

    private void permissionsGranted()
    {
        saveDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), SAVE_DIR_NAME);
        saveDir.mkdirs();
        LogFile.initLogFile(saveDir);
        partialDir = new File(saveDir, PARTIAL_DIR_NAME);
        partialDir.mkdirs();
        requestRoot();

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

    private void openCamera()
    {
        openCamera(false);
    }

    private void openCamera(final boolean reopen)
    {
        hide();

        CameraThread.getInstance().openCamera(currentCamera, getApplicationContext(), new IOpenCameraCallback()
        {
            @Override
            public void cameraOpened(final CameraAccess access, CameraOpenError openError)
            {
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
                    }
                    LogFile.writeLine("Build.MODEL: " + Build.MODEL);
                    LogFile.writeLine("Build.MANUFACTURER: " + Build.MANUFACTURER);
                    LogFile.writeLine("Has Intel Camera available: " + IntelCamera.isIntelCameraAvailable());
                    LogFile.writeLine("Camera Parameters: " + cameraConfig.dumpParameters());

                    DeviceInfo deviceInfo = access.getDeviceInfo();
                    isoInterface.updateISOValues(deviceInfo.getCameras()[currentCamera].getExposure(), access);
                    shutterSpeedInterface.updateSSValues(deviceInfo.getCameras()[currentCamera].getExposure(), access);
                    flashBt.setVisibility(access.hasFlash() ? View.VISIBLE : View.INVISIBLE);

                    access.startPreview();
                    /*access.startPreviewAsync(new IStartPreviewCallback()
                    {
                        @Override
                        public void previewStarted()
                        {
                            //no-op
                        }
                    });*/
                }
                else
                {
                    throw new RuntimeException("Error opening camera!");
                }
            }
        });
    }

    private void showNeedsPermissionsAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("The app needs every permission to work.");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });

        AlertDialog alert = builder.create();
        UiUtils.showDialogInImmersiveMode(alert, this);
    }

    private void showNeedsRootPermissionsAlert()
    {
        hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("The app needs root permission to work.");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });

        AlertDialog alert = builder.create();
        UiUtils.showDialogInImmersiveMode(alert, this);
    }

    private void showDeviceIncompatibleAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Your device is currently incompatible with this app.");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });

        AlertDialog alert = builder.create();
        UiUtils.showDialogInImmersiveMode(alert, this);
    }

    private void showAboutScreen()
    {
        //TODO: UNHARDCODE THIS METHOD!

        PackageInfo packageInfo = null;

        try
        {
            packageInfo = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        String messageStr = "Raw Dumper";
        if (packageInfo != null)
        {
            messageStr += " v" + packageInfo.versionName + "\n\n";
        }

        messageStr += "Copyright © 2017, Flávio Keglevich\n\n";

        messageStr += "This app and its launcher icon are licensed under Apache License 2.0. The license can be found here:\n";
        messageStr += "https://www.apache.org/licenses/LICENSE-2.0\n\n";

        messageStr += "The following open source libraries are used:\n\n";

        messageStr += "LibTIFF\n";
        messageStr += "Copyright (c) 1988-1997 Sam Leffler\n" + "Copyright (c) 1991-1997 Silicon Graphics, Inc.\n";
        messageStr += "Link: http://www.simplesystems.org/libtiff\n";
        messageStr += "License: http://www.simplesystems.org/libtiff/misc.html\n";

        messageStr += "\n";

        messageStr += "Libsuperuser\n";
        messageStr += "Copyright (C) 2012-2015 Jorrit \"Chainfire\" Jongma\n";
        messageStr += "Link: https://github.com/Chainfire/libsuperuser\n";
        messageStr += "License: https://github.com/Chainfire/libsuperuser/blob/master/LICENSE\n";

        messageStr += "\n";

        messageStr += "Transitions Everywhere\n";
        messageStr += "Copyright (C) 2016 Andrey Kulikov (andkulikov@gmail.com)\n";
        messageStr += "Link: https://github.com/andkulikov/Transitions-Everywhere\n";
        messageStr += "License: https://github.com/andkulikov/Transitions-Everywhere/blob/master/LICENSE\n";

        messageStr += "\n";

        messageStr += "Moshi\n";
        messageStr += "Copyright 2015 Square, Inc.\n";
        messageStr += "Link: https://github.com/square/moshi\n";
        messageStr += "License: https://github.com/square/moshi/blob/master/LICENSE.txt\n";

        messageStr += "\n";

        messageStr += "Metadata Extractor\n";
        messageStr += "Copyright 2002-2017 Drew Noakes\n";
        messageStr += "Link: https://github.com/drewnoakes/metadata-extractor\n";
        messageStr += "License: https://github.com/drewnoakes/metadata-extractor/blob/master/LICENSE-2.0.txt\n";

        messageStr += "\n";

        messageStr += "The following icon sets are used:\n\n";

        messageStr += "Material design icons\n";
        messageStr += "Copyright (C) 2017 Google\n";
        messageStr += "Link: https://material.io/icons\n";
        messageStr += "License: https://github.com/google/material-design-icons/blob/master/LICENSE\n";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final TextView messageView = new TextView(this);
        int paddingValue = UiUtils.dpToPixels(8, this);
        messageView.setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
        final SpannableString spannableStr = new SpannableString(messageStr);
        Linkify.addLinks(spannableStr, Linkify.WEB_URLS);
        messageView.setMaxLines(40);
        messageView.setVerticalScrollBarEnabled(true);
        messageView.setMovementMethod(new ScrollingMovementMethod());
        messageView.setText(spannableStr);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setCancelable(false);
        builder.setTitle("About");

        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        hide();
                    }
                });
        builder.setView(messageView);
        AlertDialog alert = builder.create();
        UiUtils.showDialogInImmersiveMode(alert, this);
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
