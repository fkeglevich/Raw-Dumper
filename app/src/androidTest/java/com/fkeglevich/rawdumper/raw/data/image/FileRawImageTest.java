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

package com.fkeglevich.rawdumper.raw.data.image;

import android.util.Log;

import com.fkeglevich.rawdumper.io.async.IOUtil;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.su.MainSUShell;
import com.fkeglevich.rawdumper.su.ShellFactory;
import com.fkeglevich.rawdumper.util.event.EventListener;
import com.topjohnwu.superuser.Shell;

import org.junit.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import static org.junit.Assert.*;

public class FileRawImageTest
{
    private byte[] buffer = new byte[4096];

    private volatile boolean isReady = false;

    @Test
    public void getData() throws IOException
    {
        final long nanos = System.nanoTime();
        ShellFactory.getInstance().onSuccess.addListener(eventData ->
        {
            Log.i("ASD", "root time: " + (System.nanoTime() - nanos)/1e6);
            isReady = true;
        });
        MainSUShell.getInstance().requestShell();
        ShellFactory.getInstance().startCreatingShells();
        while(!isReady)
        {

        }

        Log.i("ASD", "is root: " + Shell.getShell().isRoot());
        Log.i("ASD", "root time: " + (System.nanoTime() - nanos)/1e6);

        //Shell.su("mv /sdcard/IMG_20181129_011748006.i3av4 /data/misc/media/IMG_20181129_011748006.i3av4").exec().getOut();
        //Shell.su("cp /sdcard/test2.i3av4 /data/misc/media/IMG_20181129_011748006.i3av4").exec().getOut();

        System.out.println("aaaaaaaaaaaaa");
        Log.i("ASD", "aaaaa");
        FileRawImage test = new FileRawImage(new File("/data/misc/media/IMG_20181129_011748006.i3av4")
                , RawImageSize.createSimple(2688, 1944));
        try
        {
            //byte[] data = new byte[10456018];
            long nanos2 = System.nanoTime();
            byte[] data = test.getData();

            Log.i("ASD", "all time: " + (System.nanoTime() - nanos2)/1e6);
            Log.i("ASD", "data len: " + data.length);
            Log.i("ASD", "mkn len " + test.getMakerNotes().length);
            IOUtil.saveBytes(data, "/sdcard/test3.i3av4");
            IOUtil.saveBytes(test.getMakerNotes(), "/sdcard/mkn.i3av4");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        }
        //mv /sdcard/IMG_20181129_011748006.i3av4 /data/misc/media/IMG_20181129_011748006.i3av4
    }
}