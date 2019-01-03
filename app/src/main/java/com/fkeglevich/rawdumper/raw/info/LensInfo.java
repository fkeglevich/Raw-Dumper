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

package com.fkeglevich.rawdumper.raw.info;

import com.fkeglevich.rawdumper.camera.data.Aperture;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

/**
 * Contains all lens-related information about a camera.
 *
 * Created by Flávio Keglevich on 11/06/2017.
 */

@Keep
@SuppressWarnings("unused")
public class LensInfo
{
    private Double aperture = null;
    private Boolean opticalZoom = null;

    @Nullable
    public Aperture getAperture()
    {
        if (aperture == null || Aperture.isInvalidAperture(aperture))
            return null;

        return Aperture.create(aperture);
    }

    @Nullable
    public Double getNumericAperture()
    {
        Aperture aperture = getAperture();
        if (aperture != null)
            return aperture.getValue();

        return null;
    }

    public boolean hasOpticalZoom()
    {
        return (opticalZoom == null) ? false : opticalZoom;
    }
}
