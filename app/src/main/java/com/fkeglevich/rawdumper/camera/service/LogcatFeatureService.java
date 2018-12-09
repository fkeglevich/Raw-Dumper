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

import androidx.annotation.Nullable;
import android.util.Log;

import static com.fkeglevich.rawdumper.camera.service.LogcatMatch.MATCH_BUFFER_SIZE;

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
        fixedValue = getMatchString();
    }

    @Nullable
    public synchronized T getValue()
    {
        String string = fixedValue != null ? fixedValue : getMatchString();
        fixedValue = null;
        if (match.enabled && !string.isEmpty())
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
        match.enabled = available;
    }

    private String getMatchString()
    {
        synchronized (match.tag)
        {
            return new String(match.volatileBuffer, 0, MATCH_BUFFER_SIZE);
        }
    }
}
