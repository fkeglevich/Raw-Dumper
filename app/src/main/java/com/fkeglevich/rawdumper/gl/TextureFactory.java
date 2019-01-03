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

package com.fkeglevich.rawdumper.gl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * Created by flavio on 25/11/17.
 */

public class TextureFactory
{
    public static Texture createCameraPreviewTexture()
    {
        Texture result = Texture.create(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        result.setHorizontalWrapMode(GLES20.GL_CLAMP_TO_EDGE);
        result.setVerticalWrapMode  (GLES20.GL_CLAMP_TO_EDGE);
        result.setMinFilter(GLES20.GL_LINEAR);
        result.setMagFilter(GLES20.GL_LINEAR);

        return result;
    }

    public static Texture create2DTexture()
    {
        Texture result = Texture.create(GLES20.GL_TEXTURE_2D);
        result.setHorizontalWrapMode(GLES20.GL_CLAMP_TO_EDGE);
        result.setVerticalWrapMode  (GLES20.GL_CLAMP_TO_EDGE);
        result.setMinFilter(GLES20.GL_LINEAR);
        result.setMagFilter(GLES20.GL_LINEAR);

        return result;
    }
}
