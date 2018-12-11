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

import com.topjohnwu.superuser.Shell;

/**
 * Created by Flávio Keglevich on 09/07/2017.
 * TODO: Add a class header comment!
 */

public class MainSUShell
{
    private static final MainSUShell instance = new MainSUShell();

    public synchronized static MainSUShell getInstance()
    {
        return instance;
    }

    private Shell shell = null;

    public synchronized void requestShell()
    {
        if (isRunning()) throw new RuntimeException("The shell is already running!");

        ShellFactory factory = ShellFactory.getInstance();
        factory.requestShell();
        factory.onSuccess.addListener(eventData -> shell = factory.getShell());
    }

    public synchronized boolean isRunning()
    {
        return shell != null && shell.isAlive();
    }

    public synchronized void addCommand(String[] commands, Shell.ResultCallback callback)
    {
        if (!isRunning())
            throw new RuntimeException("The shell is not running!");

        shell.newJob().add(commands).submit(callback);
    }

    public synchronized void addSingleCommand(String command, Shell.ResultCallback callback)
    {
        addCommand(new String[] {command}, callback);
    }
}
