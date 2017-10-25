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

package com.fkeglevich.rawdumper.controller.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 25/10/17.
 */

public class ButtonAnimationController
{
    private final View button;
    private final AlphaAnimation enableAnimation;
    private final AlphaAnimation disableAnimation;

    public ButtonAnimationController(View button)
    {
        this.button = button;
        this.enableAnimation = new AlphaAnimation(0.5f, 1f);
        setupAnimation(enableAnimation);

        this.disableAnimation = new AlphaAnimation(1f, 0.5f);
        setupAnimation(disableAnimation);
    }

    public void startEnableAnimation()
    {
        startAlphaAnimation(enableAnimation);
    }

    public void startDisableAnimation()
    {
        startAlphaAnimation(disableAnimation);
    }

    private void setupAnimation(AlphaAnimation animation)
    {
        animation.setDuration(150L);
        animation.setFillAfter(true);
    }

    private void startAlphaAnimation(AlphaAnimation enableAnimation)
    {
        button.setAlpha(1f);
        button.startAnimation(enableAnimation);
    }
}
