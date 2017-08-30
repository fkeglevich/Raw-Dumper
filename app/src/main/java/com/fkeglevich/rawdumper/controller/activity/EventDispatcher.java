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

package com.fkeglevich.rawdumper.controller.activity;

import com.fkeglevich.rawdumper.controller.activity.event.IEvent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Flávio Keglevich on 29/08/2017.
 * TODO: Add a class header comment!
 */

public class EventDispatcher<T extends Enum<T> & IEvent>
{
    private EnumMap<T, List<EventListener<T>>> innerMap;

    public EventDispatcher(Class<T> eventType)
    {
        innerMap = new EnumMap<>(eventType);
        for (T value : eventType.getEnumConstants())
            innerMap.put(value, new ArrayList<EventListener<T>>());
    }

    void addListener(EventListener<T> listener)
    {
        innerMap.get(listener.getEventType()).add(listener);
    }

    void dispatchEvent(T eventType, Object optionalData)
    {
        for (EventListener<T> listener : innerMap.get(eventType))
            listener.onEvent(optionalData);
    }

    void dispatchEvent(T eventType)
    {
        dispatchEvent(eventType, null);
    }
}
