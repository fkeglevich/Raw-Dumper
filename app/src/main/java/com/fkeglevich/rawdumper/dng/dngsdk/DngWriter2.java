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

import com.fkeglevich.rawdumper.dng.DngWriter;
import com.fkeglevich.rawdumper.dng.tiffwriter.ADngImageWriter;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.ExifInfo;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.IOException;

public class DngWriter2 implements DngWriter
{
    private static final String TAG = "DngWriter2";
    private static final boolean LOG_ERRORS = true;

    static
    {
        System.loadLibrary("dng-writer");
    }

    public static native long openNative(String filePath);
    public native void dummy();

    public static DngWriter2 open(String filepath)
    {
        long pointer = openNative(filepath);
        return (pointer != 0) ? new DngWriter2(pointer) : null;
    }

    private long pointer = 0;

    public DngWriter2(long pointer)
    {
        this.pointer = pointer;
    }

    @Override
    public void write(CaptureInfo captureInfo, ADngImageWriter writer, RawImageData imageData) throws IOException
    {
        /*try
        {
            ExifInfo exifInfo = new ExifInfo();
            exifInfo.getExifDataFromCapture(captureInfo);

            writeMetadata(captureInfo, exifInfo);
            writer.writeImageData(tiffWriter, imageData, captureInfo.shouldInvertRows());
            writeExifInfo(exifInfo);
        }
        finally
        {
            close();
        }*/
    }

    private void writeBasicHeader(RawImageSize rawImageSize)
    {

    }
}
