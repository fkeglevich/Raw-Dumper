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

package com.fkeglevich.rawdumper.controller.feature;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;

import java.util.List;
import java.util.Locale;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 24/05/18.
 */
public class PictureSizeController extends RadioGroupController<CaptureSize>
{
    PictureSizeController(ActivityReference reference)
    {
        super(reference, reference.findViewById(R.id.pictureSizeRg));
    }

    @Override
    protected WritableFeature<CaptureSize, List<CaptureSize>> selectFeature(TurboCamera camera)
    {
        return camera.getPictureSizeFeature();
    }

    @Override
    protected String displayValue(CaptureSize value)
    {
        return value.displayValue() + " (" + value.getAspectRatioX() + ":" + value.getAspectRatioY() + ", " + calculateNumMegaPixels(value) + ")";
    }

    private String calculateNumMegaPixels(CaptureSize size)
    {
        return String.format(Locale.getDefault(), "%.2f", size.getNumberOfPixels() / 1E6f) + "MP";
    }
}
