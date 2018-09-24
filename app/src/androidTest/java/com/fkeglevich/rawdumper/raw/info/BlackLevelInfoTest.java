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

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BlackLevelInfoTest
{
    @Test
    public void testAllBlackLevelInfos() throws DeviceLoadException
    {
        DeviceInfoLoader deviceInfoLoader = new DeviceInfoLoader();
        List<DeviceInfo> deviceInfos = deviceInfoLoader.loadAllDeviceInfos();
        for (DeviceInfo deviceInfo : deviceInfos)
        {
            for (ExtraCameraInfo cameraInfo : deviceInfo.getCameras())
            {
                BlackLevelInfo blackLevelInfo = cameraInfo.getSensor().blackLevelInfo;
                assertEquals(4, blackLevelInfo.defaultValues.length);
                if (blackLevelInfo.blackLevelMatrix != null)
                {
                    assertNotEquals(0, blackLevelInfo.blackLevelMatrix.length);
                    testBlackLevelMatrix(blackLevelInfo.blackLevelMatrix);
                }
            }
        }
        assertTrue(deviceInfos.size() > 0);
    }

    private void testBlackLevelMatrix(BlackLevel[][] matrix)
    {
        //The exposure time of each row should be the same
        for (BlackLevel[] row : matrix)
        {
            double exposureTime = row[0].exposureTime;
            for (BlackLevel blackLevel : row)
            {
                assertEquals(exposureTime, blackLevel.exposureTime, 0.001);
            }
        }

        //The ISO of each column should be the same
        /*for (int row = 0; row < matrix.length; row ++)
        {
            int iso = matrix[row][0].iso;
            for (int col = 0; col < matrix[row].length; col ++)
            {
                assertEquals(iso, matrix[row][col].iso);
            }
        }*/
    }
}
