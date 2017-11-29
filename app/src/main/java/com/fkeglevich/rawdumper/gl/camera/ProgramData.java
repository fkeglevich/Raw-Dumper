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

package com.fkeglevich.rawdumper.gl.camera;

import android.graphics.SurfaceTexture;

/**
 * Created by flavio on 28/11/17.
 */

class ProgramData
{
    private volatile int previewWidth;
    private volatile int previewHeight;

    private float[] surfaceMatrix = new float[16];
    private float[] surfaceSize   = new float[2];
    private float[] previewScale  = new float[2];

    void updateSurfaceSize(int surfaceWidth, int surfaceHeight)
    {
        this.surfaceSize[0] = surfaceWidth;
        this.surfaceSize[1] = surfaceHeight;
        this.previewScale[0] = 1f;
        this.previewScale[1] = 1f;
    }

    void updateSurfaceMatrix(SurfaceTexture surfaceTexture)
    {
        surfaceTexture.getTransformMatrix(surfaceMatrix);
    }

    void updatePreviewSize(int previewWidth, int previewHeight)
    {
        this.previewWidth  = previewWidth;
        this.previewHeight = previewHeight;
    }

    void updatePreviewScale()
    {
        previewScale[1] =  (previewHeight*1.0f / previewWidth);
    }

    void writeData(PreviewProgram program)
    {
        program.setSurfaceMatrix(surfaceMatrix);
        program.setSurfaceSize  (surfaceSize);
        program.setPreviewScale (previewScale);
    }
}
