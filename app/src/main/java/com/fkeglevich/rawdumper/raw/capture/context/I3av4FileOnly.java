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

package com.fkeglevich.rawdumper.raw.capture.context;

import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.DateExtractor;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfoExtractor;
import com.fkeglevich.rawdumper.raw.capture.WhiteBalanceInfo;
import com.fkeglevich.rawdumper.raw.capture.WhiteBalanceInfoExtractor;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Fills a CaptureInfo object when the only sources of information are a DeviceInfo object and
 * a .i3av4 raw file.
 *
 * Created by Flávio Keglevich on 24/07/2017.
 */

public class I3av4FileOnly implements ICaptureContext
{
    private MakerNoteInfoExtractor makerNoteInfoExtractor;
    private WhiteBalanceInfoExtractor whiteBalanceExtractor;
    private DateExtractor dateExtractor;

    /**
     * Creates a new I3av4FileOnly object
     */
    public I3av4FileOnly()
    {
        this.makerNoteInfoExtractor = new MakerNoteInfoExtractor();
        this.whiteBalanceExtractor = new WhiteBalanceInfoExtractor();
        this.dateExtractor = new DateExtractor();
    }

    @Override
    public void fillCaptureInfo(CaptureInfo captureInfo)
    {
        if (!canFillCaptureInfo(captureInfo))
            throw new RuntimeException("This capture does not contain enough information!");

        List<CameraSizePair> pairList = initializePairList(captureInfo.device);
        CameraSizePair pair = getBestPair(captureInfo.relatedI3av4File.length(), pairList);

        MakerNoteInfo makerNoteInfo;
        WhiteBalanceInfo whiteBalanceInfo;
        byte[] mknBytes = readMknFromFile(captureInfo.relatedI3av4File, pair.getRawImageSize());

        if (pair.getExtraCameraInfo().hasKnownMakernote())
        {
            makerNoteInfo = makerNoteInfoExtractor.extractFrom(mknBytes);
            whiteBalanceInfo = whiteBalanceExtractor.extractFrom(captureInfo.makerNoteInfo, pair.getExtraCameraInfo().getColor());
        }
        else
        {
            makerNoteInfo = new MakerNoteInfo(mknBytes);
            whiteBalanceInfo = whiteBalanceExtractor.extractFrom(pair.getExtraCameraInfo().getColor());
        }

        //Filling missing required fields
        captureInfo.camera = pair.getExtraCameraInfo();
        captureInfo.date = dateExtractor.extractFromFilename(captureInfo.relatedI3av4File.getName());
        captureInfo.whiteBalanceInfo = whiteBalanceInfo;
        captureInfo.imageSize = pair.getRawImageSize();
        captureInfo.originalRawFilename = captureInfo.relatedI3av4File.getName();
        captureInfo.orientation = ImageOrientation.TOPLEFT;

        //Filling optional fields
        captureInfo.makerNoteInfo = makerNoteInfo;
        captureInfo.captureParameters = null;
        captureInfo.extraJpegBytes = null;
    }

    @Override
    public boolean canFillCaptureInfo(CaptureInfo captureInfo)
    {
        return captureInfo.device != null && captureInfo.relatedI3av4File != null;
    }

    /**
     * Reads the maker notes (header) from the i3av4 file.
     *
     * @param i3av4File The i3av4 file
     * @param imageSize The raw size of the image contained in the file
     * @return  A byte array containing the maker notes (or null if the file couldn't be read)
     */
    private byte[] readMknFromFile(File i3av4File, RawImageSize imageSize)
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
     * Initializes a camera size pair list containing all valid combinations of
     * device cameras and raw image sizes.
     *
     * @param device    The DeviceInfo object whose the information is taken from
     * @return  A list of camera size pairs
     */
    private List<CameraSizePair> initializePairList(DeviceInfo device)
    {
        List<CameraSizePair> pairList = new ArrayList<>();
        for (ExtraCameraInfo extraCameraInfo : device.getCameras())
            for (RawImageSize imageSize : extraCameraInfo.getSensor().getRawImageSizes())
                pairList.add(new CameraSizePair(imageSize, extraCameraInfo));

        return pairList;
    }

    /**
     * Finds the nearest CameraSizePair relative to the size of the i3av4 file.
     *
     * @param i3av4Size Size of the file
     * @param pairList  List of the available camera size pairs
     * @return  The chosen camera size pair
     */
    private CameraSizePair getBestPair(long i3av4Size, List<CameraSizePair> pairList)
    {
        long diff = Long.MAX_VALUE;
        long currentDiff;
        long currentLength;
        CameraSizePair finalConfig = null;
        for (CameraSizePair config : pairList)
        {
            currentLength = config.getRawImageSize().getBufferLength();
            currentDiff = i3av4Size - currentLength;
            if (i3av4Size > currentLength && currentDiff < diff)
            {
                diff = currentDiff;
                finalConfig = config;
            }
        }
        return finalConfig;
    }
}
