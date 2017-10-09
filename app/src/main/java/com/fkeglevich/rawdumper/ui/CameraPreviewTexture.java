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

package com.fkeglevich.rawdumper.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.util.event.EventListener;

import static com.fkeglevich.rawdumper.R.id.textureView;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 09/10/17.
 */

public class CameraPreviewTexture extends PausingTextureView
{
    public CameraPreviewTexture(Context context)
    {
        super(context);
    }

    public CameraPreviewTexture(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CameraPreviewTexture(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void setupPreview(TurboCamera camera)
    {
        updateGeometry(camera.getPreviewFeature().getValue());
        camera.getPreviewFeature().getOnChanged().addListener(new EventListener<ParameterChangeEvent<CaptureSize>>()
        {
            @Override
            public void onEvent(ParameterChangeEvent<CaptureSize> eventData)
            {
                updateGeometry(eventData.parameterValue);
            }
        });
    }

    private void updateGeometry(CaptureSize previewSize)
    {
        float scale = (float) previewSize.getHeight() / (float) previewSize.getWidth();
        float translation = (getHeight() / 2f) - (getHeight() * scale / 2f);

        Matrix matrix = new Matrix();
        matrix.postScale(1, scale);
        //We currently are not using this yet
        //matrix.postTranslate(0, translation);
        setTransform(matrix);
    }
}
