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

import com.fkeglevich.rawdumper.controller.permission.MandatoryRootManager;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 20/05/18.
 */
public class RootAccessStage implements SetupStage
{
    @Override
    public void executeStage(SetupStageLink setupBase)
    {
        if (DebugFlag.isDisableMandatoryRoot())
        {
            setupBase.setRootToken();
            setupBase.processNextStage();
            return;
        }

        MandatoryRootManager permissionManager = setupBase.getPermissionManager();

        permissionManager.onRootAccessGranted.addListener(new EventListener<Void>()
        {
            @Override
            public void onEvent(Void eventData)
            {
                setupBase.setRootToken();
                setupBase.processNextStage();
                permissionManager.onRootAccessGranted.removeListener(this);
            }
        });
        permissionManager.requestRootAccess();
    }
}
