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

package com.fkeglevich.rawdumper.camera.feature;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.fkeglevich.rawdumper.camera.action.AutoFocusAction;
import com.fkeglevich.rawdumper.camera.action.listener.AutoFocusResult;
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.PreviewArea;
import com.fkeglevich.rawdumper.camera.extension.Parameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ListValidator;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.HandlerDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class FocusFeature extends ListFeature<FocusMode> implements AutoFocusAction
{
    public EventDispatcher<Void> onStartAutoFocus = new SimpleDispatcher<>();
    public EventDispatcher<Boolean> onAutoFocusResult = new HandlerDispatcher<>(Looper.getMainLooper());

    private final AutoFocusAction autoFocusAction;
    private final Handler handler = new Handler(Looper.myLooper());

    @NonNull
    private static ListValidator<FocusMode> getValidator(ParameterCollection parameterCollection, ListFeature<Flash> flashFeature)
    {
        ListValidator<FocusMode> validator = ListValidator.createFromListParameter(parameterCollection, Parameters.FOCUS_MODE_VALUES);
        if (flashFeature != null && flashFeature.getAvailableValues().contains(Flash.ON))
        {
            List<FocusMode> availableValues = new ArrayList<>(validator.getAvailableValues());
            availableValues.add(FocusMode.FLASH);
            validator = new ListValidator<>(availableValues);
        }
        return validator;
    }

    FocusFeature(ParameterCollection parameterCollection, ListFeature<Flash> flashFeature, AutoFocusAction autoFocusAction)
    {
        super(Parameters.FOCUS_MODE, parameterCollection, getValidator(parameterCollection, flashFeature));
        this.autoFocusAction = autoFocusAction;
    }

    @Override
    public boolean isAvailable()
    {
        List<FocusMode> values = getAvailableValues();
        boolean isUnavailable = values.size() == 1 && values.get(0).equals(FocusMode.FIXED);

        return !isUnavailable;
    }

    @Override
    public void startAutoFocus(PreviewArea focusArea, AutoFocusResult callback)
    {
        checkFeatureAvailability(this);
        onStartAutoFocus.dispatchEvent(null);

        handler.postDelayed(() ->
        {
            autoFocusAction.startAutoFocus(focusArea, success ->
            {
                onAutoFocusResult.dispatchEvent(success);
                callback.autoFocusDone(success);
            });
        }, 100);
    }

    @Override
    public void cancelAutoFocus()
    {
        checkFeatureAvailability(this);
        autoFocusAction.cancelAutoFocus();
    }

    @Override
    public boolean setMeteringArea(PreviewArea area)
    {
        return autoFocusAction.setMeteringArea(area);
    }
}
