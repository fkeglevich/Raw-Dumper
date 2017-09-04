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

package com.fkeglevich.rawdumper.test.controller.dependency;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.fkeglevich.rawdumper.async.Locked;
import com.fkeglevich.rawdumper.controller.camera.CameraResourceType;
import com.fkeglevich.rawdumper.controller.dependency.ConcurrentResourceList;
import com.fkeglevich.rawdumper.controller.dependency.ResourceListener;
import com.fkeglevich.rawdumper.test.Test;

/**
 * Created by Flávio Keglevich on 03/09/2017.
 * TODO: Add a class header comment!
 */

public class ConcurrentResourceListDeadlockTest extends Test
{
    @Override
    protected void executeTest() throws Exception
    {
        final ConcurrentResourceList<CameraResourceType> list = new ConcurrentResourceList<>(CameraResourceType.class);
        list.addListener(new ResourceListener()
        {
            @Override
            public void onResourcesAvailable()
            {
                Locked<Object> resource = list.getResource(CameraResourceType.SURFACE_TEXTURE);
                synchronized (resource.getLock())
                {
                    list.clearResource(CameraResourceType.SURFACE_TEXTURE);
                }
                //list.deadlock(CameraResourceType.SURFACE_TEXTURE);
                Log.i(getTag(), "Got SURFACE_TEXTURE and CAMERA");
            }
        }, new CameraResourceType[]{CameraResourceType.SURFACE_TEXTURE, CameraResourceType.CAMERA});

        list.addListener(new ResourceListener()
        {
            @Override
            public void onResourcesAvailable()
            {
                Log.i(getTag(), "Got DEVICE_INFO");
            }
        }, new CameraResourceType[] {CameraResourceType.DEVICE_INFO});

        list.setResource(CameraResourceType.SURFACE_TEXTURE, 3);
        Thread.sleep(100, 0);
        list.setResource(CameraResourceType.DEVICE_INFO, 4);
        list.clearResource(CameraResourceType.DEVICE_INFO);
        list.clearResource(CameraResourceType.DEVICE_INFO);
        Thread.sleep(50, 0);
        list.setResource(CameraResourceType.CAMERA, 4);
        list.setResource(CameraResourceType.SURFACE_TEXTURE, 3);

        HandlerThread thread = new HandlerThread("thread2");
        thread.start();
        Handler handler2 = new Handler(thread.getLooper());
        handler2.post(new Runnable()
        {
            @Override
            public void run()
            {
                list.addListener(new ResourceListener()
                {
                    @Override
                    public void onResourcesAvailable()
                    {
                        Locked<Object> resource = list.getResource(CameraResourceType.SURFACE_TEXTURE);
                        synchronized (resource.getLock())
                        {
                            list.clearResource(CameraResourceType.SURFACE_TEXTURE);
                        }
                        //list.deadlock(CameraResourceType.SURFACE_TEXTURE);
                        Log.i(getTag(), "Got SURFACE_TEXTURE and CAMERA 2");
                    }
                }, new CameraResourceType[]{CameraResourceType.SURFACE_TEXTURE, CameraResourceType.CAMERA});
            }
        });
        post(handler2, list);
        post(new Handler(Looper.getMainLooper()), list);
    }

    @Override
    protected int getTimeout()
    {
        return 2000;
    }

    private void post(final Handler handler, final ConcurrentResourceList<CameraResourceType> list)
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Locked<Object> resource = list.getResource(CameraResourceType.SURFACE_TEXTURE);
                synchronized (resource.getLock())
                {
                    if (resource.get() != null)
                        list.clearResource(CameraResourceType.SURFACE_TEXTURE);
                    else
                        list.setResource(CameraResourceType.SURFACE_TEXTURE, 3);
                }

                post(handler, list);
            }
        }, 250);
    }
}
