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

package com.fkeglevich.rawdumper.util.event;

import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.async.operation.AsyncOperationPoster;
import com.fkeglevich.rawdumper.util.Assertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: Add class header
 *
 * Created by Flávio Keglevich on 11/09/17.
 */

@AnyThread
public class ConcurrentDispatcher<T> implements EventDispatcher<T>
{
    private final List<EventData> listenerList = new ArrayList<>();
    private final HashMap<Looper, AsyncOperationPoster> posterMap = new HashMap<>();

    @Override
    public synchronized void addListener(@NonNull EventListener<T> listener)
    {
        Assertion.isNotNull(Looper.myLooper());

        EventData eventData = new EventData();
        eventData.listener = listener;
        eventData.poster = getPoster(Looper.myLooper());
        listenerList.add(eventData);
    }

    @Override
    public synchronized void removeListener(@NonNull EventListener<T> listener)
    {
        EventData toBeRemoved = null;
        for (EventData eventData : listenerList)
        {
            if (eventData.listener == listener)
            {
                toBeRemoved = eventData;
                break;
            }
        }

        if (toBeRemoved != null)
            listenerList.remove(toBeRemoved);
    }

    @Override
    public synchronized void removeAllListeners()
    {
        listenerList.clear();
    }

    public synchronized void cancelAllListeners()
    {
        for (AsyncOperationPoster poster : posterMap.values())
            poster.removeAllPendingOperations();
    }

    @Override
    public synchronized void dispatchEvent(final T data)
    {
        for (final EventData eventData : listenerList)
            eventData.poster.enqueueOperation(new AsyncOperation<T>()
            {
                @Override
                protected void execute(T argument)
                {
                    eventData.listener.onEvent(argument);
                }
            }, data);
    }

    private AsyncOperationPoster getPoster(Looper looper)
    {
        if (!posterMap.containsKey(looper))
            posterMap.put(looper, new AsyncOperationPoster(looper));

        return posterMap.get(looper);
    }

    private class EventData
    {
        private EventListener<T> listener;
        private AsyncOperationPoster poster;
    }
}
