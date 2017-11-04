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

import com.fkeglevich.rawdumper.camera.action.AutoFocusAction;
import com.fkeglevich.rawdumper.camera.action.listener.AutoFocusResult;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.PreviewArea;
import com.fkeglevich.rawdumper.camera.extension.Parameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ListValidator;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class FocusFeature extends WritableFeature<FocusMode, List<FocusMode>> implements AutoFocusAction
{
    private final AutoFocusAction autoFocusAction;

    FocusFeature(ParameterCollection parameterCollection, AutoFocusAction autoFocusAction)
    {
        super(Parameters.FOCUS_MODE, parameterCollection, ListValidator.createFromListParameter(parameterCollection, Parameters.FOCUS_MODE_VALUES));
        this.autoFocusAction = autoFocusAction;
    }

    @Override
    public boolean isAvailable()
    {
        return getAvailableValues().size() > 1;
    }

    @Override
    public void startAutoFocus(PreviewArea focusArea, AutoFocusResult callback)
    {
        checkFeatureAvailability(this);
        autoFocusAction.startAutoFocus(focusArea, callback);
    }
}
