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

package com.fkeglevich.rawdumper.dng.writer;

import com.fkeglevich.rawdumper.dng.opcode.Opcode;
import com.fkeglevich.rawdumper.dng.opcode.OpcodeListWriter;
import com.fkeglevich.rawdumper.raw.data.CalibrationIlluminant;

import java.util.List;

public class DngNegative
{
    static
    {
        System.loadLibrary("raw-dumper");
    }

    private final long pointer;

    private native long nativeConstructor();
    private native void nativeDispose(long pointer);

    private native void setModelNative(long pointer, String model);
    private native void setOriginalRawFileNameNative(long pointer, String fileName);
    private native void setSensorInfoNative(long pointer, int whiteLevel, float[] blackLevels, int bayerPhase);
    private native void setCameraNeutralNative(long pointer, double[] cameraNeutral);
    private native void setImageSizeAndOrientationNative(long pointer, int width, int height, int orientationExifCode);
    private native void setCameraCalibrationNative(long pointer, float[] cameraCalibration1, float[] cameraCalibration2);
    private native void addColorProfileNative(long pointer, String name, float[] colorMatrix1, float[] colorMatrix2,
                                        float[] forwardMatrix1, float[] forwardMatrix2,
                                        int calibrationIlluminant1, int calibrationIlluminant2,
                                        float[] toneCurve);
    private native void setAsShotProfileNameNative(long pointer, String name);
    private native void setOpcodeListNative(long pointer, byte[] bytes, int listType);
    private native void setNoiseProfileNative(long pointer, double[] noiseProfile);
    private native void writeImageToFileNative(long pointer, String fileName, int width, int height, int bpl,
                                               boolean shouldInvertRows, byte[] imageData, boolean uncompressed, boolean calculateDigest);
    private native long getExifHandleNative(long pointer);

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

    public void setCameraNeutral(double[] cameraNeutral)
    {
        setCameraNeutralNative(pointer, cameraNeutral);
    }

    public void setImageSizeAndOrientation(int width, int height, int orientationExifCode)
    {
        setImageSizeAndOrientationNative(pointer, width, height, orientationExifCode);
    }

    public void setCameraCalibration(float[] cameraCalibration1, float[] cameraCalibration2)
    {
        setCameraCalibrationNative(pointer, cameraCalibration1, cameraCalibration2);
    }

    public void addColorProfile(String name, float[] colorMatrix1, float[] colorMatrix2,
                                float[] forwardMatrix1, float[] forwardMatrix2,
                                CalibrationIlluminant calibrationIlluminant1, CalibrationIlluminant calibrationIlluminant2,
                                float[] toneCurve)
    {
        addColorProfileNative(pointer, name, colorMatrix1, colorMatrix2,
                forwardMatrix1, forwardMatrix2,
                calibrationIlluminant1 != null ? calibrationIlluminant1.getExifCode() : 0,
                calibrationIlluminant2 != null ? calibrationIlluminant2.getExifCode() : 0,
                toneCurve);
    }

    public void setAsShotProfileName(String name)
    {
        setAsShotProfileNameNative(pointer, name);
    }

    public void setOpcodeList1(byte[] opcodeList)
    {
        if (opcodeList != null)
            setOpcodeListNative(pointer, opcodeList, 1);
    }

    public void setOpcodeList2(byte[] opcodeList)
    {
        if (opcodeList != null)
            setOpcodeListNative(pointer, opcodeList, 2);
    }

    public void setOpcodeList3(byte[] opcodeList)
    {
        if (opcodeList != null)
            setOpcodeListNative(pointer, opcodeList, 3);
    }

    public void setOpcodeList3(List<Opcode> opcodes)
    {
        //We don't even bother writing if the opcode list is empty
        if (opcodes.size() > 0)
            setOpcodeList3(OpcodeListWriter.toByteArray(opcodes));
    }

    public void setNoiseProfile(double[] noiseProfile)
    {
        setNoiseProfileNative(pointer, noiseProfile);
    }

    public void writeImageToFile(String fileName, int width, int height, int bpl, boolean shouldInvertRows, byte[] imageData, boolean uncompressed, boolean calculateDigest)
    {
        writeImageToFileNative(pointer, fileName, width, height, bpl, shouldInvertRows, imageData, uncompressed, calculateDigest);
    }

    public long getExifHandle()
    {
        return getExifHandleNative(pointer);
    }

    public long getNativeHandle()
    {
        return pointer;
    }
}
