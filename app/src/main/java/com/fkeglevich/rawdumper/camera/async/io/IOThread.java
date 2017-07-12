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

package com.fkeglevich.rawdumper.camera.async.io;

import android.content.Context;
import android.os.HandlerThread;

/**
 * Created by Flávio Keglevich on 25/06/2017.
 * TODO: Add a class header comment!
 */

public class IOThread
{
    private static final String THREAD_NAME = "IOThread";

    private static IOThread instance = null;

    public static void initialize(Context applicationContext)
    {
        instance = new IOThread(applicationContext);
    }

    static IOThread getInstance()
    {
        return instance;
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

    public void quitSafely()
    {
        thread.quitSafely();
        instance = null;
    }
}
