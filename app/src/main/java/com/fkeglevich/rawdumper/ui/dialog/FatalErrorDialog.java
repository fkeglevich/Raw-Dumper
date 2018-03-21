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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.ui.UiUtil;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * Created by Flávio Keglevich on 13/08/2017.
 * TODO: Add a class header comment!
 */

public class FatalErrorDialog
{
    public static void show(ActivityReference reference, MessageException messageException)
    {
        AppCompatActivity activity = reference.weaklyGet();
        AlertDialog dialog = buildTerminatingDialog(activity, messageException.getMessageResource(activity));
        UiUtil.showDialogInImmersiveMode(dialog, activity);
    }

    private static AlertDialog buildTerminatingDialog(Context context, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(
                context.getResources().getString(R.string.terminating_dialog_button),
                (dialog, id) -> System.exit(0));

        return builder.create();
    }
}
