/*
 * Copyright 2018, Flávio Keglevich
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

import androidx.annotation.NonNull;

public class NullEventDispatcher<T> implements EventDispatcher<T>
{
    @Override
    public void addListener(@NonNull EventListener<T> listener)
    {
        //no op
    }

    @Override
    public void removeListener(@NonNull EventListener<T> listener)
    {
        //no op
    }

    @Override
    public boolean hasListener(@NonNull EventListener<T> listener)
    {
        return false; //no op
    }

    @Override
    public void removeAllListeners()
    {
        //no op
    }

    @Override
    public void dispatchEvent(T data)
    {
        //no op
    }
}
