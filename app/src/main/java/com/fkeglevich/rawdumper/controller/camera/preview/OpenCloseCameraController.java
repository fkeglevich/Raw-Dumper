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

package com.fkeglevich.rawdumper.controller.camera.preview;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.async.CameraAccess;
import com.fkeglevich.rawdumper.camera.async.CameraThread;
import com.fkeglevich.rawdumper.controller.permission.IPermissionResultListener;
import com.fkeglevich.rawdumper.controller.requirement.RequirementList;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * A controller for dealing with opening and closing cameras in the right moment
 *
 * Created by Flávio Keglevich on 14/08/2017.
 */

class OpenCloseCameraController implements IPermissionResultListener
{
    private final PreviewControllerActivity activity;
    private final CameraOpenedListener cameraOpenedListener;
    private final RequirementList<PreviewRequirements> previewRequirements;

    OpenCloseCameraController(PreviewControllerActivity activity,
                              CameraOpenedListener cameraOpenedListener,
                              RequirementList<PreviewRequirements> previewRequirements)
    {
        this.activity = activity;
        this.cameraOpenedListener = cameraOpenedListener;
        this.previewRequirements = previewRequirements;
    }

    @Override
    public void onMissingMandatoryPermission()
    {
        activity.showMissingMandatoryPermissionUi();
    }

    @Override
    public void onMissingRootAccess()
    {
        activity.showMissingRootAccessUi();
    }

    @Override
    public void onAllPermissionsGranted(final boolean hadDialogPrompt)
    {
        CameraThread.getInstance().openCamera(1, new AsyncOperation<CameraAccess>()
                {
                    @Override
                    protected void execute(CameraAccess argument)
                    {
                        previewRequirements.setRequirement(PreviewRequirements.CAMERA_OPENED, argument);
                        cameraOpenedListener.cameraOpened(hadDialogPrompt);
                    }
                },
                new AsyncOperation<MessageException>()
                {
                    @Override
                    protected void execute(MessageException argument)
                    {
                        activity.showErrorOpeningCameraUi(argument.getMessageResource(activity));
                    }
                });
    }

    void closeCamera()
    {
        previewRequirements.unsetRequirement(PreviewRequirements.CAMERA_OPENED);
        CameraThread.getInstance().closeCamera();
        activity.onCameraClosed(activity.getTextureView());
    }
}
