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

package com.fkeglevich.rawdumper.controller.camera;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.ActionBar;
import android.view.TextureView;
import android.view.View;

import com.fkeglevich.rawdumper.controller.camera.preview.PreviewControllerActivity;
import com.fkeglevich.rawdumper.ui.animation.CameraOpenAnimation;
import com.fkeglevich.rawdumper.ui.dialog.FatalErrorDialogs;

/**
 * An activity ready for a basic fullscreen camera preview setup and with simple error UI capabilities.
 *
 * Created by Flávio Keglevich on 13/08/2017.
 */

public class FullscreenPreviewActivity extends PreviewControllerActivity
{
    private FatalErrorDialogs fatalErrorDialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fatalErrorDialogs = new FatalErrorDialogs(this);
        goToFullscreenMode();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        goToFullscreenMode();
    }

    @Override
    protected void showMissingMandatoryPermissionUi()
    {
        fatalErrorDialogs.showNeedsPermissionDialog(this);
    }

    @Override
    protected void showMissingRootAccessUi()
    {
        fatalErrorDialogs.showNeedsRootAccessDialog(this);
    }

    @Override
    protected void showErrorOpeningCameraUi(String message)
    {
        fatalErrorDialogs.showGenericFatalErrorDialog(this, message);
    }

    @CallSuper
    @Override
    protected void onCameraPreviewStarted(TextureView textureView)
    {
        textureView.setAlpha(1);
        CameraOpenAnimation.animateTextureView(textureView);
    }

    @CallSuper
    @Override
    protected void onCameraClosed(TextureView textureView)
    {
        textureView.setAlpha(0);
    }

    /**
     * Hides the action bar and the system UI.
     */
    private void goToFullscreenMode()
    {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        View decorView = getWindow().getDecorView();
        if (decorView != null)
        {
            int systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                systemUiVisibility = systemUiVisibility | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            decorView.setSystemUiVisibility(systemUiVisibility);
        }
    }
}
