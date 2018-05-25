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
import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;

import java.util.List;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 24/05/18.
 */
public class PictureFormatController extends RadioGroupController<PictureFormat>
{
    PictureFormatController(ActivityReference reference)
    {
        super(reference, reference.findViewById(R.id.pictureFormatRg));
    }

    @Override
    protected WritableFeature<PictureFormat, List<PictureFormat>> selectFeature(TurboCamera camera)
    {
        return camera.getPictureFormatFeature();
    }

    @Override
    protected String displayValue(PictureFormat value)
    {
        return value.getFileFormat().getExtension().substring(1).toUpperCase();
    }
}
