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

package com.fkeglevich.rawdumper.controller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.camera.data.Displayable;
import com.fkeglevich.rawdumper.controller.context.ContextManager;
import com.fkeglevich.rawdumper.controller.feature.ExternalNotificationController;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 23/10/17.
 */

public class ToastNotificationController implements ExternalNotificationController
{
    private final Toast toast;
    private final int messageResource;

    @SuppressLint("ShowToast")
    public ToastNotificationController(Context context, @StringRes int messageResource)
    {
        toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        this.messageResource = messageResource;
    }

    @Override
    public void showNotification(Displayable newValue)
    {
        toast.setText(ContextManager.getApplicationContext().getString(messageResource, newValue.displayValue()));
        toast.show();
    }
}
