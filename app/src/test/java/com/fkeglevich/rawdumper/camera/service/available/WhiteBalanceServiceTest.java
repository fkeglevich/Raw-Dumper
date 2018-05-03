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

public class WhiteBalanceServiceTest
{
    @Test
    public void parseStringTest()
    {
        String example1 = "@setAicParameter: wb integer_bits=1 gr=32768 r=60811 b=50613 gb=32768";
        float[] floats = WhiteBalanceService.getInstance().parseString(example1);
        assertArrayEquals(new float[]{  32768f / 60811f,
                                        1f,
                                        32768f / 50613f}, floats, 0.001f);

        String example2 = "@setAicParameter\n" +
                "    @setAicParameter: wb integer_bits=1 gr=32768 r=61991 b=51904 gb=32768";
        floats = WhiteBalanceService.getInstance().parseString(example2);

        assertArrayEquals(new float[]{32768f / 61991f,
                1f,
                32768f / 51904}, floats, 0.001f);
    }
}