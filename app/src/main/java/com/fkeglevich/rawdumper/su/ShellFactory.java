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
import com.topjohnwu.superuser.Shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private int numRequestedShells = 0;
    private List<Shell> createdShells = new ArrayList<>();

    private final Handler managerHandler;
    private final Runnable createShellsRunnable = () ->
    {
        synchronized (ShellFactory.this)
        {
            List<ShellCreationThread> threadList = new ArrayList<>();
            for (int i = 0; i < numRequestedShells; i++)
            {
                ShellCreationThread thread = new ShellCreationThread();
                thread.start();
                threadList.add(thread);
            }

            for (ShellCreationThread thread : threadList)
                createdShells.add(thread.getShell());

            numRequestedShells = 0;
            creatingShells = false;

            if (shellsHaveRootAccess())
                dispatchSuccess();
            else
            {
                closeShells();
                dispatchError();
            }
        }
    };

    private ShellFactory()
    {
        HandlerThread thread = new HandlerThread("ShellManagerThread");
        thread.start();
        managerHandler = new Handler(thread.getLooper());
    }

    private boolean creatingShells = false;

    public synchronized void requestShell()
    {
        requiresIdleState();
        numRequestedShells ++;
    }

    public synchronized Shell getShell()
    {
        requiresIdleState();
        return createdShells.remove(createdShells.size() - 1);
    }

    public synchronized void startCreatingShells()
    {
        requiresIdleState();
        if (!createdShells.isEmpty())
            throw new IllegalStateException("There are shells that weren't used!");

        creatingShells = true;
        managerHandler.post(createShellsRunnable);
    }

    private synchronized boolean isCreatingShells()
    {
        return creatingShells;
    }

    private void requiresIdleState()
    {
        if (isCreatingShells()) throw new IllegalStateException("The state needs to be IDLE!");
    }

    private void dispatchError()
    {
        managerHandler.post(() -> onError.dispatchEvent(null));
    }

    private void dispatchSuccess()
    {
        managerHandler.post(() -> onSuccess.dispatchEvent(null));
    }

    private boolean shellsHaveRootAccess()
    {
        for (Shell shell : createdShells)
            if (!shell.isRoot()) return false;

        return true;
    }

    private void closeShells()
    {
        for (Shell shell : createdShells)
        {
            try
            {
                shell.close();
            }
            catch (IOException ignored)
            { }
        }
        createdShells.clear();
    }
}
