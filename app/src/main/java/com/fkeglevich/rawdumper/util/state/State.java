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

package com.fkeglevich.rawdumper.util.state;

import android.support.annotation.AnyThread;

import com.fkeglevich.rawdumper.util.event.ConcurrentDispatcher;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 11/09/17.
 */

public abstract class State<T, D extends Enum<D>>
{
    private final ConcurrentDispatcher<StateDataReader<D>> eventDispatcher = new ConcurrentDispatcher<>();

    @AnyThread
    public ConcurrentDispatcher<StateDataReader<D>> getEventDispatcher()
    {
        return eventDispatcher;
    }

    protected void onEnter(StateData<T, D> stateData)
    {   }

    protected void onExit()
    {   }

    protected void onEvent(T event, Object data, StateMachine<T, D> stateMachine)
    {   }
}
