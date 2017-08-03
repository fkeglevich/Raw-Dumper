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

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;

/**
 * Represents a function that is executed asynchronously.
 *
 * The I type represents the input argument of the function.
 * The O type represents the output return value of the function.
 *
 * An async function is represented by two async operations that happen is a specific order:
 * - The first is called task, because it represents the actual task performed by the function.
 *   The task is executed in the function's own thread.
 *
 * - The second is called callback, because it contains the result after the function execution.
 *   The callback is executed in the caller's own thread.
 *
 * Every asynchronous function has a function context that represents the threads that will
 * execute the task code and the callback code.
 *
 * To actually use the function, use the call method, passing the input argument and the necessary
 * callback for retrieving the resulting value.
 *
 * To implement a new asynchronous function you only need to extend this class and implement the
 * performCall method, that contains the code executing in the task thread.
 *
 * WARNING: don't forget to use the returnCallback method for properly calling the callback after
 * the function's execution; otherwise the callback will never happen.
 *
 * Created by Flávio Keglevich on 02/08/2017.
 */

public abstract class AsyncFunction<I, O>
{
    private final AsyncFunctionContext asyncFunctionContext;

    /**
     * Creates a new AsyncFunction that will be called in a given context.
     *
     * @param asyncFunctionContext  The AsyncFunctionContext
     */
    public AsyncFunction(AsyncFunctionContext asyncFunctionContext)
    {
        this.asyncFunctionContext = asyncFunctionContext;
    }

    /**
     * Executes the asynchronous function.
     *
     * @param argument  The input argument of the function
     * @param callback  The callback for returning the output of the function
     */
    public final void call(I argument, final AsyncOperation<O> callback)
    {
        final AsyncOperation<I> task = new AsyncOperation<I>()
        {
            @Override
            protected void execute(I argument)
            {
                performCall(argument, callback);
            }
        };
        asyncFunctionContext.getTaskPoster().enqueueOperation(task, argument);
    }

    /**
     * Properly returns the callback and executes its code in the callback thread.
     *
     * @param callback          The callback
     * @param callbackArgument  The function output data
     */
    protected final void returnCallback(AsyncOperation<O> callback, O callbackArgument)
    {
        asyncFunctionContext.getCallbackPoster().enqueueOperation(callback, callbackArgument);
    }

    /**
     * Represents the actual code that will be executed in the task thread.
     *
     * @param argument  The input argument
     * @param callback  The callback for returning the output
     */
    abstract protected void performCall(I argument, AsyncOperation<O> callback);
}
