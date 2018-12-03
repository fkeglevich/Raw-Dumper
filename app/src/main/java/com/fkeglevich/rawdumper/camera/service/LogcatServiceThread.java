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

package com.fkeglevich.rawdumper.camera.service;

import android.util.Log;

import com.topjohnwu.superuser.Shell;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LogcatServiceThread extends Thread
{
    private static final String THREAD_NAME = "LogcatServiceThread";

    private final LogcatMatch[] matchArray;
    private final byte[] readingBuffer = new byte[4096];

    LogcatServiceThread(LogcatMatch[] matchArray)
    {
        super(THREAD_NAME);
        this.matchArray = matchArray;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public void run()
    {
        String[] commands = CommandHelper.buildLogcatCommands(matchArray);
        try
        {
            Shell.getShell().execTask((in, out, err) ->
            {
                execCommands(commands, in);

                DataInputStream dis = new DataInputStream(out);

                int readBytes;
                while((readBytes = dis.read(readingBuffer)) != -1)
                {
                    long nanos = System.nanoTime();

                    LogcatMatch match;
                    for (int k = 0; k < matchArray.length; k++)
                    {
                        match = matchArray[k];
                        if (match.enabled)
                        {
                            byte[] fingerPrintBytes = match.fingerprintPrefixBytes;

                            boolean fingerPrintFound = false;
                            int i;
                            for (i = 0; i < readBytes - fingerPrintBytes.length + 1; i++)
                            {
                                boolean found = true;
                                for (int j = 0; j < fingerPrintBytes.length; j++)
                                {
                                    if (readingBuffer[i + j] != fingerPrintBytes[j])
                                    {
                                        found = false;
                                        break;
                                    }
                                }
                                if (found)
                                {
                                    fingerPrintFound = true;
                                    break;
                                }
                            }

                            if (fingerPrintFound)
                            {
                                int start = i;
                                i += fingerPrintBytes.length;
                                for (; i < readBytes; i++)
                                {
                                    if (readingBuffer[i] == '\n')
                                    {
                                        int length = i - start + 1;
                                        synchronized (match.tag)
                                        {
                                            System.arraycopy(readingBuffer, start, match.volatileBuffer, 0, length);
                                            match.volatileBufferSize = match.volatileBuffer.length >= length ? length : match.volatileBuffer.length;
                                        }

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    //Log.i("ASD", "find time: " + (System.nanoTime() - nanos)/1e6);
                }
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void execCommands(String[] commands, OutputStream in) throws IOException
    {
        for (String command : commands)
        {
            in.write(command.getBytes("UTF-8"));
            in.write('\n');
        }
        in.flush();
    }
}
