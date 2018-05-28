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
    private final CameraActions cameraActions;
    private final ParameterCollection pictureSizeCollection;

    public VirtualFeatureRecyclerFactory(CameraActions cameraActions, ParameterCollection pictureSizeCollection)
    {
        this.cameraActions = cameraActions;
        this.pictureSizeCollection = pictureSizeCollection;
    }

    public PictureModeFeature createPictureModeFeature(List<Mode> valueList)
    {
        final PictureModeFeature result = new PictureModeFeature(virtualParameterCollection, cameraActions, valueList);
        registerFeature(result);
        return result;
    }

    public PictureFormatFeature createPictureFormatFeature()
    {
        PictureFormatFeature result = new PictureFormatFeature(virtualParameterCollection, cameraActions);
        registerFeature(result);
        return result;
    }

    public PictureSizeFeature createPictureSizeFeature()
    {
        PictureSizeFeature result = new PictureSizeFeature(pictureSizeCollection);
        List<CaptureSize> pictureSizes = new ArrayList<>(result.getAvailableValues());
        Collections.sort(pictureSizes);
        result.setValue(pictureSizes.get(pictureSizes.size() - 1));
        registerFeature(result);
        return result;
    }

    public WritableFeature<Flash, List<Flash>> createFlashFeature(ParameterCollection cameraParameterCollection, CameraContext cameraContext)
    {
        FlashFeature result = new FlashFeature(virtualParameterCollection, cameraParameterCollection, cameraActions, cameraContext);
        registerFeature(result);
        return result;
    }

    public WritableFeature<ShutterSpeed, List<ShutterSpeed>> createShutterSpeedFeature(ParameterCollection cameraParameterCollection, CameraContext cameraContext)
    {
        ShutterSpeedFeature result = ShutterSpeedFeature.create(cameraContext.getExposureInfo(), cameraParameterCollection, cameraActions);
        registerFeature(result);
        return result;
    }
}
