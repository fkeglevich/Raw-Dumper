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

import com.fkeglevich.rawdumper.util.Assertion;

import java.util.EnumMap;
import java.util.Stack;

/**
 * TODO: Add class header
 *
 * Created by Flávio Keglevich on 11/09/17.
 */

public class StateData<T, D extends Enum<D>> implements StateDataReader<D>
{
    private final Stack<StackData> stateStack = new Stack<>();
    private final EnumMap<D, Object> dataMap;

    public StateData(Class<D> dataType)
    {
        dataMap = new EnumMap<>(dataType);
    }

    public synchronized void set(State<T, D> state, D dataId, Object data)
    {
        StackData stackData = new StackData();
        stackData.dataId = dataId;
        stackData.state = state;

        stateStack.push(stackData);
        dataMap.put(dataId, data);
    }

    public synchronized Object get(D dataId)
    {
        Assertion.isTrue(!dataMap.containsKey(dataId), "Invalid state!");
        return dataMap.get(dataId);
    }

    synchronized boolean hasData(D dataId)
    {
        return dataMap.containsKey(dataId);
    }

    synchronized void updateState(State<T, D> state)
    {
        Stack<StackData> removedEntriesStack = new Stack<>();

        while(stateStack.peek().state != state)
            removedEntriesStack.push(stateStack.pop());

        if (stateStack.empty())
            while(!removedEntriesStack.isEmpty())
                stateStack.push(removedEntriesStack.pop());
        else
            while(!removedEntriesStack.isEmpty())
                dataMap.remove(removedEntriesStack.pop().dataId);
    }

    private class StackData
    {
        private State<T, D> state;
        private D dataId;
    }
}
