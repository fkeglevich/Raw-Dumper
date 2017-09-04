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

package com.fkeglevich.rawdumper.controller.dependency;

import android.os.Handler;
import android.os.Looper;

import java.util.BitSet;

/**
 * Created by Flávio Keglevich on 02/09/2017.
 * TODO: Add a class header comment!
 */

class ListenerData
{
    private final BitSet neededResources;
    private final Handler listenerHandler;
    private final Runnable listenerRunnable;

    <T extends Enum<T>> ListenerData(final ResourceListener listener, T[] neededResources, int numResources)
    {
        this.neededResources = bitSetFromEnumArray(neededResources, numResources);
        this.listenerHandler = createHandler();
        this.listenerRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                listener.onResourcesAvailable();
            }
        };
    }

    BitSet getNeededResources()
    {
        return neededResources;
    }

    void dispatchListener()
    {
        listenerHandler.post(listenerRunnable);
    }

    private static <T extends Enum<T>> BitSet bitSetFromEnumArray(T[] flags, int numResources)
    {
        BitSet result = new BitSet(numResources);
        for (T flag : flags)
            result.set(flag.ordinal());

        return result;
    }

    private static Handler createHandler()
    {
        Looper looper = Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper();
        return new Handler(looper);
    }
}
