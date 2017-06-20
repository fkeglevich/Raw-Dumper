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
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fkeglevich.rawdumper.camera.TurboCamera;

/**
 * Created by Flávio Keglevich on 18/06/2017.
 * TODO: Add a class header comment!
 */

public class ValueButton extends AppCompatButton implements View.OnClickListener
{
    private String key;
    private String value;
    private TurboCamera camera;
    private Toast currentToast;

    public ValueButton(Context context, String key, String value, TurboCamera camera, Toast currentToast)
    {
        super(context);
        this.key = key;
        this.value = value;
        this.camera = camera;
        this.setBackground(null);
        this.currentToast = currentToast; //Toast.makeText(context, "", Toast.LENGTH_LONG);
        this.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(UiUtils.dpToPixels(70, context),
                LinearLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
        this.setOnClickListener(this);
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public void onClick(View v)
    {
        camera.setParameter(key, value);
        currentToast.setText(this.getText());
        currentToast.show();
    }
}
