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

package com.fkeglevich.rawdumper.camera.init;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.context.CameraContext;
import com.fkeglevich.rawdumper.camera.context.CameraContextBuilder;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * Created by Flávio Keglevich on 15/09/2017.
 * TODO: Add a class header comment!
 */

public class SurfaceTextureStage extends CameraInitStage
{
    private final TextureView textureView;

    SurfaceTextureStage(TextureView textureView, CameraContextBuilder builder,
                        CameraInitStage nextStage)
    {
        super(builder, nextStage);
        this.textureView = textureView;
    }

    @Override
    public void executeStage(final AsyncOperation<CameraContext> callback,
                             final AsyncOperation<MessageException> exception)
    {
        if (textureView.getSurfaceTexture() != null)
        {
            executeStageImpl(textureView.getSurfaceTexture(), callback, exception);
        }
        else
        {
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
            {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
                {
                    executeStageImpl(surface, callback, exception);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
                {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
                {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface)
                {

                }
            });
        }
    }

    private void executeStageImpl(SurfaceTexture surface, AsyncOperation<CameraContext> callback,
                                  AsyncOperation<MessageException> exception)
    {
        builder.buildSurfaceTexture(surface);
        nextStage.executeStage(callback, exception);
    }
}
