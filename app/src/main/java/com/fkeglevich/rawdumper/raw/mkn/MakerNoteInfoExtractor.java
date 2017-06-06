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

package com.fkeglevich.rawdumper.raw.mkn;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Flávio Keglevich on 01/06/2017.
 * TODO: Add a class header comment!
 */

public class MakerNoteInfoExtractor
{
    private static final String TAG = "MakerNoteInfoExtractor";

    private static final ByteOrder makerNoteByteOrder = ByteOrder.LITTLE_ENDIAN;

    //Exposure time and ISO extraction related constants and functions:
    private static final String EXPOSURE_PATTERN = "first\\s+fail:\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+\\s+[(]\\s*\\d+\\s*[/]\\s*\\d+\\s*[)]\\s*[(]\\s*\\d+\\s*[)]\\s+\\d+\\s+[:]\\s+\\d+";
    private static final int EXPOSURELIST_OFFSET = 46;      //Offset between the pattern and the exposure list
    private static final int EXPOSURELIST_ITEM_SIZE = 16;   //Size (in bytes) of each item on the list
    private static final int EXPOSURELIST_NUM_ITEMS = 30;   //Number of items on the list
    private static double decodeExposureTime(int encoded) { return encoded / 1000000.0; }
    private static int decodeISO(float encoded) { return (int)Math.round(50.0 * encoded); }

    //Color matrix and white balance related constants and functions:
    private static final String COLOR_MATRIX_WB_PATTERN = "grass\\s*=\\s*\\d+\\s+sky\\s*=\\s*\\d+\\s+cw\\s*=\\s*\\d+\\s+sat\\s*=\\s*\\d+\\s+sat_dyn\\s*=\\s*\\d+\\s+prefer\\s*=\\s*\\d+\\s+CCM\\s*=\\s*";
    private static final String COLOR_MATRIX_LINE_PATTERN = "(\\-)?\\d+(\\.\\d+)?[ ](\\-)?\\d+(\\.\\d+)?[ ](\\-)?\\d+(\\.\\d+)?[\\n]";
    private static final String WHITE_BALANCE_PATTERN = "\\d+=\\d+[ ]\\d+=\\d+[ ]\\d+=\\d+[ ]\\d+=\\d+[ ]\\d+=\\d+[ ]\\d+=\\d+";
    private static final String COLOR_MATRIX_WB_CHECKER_PATTERN = COLOR_MATRIX_LINE_PATTERN + COLOR_MATRIX_LINE_PATTERN + COLOR_MATRIX_LINE_PATTERN + WHITE_BALANCE_PATTERN;
    private static final int NUM_LINES_COLOR_MATRIX_WB = 4;
    //\d+(\.\d+)?

    private Pattern exposurePattern;
    private Pattern colorMatrixWBPattern;
    private Pattern colorMatrixWBCheckerPattern;

    public MakerNoteInfoExtractor()
    {
        exposurePattern = Pattern.compile(EXPOSURE_PATTERN);
        colorMatrixWBPattern = Pattern.compile(COLOR_MATRIX_WB_PATTERN);
        colorMatrixWBCheckerPattern = Pattern.compile(COLOR_MATRIX_WB_CHECKER_PATTERN);
    }

    private boolean extractExposureTimeAndIso(MakerNoteInfo info, byte[] mknBytes, String mknStringBytes)
    {
        /*
        In the middle of the makernotes there's a exposure list containing the exposure info
        (exposure time, ISO and other currently unknown data) of the last 30 frames preceding
        the photo capture.

        This code uses a regex pattern to find the right location of the exposure list and,
        when the list is found, get the latest item because it has the exposure info from the photo.
         */

        int lastIndex = findLastOccurrence(exposurePattern, mknStringBytes);
        if (lastIndex != -1)
        {
            //calculate the indices of the first and last items of the exposure list
            int firstExposureIndex = lastIndex + EXPOSURELIST_OFFSET;
            int lastExposureIndex = EXPOSURELIST_ITEM_SIZE * (EXPOSURELIST_NUM_ITEMS - 1);

            //Creates a ByteBuffer to get the data and write it on MakerNoteInfo
            ByteBuffer wrapped = ByteBuffer.wrap(mknBytes, lastExposureIndex, EXPOSURELIST_ITEM_SIZE);
            wrapped.order(makerNoteByteOrder);
            info.exposureTime = decodeExposureTime(wrapped.getInt());
            wrapped.getInt();
            info.iso = decodeISO(wrapped.getFloat());
            return true;
        }
        Log.v(TAG, "Could not extract exposure time and ISO from makernotes!");
        return false;
    }

    public boolean extractColorMatrixAndWB(MakerNoteInfo info, byte[] mknBytes, String mknStringBytes)
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
                    //matcher.start()


                    //found.trim()
                    //int

                    //mknStringBytes.substring()
                    //Validate string
                    //mknStringBytes.
                    return true;
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

    /*private float[] extractColorMatrix(int start, String string)
    {
        string.in
    }*/
}
