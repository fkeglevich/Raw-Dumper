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
import android.view.TextureView;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.CameraSelector;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionManager;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/09/17.
 */

interface SetupStageLink
{
    void setDeviceInfo(DeviceInfo deviceInfo);
    void setSurfaceTexture(SurfaceTexture surfaceTexture);

    TextureView getTextureView();
    ActivityReference getActivity();
    MandatoryPermissionManager getPermissionManager();
    CameraSelector getCameraSelector();

    CameraContext buildCameraContext();

    void setTurboCamera(TurboCamera camera);

    void processNextStage();
    void sendException(MessageException exception);

    void setPermissionToken();
}
