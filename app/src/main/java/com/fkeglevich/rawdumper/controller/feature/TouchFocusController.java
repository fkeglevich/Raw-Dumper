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

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import com.fkeglevich.rawdumper.camera.action.listener.AutoFocusResult;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.PreviewArea;
import com.fkeglevich.rawdumper.camera.feature.FocusFeature;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.ui.TouchFocusView;
import com.fkeglevich.rawdumper.ui.UiUtil;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class TouchFocusController extends FeatureController
{
    private final TouchFocusView focusView;
    private final Handler uiHandler;
    private FocusFeature focusFeature;
    private boolean enabled = true;
    private PreviewArea lastTouchArea = null;

    private final AutoFocusResult autoFocusResult = new AutoFocusResult()
    {
        @Override
        public void autoFocusDone(final boolean success)
        {
            uiHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    focusView.setMeteringArea(lastTouchArea,
                            success ? TouchFocusView.FOCUS_METERING_SUCCESS : TouchFocusView.FOCUS_METERING_FAIL);

                };
            });
        }
    };

    TouchFocusController(View clickArea, TouchFocusView focusView)
    {
        this.focusView = focusView;
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.focusFeature = null;

        clickArea.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(enabled && event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if (focusFeature != null && focusFeature.getValue().canAutoFocus())
                    {
                        int touchSize = UiUtil.dpToPixels(36, TouchFocusController.this.focusView.getContext());
                        lastTouchArea = PreviewArea.createTouchArea(v, event, touchSize);
                        TouchFocusController.this.focusView.setMeteringArea(lastTouchArea, TouchFocusView.FOCUS_METERING);
                        focusFeature.startAutoFocus(lastTouchArea, autoFocusResult);
                    }

                    v.performClick();
                }
                return false;
            }
        });
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        focusFeature = camera.getFocusFeature();
        if (!focusFeature.isAvailable())
        {
            reset();
            return;
        }

        focusFeature.getOnChanged().addListener(new EventListener<ParameterChangeEvent<FocusMode>>()
        {
            @Override
            public void onEvent(ParameterChangeEvent<FocusMode> eventData)
            {
                if (!eventData.parameterValue.canAutoFocus())
                    cleanFocus();
            }
        });
    }

    @Override
    protected void reset()
    {
        focusFeature = null;
        cleanFocus();
    }

    @Override
    protected void disable()
    {
        enabled = false;
    }

    @Override
    protected void enable()
    {
        enabled = true;
    }

    private void cleanFocus()
    {
        focusView.setMeteringArea(null, TouchFocusView.FOCUS_METERING);
    }
}
