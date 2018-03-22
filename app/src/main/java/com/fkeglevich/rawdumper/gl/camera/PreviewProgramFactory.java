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

import com.fkeglevich.rawdumper.gl.Program;
import com.fkeglevich.rawdumper.gl.ProgramFactory;
import com.fkeglevich.rawdumper.gl.exception.GLException;

import java.io.IOException;

/**
 * Created by flavio on 29/11/17.
 */

public class PreviewProgramFactory
{
    private static final String VERTEX_SHADER_ASSET = "shaders/preview_vertex.glsl";

    public static PreviewProgram createDefaultProgram() throws IOException, GLException
    {
        Program program = ProgramFactory.createFromAssets(VERTEX_SHADER_ASSET, "shaders/preview_frag.glsl");
        return PreviewProgram.create(program, false);
    }

    public static PreviewProgram createRevealProgram() throws IOException, GLException
    {
        Program program = ProgramFactory.createFromAssets(VERTEX_SHADER_ASSET, "shaders/reveal_preview_frag.glsl");
        return PreviewProgram.create(program, true);
    }

    public static PreviewProgram createTakePictureProgram() throws IOException, GLException
    {
        Program program = ProgramFactory.createFromAssets(VERTEX_SHADER_ASSET, "shaders/take_picture_frag.glsl");
        return PreviewProgram.create(program, true);
    }

    public static PreviewProgram createFocusPeakingProgram() throws IOException, GLException
    {
        Program program = ProgramFactory.createFromAssets(VERTEX_SHADER_ASSET, "shaders/focus_peak_frag.glsl");
        return PreviewProgram.create(program, false);
    }
}
