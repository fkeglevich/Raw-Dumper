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

package com.fkeglevich.rawdumper.camera.data.mode;

import com.fkeglevich.rawdumper.camera.data.DataContainer;
import com.fkeglevich.rawdumper.camera.data.ParameterValue;
import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.data.mode.format.FormatStrategy;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public abstract class Mode implements DataContainer<PictureFormat>, ParameterValue
{
    private final String modeParameterValue;
    private final boolean useUltraPixels;
    private final FormatStrategy formatStrategy;
    private final ModeType modeType;

    Mode(String modeParameterValue, boolean useUltraPixels, FormatStrategy formatStrategy, ModeType modeType)
    {
        this.modeParameterValue = modeParameterValue;
        this.useUltraPixels = useUltraPixels;
        this.formatStrategy = formatStrategy;
        this.modeType = modeType;
    }

    @Override
    public List<PictureFormat> getAvailableValues()
    {
        return formatStrategy.getAvailableValues();
    }

    @Override
    public String getParameterValue()
    {
        return modeParameterValue;
    }

    public boolean isAvailable()
    {
        return formatStrategy.isAvailable();
    }

    public boolean isUseUltraPixels()
    {
        return useUltraPixels;
    }

    public ModeType getModeType()
    {
        return modeType;
    }
}
