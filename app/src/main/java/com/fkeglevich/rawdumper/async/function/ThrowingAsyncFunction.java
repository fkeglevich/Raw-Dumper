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

/**
 * Represents a function that will be executed asynchronously and can throw an exception.
 *
 * Created by Flávio Keglevich on 06/08/2017.
 */

public abstract class ThrowingAsyncFunction<I, O, E extends Exception>
{
    /**
     * Contains the code to be executed asynchronously.
     * Can only be called by a proper AsyncFunctionContext.
     *
     * @param argument  The argument (input) of the function
     * @return          The result (output) of the function
     * @throws E        An exception that can be raised by the function
     */
    abstract protected O call(I argument) throws E;
}
