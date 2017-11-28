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

package com.fkeglevich.rawdumper.ui;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

import com.fkeglevich.rawdumper.camera.data.SurfaceTextureSource;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * Created by Flávio Keglevich on 27/11/17.
 */

class TextureViewSourceAdapter implements SurfaceTextureSource
{
    private final TextureView textureView;

    TextureViewSourceAdapter(TextureView textureView)
    {
        this.textureView = textureView;
    }

    @Override
    public void requestSurfaceTexture(final EventListener<SurfaceTexture> surfaceTextureListener)
    {
        if (textureView.getSurfaceTexture() != null)
        {
            surfaceTextureListener.onEvent(textureView.getSurfaceTexture());
        }
        else
        {
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
            {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
                {
                    surfaceTextureListener.onEvent(surface);
                    textureView.setSurfaceTextureListener(null);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
                {
                    //no op
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
                {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface)
                {
                    //no op
                }
            });
        }
    }
}
