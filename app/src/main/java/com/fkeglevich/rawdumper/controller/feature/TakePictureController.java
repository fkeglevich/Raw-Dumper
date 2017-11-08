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

package com.fkeglevich.rawdumper.controller.feature;

import android.annotation.SuppressLint;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.controller.animation.ButtonDisabledStateController;
import com.fkeglevich.rawdumper.ui.CameraPreviewTexture;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 05/11/17.
 */

public class TakePictureController extends FeatureController
{
    private final View captureButton;
    private final ButtonDisabledStateController buttonDisabledStateController;
    private final Toast toast;
    private final View pictureLayer;
    private final CameraPreviewTexture previewTexture;

    @SuppressLint("ShowToast")
    TakePictureController(View captureButton, View pictureLayer, CameraPreviewTexture previewTexture)
    {
        this.captureButton = captureButton;
        this.buttonDisabledStateController = new ButtonDisabledStateController(captureButton, false);
        this.toast = Toast.makeText(captureButton.getContext(), "", Toast.LENGTH_LONG);
        this.pictureLayer = pictureLayer;
        this.previewTexture = previewTexture;
    }

    @Override
    protected void setup(final TurboCamera camera)
    {
        buttonDisabledStateController.enableAnimated();
        captureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //play animation
                disableUi();
                camera.takePicture(new PictureListener()
                {
                    @Override
                    public void onPictureTaken()
                    {
                        //Reserved for future versions
                    }

                    @Override
                    public void onPictureSaved()
                    {
                        showToast(R.string.picture_saved);
                        enableUi();
                    }
                }, new PictureExceptionListener()
                {
                    @Override
                    public void onException(MessageException exception)
                    {
                        showToast(R.string.error_saving_picture);
                        enableUi();
                    }
                });
            }
        });
    }

    private void disableUi()
    {
        previewTexture.pauseUpdating();
        pictureLayer.setVisibility(View.VISIBLE);
    }

    private void enableUi()
    {
        pictureLayer.setVisibility(View.INVISIBLE);
        previewTexture.resumeUpdating();
    }

    private void showToast(@StringRes int id)
    {
        toast.setText(id);
        toast.show();
    }

    @Override
    protected void reset()
    {
        disable();
    }

    @Override
    protected void disable()
    {
        buttonDisabledStateController.disableAnimated();
    }

    @Override
    protected void enable()
    {
        buttonDisabledStateController.enableAnimated();
    }
}
