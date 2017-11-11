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

import android.util.Log;

import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;
import com.fkeglevich.rawdumper.util.ColorUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class extracts useful information from the makernotes of JPEG/RAW pics.
 *
 * Created by Flávio Keglevich on 01/06/2017.
 */

public class MakerNoteInfoExtractor
{
    private static final String TAG = "MakerNoteInfoExtractor";

    private static final ByteOrder makerNoteByteOrder = ByteOrder.LITTLE_ENDIAN;

    /***** Exposure time and ISO extraction related constants and functions: *****/

    //Regex pattern to match exposure time and ISO
    private static final int LAST_EXPOSUREITEM_DELTA = 48;  //Last exposure item position relative to the end
    private static final int EXPOSURELIST_ITEM_SIZE = 16;   //Size (in bytes) of each item on the list

    /***** Color matrix and white balance related constants and functions: *****/
    private static final String FLOAT_PATTERN = "(\\-)?\\d+(\\.\\d+)?"; //Regex pattern for matching floating point numbers

    //Regex pattern to match color matrix and white balance
    private static final String COLOR_MATRIX_WB_PATTERN =   "grass\\s*=\\s*" + FLOAT_PATTERN + "\\s+" +
                                                            "sky\\s*=\\s*" + FLOAT_PATTERN + "\\s+" +
                                                            "cw\\s*=\\s*" + FLOAT_PATTERN + "\\s+" +
                                                            "sat\\s*=\\s*" + FLOAT_PATTERN + "\\s+" +
                                                            "sat_dyn\\s*=\\s*" + FLOAT_PATTERN +"\\s+" +
                                                            "prefer\\s*=\\s*" + FLOAT_PATTERN +"\\s+" +
                                                            "CCM\\s*=\\s*";

    //Regex pattern to check if a line of the color matrix is valid
    private static final String COLOR_MATRIX_LINE_PATTERN = FLOAT_PATTERN + "[ ]" + FLOAT_PATTERN + "[ ]" + FLOAT_PATTERN + "[\\n]";
    //Regex pattern to check if the encoded white balance is valid
    private static final String WHITE_BALANCE_PATTERN = "\\d+=\\d+[ ]\\d+=\\d+[ ]\\d+=\\d+[ ]\\d+=\\d+[ ]\\d+=\\d+[ ]\\d+=\\d+";
    //Regex pattern to check if color matrix + white balance are valid
    private static final String COLOR_MATRIX_WB_CHECKER_PATTERN = COLOR_MATRIX_LINE_PATTERN + COLOR_MATRIX_LINE_PATTERN + COLOR_MATRIX_LINE_PATTERN + WHITE_BALANCE_PATTERN;
    private static final int NUM_LINES_COLOR_MATRIX_WB = 4; //Number of lines used for storing the color matrix + wb
    private static final double MAX_ILLUMINANT_VALUE = 1024.0;

    /*
    Array containing the correlated color temperatures found in the white balance in their respective order.

    index 0: 2856 K (Tungsten)
    index 1: 7504 K (Shade)
    index 2: 4000 K (Fluorescent)
    index 3: 5503 K (Daylight)
    index 4: 6504 K (Cloudy)
    index 5: 9000 K (Dark Shade)
     */
    private static final double[] TEMPERATURES = new double[] {2856, 7504, 4000, 5503, 6504, 9000};

    private Pattern colorMatrixWBPattern;
    private Pattern colorMatrixWBCheckerPattern;
    private final int baseISO;

    public MakerNoteInfoExtractor(int baseISO)
    {
        colorMatrixWBPattern = Pattern.compile(COLOR_MATRIX_WB_PATTERN);
        colorMatrixWBCheckerPattern = Pattern.compile(COLOR_MATRIX_WB_CHECKER_PATTERN);
        this.baseISO = baseISO;
    }

