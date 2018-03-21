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

package com.fkeglevich.rawdumper.util.event;

import android.os.Handler;
import android.os.Looper;

import junit.framework.Assert;

/**
 * Created by Flávio Keglevich on 16/09/2017.
 * TODO: Add a class header comment!
 */

public class HandlerDispatcher<T> extends SimpleDispatcher<T>
{
    private final Handler handler;

    public HandlerDispatcher(Looper threadLooper)
    {
        Assert.assertNotNull(threadLooper);
        handler = new Handler(threadLooper);
    }

    @Override
    public void dispatchEvent(final T data)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                HandlerDispatcher.super.dispatchEvent(data);
            }
        });
    }
}
