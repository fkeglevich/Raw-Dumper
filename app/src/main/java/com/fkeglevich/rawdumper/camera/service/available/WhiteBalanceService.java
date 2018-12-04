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

public class WhiteBalanceService extends LogcatFeatureService<float[]>
{
    private static final String FINGERPRINT = "@setAicParameter: wb int";
    private static final LogcatMatch LOGCAT_MATCH = new LogcatMatch("Camera_ISP", LogPriority.D, FINGERPRINT);

    private static final WhiteBalanceService instance = new WhiteBalanceService();

    public static WhiteBalanceService getInstance()
    {
        return instance;
    }

    private WhiteBalanceService()
    {
        super(LOGCAT_MATCH);
    }

    @Override
    protected float[] parseString(String string)
    {
        String firstPiece = string.substring(string.indexOf(FINGERPRINT));
        //@setAicParameter: wb integer_bits=1 gr=32768 r=60811 b=50613 gb=32768

        String[] split = firstPiece.split("\\s");

        String gr = split[3].split("=")[1];
        String r = split[4].split("=")[1];
        String b = split[5].split("=")[1];
        String gb = split[6].split("=")[1];

        float rGain = (float) (Double.parseDouble(gr) / Double.parseDouble(r));
        float bGain = (float) (Double.parseDouble(gb) / Double.parseDouble(b));

        return new float[] {rGain, 1f, bGain};
    }
}
