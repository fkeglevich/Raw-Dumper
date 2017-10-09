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

package com.fkeglevich.rawdumper.raw.info;

import android.support.annotation.Keep;

import com.fkeglevich.rawdumper.raw.data.CalibrationIlluminant;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;
import com.fkeglevich.rawdumper.util.MathUtil;

/**
 * Represents a collection of color matrices and calibration
 * illuminants that composes the color profile of the DNG files.
 *
 * Created by Flávio Keglevich on 11/06/2017.
 */

@Keep
@SuppressWarnings("unused")
public class ColorInfo
{
    private float[] colorMatrix1;
    private float[] colorMatrix2;

    private float[] forwardMatrix1;
    private float[] forwardMatrix2;

    private float[] cameraCalibration1;
    private float[] cameraCalibration2;

    private CalibrationIlluminant calibrationIlluminant1;
    private CalibrationIlluminant calibrationIlluminant2;

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_COLORMATRIX1,           colorMatrix1, true);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_COLORMATRIX2,           colorMatrix2, true);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_FORWARDMATRIX1,         forwardMatrix1, true);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_FORWARDMATRIX2,         forwardMatrix2, true);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_CAMERACALIBRATION1,     cameraCalibration1, true);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_CAMERACALIBRATION2,     cameraCalibration2, true);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_CALIBRATIONILLUMINANT1, calibrationIlluminant1);
        safeWriteField(tiffWriter, TiffTag.TIFFTAG_CALIBRATIONILLUMINANT2, calibrationIlluminant2);
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

    public double[] calculateSimpleAsShotNeutral(double x, double y)
    {
        double Y = 1;
        double[] xyz = new double[] {(x*Y)/y, Y, ((1-x-y)*Y)/y};
        double[] dColorMatrix1 = MathUtil.floatArrayToDouble(colorMatrix1);
        return MathUtil.multiply3x3MatrixToVector3(dColorMatrix1, xyz);
    }
}
