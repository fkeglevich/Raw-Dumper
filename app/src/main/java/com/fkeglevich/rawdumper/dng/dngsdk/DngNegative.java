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

package com.fkeglevich.rawdumper.dng.dngsdk;

public class DngNegative
{
    static
    {
        System.loadLibrary("dng-writer");
    }

    private final long pointer;

    private native long nativeConstructor();
    private native void nativeDispose(long pointer);

    private native void setModelNative(long pointer, String model);
    private native void setOriginalRawFileNameNative(long pointer, String fileName);
    private native void setSensorInfoNative(long pointer, int whiteLevel, float[] blackLevels, int bayerPhase);
    private native void setCameraNeutralNative(long pointer, float[] cameraNeutral);
    private native void setImageSizeAndOrientationNative(long pointer, int width, int height, int orientationExifCode);

    public DngNegative()
    {
        pointer = nativeConstructor();
    }

    public void dispose()
    {
        nativeDispose(pointer);
    }

    public void setModel(String model)
    {
        setModelNative(pointer, model);
    }

    public void setOriginalRawFileName(String fileName)
    {
        setOriginalRawFileNameNative(pointer, fileName);
    }

    public void setSensorInfo(int whiteLevel, float[] blackLevels, int bayerPhase)
    {
        setSensorInfoNative(pointer, whiteLevel, blackLevels, bayerPhase);
    }

    public void setCameraNeutral(float[] cameraNeutral)
    {
        setCameraNeutralNative(pointer, cameraNeutral);
    }

    public void setImageSizeAndOrientation(int width, int height, int orientationExifCode)
    {
        setImageSizeAndOrientationNative(pointer, width, height, orientationExifCode);
    }
}
