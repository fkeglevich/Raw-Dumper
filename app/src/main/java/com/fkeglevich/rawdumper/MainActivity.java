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
import android.graphics.Matrix;
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

import com.fkeglevich.rawdumper.camera.CaptureConfig;
import com.fkeglevich.rawdumper.camera.CaptureSize;
import com.fkeglevich.rawdumper.camera.ModeInfo;
import com.fkeglevich.rawdumper.camera.TurboCamera;
import com.fkeglevich.rawdumper.i3av4.I3av4ToDngConverter;
import com.fkeglevich.rawdumper.log.LogFile;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfoLoader;
import com.fkeglevich.rawdumper.ui.ISOInterface;
import com.fkeglevich.rawdumper.ui.ModesInterface;
import com.fkeglevich.rawdumper.ui.ShutterSpeedInterface;
import com.fkeglevich.rawdumper.ui.UiUtils;
import com.intel.camera.extensions.IntelCamera;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private static final String SAVE_DIR_NAME = "RawDumper";
    private static final String RAW_PATH = "/data/misc/media";
    private static final String RAW_PATH_ALT = "/data";
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
    private TurboCamera turboCamera;
    private TextureView textureView;
    private ImageButton captureBt;
    private ImageButton flashBt;
    private ImageButton infoBt;
    private ProgressBar progressBar;
    private Toast currentToast;
    private ISOInterface isoInterface;
    private ShutterSpeedInterface shutterSpeedInterface;
    private Button isoBt;
    private ImageButton shutterSpeedBt;
    private DeviceInfo deviceInfo;

    private boolean flashIsOn = false;

    private Shell.Interactive rootShell;

    private Camera.PictureCallback jpegCallback;

    private File saveDir;
    private File partialDir;
    private File bkpDir;

    I3av4ToDngConverter converter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        log("Starting app");
        setContentView(R.layout.activity_main);

        jpegCallback = new Camera.PictureCallback()
        {
            @Override
            public void onPictureTaken(byte[] data, Camera camera)
            {
                log("JPEG callback, restarting preview");
                camera.startPreview();
                log("JPEG callback, preview started");

                rootShell.addCommand(new String[] {"ls " + RAW_PATH}, 2, new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode) {
                        LogFile.writeLine("ls " + RAW_PATH + " command result: " + exitCode);
                    }
                    @Override
                    public void onLine(String line) {
                        if (line.endsWith(".i3av4"))
                            LogFile.writeLine("ls " + RAW_PATH + " command line: " + line);
                    }
                });

                rootShell.addCommand(new String[] {"ls " + RAW_PATH_ALT}, 2, new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode) {
                        LogFile.writeLine("ls " + RAW_PATH_ALT + " command result: " + exitCode);
                    }
                    @Override
                    public void onLine(String line) {
                        if (line.endsWith(".i3av4"))
                            LogFile.writeLine("ls " + RAW_PATH_ALT + " command line: " + line);
                    }
                });

                rootShell.addCommand(new String[] {"mv " + RAW_PATH + "/* " + partialDir.getAbsolutePath()}, 2, new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode) {
                        if (exitCode < 0)
                        {
                            enableCaptureButton();
                            showTextToast("Error taking picture!");
                        }
                        else
                        {
                            showTextToast("Picture taken, converting to DNG...");
                            if (!convertToDng())
                                showTextToast("DNG file created successfully!");
                            else
                                showTextToast("Error creating DNG file!");
                            enableCaptureButton();
                        }
                    }

                    @Override
                    public void onLine(String line) {
                    }
                });
            }
        };

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
                if (turboCamera != null)
                {
                    turboCamera.takePicture(jpegCallback);
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
                if (turboCamera != null)
                {
                    if (!flashIsOn)
                        flashBt.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_flash_on_black_24dp));
                    else
                        flashBt.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_flash_off_black_24dp));

                    flashIsOn = !flashIsOn;
                    turboCamera.setFlash(flashIsOn);
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
                if (event.getAction() == MotionEvent.ACTION_UP && turboCamera != null)
                {
                    turboCamera.touchFocus((int)event.getY(), (int)event.getY(), textureView.getWidth(), textureView.getHeight());
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
                deviceInfo = new DeviceInfoLoader().loadDeviceInfo(getApplicationContext());
                if (deviceInfo != null)
                {
                    converter = new I3av4ToDngConverter(deviceInfo);
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
                }
                else
                {
                    showDeviceIncompatibleAlert();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
            {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
            {
                if (turboCamera != null)
                {
                    try
                    {
                        turboCamera.stopPreview();
                    }
                    finally
                    {
                        turboCamera.close();
                    }
                }
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

    @Override
    public void onPause()
    {
        isoInterface.clean();
        shutterSpeedInterface.clean();
        super.onPause();
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
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Requesting root privilege...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        rootShell = new Shell.Builder().
                useSU().
                setWantSTDERR(true).
                setWatchdogTimeout(5).
                setMinimalLogging(true).
                open(new Shell.OnCommandResultListener() {

                    // Callback to report whether the shell was successfully started up
                    @Override
                    public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                        dialog.dismiss();
                        hide();
                        if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING)
                            showNeedsRootPermissionsAlert();
                        else
                            openCamera();
                    }
                });
    }

    private void permissionsGranted()
    {
        requestRoot();
        saveDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), SAVE_DIR_NAME);
        saveDir.mkdirs();
        LogFile.initLogFile(saveDir);
        partialDir = new File(saveDir, PARTIAL_DIR_NAME);
        partialDir.mkdirs();

        if (DEBUG_MODE)
        {
            bkpDir = new File(saveDir, BKP_DIR_NAME);
            bkpDir.mkdirs();
        }

        if (partialDir.listFiles().length > 0)
        {
            showTextToast("Found partial pictures! Converting them to DNG");
            convertToDng();
        }
    }

    private void openCamera()
    {
        hide();
        turboCamera = TurboCamera.open(0);
        isoInterface.updateISOValues(deviceInfo.getCameras()[0].getExposure(), turboCamera);
        shutterSpeedInterface.updateSSValues(deviceInfo.getCameras()[0].getExposure(), turboCamera);
        turboCamera.setCameraMode(ModeInfo.SINGLE_JPEG);
        turboCamera.setAutoFocus();
        turboCamera.enableRaw();
        turboCamera.setFlash(flashIsOn);
        turboCamera.selectLargestPictureSize();
        Matrix matrix = new Matrix();
        CaptureSize previewSize = turboCamera.getCaptureConfig().getSelectedPreviewSize();

        LogFile.writeLine("Build.MODEL: " + Build.MODEL);
        LogFile.writeLine("Build.MANUFACTURER: " + Build.MANUFACTURER);
        LogFile.writeLine("Has Intel Camera available: " + IntelCamera.isIntelCameraAvailable());
        LogFile.writeLine("Camera Parameters: " + turboCamera.dumpParameters());

        matrix.setScale(1, (float)previewSize.getHeight() / (float)previewSize.getWidth());
        textureView.setTransform(matrix);
        turboCamera.setDisplayOrientation(90);
        try
        {
            turboCamera.setPreviewTexture(textureView.getSurfaceTexture());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        turboCamera.startPreview();
    }

    private boolean convertToDng()
    {
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
                    converter.convert(input.getAbsolutePath(), output.getAbsolutePath(), getApplicationContext());
                    if (DEBUG_MODE)
                        input.renameTo(new File(bkpDir, filePath));
                    else
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
        alert.show();
    }

    private void showNeedsRootPermissionsAlert()
    {
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
        alert.show();
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
        alert.show();
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
        alert.show();
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
        captureBt.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void enableCaptureButton()
    {
        captureBt.setClickable(true);
        captureBt.setEnabled(true);
        captureBt.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
