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

package com.fkeglevich.rawdumper.gl.camera;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;

import com.fkeglevich.rawdumper.gl.Texture;
import com.fkeglevich.rawdumper.gl.TextureFactory;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;

/**
 * Created by flavio on 28/11/17.
 */

public class SurfaceTextureManager
{
    final EventDispatcher<SurfaceTexture> onSurfaceTextureAvailable = new SimpleDispatcher<>();

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private Texture texture                 = null;
    private SurfaceTexture surfaceTexture   = null;

    void createSurfaceTexture()
    {
        texture = TextureFactory.createCameraPreviewTexture();

        if (surfaceTexture != null)
            surfaceTexture.release();

        surfaceTexture = new SurfaceTexture(texture.getHandle());
        dispatchSurfaceTextureAvailable();
    }

    void activateSurfaceTexture()
    {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture.getHandle());
    }

    void updateTexImage()
    {
        surfaceTexture.updateTexImage();
    }

    SurfaceTexture getSurfaceTexture()
    {
        return surfaceTexture;
    }

    Texture getTexture()
    {
        return texture;
    }

    private void dispatchSurfaceTextureAvailable()
    {
        uiHandler.post(() -> onSurfaceTextureAvailable.dispatchEvent(surfaceTexture));
    }
}
