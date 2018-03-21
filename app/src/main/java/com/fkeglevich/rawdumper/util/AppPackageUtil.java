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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.controller.context.ContextManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Deals with miscellaneous stuff related to the application package.
 *
 * Created by Flávio Keglevich on 22/08/2017.
 */

public class AppPackageUtil
{
    private static final String PACKAGE_NOT_FOUND_MESSAGE = "The PackageManager couldn't find the name of the own app. This should NOT HAPPEN!";

    public static String getAppNameWithVersion()
    {
        Context context = ContextManager.getApplicationContext();
        String appVersion = getPackageInfo(context,0).versionName;
        return context.getResources().getString(R.string.app_name_with_version, appVersion);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static List<String> getAllDeniedPermissions(Context context)
    {
        List<String> deniedPermissions = new ArrayList<>();
        String[] requestedPermissions = getPackageInfo(context, PackageManager.GET_PERMISSIONS).requestedPermissions;

        for (String permission : requestedPermissions)
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                deniedPermissions.add(permission);

        return deniedPermissions;
    }

    private static PackageInfo getPackageInfo(Context context, int flags)
    {
        try
        {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), flags);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException(PACKAGE_NOT_FOUND_MESSAGE);
        }
    }
}
