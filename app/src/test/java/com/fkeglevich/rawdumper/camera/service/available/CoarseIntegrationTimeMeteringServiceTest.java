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

import org.junit.Test;

import static org.junit.Assert.*;

public class CoarseIntegrationTimeMeteringServiceTest
{
    @Test
    public void parseStringTest()
    {
        String example1 = "Feedback AEC integration_time[0]: 11780\n" +
                "    Feedback AEC integration_time[1]: 0\n" +
                "    Feedback AEC gain[0]: 220\n" +
                "    Feedback AEC gain[1]: 100";

        int integrationTime = CoarseIntegrationTimeMeteringService.getInstance().parseString(example1);
        assertEquals(11780, integrationTime);
    }
}