    public MakerNoteInfo extractFrom(byte[] mknBytes, ColorInfo colorInfo)
    {
        MakerNoteInfo result = new MakerNoteInfo(mknBytes);
        String mknStringBytes;
        try
        {
            mknStringBytes = new String(mknBytes, "ISO-8859-1");
            extractExposureTimeAndIso(result, mknBytes);
            extractColorMatrixAndWB(result, mknStringBytes, colorInfo);
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
        return result;
    }

    private boolean extractExposureTimeAndIso(MakerNoteInfo info, byte[] mknBytes)
    {
        if (mknBytes.length <= LAST_EXPOSUREITEM_DELTA)
        {
            Log.v(TAG, "Could not extract exposure time and ISO from makernotes!");
            return false;
        }

        ByteBuffer wrapped = ByteBuffer.wrap(mknBytes, mknBytes.length - LAST_EXPOSUREITEM_DELTA, EXPOSURELIST_ITEM_SIZE);
        wrapped.order(makerNoteByteOrder);
        info.exposureTime = ShutterSpeed.decodeIntegerExposureTime(wrapped.getInt());
        wrapped.getInt();
        info.iso = Iso.decodeFloatIso(wrapped.getFloat(), baseISO);
        return true;
    }

    private boolean extractColorMatrixAndWB(MakerNoteInfo info, String mknStringBytes, ColorInfo colorInfo)
    {
        int start = findLastOccurrence(colorMatrixWBPattern, mknStringBytes);
        if (start != -1)
        {
            int end = getIndexAfterLines(start, NUM_LINES_COLOR_MATRIX_WB, mknStringBytes);
            if (end != -1)
            {
                String found = mknStringBytes.substring(start, end);
                Matcher matcher = colorMatrixWBCheckerPattern.matcher(found);
                if (matcher.find() && (matcher.start(0) == 0))
                {
                    String[] lines = found.split("\n");
                    if (lines.length == 4)
                    {
                        info.colorMatrix = getColorMatrix(lines[0] + " " + lines[1] + " " + lines[2]);
                        info.wbTemperature = getMeanTemperature(lines[3]);
                        info.wbCoordinatesXY = ColorUtil.getXYFromCCT(info.wbTemperature, colorInfo);
                        return true;
                    }
                }
            }
        }
        Log.v(TAG, "Could not extract color matrix and white balance from makernotes!");
        return false;
    }

    private int findLastOccurrence(Pattern pattern, String mknStringBytes)
    {
        Matcher matcher = pattern.matcher(mknStringBytes);
        int lastMatcherEnd = -1;
        while (matcher.find())
            lastMatcherEnd = matcher.end();

        return lastMatcherEnd;
    }

    private int getIndexAfterLines(int start, int numLines, String str)
    {
        for (int i = 0; i < numLines; i++)
            start = str.indexOf('\n', start + 1);
        return start;
    }

    private float[] getColorMatrix(String colorMatrixStr)
    {
        String[] values = colorMatrixStr.split(" ");
        float[] colorMatrix = new float[values.length];
        for (int i = 0; i < values.length; i++)
            colorMatrix[i] = Float.parseFloat(values[i]);

        return colorMatrix;
    }

    private double getMeanTemperature(String whiteBalanceStr)
    {
        String[] spaceSeparated = whiteBalanceStr.trim().split(" ");
        int[] illuminantIds = new int[spaceSeparated.length];
        int[] illuminantValues = new int[spaceSeparated.length];
        String[] mappingString;
        for (int i = 0; i < spaceSeparated.length; i++)
        {
            mappingString = spaceSeparated[i].split("=");
            illuminantIds[i] = Integer.parseInt(mappingString[0].trim());
            illuminantValues[i] = Integer.parseInt(mappingString[1].trim());
        }

        double meanTemperature = 0;
        for (int i = 0; i < illuminantValues.length; i++)
            meanTemperature += TEMPERATURES[i] * (illuminantValues[illuminantIds[i]] / MAX_ILLUMINANT_VALUE);

        return meanTemperature;
    }
}
