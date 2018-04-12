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

/**
 * Counts the delay (in ms) between the operations of startCounting() and stopCounting()
 * while enforcing a user-defined minimum delay. This class is useful when a minimum enforced delay
 * is required, while avoiding the cost of unnecessary extra time delays.
 *
 * Created by Flávio Keglevich on 12/04/18.
 */
public class MinDelay
{
    private final int minimumDelay;

    private long latestTime;

    public MinDelay(int minimumDelay)
    {
        this.minimumDelay = minimumDelay;
        this.latestTime = -1;
    }

    public void startCounting()
    {
        latestTime = System.nanoTime();
    }

    public int stopCounting()
    {
        int delay = minimumDelay - calculateTimeDelta();
        return delay > 0 ? delay : 0;
    }

    private int calculateTimeDelta()
    {
        long timeDelta = System.nanoTime() - latestTime;
        return (int)(timeDelta / 1000000L);
    }
}
