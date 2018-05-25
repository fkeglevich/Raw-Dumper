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

package com.fkeglevich.rawdumper.camera.async.impl;

import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.Closeable;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.async.direct.LowLevelCamera;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.data.WhiteBalancePreset;
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.data.mode.ModeList;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.feature.FeatureRecyclerFactory;
import com.fkeglevich.rawdumper.camera.feature.FocusFeature;
import com.fkeglevich.rawdumper.camera.feature.ManualFocusFeature;
import com.fkeglevich.rawdumper.camera.feature.ManualTemperatureFeature;
import com.fkeglevich.rawdumper.camera.feature.PictureFormatFeature;
import com.fkeglevich.rawdumper.camera.feature.PictureModeFeature;
import com.fkeglevich.rawdumper.camera.feature.PictureSizeFeature;
import com.fkeglevich.rawdumper.camera.feature.PreviewFeature;
import com.fkeglevich.rawdumper.camera.feature.VirtualFeatureRecyclerFactory;
import com.fkeglevich.rawdumper.camera.feature.WhiteBalancePresetFeature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.camera.feature.restriction.ExposureRestriction;
import com.fkeglevich.rawdumper.camera.feature.restriction.FocusRestriction;
import com.fkeglevich.rawdumper.camera.feature.restriction.WhiteBalanceRestriction;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.ModeRestrictionChain;
import com.fkeglevich.rawdumper.camera.service.CameraServiceManager;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.util.Nullable;
import com.fkeglevich.rawdumper.util.ThreadUtil;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 03/10/17.
 */

public class TurboCameraImpl implements TurboCamera, Closeable
{
    private final LowLevelCamera lowLevelCamera;
    private final FeatureRecyclerFactory recyclerFactory;
    private final VirtualFeatureRecyclerFactory virtualRecyclerFactory;

    private WritableFeature<Iso, List<Iso>>                     isoFeature;
    private Feature<Nullable<Iso>>                              isoMeteringFeature;
    private WritableFeature<ShutterSpeed, List<ShutterSpeed>>   shutterSpeedFeature;
    private Feature<Nullable<ShutterSpeed>>                     ssMeteringFeature;
    private WritableFeature<Ev, List<Ev>>                       evFeature;
    private WritableFeature<Flash, List<Flash>>                 flashFeature;
    private PreviewFeature                                      previewFeature;
    private PictureSizeFeature                                  pictureSizeFeature;
    private FocusFeature                                        focusFeature;
    private ManualFocusFeature                                  manualFocusFeature;
    private WhiteBalancePresetFeature                           whiteBalancePresetFeature;
    private ManualTemperatureFeature                            manualTemperatureFeature;
    private PictureModeFeature                                  pictureModeFeature;
    private PictureFormatFeature                                pictureFormatFeature;

    private ExposureRestriction                                 exposureRestriction;
    private FocusRestriction                                    focusRestriction;
    private ModeRestrictionChain                                modeRestrictionChain;
    private WhiteBalanceRestriction                             whiteBalanceRestriction;
    private final ModeList modeList;

    public TurboCameraImpl(LowLevelCamera lowLevelCamera)
    {
        this.lowLevelCamera = lowLevelCamera;
        this.recyclerFactory = new FeatureRecyclerFactory(  lowLevelCamera.getCameraContext(),
                                                            lowLevelCamera.getParameterCollection(),
                                                            lowLevelCamera.getAsyncParameterSender(),
                                                            lowLevelCamera.getCameraActions());

        modeList = new ModeList(lowLevelCamera.getParameterCollection(), lowLevelCamera.getCameraContext().getCameraInfo());
        virtualRecyclerFactory = new VirtualFeatureRecyclerFactory(lowLevelCamera.getCameraActions(), lowLevelCamera.getPictureSizeLayer());

        createFeatures();
        createVirtualFeatures();
        createRestrictions();
        //mode format
        pictureModeFeature.setValue(pictureModeFeature.getAvailableValues().get(0));
        pictureFormatFeature.setValue(pictureFormatFeature.getAvailableValues().get(1));
        if (DebugFlag.useSmallestPicSize())
            pictureSizeFeature.setValue(pictureSizeFeature.getAvailableValues().get(0));

        lowLevelCamera.getCameraActions().startPreview();
        ThreadUtil.simpleDelay(150);
    }

