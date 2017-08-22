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

package com.fkeglevich.rawdumper.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.controller.context.ContextManager;
import com.fkeglevich.rawdumper.util.exception.NameNotFoundFromItselfException;

/**
 * Deals with miscellaneous stuff related to the application package.
 *
 * Created by Flávio Keglevich on 22/08/2017.
 */

public class PackageUtil
{
    public static String getAppNameWithVersion()
    {
        synchronized (ContextManager.getApplicationContext().getLock())
        {
            Context context = ContextManager.getApplicationContext().get();
            return context.getResources().getString(R.string.app_name_with_version, getAppVersion());
        }
    }

    private static String getAppVersion()
    {
        synchronized (ContextManager.getApplicationContext().getLock())
        {
            Context context = ContextManager.getApplicationContext().get();

            try
            {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0);
                return packageInfo.versionName;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                throw new NameNotFoundFromItselfException();
            }
        }
    }
}
