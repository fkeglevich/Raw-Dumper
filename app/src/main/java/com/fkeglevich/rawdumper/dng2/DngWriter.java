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

package com.fkeglevich.rawdumper.dng2;

import com.fkeglevich.rawdumper.raw.info.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Simple class for generating DNG files.
 * The suggested order for calling the write* methods is:
 *  1 writeBasicHeader
 *  2 writeSensorInfo
 *  3 writeImageInfo
 *  4 writeColorInfo
 *  5 writeImageData
 *  6 writeExifInfo
 *
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

    public void writeBasicHeader(RawImageSize rawImageSize)
    {
        this.rawImageSize = rawImageSize;
        tiffWriter.setField(TiffTag.TIFFTAG_SUBFILETYPE,            DngDefaults.RAW_SUB_FILE_TYPE);
        tiffWriter.setField(TiffTag.TIFFTAG_PHOTOMETRIC,            DngDefaults.RAW_PHOTOMETRIC);
        tiffWriter.setField(TiffTag.TIFFTAG_SAMPLESPERPIXEL,        DngDefaults.RAW_SAMPLES_PER_PIXEL);
        tiffWriter.setField(TiffTag.TIFFTAG_PLANARCONFIG,           DngDefaults.RAW_PLANAR_CONFIG);
        tiffWriter.setField(TiffTag.TIFFTAG_DNGVERSION,             DngDefaults.DNG_VERSION, false);
        tiffWriter.setField(TiffTag.TIFFTAG_DNGBACKWARDVERSION,     DngDefaults.DNG_BACKWARD_VERSION, false);
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGEWIDTH,             rawImageSize.getRawBufferWidth());
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGELENGTH,            rawImageSize.getRawBufferHeight());
    }

    public void writeSensorInfo(SensorInfo sensorInfo)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_BITSPERSAMPLE,          sensorInfo.getNumOfBitsPerPixel());
        tiffWriter.setField(TiffTag.TIFFTAG_CFAREPEATPATTERNDIM,    DataFormatter.DEFAULT_CFA_REPEAT_PATTERN_DIM, false);
        tiffWriter.setField(TiffTag.TIFFTAG_CFAPATTERN,             DataFormatter.formatBayerPattern(sensorInfo.getBayerPattern()), false);
        tiffWriter.setField(TiffTag.TIFFTAG_WHITELEVEL,             DataFormatter.formatWhiteLevel(sensorInfo.getWhiteLevel()), true);
        tiffWriter.setField(TiffTag.TIFFTAG_BLACKLEVELREPEATDIM,    DataFormatter.DEFAULT_BLACK_LEVEL_REPEAT_DIM, false);
        tiffWriter.setField(TiffTag.TIFFTAG_BLACKLEVEL,             DataFormatter.formatBlackLevel(sensorInfo.getBlackLevel()), true);
    }

    public void writeImageInfo(ImageInfo imageInfo)
    {
        imageInfo.setTiffFields(tiffWriter);
    }

    public void writeColorInfo(ImageColorInfo colorInfo)
    {
        colorInfo.setTiffFields(tiffWriter);
    }

    public void writeExifInfo(ExifInfo exifInfo)
    {
        exifInfo.writeExifTags(tiffWriter);
    }

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
