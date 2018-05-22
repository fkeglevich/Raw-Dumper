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

package com.fkeglevich.rawdumper.camera.data.mode;

import android.support.annotation.NonNull;

import com.fkeglevich.rawdumper.camera.data.mode.format.DefaultFormatStrategy;
import com.fkeglevich.rawdumper.camera.data.mode.size.JpegStrategy;
import com.fkeglevich.rawdumper.camera.data.mode.size.RawStrategy;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class NormalMode extends Mode
{
    NormalMode(ParameterCollection parameterCollection, ExtraCameraInfo cameraInfo)
    {
        super("PRO", false, getFormatStrategy(parameterCollection, cameraInfo), ModeType.NORMAL);
    }

    @NonNull
    private static DefaultFormatStrategy getFormatStrategy(ParameterCollection parameterCollection, ExtraCameraInfo cameraInfo)
    {
        return new DefaultFormatStrategy(   new JpegStrategy(parameterCollection),
                                            new JpegStrategy(parameterCollection),
                                            new RawStrategy(cameraInfo));
    }
}
