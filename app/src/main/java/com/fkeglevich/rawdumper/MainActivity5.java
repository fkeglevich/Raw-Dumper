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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.fkeglevich.rawdumper.activity.ModularActivity;
import com.fkeglevich.rawdumper.camera.async.CameraManager;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.controller.adapter.ButtonEnablerController;
import com.fkeglevich.rawdumper.controller.adapter.DismissibleManagerAdapter;
import com.fkeglevich.rawdumper.controller.adapter.ToastNotificationController;
import com.fkeglevich.rawdumper.controller.adapter.WheelViewAdapter;
import com.fkeglevich.rawdumper.controller.feature.DisplayableFeatureController;
import com.fkeglevich.rawdumper.controller.orientation.OrientationModule;
import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionModule;
import com.fkeglevich.rawdumper.ui.CameraPreviewTexture;
import com.fkeglevich.rawdumper.ui.ModesInterface;
import com.fkeglevich.rawdumper.ui.activity.FullscreenManager;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.event.EventListener;
import com.fkeglevich.rawdumper.util.exception.MessageException;
import com.lantouzi.wheelview.WheelView;

/**
 * Created by Flávio Keglevich on 29/08/2017.
 * TODO: Add a class header comment!
 */

public class MainActivity5 extends ModularActivity
{
    private FullscreenManager fullscreenManager = new FullscreenManager(reference);
    private OrientationModule orientationModule = new OrientationModule(reference);
    private MandatoryPermissionModule permissionModule = new MandatoryPermissionModule(reference);//new MandatoryRootModule(reference);

    private ModesInterface modesInterface;
    private WheelView wheelView;
    private CameraManager cameraManager;
    private CameraPreviewTexture textureView;

    private ImageButton switchButton;
    private Button isoButton;

    private EventListener<Nothing> pauseListener = new EventListener<Nothing>() {
        @Override
        public void onEvent(Nothing eventData)
        {
            cameraManager.closeCamera();
            textureView.setAlpha(0);
        }
    };
    private DisplayableFeatureController controller;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);

        modesInterface = new ModesInterface(reference);
        switchButton = (ImageButton) findViewById(R.id.camSwitchButton);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textureView.startCloseCameraAnimation();
                cameraManager.switchCamera();
            }
        });

        wheelView = (WheelView) findViewById(R.id.view2);
        View wheelParent = findViewById(R.id.wheelFrame);
        isoButton = (Button) findViewById(R.id.isoBt);

        ButtonEnablerController buttonController = new ButtonEnablerController(isoButton,
                new WheelViewAdapter(wheelView), wheelParent, new ToastNotificationController(this, R.string.iso_changed_notification),
                new DismissibleManagerAdapter());

        controller = new DisplayableFeatureController(buttonController)
        {
            @Override
            protected WritableFeature selectFeature(TurboCamera camera)
            {
                return camera.getIsoFeature();
            }
        };

        textureView = (CameraPreviewTexture) findViewById(R.id.textureView);

        cameraManager = new CameraManager(reference, textureView);
        cameraManager.onCameraOpened.addListener(new EventListener<TurboCamera>() {
            @Override
            public void onEvent(TurboCamera eventData)
            {
                init(eventData);
            }
        });

        cameraManager.onCameraException.addListener(new EventListener<MessageException>() {
            @Override
            public void onEvent(MessageException eventData)
            {
                Log.i("CameraSetup", "ON EXCEPTION: " + eventData.getMessageResource(getApplicationContext()));
            }
        });

        reference.onResume.addListener(new EventListener<Nothing>()
        {
            @Override
            public void onEvent(Nothing eventData)
            {
                cameraManager.openCamera();
            }
        });
    }

    private void init(final TurboCamera turboCamera)
    {
        reference.onPause.removeListener(pauseListener);
        reference.onPause.addListener(pauseListener);

        textureView.setupPreview(turboCamera);
        textureView.startOpenCameraAnimation();

        controller.setupFeature(turboCamera, cameraManager.onCameraClosed);
    }
}
