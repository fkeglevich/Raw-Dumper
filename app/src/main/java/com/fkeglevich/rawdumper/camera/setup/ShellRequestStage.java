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

package com.fkeglevich.rawdumper.camera.setup;

import com.fkeglevich.rawdumper.camera.service.CameraServiceManager;
import com.fkeglevich.rawdumper.su.MainSUShell;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 01/05/18.
 */
public class ShellRequestStage implements SetupStage
{
    @Override
    public void executeStage(SetupStageLink setupBase)
    {
        if (!MainSUShell.getInstance().isRunning())
            MainSUShell.getInstance().requestShell();

        if (setupBase.getDeviceInfo().needsLogcatServices())
            CameraServiceManager.getInstance().prepare();

        setupBase.processNextStage();
    }
}
