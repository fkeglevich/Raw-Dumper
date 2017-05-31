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
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.fkeglevich.rawdumper.R;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.Visibility;

/**
 * Created by Flávio Keglevich on 03/05/2017.
 * TODO: Add a class header comment!
 */

public class ModesInterface
{
    private AppCompatActivity compatActivity;

    private ImageButton modesButton = null;
    private ImageButton backButton = null;
    private RelativeLayout modesLayout = null;

    private Fade fadeTransition = new Fade();

    public ModesInterface(AppCompatActivity compatActivity)
    {
        this.compatActivity = compatActivity;

        modesLayout = (RelativeLayout)compatActivity.findViewById(R.id.modesLayout);
        modesButton = (ImageButton)compatActivity.findViewById(R.id.modesButton);
        backButton = (ImageButton)compatActivity.findViewById(R.id.backButton);

        fadeTransition.setDuration(150L);

        modesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setIsVisible(true);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setIsVisible(false);
            }
        });
    }

    public boolean isVisible()
    {
        return modesLayout.getVisibility() == View.VISIBLE;
    }

    public void setIsVisible(boolean visible)
    {
        TransitionManager.beginDelayedTransition(modesLayout, fadeTransition);
        modesLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
