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

package com.fkeglevich.rawdumper.raw.mkn;

import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;

/**
 * Created by flavio on 24/03/18.
 */

public class MakerNoteInfo
{
    public final byte[] originalMakerNote;

    //Exposure
    public Iso iso                      = null;
    public ShutterSpeed exposureTime    = null;

    //White Balance and color
    public Double temperature           = null;
    public Double tint                  = null;
    public float[] colorMatrix          = null;

    public MakerNoteInfo(byte[] makerNote)
    {
        this.originalMakerNote = makerNote.clone();
    }
}
