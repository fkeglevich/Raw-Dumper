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

package com.fkeglevich.rawdumper.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fkeglevich.rawdumper.BuildConfig;

/**
 * Simple class for simple debug-time assertions
 *
 * Created by Flávio Keglevich on 30/08/2017.
 */

public class Assert
{
    public static void isTrue(boolean condition, @Nullable String message)
    {
        if (BuildConfig.DEBUG && !condition)
            throw message != null ? new AssertionError(message) : new AssertionError();
    }

    public static void isTrue(boolean condition)
    {
        isTrue(condition, null);
    }

    public static Object isInstanceOf(Object object, @NonNull Class<?> objClass)
    {
        if (BuildConfig.DEBUG)
        {
            if (object == null && objClass == Void.class)
                return null;

            if (!objClass.isInstance(object))
                throw new AssertionError("The object: " + object + " is not an instance of: " + objClass);
        }

        return object;
    }
}
