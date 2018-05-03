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

import com.fkeglevich.rawdumper.debug.PerfInfo;

import org.junit.Assert;
import org.junit.Test;

import eu.chainfire.libsuperuser.Shell;

public class ShellFactoryTest
{
    private static final Object lock = new Object();
    public static final int NUM_SHELLS = 1;

    private static void onSuccess(Void eventData)
    {
        System.out.println("ShellManager2Test Success!");
        for (int i = 0; i < NUM_SHELLS; i++)
            Assert.assertNotNull(ShellFactory.getInstance().getShell(i));
        defaultCb();
    }

    private static void onError(Void eventData)
    {
        System.out.println("ShellManager2Test Error!");
        defaultCb();
    }

    private static void defaultCb()
    {
        PerfInfo.end("ShellManager2Test");
        synchronized (lock) { lock.notify(); }
    }

    @Test
    public void shellManagerTest() throws InterruptedException
    {
        ShellFactory shellManager = ShellFactory.getInstance();

        for (int i = 0; i < NUM_SHELLS; i++)
            shellManager.requestShell(createBuilder());

        shellManager.onSuccess.addListener(ShellFactoryTest::onSuccess);
        shellManager.onError.addListener(ShellFactoryTest::onError);
        PerfInfo.start("ShellManager2Test");
        shellManager.startCreatingShells();

        synchronized (lock) { lock.wait(); }
    }

    private Shell.Builder createBuilder()
    {
        return new Shell.Builder().
                                useSU().
                                setWantSTDERR(true).
                                setWatchdogTimeout(5).
                                setMinimalLogging(true);
    }
}