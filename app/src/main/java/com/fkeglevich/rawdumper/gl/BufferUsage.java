/*
 * Copyright 2019, Flávio Keglevich
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

package com.fkeglevich.rawdumper.gl;

public enum BufferUsage
{
    STREAM_READ(0x88E1),
    STREAM_COPY(0x88E2),

    STATIC_READ(0x88E5),
    STATIC_COPY(0x88E6),

    DYNAMIC_READ(0x88E9),
    DYNAMIC_COPY(0x88EA);

    private final int glBufferUsage;

    BufferUsage(int glBufferUsage)
    {
        this.glBufferUsage = glBufferUsage;
    }

    public int getBufferUsage()
    {
        return glBufferUsage;
    }
}
