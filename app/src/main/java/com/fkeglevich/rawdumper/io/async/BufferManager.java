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

package com.fkeglevich.rawdumper.io.async;

import java.util.ArrayList;
import java.util.List;

public class BufferManager
{
    private static final int MAX_AVAILABLE_BUFFERS = 3;

    private static final BufferManager instance = new BufferManager();

    public static BufferManager getInstance()
    {
        return instance;
    }

    private final List<byte[]> bufferList = new ArrayList<>(MAX_AVAILABLE_BUFFERS);

    private BufferManager()
    {
        for (int i = 0; i < MAX_AVAILABLE_BUFFERS; i++)
            bufferList.add(new byte[0]);
    }

    public byte[] getBuffer(int minSize)
    {
        synchronized (bufferList)
        {
            for (int i = 0; i < bufferList.size(); i++)
            {
                byte[] buffer = bufferList.get(i);
                if (buffer.length >= minSize)
                {
                    bufferList.remove(i);
                    return buffer;
                }
            }

            if (bufferList.isEmpty())
            {
                try
                {
                    bufferList.wait();
                }
                catch (InterruptedException ignored)
                { }

                return getBuffer(minSize);
            }
            else
            {
                bufferList.set(0, new byte[minSize]);
                return bufferList.remove(0);
            }
        }
    }

    public void sendBuffer(byte[] buffer)
    {
        synchronized (bufferList)
        {
            bufferList.add(buffer);
            bufferList.notify();
        }
    }
}
