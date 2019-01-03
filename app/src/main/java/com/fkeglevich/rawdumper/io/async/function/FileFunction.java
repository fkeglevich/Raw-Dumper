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

package com.fkeglevich.rawdumper.io.async.function;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunction;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * Created by Flávio Keglevich on 24/08/2017.
 * TODO: Add a class header comment!
 */

abstract class FileFunction<I> extends ThrowingAsyncFunction<I, Void, MessageException>
{
    private final String destinationFilePath;

    FileFunction(String destinationFilePath)
    {
        this.destinationFilePath = destinationFilePath;
    }

    String getDestinationFilePath()
    {
        return destinationFilePath;
    }
}
