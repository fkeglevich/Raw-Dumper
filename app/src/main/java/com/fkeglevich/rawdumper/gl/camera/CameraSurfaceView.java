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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CameraPreview;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * Created by flavio on 25/11/17.
 */

public class CameraSurfaceView extends GLSurfaceView implements CameraPreview
{
    private PreviewRenderer previewRenderer = new PreviewRenderer();
    private ValueAnimator openingAnimation = null;
    private ValueAnimator takePictureAnimation = null;

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
        previewFeature.getOnChanged().addListener(eventData -> previewRenderer.updatePreviewSize(eventData.parameterValue));
        previewRenderer.updatePreviewSize(previewFeature.getValue());
        previewRenderer.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture ->
        {
            if (openingAnimation != null && !openingAnimation.isRunning())
            {
                previewRenderer.startRender();
                requestRender();
            }
        });
        onResume();
        setBackground(null);
        previewRenderer.useRevealProgram();
        previewRenderer.startRender();

        //if (openingAnimation == null)
        //{

            openingAnimation = ValueAnimator.ofFloat(0f, (float) (Math.hypot( (getWidth()) / 2.0, (getHeight() / (getWidth() * 1.0 / getHeight())) / 2.0) ));
            openingAnimation.setDuration(1000);
            openingAnimation.addUpdateListener(animation ->
            {
                previewRenderer.revealRadius = (float) animation.getAnimatedValue();
                previewRenderer.startRender();
                requestRender();
            });
            openingAnimation.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    previewRenderer.useDefaultProgram();
                    openingAnimation.removeListener(this);
                }
            });
        //}
        openingAnimation.start();
    }

    @Override
    public void clearCamera()
    {
        setBackgroundColor(0xFF000000);
        onPause();
    }

    @Override
    public void startOpeningAnimation()
    {   }

    @Override
    public void startClosingAnimation()
    {
        if (openingAnimation != null)
        {
            previewRenderer.useRevealProgram();
            openingAnimation.reverse();
        }
    }

    boolean takePictureAnimationIsEnding = false;

    @Override
    public void pauseUpdating()
    {
        previewRenderer.pauseUpdatingPreview();
        previewRenderer.useTakePictureProgram();

        takePictureAnimation = ValueAnimator.ofFloat(0f, (float) (Math.hypot( (getWidth()) / 2.0, (getHeight() / (getWidth() * 1.0 / getHeight())) / 2.0) ));
        takePictureAnimation.setDuration(400);
        takePictureAnimation.addUpdateListener(animation ->
        {
            previewRenderer.revealRadius = (float) animation.getAnimatedValue();
            previewRenderer.startRender();
            requestRender();
        });
        takePictureAnimation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (takePictureAnimationIsEnding)
                {
                    previewRenderer.useDefaultProgram();
                    takePictureAnimationIsEnding = false;
                }
            }
        });
        takePictureAnimation.reverse();
    }

    @Override
    public void resumeUpdating()
    {
        previewRenderer.resumeUpdatingPreview();
        takePictureAnimationIsEnding = true;
        takePictureAnimation.start();
    }

    @Override
    public void startFocusPeaking()
    {
        previewRenderer.useFocusPeakProgram();
    }

    @Override
    public void stopFocusPeaking()
    {
        previewRenderer.useDefaultProgram();
    }
}
