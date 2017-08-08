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

public class ThrowingAsyncFunctionContext extends AsyncFunctionContext
{
    private final AsyncOperationPoster exceptionPoster;

    public ThrowingAsyncFunctionContext(Looper taskLooper, Looper callbackLooper, Looper exceptionLooper)
    {
        super(taskLooper, callbackLooper);
        this.exceptionPoster = new AsyncOperationPoster(exceptionLooper);
    }

    @SuppressWarnings("unchecked")
    public <I, O, E extends Exception> void call(final ThrowingAsyncFunction<I, O, E> function, I argument, final AsyncOperation<O> callback, final AsyncOperation<E> exception)
    {
        AsyncOperation<I> task = new AsyncOperation<I>()
        {
            @Override
            protected void execute(I argument)
            {
                try {   callbackPoster.enqueueOperation(callback, function.call(argument));  }
                catch (Exception e) {   exceptionPoster.enqueueOperation(exception, (E) e); }
            }
        };
        taskPoster.enqueueOperation(task, argument);
    }

    public <I, O, E extends Exception> O synchronizedCall(final ThrowingAsyncFunction<I, O, E> function, I argument) throws E
    {
        return function.call(argument);
    }

    @Override
    public void ignoreAllPendingCalls()
    {
        super.ignoreAllPendingCalls();
        exceptionPoster.removeAllPendingOperations();
    }
}
