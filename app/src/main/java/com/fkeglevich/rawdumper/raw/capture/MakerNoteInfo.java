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

package com.fkeglevich.rawdumper.raw.capture;

import com.fkeglevich.rawdumper.tiff.ExifTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;
import com.fkeglevich.rawdumper.util.MathUtil;

/**
 * Created by Flávio Keglevich on 27/05/2017.
 * TODO: Add a class header comment!
 */

public class MakerNoteInfo
{
    public Integer iso                  = null;
    public Double exposureTime          = null;
    public Double wbTemperature         = null;
    public float[] wbCoordinatesXY      = null;
    public float[] colorMatrix          = null;
    public byte[] originalMakerNote     = null;

    public MakerNoteInfo()
    {   }

    public void writeTiffExifTags(TiffWriter tiffWriter)
    {
        if (exposureTime != null)
        {
            tiffWriter.setField(ExifTag.EXIFTAG_EXPOSURETIME, exposureTime);
            tiffWriter.setField(ExifTag.EXIFTAG_SHUTTERSPEEDVALUE, apexShutterSpeedValue());
        }

        if (iso != null)
            tiffWriter.setField(ExifTag.EXIFTAG_ISOSPEEDRATINGS, new short[]{iso.shortValue()}, true);

        if (originalMakerNote != null)
            tiffWriter.setField(ExifTag.EXIFTAG_MAKERNOTE, originalMakerNote, true);
    }

    private double apexShutterSpeedValue()
    {
        return -1.0 * MathUtil.log2(exposureTime);
    }
}
