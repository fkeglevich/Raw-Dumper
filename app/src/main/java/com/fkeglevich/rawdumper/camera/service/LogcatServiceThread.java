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
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class LogcatServiceThread
{
    private static final String THREAD_NAME = "LogcatServiceThread";
    private static final int LOGCAT_COMMAND_CODE = 2;

    private final Handler handler;
    private final Handler callBackHandler;

    private Shell.Interactive logcatShell;
    private LogcatMatch[] localMatchArray;
    private volatile boolean shellIsRunning = false;

    LogcatServiceThread()
    {
        HandlerThread thread = new HandlerThread(THREAD_NAME);
        thread.start();
        handler = new Handler(thread.getLooper());
        callBackHandler = new Handler();
    }

    public void startShell(String[] commands, List<LogcatMatch> matchList, LogcatServiceStartResult resultCb)
    {
        if (shellIsRunning) throw new RuntimeException("Shell is already running!");
        LogcatMatch[] matchArray = matchList.toArray(new LogcatMatch[0]);
        handler.post(() -> startShellImpl(commands.clone(), matchArray, resultCb));
    }

    public void closeShell()
    {
        if (!shellIsRunning) return;
        handler.post(this::closeShellImpl);
    }

    @WorkerThread
    private void startShellImpl(String[] commands, LogcatMatch[] matchArray, LogcatServiceStartResult resultCb)
    {
        localMatchArray = matchArray;
        logcatShell = new Shell.Builder().
                useSU().
                setWantSTDERR(true).
                setWatchdogTimeout(0).
                setMinimalLogging(true).
                setOnSTDOUTLineListener(this::processLine).
                addCommand(commands).
                open((commandCode, exitCode, output) -> startLogcatAndPostCallback(exitCode, resultCb));
    }

    @WorkerThread
    private void closeShellImpl()
    {
        logcatShell.kill();
        shellIsRunning = false;
        localMatchArray = null;
    }

    private long lastNanos = 0;

    public static volatile double ms = 0;

    @WorkerThread
    private void processLine(String line)
    {
        //ms = (System.nanoTime() - lastNanos) / 1000000.0;
        //Log.i("ASD", ms + "");
        //lastNanos = System.nanoTime();

        if (localMatchArray == null) return;
        LogcatMatch match;
        for (int i = 0; i < localMatchArray.length; i++)
        {
            match = localMatchArray[i];
            if (line.contains(match.fingerprintPrefix))
            {
                match.latestMatch = line;
                return;
            }
        }
    }

    @WorkerThread
    private void startLogcatAndPostCallback(int exitCode, LogcatServiceStartResult resultCb)
    {
        shellIsRunning = exitCode == Shell.OnCommandResultListener.SHELL_RUNNING;
        if (shellIsRunning)
            logcatShell.addCommand(buildLogcatCommand(localMatchArray), LOGCAT_COMMAND_CODE, (Shell.OnCommandResultListener) null);
        callBackHandler.post(() -> resultCb.onResult(shellIsRunning));
    }

    @WorkerThread
    private String buildLogcatCommand(LogcatMatch[] matchArray)
    {
        StringBuilder builder = new StringBuilder("logcat ");
        for (LogcatMatch match : matchArray)
        {
            builder.append('"')
                    .append(match.tag)
                    .append(':')
                    .append(match.priority.name())
                    .append("\" ");
        }
        return builder.append("\"*:S\" -v raw").toString();
    }
}
