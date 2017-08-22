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
import com.fkeglevich.rawdumper.camera.async.CameraAccess;
import com.fkeglevich.rawdumper.camera.async.SharedCameraSetter;
import com.fkeglevich.rawdumper.camera.shared.SharedCamera;
import com.fkeglevich.rawdumper.camera.shared.SharedCameraOpener;
import com.fkeglevich.rawdumper.camera.shared.SharedData;
import com.fkeglevich.rawdumper.camera.shared.SharedDataLoader;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * Created by Flávio Keglevich on 13/08/2017.
 * TODO: Add a class header comment!
 */

public class CameraOpenFunction extends ThrowingAsyncFunction<Integer, CameraAccess, MessageException>
{
    private final SharedCameraSetter setter;
    private final CameraAccess cameraAccess;

    public CameraOpenFunction(SharedCameraSetter setter, CameraAccess cameraAccess)
    {
        this.setter = setter;
        this.cameraAccess = cameraAccess;
    }

    @Override
    protected CameraAccess call(Integer argument) throws MessageException
    {
        SharedDataLoader dataLoader = new SharedDataLoader();
        SharedData sharedData = dataLoader.load();

        SharedCameraOpener cameraOpener = new SharedCameraOpener(argument, sharedData);
        SharedCamera sharedCamera = cameraOpener.open();

        setter.setSharedCamera(sharedCamera);

        return cameraAccess;
    }
}
