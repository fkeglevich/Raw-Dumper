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
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.gl.Program;
import com.fkeglevich.rawdumper.gl.ProgramFactory;
import com.fkeglevich.rawdumper.gl.Shader;
import com.fkeglevich.rawdumper.gl.ShaderType;
import com.fkeglevich.rawdumper.gl.exception.GLException;
import com.fkeglevich.rawdumper.util.AssetUtil;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by flavio on 28/11/17.
 */

public class PreviewRenderer implements GLSurfaceView.Renderer
{
    private static final byte[] vertices = {
            -1,  1,
            -1, -1,
             1,  1,
             1, -1
    };

    private final ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length);

    private SurfaceTextureManager surfaceTextureManager = new SurfaceTextureManager();
    private ProgramData programData = new ProgramData();
    private volatile boolean rendering = false;
    private PreviewProgram program = null;

    PreviewRenderer()
    {
        vertexBuffer.put(vertices).position(0);
    }

    SurfaceTexture getSurfaceTexture()
    {
        return surfaceTextureManager.getSurfaceTexture();
    }

    EventDispatcher<SurfaceTexture> getOnSurfaceTextureAvailable()
    {
        return surfaceTextureManager.onSurfaceTextureAvailable;
    }

    void updatePreviewSize(CaptureSize previewSize)
    {
        programData.updatePreviewSize(previewSize.getWidth(), previewSize.getHeight());
    }

    void startRender()
    {
        rendering = true;
    }

    void stopRender()
    {
        rendering = false;
    }

    void setProgram(PreviewProgram program)
    {
        this.program = program;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        surfaceTextureManager.createSurfaceTexture();

        if (program != null)
            program.delete();

        try
        {
            Shader vertexShader = Shader.create(ShaderType.VERTEX);
            String vertexShaderCode = AssetUtil.getAssetAsString("shaders/preview_vertex.glsl");
            vertexShader.compile(vertexShaderCode);

            Shader fragmentShader = Shader.create(ShaderType.FRAGMENT);
            //String fragmentShaderCode = AssetUtil.getAssetAsString("shaders/reveal_preview_frag.glsl");
            String fragmentShaderCode = AssetUtil.getAssetAsString("shaders/preview_frag.glsl");
            fragmentShader.compile(fragmentShaderCode);

            //Program program = ProgramFactory.createFromAssets("shaders/preview_frag.glsl", "shaders/preview_vertex.glsl");
            Program program2 = ProgramFactory.create(vertexShader, fragmentShader);
            setProgram(PreviewProgram.create(program2));
        }
        catch (GLException e)
        {
            Log.e("ASDA", e.getMessage());
            throw new RuntimeException();
        }
        catch (IOException e)
        {
            Log.e("ASDA", e.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        programData.updateSurfaceSize(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (rendering)
        {
            rendering = false;
            clearFrame();
            SurfaceTexture surfaceTexture = surfaceTextureManager.getSurfaceTexture();
            surfaceTexture.updateTexImage();

            program.use();
            programData.updateSurfaceMatrix(surfaceTexture);
            programData.updatePreviewScale();
            programData.writeData(program);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, surfaceTextureManager.getTexture().getHandle());

            program.setupVertices(vertexBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    private void clearFrame()
    {
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
