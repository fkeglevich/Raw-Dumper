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

import com.fkeglevich.rawdumper.async.operation.AsyncOperationPoster;

/**
 * Represents one context where the execution of asynchronous functions will happen.
 * This class encapsulates the operation posters of the task and callback threads.
 *
 * This class basically tells which threads will be used when calling an asynchronous function.
 *
 * Created by Flávio Keglevich on 02/08/2017.
 */

public class AsyncFunctionContext
{
    private final AsyncOperationPoster taskPoster;
    private final AsyncOperationPoster callbackPoster;

    /**
     * Creates a new AsyncFunctionContext.
     *
     * @param taskPoster        The operation poster of the task thread
     * @param callbackPoster    The operation poster of the callback thread
     */
    private AsyncFunctionContext(AsyncOperationPoster taskPoster, AsyncOperationPoster callbackPoster)
    {
        this.taskPoster = taskPoster;
        this.callbackPoster = callbackPoster;
    }

    /**
     * Creates a new AsyncFunctionContext given the loopers of the threads.
     *
     * @param taskLooper        The looper of the task thread
     * @param callbackLooper    The looper of the callback thread
     */
    public AsyncFunctionContext(Looper taskLooper, Looper callbackLooper)
    {
        this(new AsyncOperationPoster(taskLooper), new AsyncOperationPoster(callbackLooper));
    }

    /**
     * Gets the operation poster of the task thread
     *
     * @return A AsyncOperationPoster
     */
    AsyncOperationPoster getTaskPoster()
    {
        return taskPoster;
    }

    /**
     * Gets the operation poster of the callback thread
     *
     * @return A AsyncOperationPoster
     */
    AsyncOperationPoster getCallbackPoster()
    {
        return callbackPoster;
    }
}
