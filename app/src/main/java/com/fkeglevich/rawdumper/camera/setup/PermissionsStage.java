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

import com.fkeglevich.rawdumper.controller.permission.MandatoryPermissionManager;
import java.lang.Void;
import com.fkeglevich.rawdumper.util.event.EventListener;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/09/17.
 */

public class PermissionsStage implements SetupStage
{
    @Override
    public void executeStage(final SetupStageLink setupBase)
    {
        final MandatoryPermissionManager permissionManager = setupBase.getPermissionManager();

        permissionManager.onAllPermissionsGranted.addListener(new EventListener<Void>()
        {
            @Override
            public void onEvent(Void eventData)
            {
                setupBase.setPermissionToken();
                setupBase.processNextStage();
                permissionManager.onAllPermissionsGranted.removeListener(this);
            }
        });
        permissionManager.onMissingPermissions.addListener(new EventListener<MessageException>()
        {
            @Override
            public void onEvent(MessageException eventData)
            {
                setupBase.sendException(eventData);
                permissionManager.onMissingPermissions.removeListener(this);
            }
        });
        permissionManager.requestAllPermissions(setupBase.getActivity());
    }
}
