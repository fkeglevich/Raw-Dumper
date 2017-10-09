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

package com.fkeglevich.rawdumper.raw.data;

import android.support.annotation.Keep;

/**
 * Represents the size of a raw image. TODO: better explaining of this class
 *
 * Created by Flávio Keglevich on 26/12/2016.
 */

@Keep
@SuppressWarnings("unused")
public class RawImageSize
{
    private int width;
    private int height;

    private int paddedWidth;
    private int paddedWidthBytes;
    private int bytesPerLine;
    private int paddedHeight;
    private int bufferLength;

    private RawImageSize()
    {   }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getPaddedWidth()
    {
        return paddedWidth;
    }

    public int getPaddedWidthBytes()
    {
        return paddedWidthBytes;
    }

    public int getBytesPerLine()
    {
        return bytesPerLine;
    }

    public int getPaddedHeight()
    {
        return paddedHeight;
    }

    public int getBufferLength()
    {
        return bufferLength;
    }

    public int getBytesPerPixel()
    {
        return paddedWidthBytes / paddedWidth;
    }

    public byte[] buildValidRowBuffer()
    {
        return new byte[getPaddedWidthBytes()];
    }

    public String toString()
    {
        return "[width: " + width +
                ", height: " + height +
                ", paddedWidth: " + paddedWidth +
                ", paddedWidthBytes: " + paddedWidthBytes +
                ", bytesPerLine: " + bytesPerLine +
                ", paddedHeight: " + paddedHeight +
                ", bufferLength: " + bufferLength + "]";
    }

    public boolean compareTo(RawImageSize another)
    {
        return  this == another ||
               (width               == another.width            &&
                height              == another.height           &&
                paddedWidth         == another.paddedWidth      &&
                paddedWidthBytes    == another.paddedWidthBytes &&
                bytesPerLine        == another.bytesPerLine     &&
                paddedHeight        == another.paddedHeight     &&
                bufferLength        == another.bufferLength);
    }
}