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

import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

import com.fkeglevich.rawdumper.controller.camera.FullscreenPreviewActivity;
import com.fkeglevich.rawdumper.dng.DngWriter;
import com.fkeglevich.rawdumper.dng.writer.TileImageWriter;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.DateExtractor;
import com.fkeglevich.rawdumper.raw.capture.WhiteBalanceInfoExtractor;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.raw.data.buffer.FileRawImageData;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfoLoader;
import com.fkeglevich.rawdumper.ui.ModesInterface;
import com.fkeglevich.rawdumper.ui.PausingTextureView;
import com.fkeglevich.rawdumper.util.AssetUtil;

import java.io.IOException;

/**
 * Created by Flávio Keglevich on 13/08/2017.
 * TODO: Add a class header comment!
 */

public class MainActivity4 extends FullscreenPreviewActivity
{
    private ModesInterface modesInterface;

    private ImageButton captureBt;
    private boolean visibleTex = true;

    private OrientationEventListener orientationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        //modesInterface = new ModesInterface(this);

        captureBt = (ImageButton) findViewById(R.id.captureButton);

        captureBt.setOnClickListener(new View.OnClickListener()
                                     {
                                         @Override
                                         public void onClick(View v)
                                         {
                                             if (visibleTex)
                                                ((PausingTextureView)findViewById(R.id.textureView)).pauseUpdating();
                                             else
                                                 ((PausingTextureView)findViewById(R.id.textureView)).resumeUpdating();

                                             visibleTex = !visibleTex;
                                             //getCameraAccess().takePic();
                                         }
                                     });

        orientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI)
        {
            @Override
            public void onOrientationChanged(int orientation)
            {
                Log.i("ASD", "ori: " + orientation);
            }
        };

        //initializeCameraPreview((TextureView)findViewById(R.id.textureView));

        //DngWriter writer = null;//DngWriter.open("/sdcard/outb2.dng");
        DngWriter writer = DngWriter.open("/sdcard/outb2.dng");

        boolean isFront = !true;

        if (writer != null)
        {
            DeviceInfoLoader loader = new DeviceInfoLoader();
            DeviceInfo deviceInfo = null;
            try
            {
                deviceInfo = loader.loadDeviceInfo(AssetUtil.getAssetAsString("t00x.json"));
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            CaptureInfo captureInfo = new CaptureInfo();
            captureInfo.device = deviceInfo;
            captureInfo.camera = deviceInfo.getCameras()[isFront ? 1 : 0];
            captureInfo.date = new DateExtractor().extractFromCurrentTime();
            captureInfo.whiteBalanceInfo = new WhiteBalanceInfoExtractor().extractFrom(captureInfo.camera.getColor());
            captureInfo.imageSize = captureInfo.camera.getSensor().getRawImageSizes()[1];
            captureInfo.originalRawFilename = "out2.raw";
            captureInfo.destinationRawFilename = "/sdcard/outb2.dng";
            captureInfo.orientation = ImageOrientation.TOPLEFT;

            try
            {
                FileRawImageData fileRawImageData = new FileRawImageData(captureInfo.imageSize, "/sdcard/zen5/outb.raw");
                //writer.write(captureInfo, new ScanlineImageWriter(), fileRawImageData);
                writer.write(captureInfo, new TileImageWriter(), fileRawImageData);
                Log.i("OK", "OKKK");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.i("OK", "NOT OK");
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (orientationListener != null)
            orientationListener.enable();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (orientationListener != null)
            orientationListener.disable();
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
    protected void onCameraPreviewStarted(TextureView textureView)
    {
        super.onCameraPreviewStarted(textureView);
    }
}
