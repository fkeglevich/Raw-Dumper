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

package com.fkeglevich.rawdumper.camera.feature.restriction;

import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.feature.FocusFeature;
import com.fkeglevich.rawdumper.camera.feature.ManualFocusFeature;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.util.event.EventListener;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public class FocusRestriction
{
    public FocusRestriction(FocusFeature focusFeature, ManualFocusFeature manualFocusFeature)
    {
        focusFeature.getOnChanging().addListener(eventData ->
        {
            if (manualFocusFeature.isAvailable())
                manualFocusFeature.setValue(ManualFocus.DISABLED);
        });

        manualFocusFeature.getOnChanging().addListener(eventData ->
        {
            if (!eventData.parameterValue.equals(ManualFocus.DISABLED))
                focusFeature.cancelAutoFocus();
        });
    }
}
