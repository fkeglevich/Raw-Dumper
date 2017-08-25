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

package com.fkeglevich.rawdumper.raw.capture;

import com.fkeglevich.rawdumper.raw.data.RawImageSize;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Contain useful methods when dealing with i3av4 files.
 *
 * Created by Flávio Keglevich on 25/08/2017.
 */

public class I3av4FileUtil
{
    /**
     * Reads the maker notes (header) from the i3av4 file.
     *
     * @param i3av4File The i3av4 file
     * @param imageSize The raw size of the image contained in the file
     * @return  A byte array containing the maker notes (or null if the file couldn't be read)
     */
    public static byte[] readMknFromFile(File i3av4File, RawImageSize imageSize)
    {
        byte[] mknBytes;
        try
        {
            RandomAccessFile i3av4RAFile = new RandomAccessFile(i3av4File, "r");
            mknBytes = new byte[(int)(i3av4RAFile.length() - imageSize.getBufferLength())];
            i3av4RAFile.seek(0);
            i3av4RAFile.read(mknBytes);
            i3av4RAFile.close();
        }
        catch (IOException e)
        {
            mknBytes = null;
        }
        return mknBytes;
    }
}
