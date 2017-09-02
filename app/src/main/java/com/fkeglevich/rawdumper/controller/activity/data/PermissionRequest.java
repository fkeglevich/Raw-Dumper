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

package com.fkeglevich.rawdumper.controller.activity.data;

import android.content.pm.PackageManager;

/**
 * Created by Flávio Keglevich on 30/08/2017.
 * TODO: Add a class header comment!
 */

public class PermissionRequest
{
    private final int requestCode;
    private final String[] permissions;
    private final int[] grantResults;

    public PermissionRequest(int requestCode, String[] permissions, int[] grantResults)
    {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResults = grantResults;
    }

    public boolean hasCode(int requestCode)
    {
        return this.requestCode == requestCode;
    }

    public boolean allPermissionsWereGranted()
    {
        for (int value : grantResults)
            if (value != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }
}
