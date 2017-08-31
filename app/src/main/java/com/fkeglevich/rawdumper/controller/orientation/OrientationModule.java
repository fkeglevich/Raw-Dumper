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

package com.fkeglevich.rawdumper.controller.orientation;

import com.fkeglevich.rawdumper.controller.activity.ActivityReference;
import com.fkeglevich.rawdumper.controller.activity.EventDispatcher;
import com.fkeglevich.rawdumper.controller.activity.EventListener;
import com.fkeglevich.rawdumper.controller.activity.event.LifetimeEvent;
import com.fkeglevich.rawdumper.controller.activity.module.ActivityModule;

/**
 * Created by Flávio Keglevich on 30/08/2017.
 * TODO: Add a class header comment!
 */

public class OrientationModule extends ActivityModule
{
    public OrientationModule(ActivityReference reference)
    {
        super(reference);
        setupOnCreateEvent(reference.getLifetimeEvents());
        setupOnResumeEvent(reference.getLifetimeEvents());
        setupOnPauseEvent(reference.getLifetimeEvents());
    }

    private void setupOnCreateEvent(EventDispatcher<LifetimeEvent> eventDispatcher)
    {
        new EventListener<LifetimeEvent>(LifetimeEvent.ON_CREATE, eventDispatcher)
        {
            @Override
            protected void onEvent(Object optionalData)
            {
                OrientationManager.getInstance().setup(getActivityReference().weaklyGet());
            }
        };
    }

    private void setupOnResumeEvent(EventDispatcher<LifetimeEvent> eventDispatcher)
    {
        new EventListener<LifetimeEvent>(LifetimeEvent.ON_RESUME, eventDispatcher)
        {
            @Override
            protected void onEvent(Object optionalData)
            {
                OrientationManager.getInstance().enable();
            }
        };
    }

    private void setupOnPauseEvent(EventDispatcher<LifetimeEvent> eventDispatcher)
    {
        new EventListener<LifetimeEvent>(LifetimeEvent.ON_PAUSE, eventDispatcher)
        {
            @Override
            protected void onEvent(Object optionalData)
            {
                OrientationManager.getInstance().disable();
            }
        };
    }
}