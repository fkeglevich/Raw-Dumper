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

import com.fkeglevich.rawdumper.camera.data.SurfaceTextureSource;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/09/17.
 */

public class SurfaceTextureStage implements SetupStage
{
    @Override
    public void executeStage(final SetupStageLink setupBase)
    {
        SurfaceTextureSource textureSource = setupBase.getSurfaceTextureSource();
        textureSource.requestSurfaceTexture(new EventListener<SurfaceTexture>()
        {
            @Override
            public void onEvent(SurfaceTexture surface)
            {
                endStage(surface, setupBase);
            }
        });
    }

    private void endStage(SurfaceTexture surfaceTexture, SetupStageLink setupBase)
    {
        setupBase.setSurfaceTexture(surfaceTexture);
        setupBase.processNextStage();
    }
}
