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

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 11/09/17.
 */

public class SimpleDispatcher<T> implements EventDispatcher<T>
{
    private final List<EventListener<T>> listenerList = new ArrayList<>();

    @Override
    public void addListener(@NonNull EventListener<T> listener)
    {
        listenerList.add(listener);
    }

    @Override
    public void removeListener(@NonNull EventListener<T> listener)
    {
        listenerList.remove(listener);
    }

    @Override
    public void removeAllListeners()
    {
        listenerList.clear();
    }

    @Override
    public void dispatchEvent(T data)
    {
        for (EventListener<T> listener : listenerList)
            listener.onEvent(data);
    }
}
