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
import android.support.annotation.NonNull;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 12/09/17.
 */

@AnyThread
public class StateMachine<T, D extends Enum<D>>
{
    private State<T, D> currentState = null;
    private boolean transitionHappened = false;

    private final StateData<T, D> stateData;

    public StateMachine(@NonNull State<T, D> initialState, Class<D> dataType)
    {
        stateData = new StateData<>(dataType);
        transitionTo(initialState);
    }

    public synchronized void fireEvent(T event, Object eventData)
    {
        transitionHappened = false;
        currentState.onEvent(event, eventData, this);
        if (!transitionHappened)
            throw new RuntimeException("Invalid state machine transition!");
    }

    public synchronized void transitionTo(@NonNull State<T, D> nextState)
    {
        if (currentState != null)
        {
            currentState.getEventDispatcher().cancelAllListeners();
            currentState.onExit();
        }
        currentState = nextState;
        stateData.updateState(currentState);
        currentState.onEnter(stateData);
        currentState.getEventDispatcher().dispatchEvent(stateData);
        transitionHappened = true;
    }
}