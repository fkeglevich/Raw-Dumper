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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;

import com.fkeglevich.rawdumper.controller.permission.PermissionRequest;
import com.fkeglevich.rawdumper.util.event.DefaultPreventer;

/**
 * ModularActivity is an activity that has modularized events and has a ActivityReference.
 *
 * Created by Flávio Keglevich on 16/09/2017.
 */

public abstract class ModularActivity extends AppCompatActivity
{
    protected final ActivityReference reference = new ActivityReference();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        reference.setActualReference(this);
        reference.onCreate.dispatchEvent(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        reference.onPostCreate.dispatchEvent(savedInstanceState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        reference.onDestroy.dispatchEvent(null);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        reference.onResume.dispatchEvent(null);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        reference.onPause.dispatchEvent(null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        reference.onWindowFocusChanged.dispatchEvent(hasFocus);
    }

    @Override
    public void onBackPressed()
    {
        DefaultPreventer defaultPreventer = new DefaultPreventer();
        reference.onBackPressed.dispatchEvent(defaultPreventer);
        if (!defaultPreventer.isDefaultPrevented())
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        PermissionRequest request = new PermissionRequest(requestCode, permissions, grantResults);
        reference.onRequestPermissionsResult.dispatchEvent(request);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        DefaultPreventer defaultPreventer = new DefaultPreventer();
        reference.onKeyDown.dispatchEvent(new KeyEventData(event, keyCode, defaultPreventer));
        if (defaultPreventer.isDefaultPrevented())
            return true;
        else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        DefaultPreventer defaultPreventer = new DefaultPreventer();
        reference.onKeyUp.dispatchEvent(new KeyEventData(event, keyCode, defaultPreventer));
        if (defaultPreventer.isDefaultPrevented())
            return true;
        else
            return super.onKeyUp(keyCode, event);
    }
}
