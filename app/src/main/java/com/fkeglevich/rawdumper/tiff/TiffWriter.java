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

package com.fkeglevich.rawdumper.tiff;

import android.util.Log;

/**
 * A simples low-level TIFF writing class.
 *
 * Created by Flávio Keglevich on 11/01/2017.
 */

public class TiffWriter
{
    private static final String TAG = "TiffWriter";
    private static final boolean LOG_ERRORS = true;

    static
    {
        System.loadLibrary("tiff-writer");
    }

    private static native long openNative(String filepath);
    private native void closeNative(long pointer);

    private native int setIntFieldNative     (long pointer, int tagName, int value);
    private native int setShortFieldNative   (long pointer, int tagName, short value);
    private native int setLongFieldNative    (long pointer, int tagName, long value);
    private native int setByteFieldNative    (long pointer, int tagName, byte value);
    private native int setFloatFieldNative   (long pointer, int tagName, float value);
    private native int setDoubleFieldNative  (long pointer, int tagName, double value);
    private native int setStringFieldNative  (long pointer, int tagName, String value);

    private native int setIntPointerFieldNative (long pointer, int tagName, int value, boolean writeLength);

    private native int setIntArrayFieldNative    (long pointer, int tagName, int[] value,    boolean writeLength);
    private native int setShortArrayFieldNative  (long pointer, int tagName, short[] value,  boolean writeLength);
    private native int setLongArrayFieldNative   (long pointer, int tagName, long[] value,   boolean writeLength);
    private native int setByteArrayFieldNative   (long pointer, int tagName, byte[] value,   boolean writeLength);
    private native int setFloatArrayFieldNative  (long pointer, int tagName, float[] value,  boolean writeLength);
    private native int setDoubleArrayFieldNative (long pointer, int tagName, double[] value, boolean writeLength);

    private native int writeScanlineNative(long pointer, byte[] data, int row);
    private native int writeDirectoryNative(long pointer);

    private native int createEXIFDirectoryNative(long pointer);
    private native int writeCustomDirectoryNative(long pointer, long[] dirOffset, int dirOffsetIndex);
    private native int setDirectoryNative(long pointer, short directory);

    private native long writeRawTileNative(long pointer, int tile, byte[] data, long dataSize);
    private native long writeRawStripNative(long pointer, int strip, byte[] data, long dataSize);

    private long pointer = 0;

    public static TiffWriter open(String filepath)
    {
        long tiffPointer = openNative(filepath);
        return (tiffPointer != 0) ? new TiffWriter(tiffPointer) : null;
    }

    private TiffWriter(long pointer)
    {
        this.pointer = pointer;
    }

    private int wrapError(int result, String methodName)
    {
        if (LOG_ERRORS && result == 0)
            Log.e(TAG, methodName + " failed!");
        return result;
    }

    private int wrapSetFieldError(int result)
    {
        return wrapError(result, "SetField");
    }

    private int wrapWriteScanlineError(int result)
    {
        return wrapError(result, "WriteScanline");
    }

    private int wrapWriteDirectoryError(int result)
    {
        return wrapError(result, "WriteDirectory");
    }

    private int wrapCreateEXIFDirectoryError(int result)
    {
        return wrapError(result, "CreateEXIFDirectory");
    }

    private int wrapWriteCustomDirectoryError(int result)
    {
        return wrapError(result, "WriteCustomDirectory");
    }

    private int wrapSetDirectoryError(int result)
    {
        return wrapError(result, "SetDirectory");
    }

    public long getNativeHandle()
    {
        return pointer;
    }

    public void close()
    {
        closeNative(pointer);
        pointer = 0;
    }

    public int setField(int tagName, int value)
    {
        return wrapSetFieldError(setIntFieldNative(pointer, tagName, value));
    }

    public int setField(int tagName, short value)
    {
        return wrapSetFieldError(setShortFieldNative(pointer, tagName, value));
    }

    public int setField(int tagName, long value)
    {
        return wrapSetFieldError(setLongFieldNative(pointer, tagName, value));
    }

    public int setField(int tagName, byte value)
    {
        return wrapSetFieldError(setByteFieldNative(pointer, tagName, value));
    }

    public int setField(int tagName, float value)
    {
        return wrapSetFieldError(setFloatFieldNative(pointer, tagName, value));
    }

    public int setField(int tagName, double value)
    {
        return wrapSetFieldError(setDoubleFieldNative(pointer, tagName, value));
    }

    public int setField(int tagName, String value)
    {
        return wrapSetFieldError(setStringFieldNative(pointer, tagName, value));
    }

    public int setPointerField(int tagName, int value, boolean writeLength)
    {
        return wrapSetFieldError(setIntPointerFieldNative(pointer, tagName, value, writeLength));
    }

    public int setField(int tagName, int[] value, boolean writeLength)
    {
        return wrapSetFieldError(setIntArrayFieldNative(pointer, tagName, value, writeLength));
    }

    public int setField(int tagName, short[] value, boolean writeLength)
    {
        return wrapSetFieldError(setShortArrayFieldNative(pointer, tagName, value, writeLength));
    }

    public int setField(int tagName, long[] value, boolean writeLength)
    {
        return wrapSetFieldError(setLongArrayFieldNative(pointer, tagName, value, writeLength));
    }

    public int setField(int tagName, byte[] value, boolean writeLength)
    {
        return wrapSetFieldError(setByteArrayFieldNative(pointer, tagName, value, writeLength));
    }

    public int setField(int tagName, float[] value, boolean writeLength)
    {
        return wrapSetFieldError(setFloatArrayFieldNative(pointer, tagName, value, writeLength));
    }

    public int setField(int tagName, double[] value, boolean writeLength)
    {
        return wrapSetFieldError(setDoubleArrayFieldNative(pointer, tagName, value, writeLength));
    }

    public int writeScanline(byte[] data, int row)
    {
        return wrapWriteScanlineError(writeScanlineNative(pointer, data, row));
    }

    public int writeDirectory()
    {
        return wrapWriteDirectoryError(writeDirectoryNative(pointer));
    }

    public int createEXIFDirectory()
    {
        return wrapCreateEXIFDirectoryError(createEXIFDirectoryNative(pointer) != 0 ? 0 : 1);
    }

    public int writeCustomDirectory(long[] dirOffset, int dirOffsetIndex)
    {
        return wrapWriteCustomDirectoryError(writeCustomDirectoryNative(pointer, dirOffset, dirOffsetIndex));
    }

    public int setDirectory(short directory)
    {
        return wrapSetDirectoryError(setDirectoryNative(pointer, directory));
    }

    public long writeRawTile(int tile, byte[] data, long dataSize)
    {
        return writeRawTileNative(pointer, tile, data, dataSize);
    }

    public long writeRawStrip(int strip, byte[] data, long dataSize)
    {
        return writeRawStripNative(pointer, strip, data, dataSize);
    }
}
