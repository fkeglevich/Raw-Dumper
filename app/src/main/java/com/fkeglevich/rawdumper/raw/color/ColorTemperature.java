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

package com.fkeglevich.rawdumper.raw.color;

import com.fkeglevich.rawdumper.util.Temperature;

/**
 * Represents correlated color temperature (CCT) and tint information
 *
 * Created by Flávio Keglevich on 02/04/18.
 */

public class ColorTemperature
{
    private final double temperature;
    private final double tint;

    public ColorTemperature(double temperature, double tint)
    {
        this.temperature = temperature;
        this.tint = tint;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public double getTint()
    {
        return tint;
    }

    public XYCoords toXYCoords()
    {
        return Temperature.getXYFromColorTemperature(this);
    }
}
