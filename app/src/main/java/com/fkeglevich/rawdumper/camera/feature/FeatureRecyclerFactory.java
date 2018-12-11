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
import com.fkeglevich.rawdumper.camera.async.direct.AsyncParameterSender;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.util.Nullable;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/10/17.
 */

public class FeatureRecyclerFactory extends FeatureRecyclerFactoryBase
{
    private final CameraContext cameraContext;
    private final ParameterCollection parameterCollection;
    private final AsyncParameterSender asyncParameterSender;
    private final CameraActions cameraActions;

    public FeatureRecyclerFactory(CameraContext cameraContext,
                                  ParameterCollection parameterCollection,
                                  AsyncParameterSender asyncParameterSender,
                                  CameraActions cameraActions)
    {
        this.parameterCollection = parameterCollection;
        this.asyncParameterSender = asyncParameterSender;
        this.cameraActions = cameraActions;
        this.cameraContext = cameraContext;
    }

    /*
    List features
     */

    public ListFeature<Iso> createIsoFeature()
    {
        IsoFeature result = IsoFeature.create(cameraContext.getExposureInfo(), parameterCollection);
        registerFeature(result);
        return result;
    }

    public ListFeature<Ev> createEVFeature()
    {
        EvFeature result = EvFeature.create(parameterCollection);
        registerFeature(result);
        return result;
    }

    public WhiteBalancePresetFeature createWhiteBalancePresetFeature()
    {
        WhiteBalancePresetFeature result = new WhiteBalancePresetFeature(parameterCollection);
        registerFeature(result);
        return result;
    }

    /*
    Metering features
     */

    public Feature<Nullable<Iso>> createIsoMeteringFeature()
    {
        IsoMeteringFeature result = new IsoMeteringFeature(parameterCollection, cameraContext.getSensorInfo());
        registerFeature(result);
        return result;
    }

    public Feature<Nullable<ShutterSpeed>> createSSMeteringFeature()
    {
        SSMeteringFeature result = new SSMeteringFeature(parameterCollection, cameraContext.getSensorInfo());
        registerFeature(result);
        return result;
    }

    public PreviewFeature createPreviewFeature()
    {
        PreviewFeature result = new PreviewFeature(parameterCollection);
        registerFeature(result);
        return result;
    }

    /*
    Range features
     */

    public ManualFocusFeature createManualFocusFeature()
    {
        ManualFocusFeature result = new ManualFocusFeature(asyncParameterSender, parameterCollection);
        registerFeature(result);
        return result;
    }

    public ManualTemperatureFeature createManualTemperatureFeature()
    {
        ManualTemperatureFeature result = new ManualTemperatureFeature(cameraContext.getColorInfo(), asyncParameterSender, parameterCollection);
        registerFeature(result);
        return result;
    }
}
