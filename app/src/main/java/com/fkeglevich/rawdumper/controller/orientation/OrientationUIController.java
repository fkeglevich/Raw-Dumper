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

import android.os.Handler;
import android.view.OrientationEventListener;
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
    private static final int ANGLE_MARGIN = 5;
    private static final int DELAY_MILLIS = 100;
    private static final long ROTATE_DURATION = 1000L;

    private final List<View> affectedViews = new ArrayList<>();
    private final Rotate rotate = new Rotate();
    private final Handler handler = new Handler();
    private final Runnable timedRunnable;

    private int lastOrientation = 0;
    private int lastViewRotation = 0;
    private boolean dirty = false;

    public OrientationUIController(ActivityReference reference)
    {
        rotate.setDuration(ROTATE_DURATION);

        affectedViews.add(reference.findViewById(R.id.autoWbBt));
        affectedViews.add(reference.findViewById(R.id.incandescentWbBt));
        affectedViews.add(reference.findViewById(R.id.fluorescentWbBt));
        affectedViews.add(reference.findViewById(R.id.sunnyWbBt));
        affectedViews.add(reference.findViewById(R.id.cloudyWbBt));
        affectedViews.add(reference.findViewById(R.id.shadeWbBt));
        affectedViews.add(reference.findViewById(R.id.twilightWbBt));
        affectedViews.add(reference.findViewById(R.id.manualWbBt));
        affectedViews.add(reference.findViewById(R.id.manualWbBackBt));
        affectedViews.add(reference.findViewById(R.id.manualWbCloudyIcon));
        affectedViews.add(reference.findViewById(R.id.manualWbTungstenIcon));

        affectedViews.add(reference.findViewById(R.id.wbBox));
        affectedViews.add(reference.findViewById(R.id.focusBox));
        affectedViews.add(reference.findViewById(R.id.evBox));
        affectedViews.add(reference.findViewById(R.id.ssBox));
        affectedViews.add(reference.findViewById(R.id.isoBox));

        affectedViews.add(reference.findViewById(R.id.apertureText));

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

        OrientationManager.getInstance().onOrientationChanged.addListener(eventData ->
        {
            int orientation = eventData != OrientationEventListener.ORIENTATION_UNKNOWN ? eventData : 0;
            if (hasProblematicAngle(orientation)) return;

            orientation = calculate90DegreeValue(orientation);
            if (lastOrientation != orientation)
            {
                lastOrientation = orientation;
                dirty = true;
            }
        });

        timedRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (dirty)
                {
                    int viewRotation = lastOrientation;
                    int delta = viewRotation - lastViewRotation;

                    if (delta > 180) viewRotation -= 360;
                    if (delta < -180) viewRotation += 360;

                    rotateViews(viewRotation, reference);
                    dirty = false;
                    lastViewRotation = viewRotation;
                }
                handler.postDelayed(this, DELAY_MILLIS);
            }
        };
        handler.post(timedRunnable);
    }

    private void rotateViews(int viewRotation, ActivityReference reference)
    {
        TransitionManager.beginDelayedTransition(reference.findViewById(R.id.activity_main), rotate);
        for (View view : affectedViews)
            view.setRotation(viewRotation);
    }

    private int calculate90DegreeValue(int degrees)
    {
        if (degrees >= 45 && degrees < 135) return 270;
        if (degrees >= 135 && degrees < 225) return 180;
        if (degrees >= 225 && degrees < 315) return 90;
        else return 0;
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean hasProblematicAngle(int degrees)
    {
        if (Math.abs(degrees - 45)  < ANGLE_MARGIN) return true;
        if (Math.abs(degrees - 135) < ANGLE_MARGIN) return true;
        if (Math.abs(degrees - 225) < ANGLE_MARGIN) return true;
        if (Math.abs(degrees - 315) < ANGLE_MARGIN) return true;
        return false;
    }
}
