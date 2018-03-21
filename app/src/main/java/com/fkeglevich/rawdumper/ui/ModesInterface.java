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
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.ui.dialog.AboutDialog;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;

/**
 * Represents the interface for camera mode selection
 *
 * Created by Flávio Keglevich on 03/05/2017.
 */

public class ModesInterface
{
    private final ActivityReference activityReference;
    private RelativeLayout modesLayout;
    private Fade fadeTransition = new Fade();
    private AboutDialog aboutDialog;

    public ModesInterface(ActivityReference activityReference)
    {
        this.activityReference = activityReference;
        modesLayout = (RelativeLayout) this.activityReference.weaklyGet().findViewById(R.id.modesLayout);
        aboutDialog = new AboutDialog(this.activityReference.weaklyGet());
        fadeTransition.setDuration(150L);

        setupButtons();
        setupEvents(activityReference);
    }

    private boolean isVisible()
    {
        return modesLayout.getVisibility() == View.VISIBLE;
    }

    private void setIsVisible(boolean visible)
    {
        TransitionManager.beginDelayedTransition(modesLayout, fadeTransition);
        modesLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void setupButtons()
    {
        AppCompatActivity compatActivity = activityReference.weaklyGet();

        ImageButton modesButton = (ImageButton)compatActivity.findViewById(R.id.modesButton);
        modesButton.setOnClickListener(v -> setIsVisible(true));

        ImageButton backButton = (ImageButton)compatActivity.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> setIsVisible(false));

        ImageButton infoButton = (ImageButton)compatActivity.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> aboutDialog.showDialog(activityReference.weaklyGet()));
    }

    private void setupEvents(ActivityReference activityReference)
    {
        activityReference.onBackPressed.addListener(eventData ->
        {
            if (isVisible())
            {
                eventData.preventDefault();
                setIsVisible(false);
            }
        });
    }
}
