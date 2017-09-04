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

package com.fkeglevich.rawdumper.controller.dependency;

import com.fkeglevich.rawdumper.async.Locked;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Flávio Keglevich on 02/09/2017.
 * TODO: Add a class header comment!
 */

public class ConcurrentResourceList<T extends Enum<T>>
{
    private final List<Locked<Object>> resources;
    private final int numResources;
    private final BitSet resourcesExistenceMap;
    private final Map<ResourceListener, ListenerData> dependencyMap;
    private final Object innerLock = new Object();

    public ConcurrentResourceList(Class<T> enumType)
    {
        this.numResources = enumType.getEnumConstants().length;
        this.resources = createResourcesArray(numResources);
        this.resourcesExistenceMap = new BitSet(resources.size());
        this.dependencyMap = new HashMap<>();
    }

    public void setResource(T id, Object resource)
    {
        final int index = id.ordinal();
        synchronized (resources.get(index).getLock())
        {
            if (resources.get(index).get() != resource)
            {
                resources.get(index).set(resource);
                synchronized (innerLock)
                {
                    resourcesExistenceMap.set(index);
                    dispatchListeners(index);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <O> Locked<O> getResource(T id)
    {
        return (Locked<O>) resources.get(id.ordinal());
    }

    public void clearResource(T id)
    {
        int index = id.ordinal();
        synchronized (resources.get(index).getLock())
        {
            resources.get(index).set(null);
            synchronized (innerLock)
            {
                resourcesExistenceMap.clear(index);
            }
        }
    }

    public void addListener(ResourceListener listener, T[] neededResources)
    {
        synchronized (innerLock)
        {
            dependencyMap.put(listener, new ListenerData(listener, neededResources, numResources));
        }
    }

    public void removeListener(ResourceListener listener)
    {
        synchronized (innerLock)
        {
            dependencyMap.remove(listener);
        }
    }

    public void removeAllListeners()
    {
        synchronized (innerLock)
        {
            dependencyMap.clear();
        }
    }

    private void dispatchListeners(int index)
    {
        ListenerData listenerData;
        BitSet testingBitSet, listenerResources;

        for (Map.Entry<ResourceListener, ListenerData> entry : dependencyMap.entrySet())
        {
            listenerData = entry.getValue();
            listenerResources = listenerData.getNeededResources();
            if (listenerResources.get(index))
            {
                testingBitSet = (BitSet) resourcesExistenceMap.clone();
                testingBitSet.and(listenerResources);

                if (testingBitSet.equals(listenerResources))
                    listenerData.dispatchListener();
            }
        }
    }

    private static List<Locked<Object>> createResourcesArray(int arrayLength)
    {
        ArrayList<Locked<Object>> result = new ArrayList<>(arrayLength);
        for (int i = 0; i < arrayLength; i++)
            result.add(new Locked<>(null));
        return Collections.unmodifiableList(result);
    }
}
