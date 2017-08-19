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

package com.fkeglevich.rawdumper.dng;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Simple class for generating DNG files.
 * Created by Flávio Keglevich on 16/04/2017.
 */

public class DngWriter
{
    private TiffWriter tiffWriter;
    private RawImageSize rawImageSize;

    public static DngWriter open(String dngFile)
    {
        TiffWriter tiffWriter =  TiffWriter.open(dngFile);
        if (tiffWriter == null) return null;

        return new DngWriter(tiffWriter);
    }

    private DngWriter(TiffWriter tiffWriter)
    {
        this.tiffWriter = tiffWriter;
    }

    public void close()
    {
        tiffWriter.close();
        tiffWriter = null;
        rawImageSize = null;
    }

    public void writeMetadata(Context context, CaptureInfo captureInfo)
    {
        writeBasicHeader(captureInfo.imageSize);
        captureInfo.camera.getSensor().writeTiffTags(tiffWriter);
        captureInfo.camera.writeTiffTags(tiffWriter);
        captureInfo.device.writeTiffTags(tiffWriter);
        captureInfo.writeTiffTags(tiffWriter);

        tiffWriter.setField(TiffTag.TIFFTAG_SOFTWARE, getSoftwareName(context));

        captureInfo.date.writeTiffTags(tiffWriter);
        captureInfo.camera.getColor().writeTiffTags(tiffWriter);
        captureInfo.whiteBalanceInfo.writeTiffTags(tiffWriter);

        if (captureInfo.camera.getOpcodes() != null && captureInfo.camera.getOpcodes().length >= 1)
            captureInfo.camera.getOpcodes()[0].writeTiffTags(tiffWriter, context);
    }

    private String getSoftwareName(Context context)
    {
        String softwareName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        PackageInfo packageInfo;
        try
        {
            packageInfo = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            packageInfo = null;
        }

        if (packageInfo != null)
            return softwareName + " v" + packageInfo.versionName;
        else
            return softwareName;
    }

    private void writeBasicHeader(RawImageSize rawImageSize)
    {
        this.rawImageSize = rawImageSize;
        tiffWriter.setField(TiffTag.TIFFTAG_SUBFILETYPE,            DngDefaults.RAW_SUB_FILE_TYPE);
        tiffWriter.setField(TiffTag.TIFFTAG_PHOTOMETRIC,            DngDefaults.RAW_PHOTOMETRIC);
        tiffWriter.setField(TiffTag.TIFFTAG_SAMPLESPERPIXEL,        DngDefaults.RAW_SAMPLES_PER_PIXEL);
        tiffWriter.setField(TiffTag.TIFFTAG_PLANARCONFIG,           DngDefaults.RAW_PLANAR_CONFIG);
        tiffWriter.setField(TiffTag.TIFFTAG_DNGVERSION,             DngDefaults.DNG_VERSION, false);
        tiffWriter.setField(TiffTag.TIFFTAG_DNGBACKWARDVERSION,     DngDefaults.DNG_BACKWARD_VERSION, false);
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGEWIDTH,             rawImageSize.getPaddedWidth());
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGELENGTH,            rawImageSize.getPaddedHeight());
    }

    public void writeExifInfo(CaptureInfo captureInfo)
    {
        ExifWriter exifWriter = new ExifWriter();
        exifWriter.createEXIFDirectory(tiffWriter);
        exifWriter.writeTiffExifTags(tiffWriter, captureInfo);
        exifWriter.closeEXIFDirectory(tiffWriter);
    }

    //These should be removed!
    public void writeImageData(ADngImageWriter writer, byte[] rawdata)
    {
        writer.writeImageData(tiffWriter, rawImageSize, rawdata);
    }

    public void writeImageData(ADngImageWriter writer, RandomAccessFile file) throws IOException
    {
        writer.writeImageData(tiffWriter, rawImageSize, file);
    }

    public void writeImageData(ADngImageWriter writer, String filePath) throws IOException
    {
        writer.writeImageData(tiffWriter, rawImageSize, filePath);
    }
}
