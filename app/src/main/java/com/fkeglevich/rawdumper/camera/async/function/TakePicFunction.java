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

package com.fkeglevich.rawdumper.camera.async.function;

import android.hardware.Camera;
import android.util.Log;

import com.fkeglevich.rawdumper.async.Locked;
import com.fkeglevich.rawdumper.async.function.AsyncFunction;
import com.fkeglevich.rawdumper.camera.shared.SharedCamera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Flávio Keglevich on 17/08/2017.
 * TODO: Add a class header comment!
 */

public class TakePicFunction extends AsyncFunction<Locked<SharedCamera>, Void>
{
    public TakePicFunction()
    {

    }

    @Override
    protected Void call(Locked<SharedCamera> argument)
    {
        synchronized (argument.getLock())
        {
            argument.get().getCamera().takePicture(null, new Camera.PictureCallback()
            {
                //RAW
                @Override
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    Log.i("RAW", "is null: " + (data == null));
                    if (data != null)
                    {
                        Log.i("RAW", "data size: " + (data.length));

                        try
                        {
                            writeToFile(new File("/sdcard/out.raw"), data);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Camera.PictureCallback()
            {
                //JPEG
                @Override
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    try
                    {
                        writeToFile(new File("/sdcard/out.jpg"), data);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    //Don't start preview, restart phone!
                }
            });
        }
        return null;
    }

    private void writeToFile(File file, byte[] data) throws IOException
    {
        //File file = new File("/sdcard/out.raw");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        try
        {
            bos.write(data);
            bos.flush();
        }
        finally
        {
            bos.close();
        }
    }
}
