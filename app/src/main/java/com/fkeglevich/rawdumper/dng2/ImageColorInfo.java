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

package com.fkeglevich.rawdumper.dng2;

import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.Serializable;

/**
 * Contains the necessary color info of the DNG files.
 * Without it the Raw Editors won't be able to understand the color of your photos :(
 *
 * Created by Flávio Keglevich on 16/04/2017.
 */

public class ImageColorInfo implements Serializable
{
    public float[] colorMatrix1;
    public float[] colorMatrix2;

    public float[] cameraCalibration1;
    public float[] cameraCalibration2;

    public float[] asShotNeutral;

    public CalibrationIlluminant calibrationIlluminant1;
    public CalibrationIlluminant calibrationIlluminant2;

    public ImageColorInfo()
    {   }

    public void setTiffFields(TiffWriter writer)
    {
        safeWriteField(writer, TiffTag.TIFFTAG_COLORMATRIX1,           colorMatrix1, true);
        safeWriteField(writer, TiffTag.TIFFTAG_COLORMATRIX2,           colorMatrix2, true);
        safeWriteField(writer, TiffTag.TIFFTAG_CAMERACALIBRATION1,     cameraCalibration1, true);
        safeWriteField(writer, TiffTag.TIFFTAG_CAMERACALIBRATION2,     cameraCalibration2, true);
        safeWriteField(writer, TiffTag.TIFFTAG_CALIBRATIONILLUMINANT1, calibrationIlluminant1);
        safeWriteField(writer, TiffTag.TIFFTAG_CALIBRATIONILLUMINANT2, calibrationIlluminant2);
        safeWriteField(writer, TiffTag.TIFFTAG_ASSHOTNEUTRAL,          asShotNeutral, true);
    }

    private void safeWriteField(TiffWriter writer, int tag, float[] data, boolean writeLength)
    {
        if (data != null)
            writer.setField(tag, data, writeLength);
    }

    private void safeWriteField(TiffWriter writer, int tag, CalibrationIlluminant illuminant)
    {
        if (illuminant != null)
            writer.setField(tag, illuminant.getExifCode());
    }
}
