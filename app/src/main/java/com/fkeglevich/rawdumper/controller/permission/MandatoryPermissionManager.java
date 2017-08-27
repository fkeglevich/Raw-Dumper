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

package com.fkeglevich.rawdumper.controller.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import com.fkeglevich.rawdumper.util.exception.NameNotFoundFromItselfException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flávio Keglevich on 09/08/2017.
 * TODO: Add a class header comment!
 */

class MandatoryPermissionManager
{
    private static final int REQUEST_CODE = 1;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    final IPermissionResultListener permissionResultListener;

    MandatoryPermissionManager(IPermissionResultListener listener)
    {
        permissionResultListener = listener;
    }

    void requestAllPermissions(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestAllPermissionsMarshmallow(activity);
        else
            postAllPermissionsWereGrantedAsync();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestAllPermissionsMarshmallow(Activity activity)
    {
        List<String> deniedPermissions = new ArrayList<>();

        for (String permission : getPermissionsInManifest(activity))
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                deniedPermissions.add(permission);

        if (!deniedPermissions.isEmpty())
            ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(EMPTY_STRING_ARRAY), REQUEST_CODE);
        else
            postAllPermissionsWereGrantedAsync();
    }

    protected void allPermissionsWereGranted(boolean hadDialogPrompt)
    {
        if (permissionResultListener != null)
            permissionResultListener.onAllPermissionsGranted(hadDialogPrompt);
    }

    private void missingPermissions()
    {
        if (permissionResultListener != null)
            permissionResultListener.onMissingMandatoryPermission();
    }

    void sendRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == REQUEST_CODE)
        {
            for (int value : grantResults)
                if (value != PackageManager.PERMISSION_GRANTED)
                {
                    missingPermissions();
                    return;
                }

            allPermissionsWereGranted(true);
        }
    }

    private void postAllPermissionsWereGrantedAsync()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {allPermissionsWereGranted(false);
            }
        });
    }

    private String[] getPermissionsInManifest(Context context)
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
}
