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

package com.fkeglevich.rawdumper.camera.async.direct;

import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.Closeable;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/10/17.
 */

public interface LowLevelCamera extends Closeable
{
    CameraContext getCameraContext();
    ParameterCollection getParameterCollection();
    ParameterCollection getPictureSizeLayer();
    CameraActions getCameraActions();
}
