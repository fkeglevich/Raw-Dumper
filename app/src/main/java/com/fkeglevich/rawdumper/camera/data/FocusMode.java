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

package com.fkeglevich.rawdumper.camera.data;

import android.support.annotation.StringRes;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.controller.context.ContextManager;

import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_EDOF;
import static android.hardware.Camera.Parameters.FOCUS_MODE_FIXED;
import static android.hardware.Camera.Parameters.FOCUS_MODE_INFINITY;
import static android.hardware.Camera.Parameters.FOCUS_MODE_MACRO;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 25/10/17.
 */

public enum FocusMode implements ParameterValue, Displayable
{
    AUTO(FOCUS_MODE_AUTO,                               true, R.string.focus_auto),
    CONTINUOUS_PICTURE(FOCUS_MODE_CONTINUOUS_PICTURE,   true, R.string.focus_continuous),
    CONTINUOUS_VIDEO(FOCUS_MODE_CONTINUOUS_VIDEO,       true, R.string.focus_continuous),
    MACRO(FOCUS_MODE_MACRO,                             true, R.string.focus_macro),
    INFINITY(FOCUS_MODE_INFINITY,                       false, R.string.focus_infinity),
    FIXED(FOCUS_MODE_FIXED,                             false, R.string.focus_fixed),
    EDOF(FOCUS_MODE_EDOF,                               false, R.string.focus_edof);

    private final String parameterValue;
    private final boolean canAutoFocus;
    private final int stringValueId;

    FocusMode(String parameterValue, boolean canAutoFocus, @StringRes int stringValueId)
    {
        this.parameterValue = parameterValue;
        this.canAutoFocus = canAutoFocus;
        this.stringValueId = stringValueId;
    }

    @Override
    public String getParameterValue()
    {
        return parameterValue;
    }

    public boolean canAutoFocus()
    {
        return canAutoFocus;
    }

    public String displayValue()
    {
        return ContextManager.getApplicationContext().getResources().getString(stringValueId);
    }
}
