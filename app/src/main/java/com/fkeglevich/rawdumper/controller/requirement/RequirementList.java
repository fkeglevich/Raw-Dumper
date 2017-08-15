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
 * Created by Flávio Keglevich on 11/08/2017.
 * TODO: Add a class header comment!
 */

public class RequirementList<T extends Enum<T>>
{
    private final RequirementCollection<T> requirements;
    private final IRequirementListener<T> requirementListener;

    public RequirementList(Class<T> enumType, IRequirementListener<T> listener)
    {
        requirements = new RequirementCollection<>(enumType);
        requirementListener = listener;
    }

    public synchronized void setRequirement(T id, Object requirement)
    {
        requirements.setRequirement(id, requirement);
        if (requirements.hasAllRequirements())
            requirementListener.sendRequirements(requirements);
    }

    public synchronized void unsetRequirement(T id)
    {
        requirements.setRequirement(id, null);
    }
}
