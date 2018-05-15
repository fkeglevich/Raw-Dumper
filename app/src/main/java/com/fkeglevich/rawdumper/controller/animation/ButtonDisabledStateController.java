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

public class ButtonDisabledStateController
{
    private static final float DISABLED_ALPHA       = 0.25f;
    private static final float ENABLED_ALPHA        = 1f;
    private static final long ANIM_DURATION_MILLIS  = 150L;

    private final View button;
    private final AlphaAnimation enableAnimation;
    private final AlphaAnimation disableAnimation;

    private boolean buttonEnabled;

    public ButtonDisabledStateController(View button, boolean initEnabled)
    {
        this.button = button;
        this.enableAnimation = new AlphaAnimation(DISABLED_ALPHA, ENABLED_ALPHA);
        setupAnimation(enableAnimation);

        this.disableAnimation = new AlphaAnimation(ENABLED_ALPHA, DISABLED_ALPHA);
        setupAnimation(disableAnimation);

        buttonEnabled = initEnabled;
        if (isButtonEnabled())
        {
            button.setAlpha(ENABLED_ALPHA);
            button.setClickable(true);
        }
        else
        {
            button.setAlpha(DISABLED_ALPHA);
            button.setClickable(false);
        }
    }

    public void enableAnimated()
    {
        if (!isButtonEnabled())
        {
            startAlphaAnimation(enableAnimation);
            button.setClickable(true);
        }
        buttonEnabled = true;
    }

    public void disableAnimated()
    {
        if (isButtonEnabled())
        {
            startAlphaAnimation(disableAnimation);
            button.setClickable(false);
        }
        buttonEnabled = false;

    }

    public void enable()
    {
        button.setAlpha(ENABLED_ALPHA);
        button.setClickable(true);
        buttonEnabled = true;
    }

    public void disable()
    {
        button.setAlpha(DISABLED_ALPHA);
        button.setClickable(false);
        buttonEnabled = false;
    }

    private void setupAnimation(AlphaAnimation animation)
    {
        animation.setDuration(ANIM_DURATION_MILLIS);
        animation.setFillAfter(true);
    }

    private void startAlphaAnimation(AlphaAnimation enableAnimation)
    {
        button.setAlpha(ENABLED_ALPHA);
        button.startAnimation(enableAnimation);
    }

    public boolean isButtonEnabled()
    {
        return buttonEnabled;
    }
}
