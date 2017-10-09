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

import android.content.Context;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.util.event.EventListener;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

import static com.fkeglevich.rawdumper.R.id.textureView;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 09/10/17.
 */

public class CameraPreviewTexture extends PausingTextureView
{
    private static final int ANIMATION_DURATION = 2000;

    private float scale         = 1f;
    private float translation   = 0f;

    public CameraPreviewTexture(Context context)
    {
        super(context);
    }

    public CameraPreviewTexture(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CameraPreviewTexture(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void setupPreview(TurboCamera camera)
    {
        updateGeometry(camera.getPreviewFeature().getValue());
        camera.getPreviewFeature().getOnChanged().addListener(new EventListener<ParameterChangeEvent<CaptureSize>>()
        {
            @Override
            public void onEvent(ParameterChangeEvent<CaptureSize> eventData)
            {
                updateGeometry(eventData.parameterValue);
            }
        });
    }

    private void updateGeometry(CaptureSize previewSize)
    {
        scale = (float) previewSize.getHeight() / (float) previewSize.getWidth();

        //We currently are not using this yet
        translation = 0f;//(getHeight() / 2f) - (getHeight() * scale / 2f);

        Matrix matrix = new Matrix();
        matrix.postScale(1, scale);
        matrix.postTranslate(0, translation);
        setTransform(matrix);
    }

    public void startOpenCameraAnimation()
    {
        SupportAnimator animator = createAnimator();
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
    }

    @NonNull
    private SupportAnimator createAnimator()
    {
        int cx = (getLeft() + getRight()) / 2;
        int cy = (int) (((getTop() + getBottom()) / 2) * scale + translation);
        return ViewAnimationUtils.createCircularReveal(this, cx, cy, 0, calcFinalRadius(cx, cy));
    }

    private float calcFinalRadius(int cx, int cy)
    {
        int dx = Math.max(cx, getWidth() - cx);
        int dy = Math.max(cy, getHeight() - cy);
        return (float) Math.hypot(dx, dy);
    }
}
