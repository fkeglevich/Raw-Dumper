/*
 * Copyright 2018, Flávio Keglevich
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
import android.os.HandlerThread;
import android.os.Looper;

import org.junit.Test;

import static org.junit.Assert.*;

public class AsyncEventDispatcherTest
{
    private final Object lock = new Object();
    private EventListener<Boolean> mainThreadListener = eventData ->
    {
        assertEquals(Looper.getMainLooper(), Looper.myLooper());
        System.out.println("main thread event, value: " + eventData);
    };
    private HandlerThread handlerThread;
    private EventListener<Boolean> handlerThreadListener = new EventListener<Boolean>()
    {
        @Override
        public void onEvent(Boolean eventData)
        {
            assertEquals(handlerThread.getLooper(), Looper.myLooper());
            System.out.println("HandlerThread event, value: " + eventData + ",  looper: " + Looper.myLooper());
            System.out.println("looper: " + Looper.getMainLooper());
            synchronized (lock)
            {
                lock.notify();
            }
        }
    };

    @Test
    public void mainTest() throws InterruptedException
    {
        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        AsyncEventDispatcher<Boolean> eventDispatcher = new AsyncEventDispatcher<>();
        handler.post(() -> {
            eventDispatcher.addListener(handlerThreadListener);
            synchronized (lock)
            {
                lock.notify();
            }
        });

        synchronized (lock)
        {
            lock.wait();
        }

        //eventDispatcher.addListener(mainThreadListener);
        eventDispatcher.dispatchEvent(true);
        eventDispatcher.removeListener(handlerThreadListener);
        //eventDispatcher.addListener(handlerThreadListener);

        Thread.sleep(100);
    }
}