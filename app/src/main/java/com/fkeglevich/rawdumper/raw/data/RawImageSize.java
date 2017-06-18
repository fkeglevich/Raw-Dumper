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

import com.fkeglevich.rawdumper.raw.RawUtil;

/**
 * Represents the size of a raw image.
 *
 * Created by Flávio Keglevich on 26/12/2016.
 */

public class RawImageSize
{
    private final int width;
    private final int height;

    private final int rawBufferWidth;
    private final int rawBufferWidthBytes;
    private final int rawBufferAlignedWidth;
    private final int rawBufferHeight;
    private final int rawBufferLength;

    public static int calculateRawBufferWidth(int width, int horizontalPadding)
    {
        return width + horizontalPadding;
    }

    public static int calculateRawBufferWidthBytes(int width, int horizontalPadding, int bytesPerPixel)
    {
        return (width + horizontalPadding) * bytesPerPixel;
    }

    public static int calculateRawBufferAlignedWidth(int width, int alignWidth, int horizontalPadding, int bytesPerPixel)
    {
        return RawUtil.alignWidth((width + horizontalPadding) * bytesPerPixel, alignWidth);
    }

    public static int calculateRawBufferHeight(int height, int verticalPadding)
    {
        return height + verticalPadding;
    }

    public RawImageSize(int width, int height, int alignWidth, int horizontalPadding, int verticalPadding, int bytesPerPixel)
    {
        this.width = width;
        this.height = height;

        this.rawBufferWidth = calculateRawBufferWidth(width, horizontalPadding);
        this.rawBufferWidthBytes = calculateRawBufferWidthBytes(width, horizontalPadding, bytesPerPixel);
        this.rawBufferAlignedWidth = calculateRawBufferAlignedWidth(width, alignWidth, horizontalPadding, bytesPerPixel);
        this.rawBufferHeight = calculateRawBufferHeight(height, verticalPadding);
        this.rawBufferLength = rawBufferAlignedWidth * rawBufferHeight;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getRawBufferWidth()
    {
        return rawBufferWidth;
    }

    public int getRawBufferWidthBytes()
    {
        return rawBufferWidthBytes;
    }

    public int getRawBufferAlignedWidth()
    {
        return rawBufferAlignedWidth;
    }

    public int getRawBufferHeight()
    {
        return rawBufferHeight;
    }

    public int getRawBufferLength()
    {
        return rawBufferLength;
    }

    public String toString()
    {
        return "[width: " + width +
                ", height: " + height +
                ", rawBufferWidth: " + rawBufferWidth +
                ", rawBufferWidthBytes: " + rawBufferWidthBytes +
                ", rawBufferAlignedWidth: " + rawBufferAlignedWidth +
                ", rawBufferHeight: " + rawBufferHeight +
                ", rawBufferLength: " + rawBufferLength + "]";
    }

    public boolean compareTo(RawImageSize another)
    {
        return  this == another ||
               (width                   == another.width                    &&
                height                  == another.height                   &&
                rawBufferWidth          == another.rawBufferWidth           &&
                rawBufferWidthBytes     == another.rawBufferWidthBytes      &&
                rawBufferAlignedWidth   == another.rawBufferAlignedWidth    &&
                rawBufferHeight         == another.rawBufferHeight          &&
                rawBufferLength         == another.rawBufferLength);
    }
}
