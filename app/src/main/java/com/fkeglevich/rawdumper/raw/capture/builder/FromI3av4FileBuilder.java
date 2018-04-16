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
import com.fkeglevich.rawdumper.controller.orientation.OrientationManager;
import com.fkeglevich.rawdumper.raw.capture.DateInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfoExtractor;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteUtil;
import com.fkeglevich.rawdumper.raw.capture.WhiteBalanceInfo;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;

import java.io.File;

/**
 * Builds a CaptureInfo object when the main sources of information are a DeviceInfo object and
 * a .i3av4 raw file.
 *
 * Created by Flávio Keglevich on 25/08/2017.
 */

public class FromI3av4FileBuilder extends CommonBuilder
{
    private final DeviceInfo device;
    private final File relatedI3av4File;
    private final CameraSizePair pair;
    private final Camera.Parameters parameters;
    private final ImageOrientation orientation;

    private MakerNoteInfo makerNoteInfo;

    public FromI3av4FileBuilder(CameraContext cameraContext, File relatedI3av4File, Camera.Parameters parameters)
    {
        this(cameraContext.getDeviceInfo(), CameraSizePair.createFromParameters(parameters,
                cameraContext.getCameraInfo()), relatedI3av4File, parameters,
                OrientationManager.getInstance().getImageOrientation(cameraContext));
    }

    private FromI3av4FileBuilder(DeviceInfo device, CameraSizePair cameraSizePair, File relatedI3av4File, Camera.Parameters parameters, ImageOrientation orientation)
    {
        super();
        this.device = device;
        this.pair = cameraSizePair;
        this.relatedI3av4File = relatedI3av4File;
        this.parameters = parameters;
        this.orientation = orientation;
        initDateInfo();
        initMakerNoteInfo();
    }

    public FromI3av4FileBuilder(DeviceInfo device, File relatedI3av4File, Camera.Parameters parameters)
    {
        this(device, new CameraSizePairList(device).getBestPair(relatedI3av4File.length()), relatedI3av4File, parameters, ImageOrientation.TOPLEFT);
    }

    public FromI3av4FileBuilder(DeviceInfo device, File relatedI3av4File)
    {
        this(device, relatedI3av4File, null);
    }

    private void initMakerNoteInfo()
    {
        byte[] mknBytes = MakerNoteUtil.readFromI3av4File(relatedI3av4File, pair.getRawImageSize());

        if (pair.getExtraCameraInfo().hasKnownMakernote())
            makerNoteInfo = new MakerNoteInfoExtractor(pair.getExtraCameraInfo().getSensor().getBaseISO()).extractFrom(mknBytes, pair.getExtraCameraInfo().getColor());
        else
            makerNoteInfo = new MakerNoteInfo(mknBytes);
    }

    @Override
    void initDateInfo()
    {
        dateInfo = DateInfo.createFromFilename(relatedI3av4File.getName());
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
        captureInfo.whiteBalanceInfo = WhiteBalanceInfo.create(pair.getExtraCameraInfo(), makerNoteInfo);
    }

    @Override
    public void buildImageSize()
    {
        captureInfo.imageSize = pair.getRawImageSize();
    }

    @Override
    public void buildOriginalRawFilename()
    {
        captureInfo.originalRawFilename = relatedI3av4File.getName();
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
        captureInfo.extraJpegBytes = null;
    }

    @Override
    public void buildRawDataBytes()
    {
        captureInfo.rawDataBytes = null;
    }

    @Override
    public void buildRelatedI3av4File()
    {
        captureInfo.relatedI3av4File = relatedI3av4File;
    }
}
