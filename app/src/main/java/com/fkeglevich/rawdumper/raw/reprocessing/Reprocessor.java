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

package com.fkeglevich.rawdumper.raw.reprocessing;

import android.util.Log;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.builder.FromI3av4FileBuilder;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.File;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 27/05/18.
 */
public class Reprocessor
{
    private final Object lock = new Object();

    public void reprocessI3av4File(DeviceInfo deviceInfo, File i3av4File)
    {
        FromI3av4FileBuilder captureInfoBuilder = new FromI3av4FileBuilder(deviceInfo, i3av4File);
        CaptureInfo captureInfo = captureInfoBuilder.build();
        IOThread.getIOAccess().saveDng(captureInfo, new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
            {
                Log.i("ASD", "success");
                synchronized (lock)
                {
                    lock.notify();
                }
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                Log.i("ASD", "failure");
                synchronized (lock)
                {
                    lock.notify();
                }
            }
        });
        synchronized (lock)
        {
            try
            {
                lock.wait();
            }
            catch (InterruptedException ignored)
            { }
        }
    }
}
