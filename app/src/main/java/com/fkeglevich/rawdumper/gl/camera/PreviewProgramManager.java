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

package com.fkeglevich.rawdumper.gl.camera;

import android.util.Log;

import com.fkeglevich.rawdumper.gl.exception.GLException;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by flavio on 08/01/18.
 */

public class PreviewProgramManager
{
    private static final String TAG = "PreviewProgramList";
    private static final byte[] vertices = {
            -1,  1,
            -1, -1,
             1,  1,
             1, -1
    };

    private final ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length);

    PreviewProgram defaultProgram = null;
    PreviewProgram revealProgram = null;
    PreviewProgram takePictureProgram = null;

    private PreviewProgram currentProgram = null;

    PreviewProgramManager()
    {
        vertexBuffer.put(vertices).position(0);
    }

    void setupPrograms()
    {
        try
        {
            defaultProgram = PreviewProgramFactory.createDefaultProgram();
            revealProgram = PreviewProgramFactory.createRevealProgram();
            takePictureProgram = PreviewProgramFactory.createTakePictureProgram();
        }
        catch (IOException | GLException e)
        {
            Log.e(TAG, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    void deletePrograms()
    {
        deleteProgram(defaultProgram);
        deleteProgram(revealProgram);
        deleteProgram(takePictureProgram);
    }

    private void deleteProgram(PreviewProgram program)
    {
        if (program != null) program.delete();
    }

    PreviewProgram getCurrentProgram()
    {
        return currentProgram;
    }

    void setCurrentProgram(PreviewProgram currentProgram)
    {
        this.currentProgram = currentProgram;
        this.currentProgram.use();
        this.currentProgram.setupVertices(vertexBuffer);
    }
}
