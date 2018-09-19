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

package com.fkeglevich.rawdumper.camera.data;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 03/07/18.
 */
public class DualLed
{
    public static final int LED_VALUE_MIN = 0;
    public static final int LED_VALUE_MAX = 100;
    public static final int LED_VALUE_TURNED_OFF = LED_VALUE_MIN;
    public static final int LED_VALUE_TURNED_ON_MIN = 1;

    private final int lowLedValue;
    private final int highLedValue;

    public DualLed(int lowLedValue, int highLedValue)
    {
        this.lowLedValue = lowLedValue;
        this.highLedValue = highLedValue;
    }

    public int getLowLedValue()
    {
        return lowLedValue;
    }

    public int getHighLedValue()
    {
        return highLedValue;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DualLed dualLed = (DualLed) o;

        if (lowLedValue != dualLed.lowLedValue) return false;
        return highLedValue == dualLed.highLedValue;
    }

    @Override
    public int hashCode()
    {
        int result = lowLedValue;
        result = 31 * result + highLedValue;
        return result;
    }

    @Override
    public String toString()
    {
        return "[DualLed low: " + lowLedValue + ", high: " + highLedValue + "]";
    }
}
