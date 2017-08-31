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

package com.fkeglevich.rawdumper.controller.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.fkeglevich.rawdumper.controller.activity.data.PermissionRequest;
import com.fkeglevich.rawdumper.controller.activity.data.SuperCallable;
import com.fkeglevich.rawdumper.controller.activity.event.InteractiveEvent;
import com.fkeglevich.rawdumper.controller.activity.event.LifetimeEvent;

/**
 * Created by Flávio Keglevich on 29/08/2017.
 * TODO: Add a class header comment!
 */

public class ModularActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private final ActivityReference reference = new ActivityReference();

    public ActivityReference getReference()
    {
        return reference;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        reference.updateReference(this);
        reference.getLifetimeEvents().dispatchEvent(LifetimeEvent.ON_CREATE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        reference.getLifetimeEvents().dispatchEvent(LifetimeEvent.ON_POST_CREATE);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        reference.getLifetimeEvents().dispatchEvent(LifetimeEvent.ON_DESTROY);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        reference.getLifetimeEvents().dispatchEvent(LifetimeEvent.ON_RESUME);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        reference.getLifetimeEvents().dispatchEvent(LifetimeEvent.ON_PAUSE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        reference.getInteractiveEvents().dispatchEvent(InteractiveEvent.ON_WINDOWS_FOCUS_CHANGED, hasFocus);
    }

    @Override
    public void onBackPressed()
    {
        SuperCallable superCallable = new SuperCallable();
        reference.getInteractiveEvents().dispatchEvent(InteractiveEvent.ON_BACK_PRESSED, superCallable);
        if (superCallable.superShouldBeCalled())
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        PermissionRequest request = new PermissionRequest(requestCode, permissions, grantResults);
        reference.getInteractiveEvents().dispatchEvent(InteractiveEvent.ON_REQUEST_PERMISSION_RESULT, request);
    }
}
