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
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fkeglevich.rawdumper.controller.permission.PermissionRequest;
import com.fkeglevich.rawdumper.util.event.DefaultPreventer;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;

import java.lang.ref.WeakReference;

/**
 * Represents a "safe" reference to an activity that can be stored and passed freely
 * without the risk of memory leaks.
 *
 * It also expose the activity events using EventDispatchers.
 *
 * Created by Flávio Keglevich on 16/09/2017.
 */

@SuppressWarnings("WeakerAccess")
public class ActivityReference
{
    private WeakReference<AppCompatActivity> actualReference;

    private static <T> SimpleDispatcher<T> createDispatcher()
    {
        return new SimpleDispatcher<>();
    }

    /**
     * Gets the actual activity. Don't store it!
     *
     * @return An AppCompatActivity or null, if the activity was destroyed
     */
    public AppCompatActivity weaklyGet()
    {
        return actualReference.get();
    }

    /**
     * Utility method for retrieving a view by its id.
     *
     * @param id    The view's id
     * @param <T>   The type of the view
     * @return      A T view object
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(@IdRes int id)
    {
        return (T) weaklyGet().findViewById(id);
    }

    void setActualReference(AppCompatActivity activity)
    {
        actualReference = new WeakReference<>(activity);
    }

    //Event dispatchers
    public final EventDispatcher<Bundle> onCreate        = createDispatcher();
    public final EventDispatcher<Bundle> onPostCreate    = createDispatcher();
    public final EventDispatcher<Void>   onDestroy       = createDispatcher();
    public final EventDispatcher<Void>   onResume        = createDispatcher();
    public final EventDispatcher<Void>   onPause         = createDispatcher();

    public final EventDispatcher<Boolean> onWindowFocusChanged = createDispatcher();
    public final EventDispatcher<DefaultPreventer>  onBackPressed = createDispatcher();
    public final EventDispatcher<PermissionRequest> onRequestPermissionsResult = createDispatcher();

    public final EventDispatcher<KeyEventData> onKeyDown = createDispatcher();
    public final EventDispatcher<KeyEventData> onKeyUp = createDispatcher();
}
