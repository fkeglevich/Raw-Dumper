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
 * Created by Flávio Keglevich on 06/05/18.
 */
public class ManualTemperatureRange extends DataRange<ManualTemperature>
{
    //color_temp_values: 2300~7500
    public static ManualTemperatureRange parse(String value)
    {
        if (value == null) return null;

        String[] bounds = value.split("~");
        ManualTemperature lower = ManualTemperature.create(Integer.parseInt(bounds[0]));
        ManualTemperature upper = ManualTemperature.create(Integer.parseInt(bounds[1]));
        return new ManualTemperatureRange(lower, upper);
    }

    private ManualTemperatureRange(ManualTemperature lower, ManualTemperature upper)
    {
        super(lower, upper, ManualTemperature.DISABLED);
    }
}
