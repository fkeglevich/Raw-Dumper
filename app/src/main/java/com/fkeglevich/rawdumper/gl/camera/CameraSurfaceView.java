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

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CameraPreview;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * Created by flavio on 25/11/17.
 */

public class CameraSurfaceView extends GLSurfaceView implements CameraPreview
{
    private PreviewRenderer previewRenderer = new PreviewRenderer();

    public CameraSurfaceView(Context context)
    {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);
        setRenderer(previewRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void requestSurfaceTexture(EventListener<SurfaceTexture> surfaceTextureListener)
    {
        if (previewRenderer.getSurfaceTexture() != null)
            surfaceTextureListener.onEvent(previewRenderer.getSurfaceTexture());
        else
            previewRenderer.getOnSurfaceTextureAvailable().addListener(surfaceTextureListener);
    }

    @Override
    public void setupCamera(TurboCamera turboCamera)
    {
        Feature<CaptureSize> previewFeature = turboCamera.getPreviewFeature();
        previewFeature.getOnChanged().addListener(new EventListener<ParameterChangeEvent<CaptureSize>>()
        {
            @Override
            public void onEvent(ParameterChangeEvent<CaptureSize> eventData)
            {
                previewRenderer.updatePreviewSize(eventData.parameterValue);
            }
        });
        previewRenderer.updatePreviewSize(previewFeature.getValue());
        previewRenderer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener()
        {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture)
            {
                previewRenderer.startRender();
                requestRender();
            }
        });
        onResume();
        previewRenderer.startRender();
    }

    @Override
    public void pausePreview()
    {
        previewRenderer.stopRender();
        onPause();
    }
}
