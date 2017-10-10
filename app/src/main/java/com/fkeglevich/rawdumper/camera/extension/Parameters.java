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

package com.fkeglevich.rawdumper.camera.extension;

import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterFactory;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 22/09/17.
 */

public class Parameters
{
    public static final Parameter<Integer> MIN_EXPOSURE_COMPENSATION = ParameterFactory.createReadOnly("min-exposure-compensation", Integer.TYPE);
    public static final Parameter<Integer> MAX_EXPOSURE_COMPENSATION = ParameterFactory.createReadOnly("max-exposure-compensation", Integer.TYPE);
    public static final Parameter<Float> EXPOSURE_COMPENSATION_STEP = ParameterFactory.createReadOnly("exposure-compensation-step", Float.TYPE);

    public static final Parameter<CaptureSize> PREVIEW_SIZE = ParameterFactory.create("preview-size", CaptureSize.class);
}