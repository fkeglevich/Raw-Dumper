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

import android.support.annotation.Keep;

import com.fkeglevich.rawdumper.dng.dngsdk.DngNegative;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

/**
 * Contains all noise-related information about a camera.
 *
 * Created by Flávio Keglevich on 27/08/2017.
 */

@Keep
@SuppressWarnings("unused")
public class NoiseInfo
{
    private double[] noiseProfile;

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        if (noiseProfile != null && noiseProfile.length > 0)
            tiffWriter.setField(TiffTag.TIFFTAG_NOISEPROFILE, noiseProfile, true);
    }

    public void writeInfo(DngNegative negative)
    {
        if (noiseProfile != null && noiseProfile.length > 0)
            negative.setNoiseProfile(noiseProfile);
    }
}
