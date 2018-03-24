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
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by flavio on 28/11/17.
 */

public class PreviewRenderer implements GLSurfaceView.Renderer
{
    private static final String TAG = "PreviewRenderer";

    private SurfaceTextureManager surfaceTextureManager = new SurfaceTextureManager();
    private volatile boolean rendering = false;
    private volatile boolean updatingPreview = true;
    private ProgramData programData = new ProgramData();
    volatile float revealRadius = 0;

    private final Object programLock = new Object();
    private PreviewProgramManager previewProgramManager = new PreviewProgramManager(programLock);

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

    void pauseUpdatingPreview()
    {
        updatingPreview = false;
    }

    void resumeUpdatingPreview()
    {
        updatingPreview = true;
    }

    void useDefaultProgram()
    {
        //Log.i("PreviewRenderer", "Using default program");
        previewProgramManager.setCurrentProgram(previewProgramManager.defaultProgram);
    }

    void useTakePictureProgram()
    {
        //Log.i("PreviewRenderer", "Using take picture program");
        previewProgramManager.setCurrentProgram(previewProgramManager.takePictureProgram);
    }

    void useRevealProgram()
    {
        //Log.i("PreviewRenderer", "Using reveal program");
        previewProgramManager.setCurrentProgram(previewProgramManager.revealProgram);
    }

    void useFocusPeakProgram()
    {
        //Log.i("PreviewRenderer", "Using focus peak program");
        synchronized (programLock)
        {
            if (previewProgramManager.getCurrentProgram() == previewProgramManager.defaultProgram)
                previewProgramManager.setCurrentProgram(previewProgramManager.focusPeakProgram);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        surfaceTextureManager.createSurfaceTexture();
        surfaceTextureManager.activateSurfaceTexture();

        previewProgramManager.deletePrograms();
        previewProgramManager.setupPrograms();
        previewProgramManager.setCurrentProgram(previewProgramManager.revealProgram);
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
            if (updatingPreview)
                surfaceTextureManager.updateTexImage();

            programData.updateSurfaceMatrix(surfaceTextureManager.getSurfaceTexture());
            programData.updatePreviewScale();
            synchronized (programLock)
            {
                previewProgramManager.useCurrentProgram();
                programData.writeData(previewProgramManager.getCurrentProgram());
                previewProgramManager.getCurrentProgram().setRevealRadius(revealRadius);
            }

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    private void clearFrame()
    {
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
