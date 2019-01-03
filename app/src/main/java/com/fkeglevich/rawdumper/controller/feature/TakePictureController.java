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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.activity.KeyEventData;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CameraPreview;
import com.fkeglevich.rawdumper.controller.animation.ButtonDisabledStateController;
import com.fkeglevich.rawdumper.util.event.EventListener;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.StringRes;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 05/11/17.
 */

public class TakePictureController extends FeatureController
{
    private final static List<Integer> KEY_CODES = Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_CAMERA);

    private final View captureButton;
    private final ButtonDisabledStateController buttonDisabledStateController;
    private final Toast errorToast;
    private final Toast pictureToast;
    private final View pictureLayer;
    private final CameraPreview cameraPreview;
    private final List<FeatureController> meteringControllers;
    private final View progressBar;
    private final ActivityReference reference;
    private EventListener<KeyEventData> keyListener;
    private boolean isTakingPicture = false;

    @SuppressLint("ShowToast")
    TakePictureController(View captureButton, View pictureLayer, CameraPreview cameraPreview,
                          List<FeatureController> meteringControllers, View progressBar, ActivityReference reference)
    {
        this.captureButton = captureButton;
        this.buttonDisabledStateController = new ButtonDisabledStateController(captureButton, false);
        this.errorToast = Toast.makeText(captureButton.getContext(), "", Toast.LENGTH_LONG);
        this.pictureToast = Toast.makeText(captureButton.getContext(), "", Toast.LENGTH_SHORT);
        this.pictureLayer = pictureLayer;
        this.cameraPreview = cameraPreview;
        this.meteringControllers = meteringControllers;
        this.progressBar = progressBar;
        this.reference = reference;
    }

    @Override
    protected void setup(final TurboCamera camera)
    {
        isTakingPicture = false;
        buttonDisabledStateController.enableAnimated();
        captureButton.setOnClickListener(v -> actualTakePicture(camera));
        keyListener = eventData ->
        {
            if (KEY_CODES.contains(eventData.keyCode))
            {
                eventData.defaultPreventer.preventDefault();
                actualTakePicture(camera);
            }
        };
        reference.onKeyDown.addListener(keyListener);
    }

    private void actualTakePicture(TurboCamera camera)
    {
        if (isTakingPicture) return;
        //play animation
        disableUi();
        isTakingPicture = true;
        camera.takePicture(new PictureListener()
        {
            @Override
            public void onPictureTaken()
            {
                //Reserved for future versions
                enableUi();
                isTakingPicture = false;
            }

            @Override
            public void onPictureSaved()
            {
                showToast(R.string.picture_saved, pictureToast);
            }
        }, exception ->
        {
            showToast(R.string.error_saving_picture, errorToast);
            enableUi();
            isTakingPicture = false;
        });
    }

    private void disableUi()
    {
        for (FeatureController meteringController : meteringControllers)
            meteringController.disable();
        cameraPreview.pauseUpdating();
        pictureLayer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        captureButton.setAlpha(0);
    }

    private void enableUi()
    {
        captureButton.setAlpha(1f);
        progressBar.setVisibility(View.INVISIBLE);
        pictureLayer.setVisibility(View.INVISIBLE);
        cameraPreview.resumeUpdating();
        for (FeatureController meteringController : meteringControllers)
            meteringController.enable();
    }

    private void showToast(@StringRes int id, Toast toast)
    {
        toast.setText(id);
        toast.show();
    }

    @Override
    protected void reset()
    {
        disable();
        reference.onKeyDown.removeListener(keyListener);
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
