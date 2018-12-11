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

public class CoarseIntegrationTimeMeteringService extends LogcatFeatureService<Integer>
{
    private static final String FINGERPRINT = "Feedback AEC integration_time[0]: ";
    private static final LogcatMatch LOGCAT_MATCH = new LogcatMatch("Camera_AtomAIQ", LogPriority.D, FINGERPRINT);

    private static final CoarseIntegrationTimeMeteringService instance = new CoarseIntegrationTimeMeteringService();

    public static CoarseIntegrationTimeMeteringService getInstance()
    {
        return instance;
    }

    private CoarseIntegrationTimeMeteringService()
    {
        super(LOGCAT_MATCH);
    }

    @Override
    protected Integer parseString(String string)
    {
        /*Feedback AEC integration_time[0]: 11780
            Feedback AEC integration_time[1]: 0
            */

        String firstPiece = string.substring(string.indexOf(FINGERPRINT));
        String[] split = firstPiece.split("\\s");
        return Integer.parseInt(split[3].trim());
    }
}
