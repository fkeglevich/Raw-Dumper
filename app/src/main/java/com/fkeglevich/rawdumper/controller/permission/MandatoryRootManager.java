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

package com.fkeglevich.rawdumper.controller.permission;

import com.fkeglevich.rawdumper.controller.permission.exception.RootAccessException;
import com.fkeglevich.rawdumper.su.MainSUShell;
import com.fkeglevich.rawdumper.su.ShellFactory;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

/**
 * Created by Flávio Keglevich on 09/08/2017.
 * TODO: Add a class header comment!
 */

public class MandatoryRootManager extends MandatoryPermissionManager
{
    public final EventDispatcher<Void> onRootAccessGranted = createDispatcher();

    public void requestRootAccess()
    {
        if (!MainSUShell.getInstance().isRunning())
        {
            ShellFactory factory = ShellFactory.getInstance();

            factory.onError.addListener(eventData -> dispatchMissingPermissions(new RootAccessException()));
            factory.onSuccess.addListener(eventData -> dispatchRootAccessGranted());

            factory.startCreatingShells();
        }
        else
            dispatchRootAccessGranted();
    }

    private void dispatchRootAccessGranted()
    {
        onRootAccessGranted.dispatchEvent(null);
    }
}
