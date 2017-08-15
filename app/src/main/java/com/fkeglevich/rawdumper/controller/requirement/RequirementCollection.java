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

package com.fkeglevich.rawdumper.controller.requirement;

/**
 * Created by Flávio Keglevich on 13/08/2017.
 * TODO: Add a class header comment!
 */

class RequirementCollection<T extends Enum<T>> implements IRequirementReader<T>
{
    private final Object[] requirements;

    RequirementCollection(Class<T> enumType)
    {
        this.requirements = new Object[enumType.getEnumConstants().length];
    }

    public void setRequirement(T id, Object requirement)
    {
        requirements[id.ordinal()] = requirement;
    }

    @SuppressWarnings("unchecked")
    public <O> O getRequirement(T id)
    {
        return (O) requirements[id.ordinal()];
    }

    public boolean hasAllRequirements()
    {
        for (Object value : requirements)
            if (value == null)
                return false;

        return true;
    }
}
