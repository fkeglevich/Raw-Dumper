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

package com.fkeglevich.rawdumper.camera.parameter.value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ToggleValidator implements ValueValidator<Boolean, List<Boolean>>
{
    private static final List<Boolean> BOOLEANS_LIST = Collections.unmodifiableList(Arrays.asList(true, false));

    private final boolean available;

    public ToggleValidator(boolean available)
    {
        this.available = available;
    }

    @Override
    public boolean isValid(Boolean value)
    {
        return value != null;
    }

    @Override
    public List<Boolean> getAvailableValues()
    {
        return BOOLEANS_LIST;
    }

    @Override
    public boolean isAvailable()
    {
        return available;
    }
}
