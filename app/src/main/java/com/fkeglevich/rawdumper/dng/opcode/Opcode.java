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

package com.fkeglevich.rawdumper.dng.opcode;

import com.fkeglevich.rawdumper.dng.DngVersion;

import java.nio.ByteBuffer;

import static com.fkeglevich.rawdumper.util.DataSize.INT_SIZE;

/**
 * Represents a DNG Opcode
 *
 * Created by Flávio Keglevich on 30/03/18.
 */

public abstract class Opcode
{
    private static final int OPCODE_HEADER_SIZE = INT_SIZE * 4;

    static final int OPCODE_DEFAULT_FLAGS = 0;

    private final int id;
    private final DngVersion dngVersion;
    private final int flags;

    Opcode(int id, DngVersion dngVersion, int flags)
    {
        this.id = id;
        this.dngVersion = dngVersion;
        this.flags = flags;
    }

    public abstract int getParameterAreaSize();

    public abstract void writeOpcodeData(ByteBuffer buffer);

    public int getOpcodeSize()
    {
        return OPCODE_HEADER_SIZE + getParameterAreaSize();
    }

    public int getId()
    {
        return id;
    }

    public DngVersion getDngVersion()
    {
        return dngVersion;
    }

    public int getFlags()
    {
        return flags;
    }
}
