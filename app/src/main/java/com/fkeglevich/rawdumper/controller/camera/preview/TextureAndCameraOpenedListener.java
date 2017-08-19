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

import android.util.Log;
import android.view.TextureView;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.async.CameraAccess;
import com.fkeglevich.rawdumper.camera.exception.CameraOpenException;
import com.fkeglevich.rawdumper.controller.requirement.IRequirementListener;
import com.fkeglevich.rawdumper.controller.requirement.IRequirementReader;

import java.io.IOException;

/**
 * A listener that start previewing the camera when all the specific requirements are met.
 * The requirements are: the camera needs to be opened and a surface texture.
 *
 * Created by Flávio Keglevich on 14/08/2017.
 */

class TextureAndCameraOpenedListener implements IRequirementListener<PreviewRequirements>
{
    private static final String TAG = "TextureAndCamListener";

    private final PreviewControllerActivity activity;

    TextureAndCameraOpenedListener(PreviewControllerActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void sendRequirements(IRequirementReader<PreviewRequirements> requirementReader)
    {
        //Gets the requirements
        TextureView textureView = activity.getTextureView();
        CameraAccess cameraAccess = requirementReader.getRequirement(PreviewRequirements.CAMERA_OPENED);

        //Try starting preview
        try
        {
            cameraAccess.setupPreview(textureView);
            startPreviewAsync(cameraAccess);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Exception occurred when setting preview texture: " + e);
            CameraOpenException newException = new CameraOpenException();
            activity.showErrorOpeningCameraUi(newException.getMessageResource(activity));
        }
    }

    private void startPreviewAsync(final CameraAccess cameraAccess)
    {
        cameraAccess.startPreviewAsync(new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
            {activity.onGotCameraAccess(cameraAccess);}
        });
    }
}
