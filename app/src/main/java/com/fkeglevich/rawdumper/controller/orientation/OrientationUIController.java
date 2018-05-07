/*
 * Copyright 2018, Flávio Keglevich
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

package com.fkeglevich.rawdumper.controller.orientation;

import android.view.View;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.transitionseverywhere.Rotate;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 07/05/18.
 */
public class OrientationUIController
{
    private final List<View> affectedViews = new ArrayList<>();
    private final Rotate rotate = new Rotate();

    public OrientationUIController(ActivityReference reference)
    {
        rotate.setDuration(500L);

        affectedViews.add(reference.findViewById(R.id.autoWbBt));
        affectedViews.add(reference.findViewById(R.id.incandescentWbBt));
        affectedViews.add(reference.findViewById(R.id.fluorescentWbBt));
        affectedViews.add(reference.findViewById(R.id.sunnyWbBt));
        affectedViews.add(reference.findViewById(R.id.cloudyWbBt));
        affectedViews.add(reference.findViewById(R.id.shadeWbBt));
        affectedViews.add(reference.findViewById(R.id.twilightWbBt));
        affectedViews.add(reference.findViewById(R.id.manualWbBt));
        affectedViews.add(reference.findViewById(R.id.manualWbBackBt));

        affectedViews.add(reference.findViewById(R.id.wbBox));
        affectedViews.add(reference.findViewById(R.id.focusBox));
        affectedViews.add(reference.findViewById(R.id.evBox));
        affectedViews.add(reference.findViewById(R.id.ssBox));
        affectedViews.add(reference.findViewById(R.id.isoBox));

        affectedViews.add(reference.findViewById(R.id.autoFocusBt));
        affectedViews.add(reference.findViewById(R.id.continuousFocusBt));
        affectedViews.add(reference.findViewById(R.id.macroFocusBt));
        affectedViews.add(reference.findViewById(R.id.infinityFocusBt));
        affectedViews.add(reference.findViewById(R.id.fixedFocusBt));
        affectedViews.add(reference.findViewById(R.id.edofFocusBt));
        affectedViews.add(reference.findViewById(R.id.manualFocusBt));
        affectedViews.add(reference.findViewById(R.id.manualFocusBackBt));
        affectedViews.add(reference.findViewById(R.id.imageView));
        affectedViews.add(reference.findViewById(R.id.imageView2));

        affectedViews.add(reference.findViewById(R.id.captureButton));
        affectedViews.add(reference.findViewById(R.id.flashButton));
        affectedViews.add(reference.findViewById(R.id.camSwitchButton));
        affectedViews.add(reference.findViewById(R.id.modesButton));
        affectedViews.add(reference.findViewById(R.id.backButton));
        affectedViews.add(reference.findViewById(R.id.infoButton));

        OrientationManager.getInstance().on90DegreesChanged.addListener(eventData ->
        {
            TransitionManager.beginDelayedTransition(reference.findViewById(R.id.activity_main), rotate);
            for (View view : affectedViews)
                view.setRotation(eventData);
        });
    }


}
