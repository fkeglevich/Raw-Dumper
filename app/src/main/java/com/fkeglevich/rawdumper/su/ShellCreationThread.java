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

package com.fkeglevich.rawdumper.su;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 01/05/18.
 */
public class ShellCreationThread extends Thread
{
    private final Shell.Builder builder;
    private final int id;
    private final ShellFactory shellManager;

    private Shell.Interactive shell = null;

    ShellCreationThread(Shell.Builder builder, int id, ShellFactory shellManager)
    {
        super();
        this.builder = builder;
        this.id = id;
        this.shellManager = shellManager;
    }

    @Override
    public void run()
    {
        shell = builder.open(new Shell.OnCommandResultListener()
        {
            @Override
            public void onCommandResult(int commandCode, int exitCode, List<String> output)
            {
                if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING)
                    killShell();
                if (!shellManager.registerShell(id, shell))
                    killShell();
            }
        });
    }

    private void killShell()
    {
        if (shell != null)
        {
            shell.kill();
            shell = null;
        }
    }
}
