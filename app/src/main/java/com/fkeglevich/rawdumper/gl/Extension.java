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

package com.fkeglevich.rawdumper.gl;

import android.opengl.GLES20;

/**
 * Lists the common OpenGL extensions used by this app and has methods for dealing with them
 *
 * Created by Flávio Keglevich on 22/03/18.
 */

public class Extension
{
    public static final String GL_OES_STANDARD_DERIVATIVES = "GL_OES_standard_derivatives";

    public static boolean hasExtension(String name)
    {
        return GLES20.glGetString(GLES20.GL_EXTENSIONS).contains(name);
    }
}
