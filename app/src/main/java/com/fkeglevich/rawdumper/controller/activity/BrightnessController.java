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

package com.fkeglevich.rawdumper.controller.activity;

import android.view.Window;
import android.view.WindowManager;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.util.MathUtil;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 13/05/18.
 */
public class BrightnessController
{
    private final ActivityReference activityReference;

    public BrightnessController(ActivityReference activityReference)
    {
        this.activityReference = activityReference;
    }

    public void overrideBrightness(float value)
    {
        Window window = activityReference.weaklyGet().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = MathUtil.clamp(value, 0f, 1f);
        window.setAttributes(layoutParams);
    }

    public void disableBrightnessOverride()
    {
        Window window = activityReference.weaklyGet().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        window.setAttributes(layoutParams);
    }
}
