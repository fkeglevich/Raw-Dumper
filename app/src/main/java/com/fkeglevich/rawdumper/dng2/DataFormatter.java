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

import android.annotation.SuppressLint;

import com.fkeglevich.rawdumper.raw.BayerPattern;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Flávio Keglevich on 16/04/2017.
 * TODO: Add a class header comment!
 */

class DataFormatter
{
    // Formatted Bayer Patterns
    private static final byte RED   = 0;
    private static final byte GREEN = 1;
    private static final byte BLUE  = 2;
    private static final byte[] FORMATTED_BGGR = new byte[] {BLUE,  GREEN,  GREEN,  RED};
    private static final byte[] FORMATTED_RGGB = new byte[] {RED,   GREEN,  GREEN,  BLUE};
    private static final byte[] FORMATTED_GBRG = new byte[] {GREEN, BLUE,   RED,    GREEN};
    private static final byte[] FORMATTED_GRBG = new byte[] {GREEN, RED,    BLUE,   GREEN};

    static short[] DEFAULT_CFA_REPEAT_PATTERN_DIM = new short[] {2, 2};

    static byte[] formatBayerPattern(BayerPattern pattern)
    {
        switch (pattern)
        {
            case BGGR:          return FORMATTED_BGGR;
            case RGGB:          return FORMATTED_RGGB;
            case GBRG:          return FORMATTED_GBRG;
            case GRBG: default: return FORMATTED_GRBG;
        }
    }

    // White and Black level formatting
    static long[] formatWhiteLevel(int whiteLevel)
    {
        return new long[] { whiteLevel };
    }

    static short[] DEFAULT_BLACK_LEVEL_REPEAT_DIM = new short[] {2, 2};

    static float[] formatBlackLevel(int[] blackLevel)
    {
        float[] result = new float[blackLevel.length];
        for (int i = 0; i < blackLevel.length; i++)
            result[i] = (float)blackLevel[i];

        return result;
    }

    // DateTime formatting
    private static final String DATE_PATTERN = "yyyy:MM:dd HH:mm:ss";

    @SuppressLint("SimpleDateFormat")
    static String formatCalendar(Calendar calendar)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }
}
