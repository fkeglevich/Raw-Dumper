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
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.data.ManualTemperature;
import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.data.WhiteBalancePreset;
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.data.mode.ModeList;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.feature.FeatureRecyclerFactory;
import com.fkeglevich.rawdumper.camera.feature.ListFeature;
import com.fkeglevich.rawdumper.camera.feature.PictureFormatFeature;
import com.fkeglevich.rawdumper.camera.feature.PictureModeFeature;
import com.fkeglevich.rawdumper.camera.feature.PictureSizeFeature;
import com.fkeglevich.rawdumper.camera.feature.PreviewFeature;
import com.fkeglevich.rawdumper.camera.feature.RangeFeature;
import com.fkeglevich.rawdumper.camera.feature.VirtualFeatureRecyclerFactory;
import com.fkeglevich.rawdumper.camera.feature.WhiteBalancePresetFeature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.camera.feature.restriction.ExposureRestriction;
import com.fkeglevich.rawdumper.camera.feature.restriction.FocusRestriction;
import com.fkeglevich.rawdumper.camera.feature.restriction.WhiteBalanceRestriction;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.ModeRestrictionChain;
import com.fkeglevich.rawdumper.camera.service.CameraServiceManager;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.raw.capture.RawSettings;
import com.fkeglevich.rawdumper.util.Nullable;
import com.fkeglevich.rawdumper.util.ThreadUtil;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final EventDispatcher<Void> onTakePicture = new SimpleDispatcher<>();

    private PreviewFeature                                      previewFeature;
    private PictureSizeFeature                                  pictureSizeFeature;
    private PictureModeFeature                                  pictureModeFeature;
    private PictureFormatFeature                                pictureFormatFeature;

    private ExposureRestriction                                 exposureRestriction;
    private FocusRestriction                                    focusRestriction;
    private ModeRestrictionChain                                modeRestrictionChain;
    private WhiteBalanceRestriction                             whiteBalanceRestriction;
    private final ModeList modeList;

    private final Map<Class, WritableFeature>   writableFeatures = new HashMap<>();
    private final Map<Class, Feature>           meteringFeatures = new HashMap<>();

    public TurboCameraImpl(LowLevelCamera lowLevelCamera)
    {
        this.lowLevelCamera = lowLevelCamera;
        this.recyclerFactory = new FeatureRecyclerFactory(  lowLevelCamera.getCameraContext(),
                                                            lowLevelCamera.getParameterCollection(),
                                                            lowLevelCamera.getAsyncParameterSender(),
                                                            lowLevelCamera.getCameraActions());

        modeList = new ModeList(lowLevelCamera.getParameterCollection(), lowLevelCamera.getCameraContext().getCameraInfo());
        virtualRecyclerFactory = new VirtualFeatureRecyclerFactory(lowLevelCamera.getCameraContext(), lowLevelCamera.getCameraActions(), lowLevelCamera.getPictureSizeLayer());

        createFeatures();
        createVirtualFeatures();
        createMeteringFeatures();
        createRestrictions();
        //mode format
        pictureModeFeature.setValue(pictureModeFeature.getAvailableValues().get(0));
        pictureFormatFeature.setValue(pictureFormatFeature.getAvailableValues().get(1));
        if (DebugFlag.useSmallestPicSize())
            pictureSizeFeature.setValue(pictureSizeFeature.getAvailableValues().get(0));

        recyclerFactory.loadFeaturesData(lowLevelCamera.getCameraContext());
        virtualRecyclerFactory.loadFeaturesData(lowLevelCamera.getCameraContext());

        lowLevelCamera.getCameraActions().startPreview();
        ThreadUtil.simpleDelay(150);
    }

    private void createFeatures()
    {
        ListFeature<Flash> flashFeature = virtualRecyclerFactory.createFlashFeature(lowLevelCamera.getParameterCollection(), lowLevelCamera.getCameraContext());

        writableFeatures.put(Iso.class, recyclerFactory.createIsoFeature());
        writableFeatures.put(Ev.class, recyclerFactory.createEVFeature());
        writableFeatures.put(FocusMode.class, virtualRecyclerFactory.createFocusFeature(lowLevelCamera.getParameterCollection(), flashFeature));
        writableFeatures.put(WhiteBalancePreset.class, recyclerFactory.createWhiteBalancePresetFeature());

        writableFeatures.put(ManualFocus.class, recyclerFactory.createManualFocusFeature());
        writableFeatures.put(ManualTemperature.class, recyclerFactory.createManualTemperatureFeature());

        //Virtual features

        writableFeatures.put(Flash.class, flashFeature);
        writableFeatures.put(ShutterSpeed.class, virtualRecyclerFactory.createShutterSpeedFeature(lowLevelCamera.getParameterCollection(), lowLevelCamera.getCameraContext()));

        previewFeature = recyclerFactory.createPreviewFeature();
    }

    private void createMeteringFeatures()
    {
        meteringFeatures.put(Iso.class, recyclerFactory.createIsoMeteringFeature());
        meteringFeatures.put(ShutterSpeed.class, recyclerFactory.createSSMeteringFeature());
    }

    private void createVirtualFeatures()
    {
        pictureModeFeature      = virtualRecyclerFactory.createPictureModeFeature(modeList.getAvailableValues());
        pictureFormatFeature    = virtualRecyclerFactory.createPictureFormatFeature();
        pictureSizeFeature      = virtualRecyclerFactory.createPictureSizeFeature();
    }

    private void createRestrictions()
    {
        exposureRestriction     = new ExposureRestriction(getListFeature(Iso.class), getListFeature(ShutterSpeed.class), getListFeature(Ev.class));
        focusRestriction        = new FocusRestriction(this, lowLevelCamera.getCameraContext().getCameraInfo());
        modeRestrictionChain    = new ModeRestrictionChain(pictureModeFeature, pictureFormatFeature, pictureSizeFeature, previewFeature, lowLevelCamera.getCameraActions());
        whiteBalanceRestriction = new WhiteBalanceRestriction((WhiteBalancePresetFeature) getListFeature(WhiteBalancePreset.class), getRangeFeature(ManualTemperature.class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ListFeature<T> getListFeature(Class<T> dataType)
    {
        return (ListFeature<T>) writableFeatures.get(dataType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> RangeFeature<T> getRangeFeature(Class<T> dataType)
    {
        return (RangeFeature<T>) writableFeatures.get(dataType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Feature<Nullable<T>> getMeteringFeature(Class<T> dataType)
    {
        return (Feature<Nullable<T>>)meteringFeatures.get(dataType);
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
    public RawSettings getRawSettings()
    {
        return lowLevelCamera.getCameraContext().getRawSettings();
    }

    @Override
    public void close()
    {
        CameraServiceManager.getInstance().disableFeatures();
        recyclerFactory.storeAndCleanupFeatures(lowLevelCamera.getCameraContext());
        virtualRecyclerFactory.storeAndCleanupFeatures(lowLevelCamera.getCameraContext());
        lowLevelCamera.close();
    }

    @Override
    public EventDispatcher<Void> getOnTakePicture()
    {
        return onTakePicture;
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        onTakePicture.dispatchEvent(null);
        lowLevelCamera.getCameraActions().takePicture(pictureCallback, exceptionCallback);
    }
}
