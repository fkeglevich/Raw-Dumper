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

package com.fkeglevich.rawdumper.util.event;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 30/04/18.
 */
public class AsyncEventDispatcher<T> implements EventDispatcher<T>
{
    private final Map<Looper, Handler> looperPosterMap = new HashMap<>();
    private final Map<EventListener<T>, Looper> listenerLooperMap = new HashMap<>();

    @Override
    public synchronized void addListener(@NonNull EventListener<T> listener)
    {
        if (listenerLooperMap.containsKey(listener)) return;

        Looper looper = getCurrentThreadLooper();
        Assert.assertNotNull(looper);

        if (!looperPosterMap.containsKey(looper))
            looperPosterMap.put(looper, new Handler(looper));

        listenerLooperMap.put(listener, looper);
    }

    @Override
    public synchronized void removeListener(@NonNull EventListener<T> listener)
    {
        if (!listenerLooperMap.containsKey(listener)) return;

        Looper looper = listenerLooperMap.get(listener);
        listenerLooperMap.remove(listener);
        if (!listenerLooperMap.containsValue(looper))
            looperPosterMap.remove(looper);
    }

    @Override
    public synchronized boolean hasListener(@NonNull EventListener<T> listener)
    {
        return listenerLooperMap.containsKey(listener);
    }

    @Override
    public synchronized void removeAllListeners()
    {
        listenerLooperMap.clear();
        looperPosterMap.clear();
    }

    @Override
    public synchronized void dispatchEvent(T data)
    {
        Set<EventListener<T>> eventListeners = listenerLooperMap.keySet();
        for (EventListener<T> listener : eventListeners)
        {
            getHandlerOfListener(listener).post(() ->
            {
                if (hasListener(listener)) listener.onEvent(data);
            });
        }
    }

    private Handler getHandlerOfListener(@NonNull EventListener<T> listener)
    {
        return looperPosterMap.get(listenerLooperMap.get(listener));
    }

    private Looper getCurrentThreadLooper()
    {
        return Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper();
    }
}
