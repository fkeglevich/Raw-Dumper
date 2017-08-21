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

import android.graphics.SurfaceTexture;
import android.view.TextureView;

import com.fkeglevich.rawdumper.controller.requirement.RequirementList;

/**
 * Basic texture listener for handling with camera preview requirements
 *
 * Created by Flávio Keglevich on 14/08/2017.
 */

class TextureListener implements TextureView.SurfaceTextureListener
{
    private final RequirementList<PreviewRequirements> previewRequirements;

    TextureListener(RequirementList<PreviewRequirements> previewRequirements)
    {
        this.previewRequirements = previewRequirements;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        previewRequirements.setRequirement(PreviewRequirements.SURFACE_TEXTURE_AVAILABLE, true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
    {
        //No op
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        previewRequirements.unsetRequirement(PreviewRequirements.SURFACE_TEXTURE_AVAILABLE);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface)
    {
        //No op
    }
}