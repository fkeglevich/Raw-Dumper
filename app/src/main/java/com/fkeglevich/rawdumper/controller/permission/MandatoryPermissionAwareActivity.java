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

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Flávio Keglevich on 09/08/2017.
 * TODO: Add a class header comment!
 */

public abstract class MandatoryPermissionAwareActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    MandatoryPermissionManager mandatoryPermissionManager;

    protected void initializePermissionManager(IPermissionResultListener listener)
    {
        if (mandatoryPermissionManager == null)
            mandatoryPermissionManager = new MandatoryPermissionManager(listener);
    }

    protected void requestAllPermissions()
    {
        mandatoryPermissionManager.requestAllPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mandatoryPermissionManager.sendRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
