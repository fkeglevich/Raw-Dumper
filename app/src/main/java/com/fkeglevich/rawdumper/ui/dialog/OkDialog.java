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

package com.fkeglevich.rawdumper.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.ui.UiUtil;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 07/11/17.
 */

public class OkDialog
{
    public static void show(ActivityReference reference, MessageException messageException, DialogInterface.OnClickListener listener)
    {
        AppCompatActivity activity = reference.weaklyGet();
        AlertDialog dialog = buildOkDialog(activity, messageException.getMessageResource(activity), listener);
        UiUtil.showDialogInImmersiveMode(dialog, activity);
    }

    private static AlertDialog buildOkDialog(Context context, String message, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(android.R.string.ok), listener);
        return builder.create();
    }
}
