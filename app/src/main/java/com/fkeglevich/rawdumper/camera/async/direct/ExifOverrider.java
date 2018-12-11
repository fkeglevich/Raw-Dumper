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

package com.fkeglevich.rawdumper.camera.async.direct;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.extension.IntelParameters;
import com.fkeglevich.rawdumper.util.AppPackageUtil;

class ExifOverrider
{
    static void overrideExifParameters(Camera camera)
    {
        //Overrides EXIF software tag with app's name
        try
        {
            Camera.Parameters parameters = camera.getParameters();
            parameters.set(IntelParameters.KEY_EXIF_SOFTWARE, AppPackageUtil.getAppNameWithVersion());
            camera.setParameters(parameters);
        }
        catch (RuntimeException ignored)
        {   }
    }
}
