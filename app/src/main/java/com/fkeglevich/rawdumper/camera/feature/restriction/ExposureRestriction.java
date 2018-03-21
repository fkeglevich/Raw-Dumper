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

import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.util.event.EventListener;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 22/09/17.
 */

@SuppressWarnings("unchecked")
public class ExposureRestriction
{
    private enum ExposureType
    {
        ISO(Iso.AUTO),
        SHUTTER_SPEED(ShutterSpeed.AUTO),
        EV(Ev.DEFAULT);

        final Object autoValue;
        ExposureType(Object autoValue)
        {
            this.autoValue = autoValue;
        }
    }

    private Set<ExposureType> historySet = new LinkedHashSet<>();
    private Map<ExposureType, WritableFeature> parameterMap = new HashMap<>();

    public ExposureRestriction(WritableFeature<Iso, List<Iso>> isoFeature,
                               WritableFeature<ShutterSpeed, List<ShutterSpeed>> shutterSpeedFeature,
                               WritableFeature<Ev, List<Ev>> evFeature)
    {
        parameterMap.put(ExposureType.ISO,              isoFeature);
        parameterMap.put(ExposureType.SHUTTER_SPEED,    shutterSpeedFeature);
        parameterMap.put(ExposureType.EV,               evFeature);

        isoFeature.getOnChanging()
                .addListener(this.<Iso>createEventListener(ExposureType.ISO));

        shutterSpeedFeature.getOnChanging()
                .addListener(this.<ShutterSpeed>createEventListener(ExposureType.SHUTTER_SPEED));

        evFeature.getOnChanging()
                .addListener(this.<Ev>createEventListener(ExposureType.EV));
    }

    private <T> EventListener<ParameterChangeEvent<T>> createEventListener(final ExposureType type)
    {
        return eventData ->
        {
            addExposureToHistory(type, eventData.parameterValue);
            if (hasInvalidExposureSequence())
                changeOldestChangedParameter();
        };
    }

    private void addExposureToHistory(ExposureType type, Object data)
    {
        if (historySet.contains(type))
            historySet.remove(type);

        if (isManual(type, data))
            historySet.add(type);
    }

    private boolean isManual(ExposureType type, Object data)
    {
        return data != type.autoValue;
    }

    private boolean hasInvalidExposureSequence()
    {
        for (ExposureType type : ExposureType.values())
            if (!historySet.contains(type))
                return false;

        return true;
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private void changeOldestChangedParameter()
    {
        for (ExposureType type : historySet)
        {
            WritableFeature feature = parameterMap.get(type);
            feature.setValue(type.autoValue);
            return;
        }
    }
}
