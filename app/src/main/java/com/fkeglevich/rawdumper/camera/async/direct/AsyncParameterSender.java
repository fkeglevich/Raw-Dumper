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

package com.fkeglevich.rawdumper.camera.async.direct;

import android.os.Handler;

import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;

import java.util.LinkedList;
import java.util.List;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 09/05/18.
 */
public class AsyncParameterSender
{
    private final Handler handler = new Handler();
    private final List<Runnable> operations = new LinkedList<>();

    //Mutable state fields
    private ParameterCollection parameterCollection;

    void setupMutableState(ParameterCollection parameterCollection)
    {
        synchronized (handler)
        {
            this.parameterCollection = parameterCollection;
        }
    }

    public <T> void sendParameterAsync(Parameter<T> parameter, T value)
    {
        synchronized (handler)
        {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    parameterCollection.set(parameter, value);
                    removeFromList(this);
                }
            };

            operations.add(runnable);
            handler.post(runnable);
        }
    }

    public void clearPendingOperations()
    {
        synchronized (handler)
        {
            for (Runnable runnable : operations)
                handler.removeCallbacks(runnable);
            operations.clear();
        }
    }

    private void removeFromList(Runnable runnable)
    {
        synchronized (handler)
        {
            operations.remove(runnable);
        }
    }
}
