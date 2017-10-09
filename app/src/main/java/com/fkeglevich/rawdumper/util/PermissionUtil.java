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
import android.content.pm.PackageManager;
import android.os.Build;

import com.fkeglevich.rawdumper.util.exception.NameNotFoundFromItselfException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flávio Keglevich on 31/08/2017.
 * TODO: Add a class header comment!
 */

public class PermissionUtil
{
    public static String[] getPermissionsInManifest(Context context)
    {
        try
        {
            return context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new NameNotFoundFromItselfException();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static List<String> getAllDeniedPermissions(Context context)
    {
        List<String> deniedPermissions = new ArrayList<>();

        for (String permission : PermissionUtil.getPermissionsInManifest(context))
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                deniedPermissions.add(permission);

        return deniedPermissions;
    }
}
