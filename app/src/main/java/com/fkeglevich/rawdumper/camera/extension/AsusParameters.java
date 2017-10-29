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

package com.fkeglevich.rawdumper.camera.extension;

import android.util.Range;

import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.data.ManualFocusRange;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterFactory;

/**
 * Created by Flávio Keglevich on 21/08/2017.
 * TODO: Add a class header comment!
 */

public class AsusParameters
{
    public static final String ASUS_XENON_ISO = "Xe_Iso";
    public static final String ASUS_XENON_BRIGHTNESS = "Xe_brightness";
    public static final String ASUS_XENON_EXPOSURE_TIME = "Xe_exposure_time";

    public static final Parameter<ManualFocus> MANUAL_FOCUS = ParameterFactory.create("set_manual_focus", ManualFocus.class);
    public static final Parameter<ManualFocusRange> MANUAL_FOCUS_RANGE = ParameterFactory.createReadOnly("focus_range_values", ManualFocusRange.class);
}
