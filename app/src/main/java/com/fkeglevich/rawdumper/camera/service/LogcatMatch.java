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

import java.io.UnsupportedEncodingException;

public class LogcatMatch
{
    public static final int MATCH_BUFFER_SIZE = 100;

    public final String tag;
    public final LogPriority priority;
    public final String fingerprintPrefix;
    public byte[] fingerprintPrefixBytes;

    public volatile boolean enabled = false;

    public final byte[] volatileBuffer = new byte[MATCH_BUFFER_SIZE];

    public LogcatMatch(String tag, LogPriority priority, String fingerprintPrefix)
    {
        this.tag = tag;
        this.priority = priority;
        this.fingerprintPrefix = fingerprintPrefix;
        try
        {
            this.fingerprintPrefixBytes = fingerprintPrefix.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException ignored)
        {   }
    }
}
