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
import android.widget.ImageButton;

import com.fkeglevich.rawdumper.activity.ModularActivity;
import com.fkeglevich.rawdumper.camera.async.CameraThread;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.async.impl.CameraSelectorImpl;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.setup.CameraSetup;
import com.fkeglevich.rawdumper.controller.orientation.OrientationModule;
import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionModule;
import com.fkeglevich.rawdumper.ui.CameraPreviewTexture;
import com.fkeglevich.rawdumper.ui.ModesInterface;
import com.fkeglevich.rawdumper.ui.activity.FullscreenManager;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.event.EventListener;
import com.fkeglevich.rawdumper.util.exception.MessageException;
import com.lantouzi.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;

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
    private WheelView mWheelView;
    private CameraSetup cameraSetup;
    private TurboCamera turboCamera = null;
    private CameraPreviewTexture textureView;

    private ImageButton switchButton;
    private CameraSelectorImpl cameraSelector;

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
                cameraSelector.selectNextCamera();
                if (turboCamera != null) {
                    CameraThread.getInstance().closeCamera(turboCamera);
                    turboCamera = null;
                    textureView.startCloseCameraAnimation();
                    cameraSetup.setupCamera();
                }
            }
        });

        mWheelView = (WheelView) findViewById(R.id.view2);
        findViewById(R.id.wheelFrame).setVisibility(View.INVISIBLE);

        textureView = (CameraPreviewTexture) findViewById(R.id.textureView);

        cameraSelector = new CameraSelectorImpl();
        cameraSetup = new CameraSetup(textureView, reference,
                permissionModule.getPermissionManager(), cameraSelector);

        cameraSetup.onComplete.addListener(new EventListener<TurboCamera>() {
            @Override
            public void onEvent(TurboCamera eventData)
            {
                turboCamera = eventData;
                init();
            }
        });

        cameraSetup.onException.addListener(new EventListener<MessageException>() {
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
                cameraSetup.setupCamera();
            }
        });

        reference.onPause.addListener(new EventListener<Nothing>() {
            @Override
            public void onEvent(Nothing eventData)
            {
                if (turboCamera != null) {
                    CameraThread.getInstance().closeCamera(turboCamera);
                    textureView.setAlpha(0);
                    turboCamera = null;
                }
            }
        });
    }

    private void init()
    {
        textureView.setupPreview(turboCamera);
        textureView.startOpenCameraAnimation();

        List<String> strList = new ArrayList<>();
        List<Iso> isoList = turboCamera.getIsoFeature().getAvailableValues();

        strList.clear();
        for (Iso iso : isoList)
            strList.add(iso.displayValue());

        mWheelView.setItems(strList);
        mWheelView.selectIndex(0);
        mWheelView.setMaxSelectableIndex(strList.size() - 1);
        mWheelView.setVisibility(View.INVISIBLE);

        mWheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {
                if (turboCamera != null)
                    turboCamera.getIsoFeature().setValue(turboCamera.getIsoFeature().getAvailableValues().get(position));
                    //turboCamera.getEVFeature().setValue(turboCamera.getEVFeature().getAvailableValues().get(position));
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {

            }
        });
    }
}
