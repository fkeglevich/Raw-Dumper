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

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.raw.capture.DateInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfoExtractor;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteUtil;
import com.fkeglevich.rawdumper.raw.capture.WhiteBalanceInfo;

/**
 * Created by Flávio Keglevich on 25/08/2017.
 * TODO: Add a class header comment!
 */

public class FromRawAndJpegBuilder extends CommonBuilder
{
    private final CameraSizePair pair;
    private final Camera.Parameters parameters;
    private final byte[] rawDataBytes;
    private final byte[] extraJpegBytes;

    private MakerNoteInfo makerNoteInfo;

    public FromRawAndJpegBuilder(CameraContext cameraContext, Camera.Parameters parameters,
                                 byte[] rawDataBytes, byte[] extraJpegBytes)
    {
        this(cameraContext, CameraSizePair.createFromParameters(parameters,
                cameraContext.getCameraInfo()), parameters,
                rawDataBytes, extraJpegBytes);
    }

    private FromRawAndJpegBuilder(CameraContext cameraContext, CameraSizePair cameraSizePair,
                                  Camera.Parameters parameters, byte[] rawDataBytes, byte[] extraJpegBytes)
    {
        super(cameraContext);
        this.pair = cameraSizePair;
        this.parameters = parameters;
        this.rawDataBytes = rawDataBytes;
        this.extraJpegBytes = extraJpegBytes;
        initDateInfo();
        initMakerNoteInfo();
    }

    private void initMakerNoteInfo()
    {
        byte[] mknBytes = MakerNoteUtil.readFromJpegBytes(extraJpegBytes);

        if (pair.getExtraCameraInfo().hasKnownMakernote())
            makerNoteInfo = new MakerNoteInfoExtractor(pair.getExtraCameraInfo().getSensor().getBaseISO()).extractFrom(mknBytes, pair.getExtraCameraInfo().getColor());
        else
            makerNoteInfo = new MakerNoteInfo(mknBytes);
    }

    @Override
    void initDateInfo()
    {
        dateInfo = DateInfo.createFromCurrentTime();
    }

    @Override
    public void buildCamera()
    {
        captureInfo.camera = pair.getExtraCameraInfo();
    }

    @Override
    public void buildWhiteBalanceInfo()
    {
        captureInfo.whiteBalanceInfo = WhiteBalanceInfo.create(pair.getExtraCameraInfo(), makerNoteInfo, parameters);
    }

    @Override
    public void buildImageSize()
    {
        captureInfo.imageSize = pair.getRawImageSize();
    }

    @Override
    public void buildOriginalRawFilename()
    {
        captureInfo.originalRawFilename = generateRawFilename();
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
