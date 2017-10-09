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

package com.fkeglevich.rawdumper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fkeglevich.rawdumper.controller.permission.PermissionRequest;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.event.DefaultPreventer;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;

import java.lang.ref.WeakReference;

/**
 * Created by Flávio Keglevich on 16/09/2017.
 * TODO: Add a class header comment!
 */

public class ActivityReference
{
    private WeakReference<AppCompatActivity> actualReference;

    private static <T> SimpleDispatcher<T> createDispatcher()
    {
        return new SimpleDispatcher<>();
    }

    public AppCompatActivity weaklyGet()
    {
        return actualReference.get();
    }

    void setActualReference(AppCompatActivity activity)
    {
        actualReference = new WeakReference<>(activity);
    }

    public final EventDispatcher<Bundle>    onCreate        = createDispatcher();
    public final EventDispatcher<Bundle>    onPostCreate    = createDispatcher();
    public final EventDispatcher<Nothing>   onDestroy       = createDispatcher();
    public final EventDispatcher<Nothing>   onResume        = createDispatcher();
    public final EventDispatcher<Nothing>   onPause         = createDispatcher();

    public final EventDispatcher<Boolean>   onWindowFocusChanged = createDispatcher();
    public final EventDispatcher<DefaultPreventer> onBackPressed = createDispatcher();
    public final EventDispatcher<PermissionRequest> onRequestPermissionsResult = createDispatcher();
}
