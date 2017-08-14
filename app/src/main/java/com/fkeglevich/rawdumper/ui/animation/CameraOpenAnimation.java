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

package com.fkeglevich.rawdumper.ui.animation;

import android.view.TextureView;
import android.view.animation.AccelerateDecelerateInterpolator;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Animates the texture view when opening the camera.
 * It's currently using the circular reveal animation.
 *
 * Created by Flávio Keglevich on 14/08/2017.
 */

public class CameraOpenAnimation
{
    private static final int ANIMATION_DURATION = 500;

    public static void animateTextureView(TextureView textureView)
    {
        // get the center for the clipping circle
        int cx = (textureView.getLeft() + textureView.getRight()) / 2;
        int cy = (textureView.getTop() + textureView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, textureView.getWidth() - cx);
        int dy = Math.max(cy, textureView.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        // Android native animator
        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(textureView, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
    }
}
