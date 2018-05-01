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

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.fkeglevich.rawdumper.su.ShellFactory;

import java.util.concurrent.atomic.AtomicReference;

import eu.chainfire.libsuperuser.Shell;

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

    private static final String THREAD_NAME = "LogcatServiceThread";

    private int logcatShellId       = -1;
    private int cameraHalShellId    = -1;
    private Shell.Interactive logcatShell;
    private Shell.Interactive cameraHalShell;

    private final LogcatMatch[] localMatchArray;
    private final Handler handler;

    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    LogcatService(@NonNull LogcatMatch[] matchArray)
    {
        localMatchArray = matchArray;
        HandlerThread thread = new HandlerThread(THREAD_NAME);
        thread.start();
        handler = new Handler(thread.getLooper());
        requestShells();
    }

    public synchronized void exitHalDebug()
    {
        if (state.compareAndSet(State.RUNNING, State.PAUSED))
            CommandHelper.addExitHalDebugCommand(cameraHalShell);
    }

    public synchronized void enterHalDebug()
    {
        if (state.compareAndSet(State.PAUSED, State.RUNNING))
            CommandHelper.addEnterHalDebugCommand(cameraHalShell);
    }

    public State getState()
    {
        return state.get();
    }

    private void requestShells()
    {
        handler.post(() ->
        {
            ShellFactory factory = ShellFactory.getInstance();
            logcatShellId    = factory.requestShell(createLogcatShellBuilder());
            cameraHalShellId = factory.requestShell(createCameraHalShellBuilder());
            factory.onSuccess.addListener(eventData ->
            {
                cameraHalShell = factory.getShell(cameraHalShellId);
                logcatShell = factory.getShell(logcatShellId);
                CommandHelper.addLogcatCommand(logcatShell, localMatchArray);

                state.set(State.PAUSED);
            });
        });
    }

    @WorkerThread
    private void processLine(String line)
    {
        LogcatMatch match;
        for (int i = 0; i < localMatchArray.length; i++)
        {
            match = localMatchArray[i];
            if (match.enabled && line.contains(match.fingerprintPrefix))
            {
                match.latestMatch = line;
                return;
            }
        }
    }

    @WorkerThread
    private Shell.Builder createLogcatShellBuilder()
    {
        return new Shell.Builder().
                    useSU().
                    setWantSTDERR(false).
                    setWatchdogTimeout(0).
                    setMinimalLogging(true).
                    setOnSTDOUTLineListener(this::processLine);
    }

    @WorkerThread
    private Shell.Builder createCameraHalShellBuilder()
    {
        return new Shell.Builder().
                    useSU().
                    setWantSTDERR(true).
                    setWatchdogTimeout(5).
                    setMinimalLogging(true);
    }
}
