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
 * Created by Flávio Keglevich on 26/10/17.
 */

public class ManualFocus
{
    private static final int INVALID_NUMERIC_VALUE = 0;

    public static final ManualFocus DISABLED = new ManualFocus(INVALID_NUMERIC_VALUE);

    public static ManualFocus create(int numericValue)
    {
        if (numericValue <= INVALID_NUMERIC_VALUE)
            throw new IllegalArgumentException();

        return new ManualFocus(numericValue);
    }

    private final int numericValue;

    private ManualFocus(int numericValue)
    {
        this.numericValue = numericValue;
    }

    public int getNumericValue()
    {
        if (this == DISABLED)
            throw new RuntimeException("Disabled manual focus doesn't have a numeric value!");

        return numericValue;
    }
}
