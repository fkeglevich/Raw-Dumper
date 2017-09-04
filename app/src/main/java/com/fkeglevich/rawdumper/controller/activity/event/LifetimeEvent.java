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

package com.fkeglevich.rawdumper.controller.activity.event;

/**
 * Created by Flávio Keglevich on 29/08/2017.
 * TODO: Add a class header comment!
 */

public enum LifetimeEvent implements IEvent
{
    ON_CREATE(Void.class),
    ON_POST_CREATE(Void.class),
    ON_DESTROY(Void.class),
    ON_PAUSE(Void.class),
    ON_RESUME(Void.class);

    private final Class<?> eventDataClass;

    LifetimeEvent(Class<?> eventDataClass)
    {
        this.eventDataClass = eventDataClass;
    }

    @Override
    public Class<?> getEventDataClass()
    {
        return eventDataClass;
    }
}