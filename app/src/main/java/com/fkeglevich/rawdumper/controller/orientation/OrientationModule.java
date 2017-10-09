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

import android.os.Bundle;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * Created by Flávio Keglevich on 30/08/2017.
 * TODO: Add a class header comment!
 */

public class OrientationModule
{
    private final ActivityReference reference;

    public OrientationModule(ActivityReference reference)
    {
        this.reference = reference;
        setupOnCreateEvent();
        setupOnResumeEvent();
        setupOnPauseEvent();
    }

    private void setupOnCreateEvent()
    {
        reference.onCreate.addListener(new EventListener<Bundle>()
        {
            @Override
            public void onEvent(Bundle eventData)
            {
                OrientationManager.getInstance().setup(reference.weaklyGet());
            }
        });
    }

    private void setupOnResumeEvent()
    {
        reference.onResume.addListener(new EventListener<Nothing>()
        {
            @Override
            public void onEvent(Nothing eventData)
            {
                OrientationManager.getInstance().enable();
            }
        });
    }

    private void setupOnPauseEvent()
    {
        reference.onPause.addListener(new EventListener<Nothing>()
        {
            @Override
            public void onEvent(Nothing eventData)
            {
                OrientationManager.getInstance().disable();
            }
        });
    }
}