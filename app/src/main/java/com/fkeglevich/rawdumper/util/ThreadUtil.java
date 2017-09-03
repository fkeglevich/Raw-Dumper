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

package com.fkeglevich.rawdumper.util;

import android.os.Looper;

/**
 * Created by Flávio Keglevich on 29/07/2017.
 * TODO: Add a class header comment!
 */

public class ThreadUtil
{
    public static boolean currentThreadIsMainThread()
    {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void checkIfSynchronized(Object lock)
    {
        if (!Thread.holdsLock(lock))
            throw new RuntimeException("This method should be called inside a proper synchronized block!");
    }

    public static void checkIfNotSynchronized(Object lock)
    {
        if (Thread.holdsLock(lock))
            throw new RuntimeException("This synchronized block has a risk of causing a dead lock!");
    }
}
