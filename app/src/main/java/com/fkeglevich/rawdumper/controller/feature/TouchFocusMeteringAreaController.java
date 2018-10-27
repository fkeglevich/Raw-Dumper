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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.activity.KeyEventData;
import com.fkeglevich.rawdumper.camera.action.listener.AutoFocusResult;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.data.DataRange;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.data.PreviewArea;
import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.feature.FocusFeature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.camera.helper.PreviewHelper;
import com.fkeglevich.rawdumper.ui.TouchFocusView;
import com.fkeglevich.rawdumper.ui.UiUtil;
import com.fkeglevich.rawdumper.util.event.EventListener;

import static com.fkeglevich.rawdumper.ui.TouchFocusView.STROKE_WIDTH_DP;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class TouchFocusMeteringAreaController extends FeatureController
{
    private final TouchFocusView focusView;
    private final ActivityReference reference;
    private final Handler uiHandler;
    private FocusFeature focusFeature;
    private boolean enabled = true;
    private PreviewArea lastTouchArea = null;
    private Feature<CaptureSize> previewFeature;

    private final AutoFocusResult autoFocusResult = new AutoFocusResult()
    {
        @Override
        public void autoFocusDone(final boolean success)
        {
            uiHandler.post(() -> focusView.setMeteringArea(lastTouchArea,
                    success ? TouchFocusView.FOCUS_METERING_SUCCESS : TouchFocusView.FOCUS_METERING_FAIL));
        }
    };
    private final EventListener<KeyEventData> keyListener;

    TouchFocusMeteringAreaController(View clickArea, TouchFocusView focusView, ActivityReference reference)
    {
        this.focusView = focusView;
        this.reference = reference;
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.focusFeature = null;

        clickArea.setOnTouchListener((v, event) ->
        {
            if(enabled && event.getAction() == MotionEvent.ACTION_DOWN)
            {
                if (focusFeature != null)
                    performFocusOrExposureLock(v, (int)event.getX(), (int)event.getY(), shouldAutoFocus());

                v.performClick();
            }
            return false;
        });
        keyListener = eventData ->
        {
            if (enabled && eventData.keyCode == KeyEvent.KEYCODE_FOCUS)
            {
                if (focusFeature != null)
                {
                    if (lastTouchArea == null)
                    {
                        CaptureSize captureSize = previewFeature.getValue();
                        double scale = PreviewHelper.calculateVerticalScale(captureSize.getWidth(), captureSize.getHeight(), clickArea.getWidth(), clickArea.getHeight());
                        int w = clickArea.getWidth(), h = (int) (clickArea.getHeight() * scale);
                        performFocusOrExposureLock(clickArea, w / 2, h / 2, shouldAutoFocus());
                    } else
                        performFocusOrExposureLock(clickArea, lastTouchArea.getX(), lastTouchArea.getY(), shouldAutoFocus());
                }
            }
        };
    }

    private void performFocusOrExposureLock(View v, int x, int y, boolean autoFocus)
    {
        CaptureSize captureSize = previewFeature.getValue();
        double scale = PreviewHelper.calculateVerticalScale(captureSize.getWidth(), captureSize.getHeight(), v.getWidth(), v.getHeight());
        int w = v.getWidth(), h = (int)(v.getHeight() * scale);
        if (y < h)
        {
            int touchSize = UiUtil.dpToPixels(36, focusView.getContext());
            lastTouchArea = PreviewArea.createTouchArea(w, h, x, y, touchSize).fix(UiUtil.dpToPixels(STROKE_WIDTH_DP, v.getContext()));
            if (autoFocus)
            {
                drawMeteringArea();
                focusFeature.cancelAutoFocus();
                focusFeature.startAutoFocus(lastTouchArea, autoFocusResult);
            }
            else
            {
                if (focusFeature.setMeteringArea(lastTouchArea))
                    drawMeteringArea();
            }
        }
    }

    @Override
    protected void setup(TurboCamera camera)
    {
        previewFeature = camera.getPreviewFeature();
        focusFeature = (FocusFeature) camera.getListFeature(FocusMode.class);
        WritableFeature<ManualFocus, DataRange<ManualFocus>> manualFocusFeature = camera.getRangeFeature(ManualFocus.class);

        if (focusFeature.isAvailable())
            focusFeature.getOnChanging().addListener(eventData ->
            {
                cleanFocus();
                focusFeature.cancelAutoFocus();
            });

        if (manualFocusFeature.isAvailable())
            manualFocusFeature.getOnChanging().addListener(eventData ->
            {
                if (!eventData.parameterValue.equals(ManualFocus.DISABLED))
                    cleanFocus();
            });
        reference.onKeyDown.addListener(keyListener);
    }

    @Override
    protected void reset()
    {
        focusFeature = null;
        reference.onKeyDown.removeListener(keyListener);
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
        lastTouchArea = null;
    }

    private boolean shouldAutoFocus()
    {
        return focusFeature.isAvailable()
                && focusFeature.getValue().canAutoFocus()
                && !focusFeature.getValue().isContinuous();
    }

    private void drawMeteringArea()
    {
        focusView.setMeteringArea(lastTouchArea, TouchFocusView.FOCUS_METERING);
    }
}
