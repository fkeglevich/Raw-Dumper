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

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Contain useful methods when dealing with maker notes.
 *
 * Created by Flávio Keglevich on 25/08/2017.
 */

public class MakerNoteUtil
{
    /**
     * Reads the maker notes (header) from the i3av4 file.
     *
     * @param i3av4File The i3av4 file
     * @param imageSize The raw size of the image contained in the file
     * @return  A byte array containing the maker notes (or null if the file couldn't be read)
     */
    public static byte[] readFromI3av4File(File i3av4File, RawImageSize imageSize)
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

    /**
     * Reads the maker notes from a jpeg file.
     *
     * @param jpegBytes The bytes containing jpeg data
     * @return  A byte array containing the maker notes (or null if there was a problem)
     */
    public static byte[] readFromJpegBytes(byte[] jpegBytes)
    {
        BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(jpegBytes));
        byte[] mknBytes = new byte[0];
        try
        {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream, jpegBytes.length);
            for(Directory directory : metadata.getDirectories())
                if (directory.containsTag(ExifIFD0Directory.TAG_MAKERNOTE))
                    mknBytes = directory.getByteArray(ExifIFD0Directory.TAG_MAKERNOTE);
        }
        catch (Exception ignored)
        {   }

        try { inputStream.close(); }
        catch (IOException ignored)
        {   }

        return mknBytes;
    }
}
