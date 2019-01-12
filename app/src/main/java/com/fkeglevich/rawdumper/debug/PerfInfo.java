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

package com.fkeglevich.rawdumper.debug;

import android.util.Log;

import com.fkeglevich.rawdumper.BuildConfig;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * PerfInfo contains useful methods for measuring time cost of certain operations
 *
 * Created by Flávio Keglevich on 02/04/18.
 */

public class PerfInfo
{
    private static class PerfData
    {
        final String tag;
        final long startTime;

        PerfData(String tag, long startTime)
        {
            this.tag = tag;
            this.startTime = startTime;
        }
    }

    private static final String TAG = "PerfInfo";
    private static final boolean FORCE_PERFORMANCE_TRACKING = false;

    private static final Deque<PerfData> perfStack = new ArrayDeque<>();
    private static final double NANO_TIME_TO_MILLISECONDS = 1e6;

    /**
     * Starts measuring performance
     * @param tag   A tag for storing/querying the result
     */
    public synchronized static void start(String tag)
    {
        if (BuildConfig.DEBUG || FORCE_PERFORMANCE_TRACKING)
            perfStack.push(new PerfData(tag, System.nanoTime()));
    }

    /**
     * Ends measuring performance and logs the result
     */
    public synchronized static void end()
    {
        if (BuildConfig.DEBUG || FORCE_PERFORMANCE_TRACKING)
        {
            if (perfStack.isEmpty()) return;

            PerfData data = perfStack.pop();
            double milliseconds = ((System.nanoTime() - data.startTime) / NANO_TIME_TO_MILLISECONDS);
            try
            {
                Log.i(TAG, data.tag + " needed " + milliseconds + " ms");
            }
            catch (RuntimeException re)
            {
                System.out.println(data.tag + " needed " + milliseconds + " ms");
            }
        }
    }
}
