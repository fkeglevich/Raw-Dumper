/*
 * Copyright 2018, Flávio Keglevich
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

import com.asus.camera.extensions.AsusCameraExtension;
import com.fkeglevich.rawdumper.camera.data.Aperture;
import com.fkeglevich.rawdumper.camera.extension.IntelParameters;
import com.fkeglevich.rawdumper.camera.service.ProDataMeteringService;
import com.fkeglevich.rawdumper.raw.info.LensInfo;
import com.fkeglevich.rawdumper.util.Nullable;

public class ApertureMeteringFeature extends ParameterlessFeature<Nullable<Aperture>>
{
    private final LensInfo lensInfo;

    ApertureMeteringFeature(LensInfo lensInfo)
    {
        super(IntelParameters.KEY_APERTURE);
        this.lensInfo = lensInfo;
    }

    @Override
    public Nullable<Aperture> getValue()
    {
        AsusCameraExtension.ProfessionalData data = ProDataMeteringService.getInstance().getLatestData();
        if (data != null)
            return Nullable.of(data.getAperture());

        return Nullable.of(lensInfo.getAperture());
    }

    @Override
    public boolean isAvailable()
    {
        return ProDataMeteringService.getInstance().isAvailable()
                || lensInfo.getAperture() != null;
    }
}
