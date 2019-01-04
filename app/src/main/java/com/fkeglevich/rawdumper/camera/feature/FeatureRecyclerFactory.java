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

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.direct.AsyncParameterSender;
import com.fkeglevich.rawdumper.camera.data.Aperture;
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

    public FeatureRecyclerFactory(CameraContext cameraContext,
                                  ParameterCollection parameterCollection,
                                  AsyncParameterSender asyncParameterSender)
    {
        this.parameterCollection = parameterCollection;
        this.asyncParameterSender = asyncParameterSender;
        this.cameraContext = cameraContext;
    }

    /*
    List features
     */

    public ListFeature<Iso> createIsoFeature()
    {
        return register(IsoFeature.create(cameraContext.getExposureInfo(), parameterCollection));
    }

    public ListFeature<Ev> createEVFeature()
    {
        return register(EvFeature.create(parameterCollection));
    }

    public WhiteBalancePresetFeature createWhiteBalancePresetFeature()
    {
        return register(new WhiteBalancePresetFeature(parameterCollection));
    }

    /*
    Metering features
     */

    public Feature<Nullable<Iso>> createIsoMeteringFeature()
    {
        return register(new IsoMeteringFeature(parameterCollection, cameraContext.getSensorInfo()));
    }

    public Feature<Nullable<ShutterSpeed>> createSSMeteringFeature()
    {
        return register(new SSMeteringFeature(parameterCollection, cameraContext.getSensorInfo()));
    }

    public Feature<Nullable<Aperture>> createApertureMeteringFeature()
    {
        return register(new ApertureMeteringFeature(cameraContext.getLensInfo()));
    }

    public Feature<Nullable<Ev>> createEvMeteringFeature()
    {
        return register(new EvMeteringFeature());
    }

    public PreviewFeature createPreviewFeature()
    {
        return register(new PreviewFeature(parameterCollection));
    }

    /*
    Range features
     */

    public ManualFocusFeature createManualFocusFeature()
    {
        return register(new ManualFocusFeature(asyncParameterSender, parameterCollection));
    }

    public ManualTemperatureFeature createManualTemperatureFeature()
    {
        return register(new ManualTemperatureFeature(cameraContext.getColorInfo(),
                asyncParameterSender, parameterCollection));
    }
}
