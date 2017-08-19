/*
 * Copyright 2017, Flávio Keglevich
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

package com.fkeglevich.rawdumper.io.async;

import android.content.Context;
import android.os.HandlerThread;

import com.fkeglevich.rawdumper.util.ThreadUtil;

/**
 * Created by Flávio Keglevich on 25/06/2017.
 * TODO: Add a class header comment!
 */

public class IOThread
{
    private static final String THREAD_NAME = "IOThread";

    private static IOThread instance = null;
    private static volatile boolean initialized = false;

    public static void initialize(Context applicationContext)
    {
        if (initialized)
            throw new RuntimeException("The IOThread can only be initialized once!");

        if (!ThreadUtil.currentThreadIsMainThread())
            throw new RuntimeException("The IOThread can only be initialized from the Main Thread!");

        instance = new IOThread(applicationContext);
        initialized = true;
    }

    static synchronized IOAccess getAccess()
    {
        if (instance == null)
            return null;

        return instance.ioAccess;
    }

    private HandlerThread thread;
    private IOAccess ioAccess;

    private IOThread(Context applicationContext)
    {
        thread = new HandlerThread(THREAD_NAME);
        thread.start();
        ioAccess = new IOAccess(applicationContext, thread.getLooper());
    }
}
