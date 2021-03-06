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

import com.fkeglevich.rawdumper.camera.data.mode.format.DefaultFormatStrategy;
import com.fkeglevich.rawdumper.camera.data.mode.size.BinningJpegStrategy;
import com.fkeglevich.rawdumper.camera.data.mode.size.BinningRawStrategy;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import androidx.annotation.NonNull;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

class LowLightMode extends Mode
{
    LowLightMode(ExtraCameraInfo cameraInfo)
    {
        super("LL", true, getFormatStrategy(cameraInfo), ModeType.LOW_LIGHT);
    }

    @NonNull
    private static DefaultFormatStrategy getFormatStrategy(ExtraCameraInfo cameraInfo)
    {
        return new DefaultFormatStrategy(   new BinningJpegStrategy(cameraInfo),
                                            new BinningJpegStrategy(cameraInfo),
                                            new BinningRawStrategy(cameraInfo));
    }
}
