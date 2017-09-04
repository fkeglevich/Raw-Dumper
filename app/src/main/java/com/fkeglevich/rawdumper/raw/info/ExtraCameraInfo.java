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

import android.os.Build;
import android.support.annotation.Keep;

import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.util.Locale;

/**
 * Simple immutable class that stores specific information about
 * a single camera of the device.
 *
 * Created by Flávio Keglevich on 11/06/2017.
 */

@Keep
@SuppressWarnings({"unused", "ConstantConditions", "FieldCanBeLocal"})
public class ExtraCameraInfo
{
    private final int id = -1;
    private final String model = null;
    private final String uniqueCameraModel = null;

    private final SensorInfo sensor = null;
    private final LensInfo lens = null;
    private final ColorInfo color = null;
    private final ExposureInfo exposure = null;
    private final OpcodeListInfo[] opcodes = new OpcodeListInfo[0];
    private final NoiseInfo noise = null;

    private final boolean hasKnownMakernote = false;
    private final boolean retryOnError = false;

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_MODEL, String.format(Locale.US, model, Build.MODEL));
        tiffWriter.setField(TiffTag.TIFFTAG_UNIQUECAMERAMODEL, String.format(Locale.US, uniqueCameraModel, Build.MODEL));
    }

    public int getId()
    {
        return id;
    }

    public SensorInfo getSensor()
    {
        return sensor;
    }

    public LensInfo getLens()
    {
        return lens;
    }

    public ColorInfo getColor()
    {
        return color;
    }

    public ExposureInfo getExposure()
    {
        return exposure;
    }

    public OpcodeListInfo[] getOpcodes()
    {
        return opcodes;
    }

    public NoiseInfo getNoise()
    {
        return noise;
    }

    public boolean hasKnownMakernote()
    {
        return hasKnownMakernote;
    }
}
