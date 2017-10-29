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

package com.fkeglevich.rawdumper.controller.feature;

import android.view.View;

import com.fkeglevich.rawdumper.camera.action.AutoFocusResult;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.feature.FocusFeature;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class TouchFocusController extends FeatureController
{
    private final View clickArea;
    private FocusFeature focusFeature;

    private final AutoFocusResult autoFocusResult = new AutoFocusResult()
    {
        @Override
        public void autoFocusDone(boolean success)
        {
            //no op
        }
    };

    public TouchFocusController(View clickArea)
    {
        this.clickArea = clickArea;
        this.focusFeature = null;

        clickArea.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (focusFeature != null && focusFeature.getValue().canAutoFocus())
                    focusFeature.startAutoFocus(autoFocusResult);
            }
        });
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        focusFeature = camera.getFocusFeature();
        if (!focusFeature.isAvailable())
            reset();
    }

    @Override
    protected void reset()
    {
        focusFeature = null;
    }
}
