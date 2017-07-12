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

package com.fkeglevich.rawdumper.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.camera.async.CameraAccess;
import com.fkeglevich.rawdumper.raw.info.ExposureInfo;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;

/**
 * Created by Flávio Keglevich on 19/06/2017.
 * TODO: Add a class header comment!
 */

public class ShutterSpeedInterface
{
    private HorizontalScrollView shutterSpeedScrollView;
    private LinearLayout shutterSpeedLayout;
    private AppCompatActivity compatActivity;
    private Toast currentToast;

    private Fade fadeTransition = new Fade();

    public ShutterSpeedInterface(AppCompatActivity compatActivity)
    {
        this.shutterSpeedScrollView = (HorizontalScrollView)compatActivity.findViewById(R.id.shutterSpeedScrollView);
        this.shutterSpeedLayout =     (LinearLayout)compatActivity.findViewById(R.id.shutterSpeedLayout);
        this.compatActivity = compatActivity;
        this.currentToast = Toast.makeText(compatActivity, "", Toast.LENGTH_LONG);
    }

    public void updateSSValues(ExposureInfo exposureInfo, CameraAccess cameraAccess)
    {
        shutterSpeedLayout.removeAllViews();
        ValueButton ssButton;
        for (String value : exposureInfo.getShutterSpeedValues())
        {
            ssButton = new ValueButton(compatActivity, exposureInfo.getShutterSpeedParameter(), value, cameraAccess, currentToast);
            if (value.equals("0"))
                ssButton.setText("auto");
            else
                ssButton.setText(value.endsWith("s") ? value : "1/"+value);
            shutterSpeedLayout.addView(ssButton);
        }
    }

    public void toggleVisibility()
    {
        TransitionManager.beginDelayedTransition(shutterSpeedScrollView, fadeTransition);
        if (shutterSpeedScrollView.getVisibility() == View.VISIBLE)
        {
            shutterSpeedScrollView.setVisibility(View.INVISIBLE);
        }
        else
        {
            shutterSpeedScrollView.setVisibility(View.VISIBLE);
        }
    }

    public void forceHide()
    {
        TransitionManager.beginDelayedTransition(shutterSpeedScrollView, fadeTransition);
        shutterSpeedScrollView.setVisibility(View.INVISIBLE);
    }
}
