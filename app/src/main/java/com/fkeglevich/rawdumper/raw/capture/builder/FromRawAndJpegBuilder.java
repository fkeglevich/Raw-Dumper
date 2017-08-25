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

package com.fkeglevich.rawdumper.raw.capture.builder;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.raw.capture.DateExtractor;
import com.fkeglevich.rawdumper.raw.capture.FilenameExtractor;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfoExtractor;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteUtil;
import com.fkeglevich.rawdumper.raw.capture.WhiteBalanceInfoExtractor;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;

import java.io.File;

/**
 * Created by Flávio Keglevich on 25/08/2017.
 * TODO: Add a class header comment!
 */

public class FromRawAndJpegBuilder extends BaseDateBuilder
{
    private final DeviceInfo device;
    private final CameraSizePair pair;
    private final Camera.Parameters parameters;
    private final ImageOrientation orientation;
    private final byte[] rawDataBytes;
    private final byte[] extraJpegBytes;
    private final WhiteBalanceInfoExtractor whiteBalanceExtractor;

    private MakerNoteInfo makerNoteInfo;

    public FromRawAndJpegBuilder(DeviceInfo device, CameraSizePair cameraSizePair,
                                 Camera.Parameters parameters, ImageOrientation orientation,
                                 byte[] rawDataBytes, byte[] extraJpegBytes)
    {
        super();
        this.device = device;
        this.pair = cameraSizePair;
        this.parameters = parameters;
        this.orientation = orientation;
        this.rawDataBytes = rawDataBytes;
        this.extraJpegBytes = extraJpegBytes;
        this.whiteBalanceExtractor = new WhiteBalanceInfoExtractor();
        initMakerNoteInfo();
    }

    private void initMakerNoteInfo()
    {
        byte[] mknBytes = MakerNoteUtil.readFromJpegBytes(extraJpegBytes);

        if (pair.getExtraCameraInfo().hasKnownMakernote())
            makerNoteInfo = new MakerNoteInfoExtractor(pair.getExtraCameraInfo().getSensor().getBaseISO()).extractFrom(mknBytes);
        else
            makerNoteInfo = new MakerNoteInfo(mknBytes);
    }

    @Override
    void initDateInfo()
    {
        dateInfo = new DateExtractor().extractFromCurrentTime();
    }

    @Override
    public void buildDevice()
    {
        captureInfo.device = device;
    }

    @Override
    public void buildCamera()
    {
        captureInfo.camera = pair.getExtraCameraInfo();
    }

    @Override
    public void buildWhiteBalanceInfo()
    {
        ColorInfo colorInfo = pair.getExtraCameraInfo().getColor();

        if (pair.getExtraCameraInfo().hasKnownMakernote())
            captureInfo.whiteBalanceInfo = whiteBalanceExtractor.extractFrom(makerNoteInfo, colorInfo);
        else
            captureInfo.whiteBalanceInfo = whiteBalanceExtractor.extractFrom(colorInfo);
    }

    @Override
    public void buildImageSize()
    {
        captureInfo.imageSize = pair.getRawImageSize();
    }

    @Override
    public void buildOriginalRawFilename()
    {
        captureInfo.originalRawFilename = new FilenameExtractor().extractFromDateInfo(dateInfo);
    }

    @Override
    public void buildOrientation()
    {
        captureInfo.orientation = orientation;
    }

    @Override
    public void buildMakerNoteInfo()
    {
        captureInfo.makerNoteInfo = makerNoteInfo;
    }

    @Override
    public void buildCaptureParameters()
    {
        captureInfo.captureParameters = parameters;
    }

    @Override
    public void buildExtraJpegBytes()
    {
        captureInfo.extraJpegBytes = extraJpegBytes;
    }

    @Override
    public void buildRawDataBytes()
    {
        captureInfo.rawDataBytes = rawDataBytes;
    }

    @Override
    public void buildRelatedI3av4File()
    {
        captureInfo.captureParameters = null;
    }
}
