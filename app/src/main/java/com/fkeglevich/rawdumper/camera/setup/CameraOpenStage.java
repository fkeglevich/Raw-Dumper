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

package com.fkeglevich.rawdumper.camera.setup;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.async.CameraThread;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 01/10/17.
 */

public class CameraOpenStage implements SetupStage
{
    @Override
    public void executeStage(final SetupStageLink setupBase)
    {
        CameraThread.getInstance().openCamera(setupBase.buildCameraContext(), new AsyncOperation<TurboCamera>()
        {
            @Override
            protected void execute(TurboCamera argument)
            {
                setupBase.setTurboCamera(argument);
                setupBase.processNextStage();
            }
        },
        new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                setupBase.sendException(argument);
            }
        });
    }
}
