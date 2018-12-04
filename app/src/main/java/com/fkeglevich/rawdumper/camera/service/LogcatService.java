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

package com.fkeglevich.rawdumper.camera.service;

import androidx.annotation.NonNull;

import com.fkeglevich.rawdumper.su.ShellFactory;
import com.topjohnwu.superuser.Shell;

import java.util.concurrent.atomic.AtomicReference;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 01/05/18.
 */
public class LogcatService
{
    public enum State
    {
        IDLE, RUNNING, PAUSED
    }

    private Shell logcatShell;
    private Shell cameraHalShell;

    private final LogcatMatch[] localMatchArray;
    private final boolean needHalDebugCommandFlag;

    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    LogcatService(@NonNull LogcatMatch[] matchArray, boolean needHalDebugCommand)
    {
        localMatchArray = matchArray;
        needHalDebugCommandFlag = needHalDebugCommand;
        requestShells();
    }

    public synchronized void exitHalDebug()
    {
        if (needHalDebugCommandFlag && state.compareAndSet(State.RUNNING, State.PAUSED))
            CommandHelper.addExitHalDebugCommand(cameraHalShell);
    }

    public synchronized void enterHalDebug()
    {
        if (needHalDebugCommandFlag && state.compareAndSet(State.PAUSED, State.RUNNING))
            CommandHelper.addEnterHalDebugCommand(cameraHalShell);
    }

    public State getState()
    {
        return state.get();
    }

    private void requestShells()
    {
        ShellFactory factory = ShellFactory.getInstance();
        factory.requestShell();

        if (needHalDebugCommandFlag)
            factory.requestShell();

        factory.onSuccess.addListener(eventData ->
        {
            if (needHalDebugCommandFlag)
                cameraHalShell = factory.getShell();
            logcatShell = factory.getShell();
            state.set(State.PAUSED);

            new LogcatServiceThread(localMatchArray).start();
        });
    }
}
