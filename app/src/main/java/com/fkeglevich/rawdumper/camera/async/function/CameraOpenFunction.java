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

package com.fkeglevich.rawdumper.camera.async.function;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunction;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.async.direct.LowLevelCamera;
import com.fkeglevich.rawdumper.camera.async.direct.LowLevelCameraImpl;
import com.fkeglevich.rawdumper.camera.async.impl.TurboCameraImpl;
import com.fkeglevich.rawdumper.camera.exception.CameraOpenException;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.IntelCameraExtensionLoader;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.IOException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 08/10/17.
 */

public class CameraOpenFunction extends ThrowingAsyncFunction<CameraContext, TurboCamera, MessageException>
{
    @Override
    protected TurboCamera call(CameraContext context) throws MessageException
    {
        ICameraExtension cameraExtension = IntelCameraExtensionLoader.extendedOpenCamera(context);

        try
        {
            LowLevelCamera llCamera = new LowLevelCameraImpl(context, cameraExtension);
            return new TurboCameraImpl(llCamera);
        }
        catch (IOException ioe)
        {
            throw new CameraOpenException();
        }
    }
}
