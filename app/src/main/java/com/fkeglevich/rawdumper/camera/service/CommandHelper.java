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

import eu.chainfire.libsuperuser.Shell;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 01/05/18.
 */
class CommandHelper
{
    private static final String CLEAR_LOGCAT_CMD = "logcat -c";
    private static final String ENTER_HAL_DEBUG_MODE = "setprop \"camera.hal.debug\" 2";
    private static final String EXIT_HAL_DEBUG_MODE = "setprop \"camera.hal.debug\" 0";
    private static final int BLOCKING_CMD_TIMEOUT = 1000;

    private static final Object commandLock = new Object();

    static void addLogcatCommand(Shell.Interactive shell, LogcatMatch[] matchArray)
    {
        shell.addCommand(buildLogcatCommands(matchArray));
    }

    static synchronized void addEnterHalDebugCommand(Shell.Interactive shell)
    {
        executeBlockingCommand(shell, ENTER_HAL_DEBUG_MODE);
    }

    static synchronized void addExitHalDebugCommand(Shell.Interactive shell)
    {
        executeBlockingCommand(shell, EXIT_HAL_DEBUG_MODE);
    }

    private static void executeBlockingCommand(Shell.Interactive shell, String command)
    {
        shell.addCommand(command, 0, (commandCode, exitCode, output) ->
        {
            synchronized (commandLock)
            {
                commandLock.notify();
            }
        });
        synchronized (commandLock)
        {
            try { commandLock.wait(BLOCKING_CMD_TIMEOUT); }
            catch (InterruptedException ignored) {   }
        }
    }

    private static String[] buildLogcatCommands(LogcatMatch[] matchArray)
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

        builder.append("\"*:S\" -v raw");
        //builder.append(" | grep -E \"total_gain: |Feedback AEC integration_time\\[0\\]: |@setAicParameter: wb int\"");

        return new String[] {CLEAR_LOGCAT_CMD, builder.toString()};
    }
}
