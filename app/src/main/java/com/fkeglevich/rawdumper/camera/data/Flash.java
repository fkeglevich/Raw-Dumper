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

import com.fkeglevich.rawdumper.raw.data.ExifFlash;

import static android.hardware.Camera.Parameters.FLASH_MODE_AUTO;
import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_ON;
import static android.hardware.Camera.Parameters.FLASH_MODE_RED_EYE;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 11/10/17.
 */

public enum Flash implements ParameterValue
{
    OFF(FLASH_MODE_OFF, ExifFlash.DID_NOT_FIRE),
    ON(FLASH_MODE_ON, ExifFlash.FIRED),
    AUTO(FLASH_MODE_AUTO, ExifFlash.UNKNOWN),
    TORCH(FLASH_MODE_TORCH, ExifFlash.FIRED),
    RED_EYE(FLASH_MODE_RED_EYE, ExifFlash.FIRED);

    private final String parameterValue;
    private final ExifFlash exifFlash;

    Flash(String parameterValue, ExifFlash exifFlash)
    {
        this.parameterValue = parameterValue;
        this.exifFlash = exifFlash;
    }

    @Override
    public String getParameterValue()
    {
        return parameterValue;
    }

    public ExifFlash getExifFlash()
    {
        return exifFlash;
    }
}
