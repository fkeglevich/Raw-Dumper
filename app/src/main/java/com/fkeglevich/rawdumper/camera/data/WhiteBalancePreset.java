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

package com.fkeglevich.rawdumper.camera.data;

import android.support.annotation.StringRes;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.controller.context.ContextManager;

import static android.hardware.Camera.Parameters.WHITE_BALANCE_AUTO;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_DAYLIGHT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_FLUORESCENT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_INCANDESCENT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_SHADE;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_TWILIGHT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public enum WhiteBalancePreset implements ParameterValue, Displayable
{
    AUTO(WHITE_BALANCE_AUTO,                            R.string.wb_auto),
    INCANDESCENT(WHITE_BALANCE_INCANDESCENT,            R.string.wb_incandescent),
    FLUORESCENT(WHITE_BALANCE_FLUORESCENT,              R.string.wb_fluorescent),
    WARM_FLUORESCENT(WHITE_BALANCE_WARM_FLUORESCENT,    R.string.wb_warm_fluorescent),
    DAYLIGHT(WHITE_BALANCE_DAYLIGHT,                    R.string.wb_daylight),
    CLOUDY(WHITE_BALANCE_CLOUDY_DAYLIGHT,               R.string.wb_cloudy),
    TWILIGHT(WHITE_BALANCE_TWILIGHT,                    R.string.wb_twilight),
    SHADE(WHITE_BALANCE_SHADE,                          R.string.wb_shade);

    private final String parameterValue;
    private final int stringValueId;

    WhiteBalancePreset(String parameterValue, @StringRes int stringValueId)
    {
        this.parameterValue = parameterValue;
        this.stringValueId = stringValueId;
    }

    @Override
    public String getParameterValue()
    {
        return parameterValue;
    }

    @Override
    public String displayValue()
    {
        return ContextManager.getApplicationContext().getResources().getString(stringValueId);
    }
}
