/*
 * Copyright 2018, Flávio Keglevich
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
import android.util.Log;

import com.fkeglevich.rawdumper.raw.capture.ExifInfo;

import java.util.Arrays;

/**
 * Stores a matrix of possible black levels and chooses the best values given ISO and exposure time configs.
 *
 * Created by Flávio Keglevich on 23/09/18.
 */

@Keep
@SuppressWarnings("unused")
public class BlackLevelInfo
{
    private BlackLevel[][] blackLevelMatrix;

    public float[] computeBlackLevel(ExifInfo exifInfo)
    {
        int iso = exifInfo.getIso().getNumericValue();
        double exposureTime = exifInfo.getExposureTime().getExposureInSeconds();

        BlackLevel[] exposureTimeRow = findExposureTimeRow(exposureTime);

        Log.i("BlackLevelInfo", Arrays.toString(exposureTimeRow));
        Log.i("BlackLevelInfo", "original iso=" + iso + ", exposure time=" + exifInfo.getExposureTime().displayValue());

        return findBestBlackLevel(exposureTimeRow, iso);
    }

    private BlackLevel[] findExposureTimeRow(double exposureTime)
    {
        double leastDifference = Double.POSITIVE_INFINITY;
        int leastDifferenceIndex = -1;

        for (int i = 0; i < blackLevelMatrix.length; i++)
        {
            double difference = Math.abs(blackLevelMatrix[i][0].exposureTime - exposureTime);
            if (difference < leastDifference)
            {
                leastDifference = difference;
                leastDifferenceIndex = i;
            }
        }

        return blackLevelMatrix[leastDifferenceIndex];
    }

    private float[] findBestBlackLevel(BlackLevel[] exposureTimeRow, int iso)
    {
        int leastDifference = Integer.MAX_VALUE;
        int leastDifferenceIndex = -1;

        for (int i = 0; i < exposureTimeRow.length; i++)
        {
            int difference = Math.abs(exposureTimeRow[i].iso - iso);
            if (difference < leastDifference)
            {
                leastDifference = difference;
                leastDifferenceIndex = i;
            }
        }

        Log.i("BlackLevelInfo", "selected black level: " + exposureTimeRow[leastDifferenceIndex].toString());

        return exposureTimeRow[leastDifferenceIndex].values;
    }
}
