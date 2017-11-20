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

package com.fkeglevich.rawdumper.camera.data;

import android.support.annotation.NonNull;

import com.fkeglevich.rawdumper.util.MathUtil;

/**
 * CaptureSize represents a valid size of picture, preview or video capture.
 *
 * Created by Flávio Keglevich on 22/04/2017.
 */

public class CaptureSize implements Comparable<CaptureSize>, Displayable
{
    private final int width;
    private final int height;

    private final int aspectRatioX;
    private final int aspectRatioY;

    public CaptureSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.aspectRatioX = width   / MathUtil.gcd(width, height);
        this.aspectRatioY = height  / MathUtil.gcd(width, height);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getAspectRatioX()
    {
        return aspectRatioX;
    }

    public int getAspectRatioY()
    {
        return aspectRatioY;
    }

    public int getNumberOfPixels()
    {
        return width * height;
    }

    public String toString()
    {
        return "[CaptureSize " + width + "x" + height + "]";
    }

    public boolean hasSameAspectRatio(CaptureSize another)
    {
        return another.getAspectRatioX() == getAspectRatioX() &&
                another.getAspectRatioY() == getAspectRatioY();
    }

    @Override
    public int compareTo(@NonNull CaptureSize another)
    {
        return getNumberOfPixels() - another.getNumberOfPixels();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaptureSize that = (CaptureSize) o;

        if (width != that.width) return false;
        return height == that.height;
    }

    @Override
    public int hashCode()
    {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String displayValue()
    {
        return getWidth() + "x" + getHeight();
    }
}
