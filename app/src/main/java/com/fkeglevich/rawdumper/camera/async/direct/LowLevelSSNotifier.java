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

import com.fkeglevich.rawdumper.camera.async.pipeline.picture.PicturePipeline;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.util.Mutable;

import static com.fkeglevich.rawdumper.camera.extension.IntelParameters.AE_MODE_AUTO;
import static com.fkeglevich.rawdumper.camera.extension.IntelParameters.AE_MODE_SHUTTER_PRIORITY;
import static com.fkeglevich.rawdumper.camera.extension.IntelParameters.KEY_AE_MODE;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 30/05/18.
 */
class LowLevelSSNotifier
{
    private ShutterSpeed latestShutterSpeed = null;

    public void notifyShutterSpeed(ShutterSpeed value, boolean requiresIntelCamera, Mutable<ICameraExtension> cameraExtension)
    {
        if (requiresIntelCamera && cameraExtension.get().hasIntelFeatures())
            updateNeededAEMode(value == null ? ShutterSpeed.AUTO : value, cameraExtension);

        latestShutterSpeed = value;
    }

    public void updatePipelineShutterSpeed(PicturePipeline picturePipeline)
    {
        picturePipeline.updateShutterSpeed(latestShutterSpeed);
    }

    private void updateNeededAEMode(ShutterSpeed shutterSpeed, Mutable<ICameraExtension> cameraExtension)
    {
        Camera.Parameters parameters = cameraExtension.get().getCameraDevice().getParameters();
        parameters.set(KEY_AE_MODE, ShutterSpeed.AUTO.equals(shutterSpeed) ? AE_MODE_AUTO :  AE_MODE_SHUTTER_PRIORITY);
        cameraExtension.get().getCameraDevice().setParameters(parameters);
    }
}
