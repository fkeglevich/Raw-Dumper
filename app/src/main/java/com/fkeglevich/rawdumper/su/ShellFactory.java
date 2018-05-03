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

import android.os.Handler;
import android.os.HandlerThread;

import com.fkeglevich.rawdumper.util.event.AsyncEventDispatcher;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 30/04/18.
 */
public class ShellFactory
{
    private static final ShellFactory instance = new ShellFactory();

    public static ShellFactory getInstance()
    {
        return instance;
    }

    public final EventDispatcher<Void> onSuccess = new AsyncEventDispatcher<>();
    public final EventDispatcher<Void> onError = new AsyncEventDispatcher<>();

    private final List<Shell.Builder> builderList = new ArrayList<>();
    private final List<Shell.Interactive> shellList = new ArrayList<>();
    private final Handler       managerHandler;
    private final Runnable      createShellsRunnable = () ->
    {
        synchronized (ShellFactory.this)
        {
            for (int i = 0; i < builderList.size(); i++)
                new ShellCreationThread(builderList.get(i), i, ShellFactory.this).start();
        }
    };

    private boolean errorFlag = false;

    private ShellFactory()
    {
        HandlerThread thread = new HandlerThread("ShellManagerThread");
        thread.start();
        managerHandler = new Handler(thread.getLooper());
    }

    private boolean creatingShells = false;

    public synchronized int requestShell(Shell.Builder builder)
    {
        requiresIdleState();
        builderList.add(builder);
        shellList.add(null);
        return builderList.size() - 1;
    }

    public synchronized Shell.Interactive getShell(int id)
    {
        requiresIdleState();
        Shell.Interactive shell = shellList.get(id);
        shellList.set(id, null);
        return shell;
    }

    public synchronized void startCreatingShells()
    {
        requiresIdleState();
        if (builderList.isEmpty())
        {
            creatingShells = false;
            dispatchSuccess();
            return;
        }

        for (Shell.Interactive shell : shellList)
            if (shell != null)
                throw new IllegalStateException("There are shells that weren't used!");

        creatingShells = true;
        errorFlag = false;
        managerHandler.post(createShellsRunnable);
    }

    synchronized boolean registerShell(int id, Shell.Interactive shell)
    {
        requiresCreatingShellsState();
        if (errorFlag) return false;

        if (shell == null)
        {
            errorFlag = true;
            killOpenedShells();
            builderList.clear();
            creatingShells = false;
            dispatchError();
        }

        shellList.set(id, shell);
        builderList.set(id, null);
        if (hasEveryShell())
        {
            builderList.clear();
            creatingShells = false;
            dispatchSuccess();
        }
        return true;
    }

    public synchronized boolean isCreatingShells()
    {
        return creatingShells;
    }

    private void requiresIdleState()
    {
        if (isCreatingShells()) throw new IllegalStateException("The state needs to be IDLE!");
    }

    private void requiresCreatingShellsState()
    {
        if (!isCreatingShells()) throw new IllegalStateException("The state needs to be CREATING SHELLS");
    }

    private boolean hasEveryShell()
    {
        for (Shell.Interactive shell : shellList)
            if (shell == null)
                return false;

        return true;
    }

    private void killOpenedShells()
    {
        int size = shellList.size();
        for (int i = 0; i < size; i++)
        {
            Shell.Interactive s = shellList.get(i);
            if (s != null) s.kill();
            shellList.set(i, null);
        }
    }

    private void dispatchError()
    {
        managerHandler.post(() -> onError.dispatchEvent(null));
    }

    private void dispatchSuccess()
    {
        managerHandler.post(() -> onSuccess.dispatchEvent(null));
    }
}
