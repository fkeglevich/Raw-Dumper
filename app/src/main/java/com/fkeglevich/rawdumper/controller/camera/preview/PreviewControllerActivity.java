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

import android.view.TextureView;

import com.fkeglevich.rawdumper.camera.async.CameraAccess;
import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionAwareActivity;
import com.fkeglevich.rawdumper.controller.requirement.RequirementList;

/**
 * An activity capable of showing a camera preview, dealing with the specific aspects of this task.
 *
 * For example, it deals with surface textures, camera opening, exceptions and requirements for
 * previewing the camera.
 *
 * However, it's NOT an UI class, so all UI-related methods are abstract.
 *
 * Created by Flávio Keglevich on 13/08/2017.
 */

public abstract class PreviewControllerActivity extends MandatoryPermissionAwareActivity
{
    private PermissionsState currentState = PermissionsState.INITIAL;
    private TextureView textureView;
    private OpenCloseCameraController openCloseCameraController;
    private CameraAccess cameraAccess;

    protected void initializeCameraPreview(TextureView textureView)
    {
        this.textureView = textureView;
        RequirementList<PreviewRequirements> previewRequirements = new RequirementList<>(PreviewRequirements.class, new TextureAndCameraOpenedListener(this));
        textureView.setSurfaceTextureListener(new TextureListener(previewRequirements));
        openCloseCameraController = new OpenCloseCameraController(this, previewRequirements);
        initializePermissionManager(openCloseCameraController);
    }

    TextureView getTextureView()
    {
        return textureView;
    }

    void onGotCameraAccess(CameraAccess cameraAccess)
    {
        this.cameraAccess = cameraAccess;
        onCameraPreviewStarted(textureView);
    }

    void updateState(boolean hadDialogPrompt)
    {
        currentState = hadDialogPrompt ? PermissionsState.HAD_DIALOG_PROMPT : PermissionsState.INITIAL;
    }

    protected CameraAccess getCameraAccess()
    {
        return cameraAccess;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        switch (currentState)
        {
            case INITIAL:
                currentState = PermissionsState.REQUESTING_PERMISSIONS;
                requestAllPermissions();
                break;

            case HAD_DIALOG_PROMPT:
                currentState = PermissionsState.INITIAL;
                break;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        switch (currentState)
        {
            case HAD_DIALOG_PROMPT:
                currentState = PermissionsState.INITIAL;
            case INITIAL:
                openCloseCameraController.closeCamera();
                break;
        }
    }

    protected abstract void showMissingMandatoryPermissionUi();

    protected abstract void showMissingRootAccessUi();

    protected abstract void showErrorOpeningCameraUi(String message);

    protected void onCameraPreviewStarted(TextureView textureView)
    {   }

    protected void onCameraClosed(TextureView textureView)
    {   }
}
