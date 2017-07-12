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
 * Created by Flávio Keglevich on 18/06/2017.
 * TODO: Add a class header comment!
 */

public class ISOInterface
{
    private HorizontalScrollView isoScrollView;
    private LinearLayout isoLayout;
    private AppCompatActivity compatActivity;
    private Toast currentToast;

    private Fade fadeTransition = new Fade();

    public ISOInterface(AppCompatActivity compatActivity)
    {
        this.isoScrollView = (HorizontalScrollView)compatActivity.findViewById(R.id.isoScrollView);
        this.isoLayout =     (LinearLayout)compatActivity.findViewById(R.id.isoLayout);
        this.compatActivity = compatActivity;
        this.currentToast = Toast.makeText(compatActivity, "", Toast.LENGTH_LONG);
    }

    public void updateISOValues(ExposureInfo exposureInfo, CameraAccess cameraAccess)
    {
        isoLayout.removeAllViews();
        ValueButton isoButton;
        for (String value : exposureInfo.getIsoValues())
        {
            isoButton = new ValueButton(compatActivity, exposureInfo.getIsoParameter(), value, cameraAccess, currentToast);
            isoButton.setText("ISO\n" + value);
            isoLayout.addView(isoButton);
        }
    }

    public void toggleVisibility()
    {
        TransitionManager.beginDelayedTransition(isoScrollView, fadeTransition);
        if (isoScrollView.getVisibility() == View.VISIBLE)
        {
            isoScrollView.setVisibility(View.INVISIBLE);
        }
        else
        {
            isoScrollView.setVisibility(View.VISIBLE);
        }
    }

    public void forceHide()
    {
        TransitionManager.beginDelayedTransition(isoScrollView, fadeTransition);
        isoScrollView.setVisibility(View.INVISIBLE);
    }
}
