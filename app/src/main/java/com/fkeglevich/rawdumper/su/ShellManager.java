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

package com.fkeglevich.rawdumper.su;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Flávio Keglevich on 09/07/2017.
 * TODO: Add a class header comment!
 */

public class ShellManager
{
    private static ShellManager instance = null;

    public static ShellManager getInstance()
    {
        if (instance == null)
            instance = new ShellManager();

        return instance;
    }

    private Shell.Interactive shell = null;

    private ShellManager()
    {   }

    public void open(Shell.OnCommandResultListener onCommandResultListener)
    {
        if (isRunning())
            throw new RuntimeException("The shell is already running!");

        shell = new Shell.Builder().
                useSU().
                setWantSTDERR(true).
                setWatchdogTimeout(5).
                setMinimalLogging(true).
                open(onCommandResultListener);
    }

    public boolean isRunning()
    {
        return shell != null && shell.isRunning();
    }

    public void addCommand(String[] commands, int exitCode, Shell.OnCommandLineListener onCommandLineListener)
    {
        if (!isRunning())
            throw new RuntimeException("The shell is not running!");

        shell.addCommand(commands, exitCode, onCommandLineListener);
    }

    public void addSingleCommand(String command, Shell.OnCommandLineListener onCommandLineListener)
    {
        addCommand(new String[] {command}, 2, onCommandLineListener);
    }
}
