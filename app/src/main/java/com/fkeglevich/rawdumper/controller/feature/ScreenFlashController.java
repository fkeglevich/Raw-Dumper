/*
 * Copyright 2018, Flávio Keglevich
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

package com.fkeglevich.rawdumper.controller.feature;

import android.view.View;
import android.view.ViewGroup;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.controller.activity.BrightnessController;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 13/05/18.
 */
class ScreenFlashController
{
    private final BrightnessController brightnessController;
    private final View brightnessView;
    private final Fade fadeTransition = new Fade();

    ScreenFlashController(ActivityReference reference)
    {
        brightnessController = new BrightnessController(reference);
        brightnessView = reference.findViewById(R.id.screenFlash);
    }

    void startScreenFlash()
    {
        brightnessController.overrideBrightness(1f);
        TransitionManager.beginDelayedTransition((ViewGroup) brightnessView.getRootView(), fadeTransition);
        brightnessView.setVisibility(View.VISIBLE);
    }

    void stopScreenFlash()
    {
        brightnessController.disableBrightnessOverride();
        TransitionManager.beginDelayedTransition((ViewGroup) brightnessView.getRootView(), fadeTransition);
        brightnessView.setVisibility(View.INVISIBLE);
    }
}
