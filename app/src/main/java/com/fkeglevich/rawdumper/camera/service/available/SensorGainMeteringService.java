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

package com.fkeglevich.rawdumper.camera.service.available;

import com.fkeglevich.rawdumper.camera.service.LogPriority;
import com.fkeglevich.rawdumper.camera.service.LogcatFeatureService;
import com.fkeglevich.rawdumper.camera.service.LogcatMatch;

import androidx.annotation.Keep;

@Keep
public class SensorGainMeteringService extends LogcatFeatureService<Double>
{
    private static final String FINGERPRINT = "total_gain: ";
    private static final LogcatMatch LOGCAT_MATCH = new LogcatMatch("Camera_AtomAIQ", LogPriority.D, FINGERPRINT);

    private static final SensorGainMeteringService instance = new SensorGainMeteringService();

    public static SensorGainMeteringService getInstance()
    {
        return instance;
    }

    private SensorGainMeteringService()
    {
        super(LOGCAT_MATCH);
    }

    @Override
    protected Double parseString(String string)
    {
        /*total_gain: 3.480188  digital gain: 1.000000
            @getManualIso - -1
            */

        String firstPiece = string.substring(string.indexOf(FINGERPRINT));
        String[] split = firstPiece.split(" ");
        return Double.parseDouble(split[1]);
    }
}
