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

import com.fkeglevich.rawdumper.raw.data.CalibrationIlluminant;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;
import com.fkeglevich.rawdumper.util.MathUtil;

/**
 * Created by Flávio Keglevich on 11/06/2017.
 * TODO: Add a class header comment!
 */

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

    private ColorInfo()
    {   }

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

    public static ColorInfo getToshibaColorInfo()
    {
        ColorInfo result = new ColorInfo();
        result.colorMatrix1 = new float[]{1.1741f, -0.2862f, -0.0448f, -0.1778f, 0.9912f, 0.2184f, 0.0223f, 0.2117f, 0.532f};
        result.calibrationIlluminant1 = CalibrationIlluminant.D55;
        result.cameraCalibration1 = new float[]{1, 0, 0, 0, 1, 0, 0, 0, 1};
        return result;
    }

    public static ColorInfo getOVColorInfo()
    {
        ColorInfo result = new ColorInfo();
        result.colorMatrix1 = new float[]{0.9678f, -0.1981f, -0.0469f, -0.1948f, 0.9686f, 0.2657f, -0.0133f, 0.2255f, 0.6228f};
        result.calibrationIlluminant1 = CalibrationIlluminant.D55;
        result.cameraCalibration1 = new float[]{1, 0, 0, 0, 1, 0, 0, 0, 1};
        return result;
    }
}
