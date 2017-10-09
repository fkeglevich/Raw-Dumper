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

package com.fkeglevich.rawdumper.async.function;

import android.os.Looper;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.async.operation.AsyncOperationPoster;

/**
 * Created by Flávio Keglevich on 06/08/2017.
 * TODO: Add a class header comment!
 */

public class AsyncFunctionContext
{
    protected final AsyncOperationPoster taskPoster;
    protected final AsyncOperationPoster callbackPoster;

    public AsyncFunctionContext(Looper taskLooper, Looper callbackLooper)
    {
        this.taskPoster     = new AsyncOperationPoster(taskLooper);
        this.callbackPoster = new AsyncOperationPoster(callbackLooper);
    }

    public <I, O> void call(final AsyncFunction<I, O> function, I argument, final AsyncOperation<O> callback)
    {
        AsyncOperation<I> task = new AsyncOperation<I>()
        {
            @Override
            protected void execute(I argument)
            {
                callbackPoster.enqueueOperation(callback, function.call(argument));
            }
        };
        taskPoster.enqueueOperation(task, argument);
    }

    public <I, O> O synchronizedCall(final AsyncFunction<I, O> function, I argument)
    {
        return function.call(argument);
    }

    public void ignoreAllPendingCalls()
    {
        taskPoster.removeAllPendingOperations();
        callbackPoster.removeAllPendingOperations();
    }
}
