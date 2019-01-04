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

package com.fkeglevich.rawdumper.camera.feature;

import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.parameter.CodeclessParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class VirtualFeatureRecyclerFactory extends FeatureRecyclerFactoryBase
{
    private final ParameterCollection virtualParameterCollection = new CodeclessParameterCollection();
    private final CameraContext cameraContext;
    private final CameraActions cameraActions;
    private final ParameterCollection pictureSizeCollection;

    public VirtualFeatureRecyclerFactory(CameraContext cameraContext, CameraActions cameraActions, ParameterCollection pictureSizeCollection)
    {
        this.cameraContext = cameraContext;
        this.cameraActions = cameraActions;
        this.pictureSizeCollection = pictureSizeCollection;
    }

    public PictureModeFeature createPictureModeFeature(List<Mode> valueList)
    {
        return register(new PictureModeFeature(virtualParameterCollection, cameraActions, valueList));
    }

    public PictureFormatFeature createPictureFormatFeature()
    {
        return register(new PictureFormatFeature(virtualParameterCollection, cameraActions));
    }

    public PictureSizeFeature createPictureSizeFeature()
    {
        PictureSizeFeature result = new PictureSizeFeature(pictureSizeCollection);
        List<CaptureSize> pictureSizes = new ArrayList<>(result.getAvailableValues());
        Collections.sort(pictureSizes);
        result.setValue(pictureSizes.get(pictureSizes.size() - 1));
        return register(result);
    }

    public ListFeature<Flash> createFlashFeature(ParameterCollection cameraParameterCollection, CameraContext cameraContext)
    {
        return register(new FlashFeature(virtualParameterCollection, cameraParameterCollection, cameraActions, cameraContext));
    }

    public WritableFeature<ShutterSpeed, List<ShutterSpeed>> createShutterSpeedFeature(ParameterCollection cameraParameterCollection, CameraContext cameraContext)
    {
        return register(ShutterSpeedFeature.create(cameraContext.getExposureInfo(), cameraParameterCollection, cameraActions));
    }

    public ListFeature<FocusMode> createFocusFeature(ParameterCollection cameraParameterCollection, ListFeature<Flash> flashFeature)
    {
        return register(new FocusFeature(virtualParameterCollection, cameraParameterCollection, flashFeature, cameraContext.getCameraInfo().getFocus(), cameraActions));
    }
}
