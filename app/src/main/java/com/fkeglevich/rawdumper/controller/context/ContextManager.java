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

package com.fkeglevich.rawdumper.controller.context;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * A simple class for getting a static application context easily.
 *
 * It's ugly to store an application context in a static field, however, since Contexts
 * are god-objects on the Android framework, passing them as arguments create a very complex
 * object-graph (Why do I need a context to save that file?) that generates a lot of boilerplate.
 *
 * Created by Flávio Keglevich on 22/08/2017.
 */

public class ContextManager
{
    @SuppressLint("StaticFieldLeak")
    private static Context applicationContext = null;

    /**
     * Gets the context associated with the lifetime of the application's process.
     *
     * @return  A Context object
     */
    public static Context getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * Initializes the application context
     *
     * @param context   A Context object
     */
    static void setApplicationContext(Context context)
    {
        applicationContext = context.getApplicationContext();
    }
}
