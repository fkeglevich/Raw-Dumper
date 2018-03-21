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

package com.fkeglevich.rawdumper.camera.setup;

import android.graphics.SurfaceTexture;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.CameraSelector;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CameraPreview;
import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionManager;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import junit.framework.Assert;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/10/17.
 */

class SetupStageLinkImpl implements SetupStageLink
{
    private DeviceInfo deviceInfo           = null;
    private SurfaceTexture surfaceTexture   = null;

    private final CameraPreview textureSource;
    private final ActivityReference activityReference;
    private final MandatoryPermissionManager permissionManager;
    private final CameraSelector cameraSelector;
    private final CameraSetup setupReference;

    SetupStageLinkImpl(CameraPreview textureSource,
                       ActivityReference activityReference,
                       MandatoryPermissionManager permissionManager,
                       CameraSelector cameraSelector,
                       CameraSetup setupReference)
    {
        this.textureSource = textureSource;
        this.activityReference = activityReference;
        this.permissionManager = permissionManager;
        this.cameraSelector = cameraSelector;
        this.setupReference = setupReference;
    }

    @Override
    public void setDeviceInfo(DeviceInfo deviceInfo)
    {
        this.deviceInfo = deviceInfo;
        setupReference.removeAllStagesOfType(DeviceInfoStage.class);
    }

    @Override
    public DeviceInfo getDeviceInfo()
    {
        return deviceInfo;
    }

    @Override
    public void setSurfaceTexture(SurfaceTexture surfaceTexture)
    {
        this.surfaceTexture = surfaceTexture;
    }

    @Override
    public CameraPreview getSurfaceTextureSource()
    {
        return textureSource;
    }

    @Override
    public ActivityReference getActivity()
    {
        return activityReference;
    }

    @Override
    public MandatoryPermissionManager getPermissionManager()
    {
        return permissionManager;
    }

    @Override
    public CameraSelector getCameraSelector()
    {
        return cameraSelector;
    }

    @Override
    public CameraContext buildCameraContext()
    {
        Assert.assertNotNull(deviceInfo);
        Assert.assertNotNull(surfaceTexture);
        return new SetupStageCameraContext(deviceInfo, surfaceTexture, cameraSelector);
    }

    @Override
    public void setTurboCamera(TurboCamera camera)
    {
        setupReference.turboCamera = camera;
    }

    @Override
    public void processNextStage()
    {
        setupReference.processNextStage();
    }

    @Override
    public void sendException(MessageException exception)
    {
        setupReference.sendException(exception);
    }

    @Override
    public void setPermissionToken()
    {
        setupReference.removeAllStagesOfType(PermissionsStage.class);
    }

    @Override
    public void setWorkaroundToken()
    {
        setupReference.removeAllStagesOfType(WorkaroundStage.class);
    }
}
