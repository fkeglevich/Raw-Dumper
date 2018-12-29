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

import com.topjohnwu.superuser.Shell;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.fkeglevich.rawdumper.camera.service.LogcatMatch.MATCH_BUFFER_SIZE;

class LogcatServiceThread extends Thread
{
    private static final String THREAD_NAME = "LogcatServiceThread";

    private final LogcatMatch[] matchArray;
    private final Shell logcatShell;
    private final byte[] readingBuffer = new byte[4096];

    LogcatServiceThread(LogcatMatch[] matchArray, Shell logcatShell)
    {
        super(THREAD_NAME);
        this.matchArray = matchArray;
        this.logcatShell = logcatShell;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public void run()
    {
        String[] commands = CommandHelper.buildLogcatCommands(matchArray);
        try
        {
            logcatShell.execTask((in, out, err) ->
            {
                execCommands(commands, in);

                DataInputStream dis = new DataInputStream(out);

                int readBytes;
                while((readBytes = dis.read(readingBuffer)) != -1)
                {
                    LogcatMatch match;
                    for (int k = 0; k < matchArray.length; k++)
                    {
                        boolean foundMatch = false;
                        match = matchArray[k];
                        if (match.enabled && match.fingerprintPrefixBytes.length <= readBytes)
                        {
                            byte[] fingerPrintBytes = match.fingerprintPrefixBytes;

                            int start = indexOfFingerprint(fingerPrintBytes, readingBuffer, readBytes);
                            if (start != -1)
                            {
                                int length = findLengthOfMatch(start, readBytes);
                                writeMatchData(match, start, length);
                                foundMatch = true;
                            }
                        }
                        if (foundMatch)
                            break;
                    }
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void writeMatchData(LogcatMatch match, int start, int length)
    {
        if ((start + length) > readingBuffer.length || length > MATCH_BUFFER_SIZE)
            return;

        synchronized (match.tag)
        {
            System.arraycopy(readingBuffer, start, match.volatileBuffer, 0, length);
        }
    }

    private int findLengthOfMatch(int start, int readBytes)
    {
        if (start + MATCH_BUFFER_SIZE + 1 > readBytes)
            return readBytes - start + 1;
        else
            return MATCH_BUFFER_SIZE;
    }

    private int indexOfFingerprint(byte[] fingerPrintBytes, byte[] readingBuffer, int readBytes)
    {
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
                return i;
        }
        return -1;
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
