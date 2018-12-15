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

package com.fkeglevich.rawdumper.controller.context;

import com.topjohnwu.superuser.BusyBox;
import com.topjohnwu.superuser.ContainerApp;

import java.io.File;

/**
 * A simple Application class that helps managing a global application context.
 *
 * Created by Flávio Keglevich on 22/08/2017.
 */

public class ContextManagerApplication extends ContainerApp
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        ContextManager.setApplicationContext(this);

        //Rely on Magisk external busybox
        BusyBox.BB_PATH = new File("/sbin/.core/busybox");
    }
}
