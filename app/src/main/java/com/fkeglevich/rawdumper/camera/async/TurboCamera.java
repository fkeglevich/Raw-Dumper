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

package com.fkeglevich.rawdumper.camera.async;

import com.fkeglevich.rawdumper.camera.action.TakePictureAction;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.feature.ListFeature;
import com.fkeglevich.rawdumper.camera.feature.RangeFeature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.raw.capture.RawSettings;
import com.fkeglevich.rawdumper.util.Nullable;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/10/17.
 */

public interface TurboCamera extends TakePictureAction
{
    <T> ListFeature<T> getListFeature(Class<T> dataType);
    <T extends Comparable<T>> RangeFeature<T> getRangeFeature(Class<T> dataType);
    <T> Feature<Nullable<T>> getMeteringFeature(Class<T> dataType);

    WritableFeature<Mode, List<Mode>> getPictureModeFeature();
    WritableFeature<PictureFormat, List<PictureFormat>> getPictureFormatFeature();
    WritableFeature<CaptureSize, List<CaptureSize>> getPictureSizeFeature();

    Feature<CaptureSize> getPreviewFeature();

    RawSettings getRawSettings();
}
