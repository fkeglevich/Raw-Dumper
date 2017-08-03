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

package com.fkeglevich.rawdumper.async.operation;

import android.os.Handler;
import android.os.Looper;

import com.fkeglevich.rawdumper.async.Locked;

import java.util.ArrayList;

/**
 * AsyncOperationPoster is responsible for posting AsyncOperations in a thread's message queue.
 *
 * Created by Flávio Keglevich on 02/08/2017.
 */

public class AsyncOperationPoster
{
    private final Handler operationThreadHandler;
    private final Locked<ArrayList<Runnable>> lockedOperationList;

    /**
     * Creates a new AsyncOperationPoster given a thread's looper.
     *
     * @param operationThreadLooper The looper of the thread
     */
    public AsyncOperationPoster(Looper operationThreadLooper)
    {
        operationThreadHandler = new Handler(operationThreadLooper);
        lockedOperationList = new Locked<>(new ArrayList<Runnable>(), operationThreadHandler);
    }

    /**
     * Posts an AsyncOperation in the thread's message queue.
     *
     * @param operation The operation to be posted
     * @param argument  The input argument used to actually perform the operation
     * @param <T>       The type parameter of the operation
     */
    public <T> void enqueueOperation(final AsyncOperation<T> operation, final T argument)
    {
        synchronized (operationThreadHandler)
        {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    operation.execute(argument);
                    removeRunnableFromList(this);
                }
            };
            lockedOperationList.get().add(runnable);
            operationThreadHandler.post(runnable);
        }
    }

    /**
     * Removes all the operations that were posted but weren't executed.
     *
     * This method was created specifically for solving certain race conditions when the thread
     * represents a resource and it is disposed, so you need to synchronously cancel the remaining
     * operations or the program will crash.
     */
    public void removeAllPendingOperations()
    {
        synchronized (operationThreadHandler)
        {
            ArrayList<Runnable> callbackArray = lockedOperationList.get();
            for (Runnable runnable : callbackArray)
                operationThreadHandler.removeCallbacks(runnable);
        }
    }

    /**
     * Thread-safely Removes the runnable reference from the runnable list, for avoiding potential
     * memory leaks.
     *
     * @param runnable  The runnable to be removed.
     */
    private void removeRunnableFromList(Runnable runnable)
    {
        synchronized (operationThreadHandler)
        {
            lockedOperationList.get().remove(runnable);
        }
    }
}
