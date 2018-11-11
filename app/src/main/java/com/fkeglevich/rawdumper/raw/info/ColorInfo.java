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

import com.fkeglevich.rawdumper.dng.writer.DngNegative;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.data.CalibrationIlluminant;
import com.fkeglevich.rawdumper.util.MathUtil;

import static com.fkeglevich.rawdumper.util.MathUtil.multiply3x3Matrices;

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
    private static final String EMBEDDED_PROFILE_NAME   = "As Shot";
    private static final String CCM_MIXED_PROFILE       = "CCM Mixed Profile";
    private static final String CCM_PROFILE             = "CCM Profile";

    private float[] colorMatrix1;
    private float[] colorMatrix2;

    private float[] forwardMatrix1;
    private float[] forwardMatrix2;

    private float[] cameraCalibration1;
    private float[] cameraCalibration2;

    private double[] tintTemperatureFunction;

    private int[] temperatureRange;

    private CalibrationIlluminant calibrationIlluminant1;
    private CalibrationIlluminant calibrationIlluminant2;

    private float[] toneCurve;

    public void writeInfoTo(DngNegative negative, CaptureInfo captureInfo)
    {
        negative.setCameraCalibration(cameraCalibration1, cameraCalibration2);
        if (captureInfo.makerNoteInfo != null && captureInfo.makerNoteInfo.colorMatrix != null)
        {
            float[] ccm = captureInfo.makerNoteInfo.colorMatrix;

            addAsShotProfile(negative);
            addMixedCCMProfile(negative, ccm);
            addCCMProfile(negative, ccm);
        }
        else
        {
            addAsShotProfile(negative);
        }
    }

    private void addAsShotProfile(DngNegative negative)
    {
        negative.addColorProfile(ColorInfo.EMBEDDED_PROFILE_NAME,
                colorMatrix1, colorMatrix2,
                forwardMatrix1, forwardMatrix2,
                calibrationIlluminant1, calibrationIlluminant2,
                toneCurve);
    }

    private void addMixedCCMProfile(DngNegative negative, float[] ccm)
    {
        negative.addColorProfile(ColorInfo.CCM_MIXED_PROFILE,
                multiply3x3Matrices(colorMatrix1, ccm), multiply3x3Matrices(colorMatrix2, ccm),
                forwardMatrix1, forwardMatrix2,
                calibrationIlluminant1, calibrationIlluminant2,
                toneCurve);
    }

    private void addCCMProfile(DngNegative negative, float[] ccm)
    {
        negative.addColorProfile(ColorInfo.CCM_PROFILE,
                ccm, ccm,
                null, null,
                calibrationIlluminant1, calibrationIlluminant2,
                null);
    }

    public double[] calculateSimpleAsShotNeutral(double x, double y)
    {
        double Y = 1;
        double[] xyz = new double[] {(x*Y)/y, Y, ((1-x-y)*Y)/y};
        double[] dColorMatrix1 = MathUtil.floatArrayToDouble(colorMatrix1);
        return MathUtil.multiply3x3MatrixToVector3(dColorMatrix1, xyz);
    }

    public double[] getTintTemperatureFunction()
    {
        if (tintTemperatureFunction == null)
            return new double[] {0, 0};

        return tintTemperatureFunction;
    }

    public int[] getTemperatureRange()
    {
        return temperatureRange;
    }
}
