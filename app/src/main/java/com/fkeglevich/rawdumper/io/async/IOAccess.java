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

package com.fkeglevich.rawdumper.io.async;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;

import com.fkeglevich.rawdumper.io.async.callbacks.IIOResultCallback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Flávio Keglevich on 25/06/2017.
 * TODO: Add a class header comment!
 */

public class IOAccess
{
    //Main thread fields
    private final IOLock ioLock;
    private Handler handler;

    IOAccess(Context applicationContext, Looper looper)
    {
        this.ioLock = new IOLock(applicationContext);
        this.handler = new Handler(looper);
    }

    public synchronized void writeBytesToFile(final byte[] data, final String filepath, final IIOResultCallback callback)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                BufferedOutputStream bos;
                try
                {
                    bos = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                    bos.write(data);
                    bos.flush();
                    bos.close();
                    synchronized(ioLock)
                    {
                        MediaScannerConnection.scanFile(ioLock.getApplicationContext(), new String[]{filepath}, null, null);
                    }
                    callback.onResult(true);
                }
                catch (IOException ioe)
                {
                    callback.onResult(false);
                }
            }
        });
    }
}
