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

package com.fkeglevich.rawdumper.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MinDelayTest
{
    private static final int MINIMUM_DELAY = 300;
    private static final int DELAY_MARGIN = 10;

    @Test
    public void basicTest()
    {
        MinDelay delay = new MinDelay(MINIMUM_DELAY);

        delay.startCounting();
        assertTrue(delay.stopCounting() >= MINIMUM_DELAY);

        delay.startCounting();
        ThreadUtil.simpleDelay((long) (MINIMUM_DELAY / 4.0));
        assertTrue(delay.stopCounting() < MINIMUM_DELAY);

        delay.startCounting();
        ThreadUtil.simpleDelay((long) (MINIMUM_DELAY * 1.5));
        assertTrue(delay.stopCounting() == 0);

        delay.startCounting();
        ThreadUtil.simpleDelay(MINIMUM_DELAY);
        assertTrue(delay.stopCounting() > (MINIMUM_DELAY + DELAY_MARGIN) ||
                delay.stopCounting() < (MINIMUM_DELAY - DELAY_MARGIN));

        delay.startCounting();
        delay.startCounting();
        delay.startCounting();
        delay.stopCounting();
        delay.stopCounting();
        delay.stopCounting();
        assertTrue(delay.stopCounting() >= MINIMUM_DELAY);
    }
}