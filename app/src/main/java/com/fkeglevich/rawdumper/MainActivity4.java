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

import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

import com.fkeglevich.rawdumper.controller.camera.FullscreenPreviewActivity;
import com.fkeglevich.rawdumper.ui.ModesInterface;

/**
 * Created by Flávio Keglevich on 13/08/2017.
 * TODO: Add a class header comment!
 */

public class MainActivity4 extends FullscreenPreviewActivity
{
    private ModesInterface modesInterface;

    private ImageButton captureBt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        modesInterface = new ModesInterface(this);

        captureBt = (ImageButton) findViewById(R.id.captureButton);
        captureBt.setOnClickListener(new View.OnClickListener()
                                     {
                                         @Override
                                         public void onClick(View v)
                                         {
                                            //getCameraAccess().takePic();
                                         }
                                     });

        initializeCameraPreview((TextureView)findViewById(R.id.textureView));
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
