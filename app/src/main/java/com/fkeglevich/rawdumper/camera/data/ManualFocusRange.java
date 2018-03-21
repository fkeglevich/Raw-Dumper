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

package com.fkeglevich.rawdumper.camera.data;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public class ManualFocusRange
{
    private final ManualFocus lower;
    private final ManualFocus upper;

    //focus_range_values: 5~100,1
    public static ManualFocusRange parse(String value)
    {
        if (value == null) return null;

         String[] bounds = value.split(",")[0].split("~");
         ManualFocus lower = ManualFocus.create(Integer.parseInt(bounds[0]));
         ManualFocus upper = ManualFocus.create(Integer.parseInt(bounds[1]));
         return new ManualFocusRange(lower, upper);
    }

    private ManualFocusRange(ManualFocus lower, ManualFocus upper)
    {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean contains(ManualFocus value)
    {
        if (value.equals(ManualFocus.DISABLED))
            return true;

        return value.compareTo(getLower()) >= 0 && value.compareTo(getUpper())  <= 0;
    }

    public ManualFocus getLower()
    {
        return lower;
    }

    public ManualFocus getUpper()
    {
        return upper;
    }
}
