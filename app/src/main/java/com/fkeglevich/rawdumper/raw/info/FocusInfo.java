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

package com.fkeglevich.rawdumper.raw.info;

import androidx.annotation.Keep;

/**
 * Represents a collection of color matrices and calibration
 * illuminants that composes the color profile of the DNG files.
 *
 * Created by Flávio Keglevich on 10/12/2018.
 */

@Keep
@SuppressWarnings("unused")
public class FocusInfo
{
    private static final int DEFAULT_FLASH_FOCUS_DELAY = 100;

    private boolean hasFlashFocus;
    private int flashFocusHighIso;
    private double flashFocusSlowShutterSpeed;

    public boolean hasFlashFocus()
    {
        return hasFlashFocus;
    }

    public int getFlashFocusHighIso()
    {
        return flashFocusHighIso;
    }

    public double getFlashFocusSlowShutterSpeed()
    {
        return flashFocusSlowShutterSpeed;
    }

    public int getFlashFocusExposureDelay()
    {
        return DEFAULT_FLASH_FOCUS_DELAY;
    }
}
