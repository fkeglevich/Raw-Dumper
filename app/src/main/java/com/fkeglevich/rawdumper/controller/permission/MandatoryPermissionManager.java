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
import android.os.Build;
import android.os.Looper;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.controller.permission.exception.PermissionException;
import com.fkeglevich.rawdumper.util.AppPackageUtil;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.HandlerDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.util.List;

import androidx.core.app.ActivityCompat;

/**
 * Created by Flávio Keglevich on 09/08/2017.
 * TODO: Add a class header comment!
 */

public class MandatoryPermissionManager
{
    static <T> SimpleDispatcher<T> createDispatcher()
    {
        return new HandlerDispatcher<>(Looper.getMainLooper());
    }

    private static final int REQUEST_CODE = 1;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public final EventDispatcher<Void> onAllPermissionsGranted       = createDispatcher();
    public final EventDispatcher<MessageException> onMissingPermissions = createDispatcher();

    public void requestAllPermissions(ActivityReference activityReference)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestAllPermissionsMarshmallow(activityReference.weaklyGet());
        else
            dispatchPermissionsGranted();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestAllPermissionsMarshmallow(Activity activity)
    {
        List<String> deniedPermissions = AppPackageUtil.getAllDeniedPermissions(activity);

        if (!deniedPermissions.isEmpty())
            ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(EMPTY_STRING_ARRAY), REQUEST_CODE);
        else
            dispatchPermissionsGranted();
    }

    void sendRequestPermissionsResult(PermissionRequest request)
    {
        if (request.allPermissionsWereGranted())
            dispatchPermissionsGranted();
        else
            dispatchMissingPermissions(new PermissionException());
    }

    void dispatchMissingPermissions(MessageException exception)
    {
        onMissingPermissions.dispatchEvent(exception);
    }

    private void dispatchPermissionsGranted()
    {
        onAllPermissionsGranted.dispatchEvent(null);
    }
}