    private void createFeatures()
    {
        isoFeature          = recyclerFactory.createIsoFeature();
        isoMeteringFeature  = recyclerFactory.createIsoMeteringFeature();
        shutterSpeedFeature = recyclerFactory.createShutterSpeedFeature();
        ssMeteringFeature   = recyclerFactory.createSSMeteringFeature();
        evFeature           = recyclerFactory.createEVFeature();
        previewFeature      = recyclerFactory.createPreviewFeature();
        focusFeature        = recyclerFactory.createFocusFeature();
        manualFocusFeature  = recyclerFactory.createManualFocusFeature();
        whiteBalancePresetFeature = recyclerFactory.createWhiteBalancePresetFeature();
        manualTemperatureFeature  = recyclerFactory.createManualTemperatureFeature();
    }

    private void createVirtualFeatures()
    {
        pictureModeFeature      = virtualRecyclerFactory.createPictureModeFeature(modeList.getAvailableValues());
        pictureFormatFeature    = virtualRecyclerFactory.createPictureFormatFeature();
        pictureSizeFeature      = virtualRecyclerFactory.createPictureSizeFeature();
        flashFeature            = virtualRecyclerFactory.createFlashFeature(lowLevelCamera.getParameterCollection(), lowLevelCamera.getCameraContext());
    }

    private void createRestrictions()
    {
        exposureRestriction     = new ExposureRestriction(isoFeature, shutterSpeedFeature, evFeature);
        focusRestriction        = new FocusRestriction(focusFeature, manualFocusFeature);
        modeRestrictionChain    = new ModeRestrictionChain(pictureModeFeature, pictureFormatFeature, pictureSizeFeature, previewFeature, lowLevelCamera.getCameraActions());
        whiteBalanceRestriction = new WhiteBalanceRestriction(whiteBalancePresetFeature, manualTemperatureFeature);
    }

    @Override
    public WritableFeature<Iso, List<Iso>> getIsoFeature()
    {
        return isoFeature;
    }

    @Override
    public Feature<Nullable<Iso>> getIsoMeteringFeature()
    {
        return isoMeteringFeature;
    }

    @Override
    public WritableFeature<ShutterSpeed, List<ShutterSpeed>> getShutterSpeedFeature()
    {
        return shutterSpeedFeature;
    }

    @Override
    public Feature<Nullable<ShutterSpeed>> getSSMeteringFeature()
    {
        return ssMeteringFeature;
    }

    @Override
    public WritableFeature<Ev, List<Ev>> getEVFeature()
    {
        return evFeature;
    }

    @Override
    public WritableFeature<Flash, List<Flash>> getFlashFeature()
    {
        return flashFeature;
    }

    @Override
    public FocusFeature getFocusFeature()
    {
        return focusFeature;
    }

    @Override
    public ManualFocusFeature getManualFocusFeature()
    {
        return manualFocusFeature;
    }

    @Override
    public WritableFeature<WhiteBalancePreset, List<WhiteBalancePreset>> getWhiteBalancePresetFeature()
    {
        return whiteBalancePresetFeature;
    }

    @Override
    public ManualTemperatureFeature getManualTemperatureFeature()
    {
        return manualTemperatureFeature;
    }

    @Override
    public WritableFeature<Mode, List<Mode>> getPictureModeFeature()
    {
        return pictureModeFeature;
    }

    @Override
    public WritableFeature<PictureFormat, List<PictureFormat>> getPictureFormatFeature()
    {
        return pictureFormatFeature;
    }

    @Override
    public WritableFeature<CaptureSize, List<CaptureSize>> getPictureSizeFeature()
    {
        return pictureSizeFeature;
    }

    @Override
    public Feature<CaptureSize> getPreviewFeature()
    {
        return previewFeature;
    }

    @Override
    public void close()
    {
        CameraServiceManager.getInstance().disableFeatures();
        recyclerFactory.cleanUpAllFeatures();
        virtualRecyclerFactory.cleanUpAllFeatures();
        lowLevelCamera.close();
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        lowLevelCamera.getCameraActions().takePicture(pictureCallback, exceptionCallback);
    }
}
