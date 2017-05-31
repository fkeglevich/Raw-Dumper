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

package com.fkeglevich.rawdumper.raw;

import com.fkeglevich.rawdumper.tiff.TiffTag;

/**
 * Created by Flávio Keglevich on 16/04/2017.
 * TODO: Add a class header comment!
 */

public enum ImageOrientation
{
    TOPLEFT(TiffTag.ORIENTATION_TOPLEFT,    "Top left"),
    TOPRIGHT(TiffTag.ORIENTATION_TOPRIGHT,  "Top right"),
    BOTRIGHT(TiffTag.ORIENTATION_BOTRIGHT,  "Bottom right"),
    BOTLEFT(TiffTag.ORIENTATION_BOTLEFT,    "Bottom left"),
    LEFTTOP(TiffTag.ORIENTATION_LEFTTOP,    "Left top"),
    RIGHTTOP(TiffTag.ORIENTATION_RIGHTTOP,  "Right top"),
    RIGHTBOT(TiffTag.ORIENTATION_RIGHTBOT,  "Right bottom"),
    LEFTBOT(TiffTag.ORIENTATION_LEFTBOT,    "Left bottom");

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
