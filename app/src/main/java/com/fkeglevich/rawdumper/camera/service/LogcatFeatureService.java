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

package com.fkeglevich.rawdumper.camera.service;

import android.support.annotation.Nullable;
import android.util.Log;

public abstract class LogcatFeatureService<T>
{
    final LogcatMatch match;

    private String fixedValue;

    protected LogcatFeatureService(LogcatMatch match)
    {
        this.match = match;
    }

    protected abstract T parseString(String string);

    public synchronized void fixValue()
    {
        fixedValue = match.latestMatch;
    }

    @Nullable
    public synchronized T getValue()
    {
        String string = fixedValue != null ? fixedValue : match.latestMatch;
        fixedValue = null;
        if (string != null)
        {
            try
            {
                return parseString(string);
            }
            catch (Exception e)
            {
                Log.i(LogcatFeatureService.class.getSimpleName(), e.getMessage());
                return null;
            }
        }
        else
            return null;
    }

    public boolean isAvailable()
    {
        return match.enabled;
    }

    synchronized void setAvailable(boolean available)
    {
        if (!available) match.latestMatch = null;
        match.enabled = available;
    }
}
