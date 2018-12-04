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

package com.fkeglevich.rawdumper.raw.data;

import androidx.annotation.Keep;

/**
 * Enumerates the possible image orientations for the DNG files.
 *
 * Created by Flávio Keglevich on 16/04/2017.
 */

@Keep
public enum ImageOrientation
{
    TOPLEFT (1, "Top left"),
    TOPRIGHT(2, "Top right"),
    BOTRIGHT(3, "Bottom right"),
    BOTLEFT (4, "Bottom left"),
    LEFTTOP (5, "Left top"),
    RIGHTTOP(6, "Right top"),
    RIGHTBOT(7, "Right bottom"),
    LEFTBOT (8, "Left bottom");

    private final int exifCode;
    private final String stringCode;

    ImageOrientation(int exifCode, String stringCode)
    {
        this.exifCode = exifCode;
        this.stringCode = stringCode;
    }

    public int getExifCode()
    {
        return exifCode;
    }

    public String getStringCode()
    {
        return stringCode;
    }
}
