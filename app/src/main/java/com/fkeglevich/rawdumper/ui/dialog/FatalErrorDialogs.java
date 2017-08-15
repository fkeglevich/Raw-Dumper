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
import com.fkeglevich.rawdumper.ui.UiUtils;

/**
 * Created by Flávio Keglevich on 13/08/2017.
 * TODO: Add a class header comment!
 */

public class FatalErrorDialogs
{
    private final AlertDialog needsPermissionDialog;
    private final AlertDialog needsRootAccessDialog;

    public FatalErrorDialogs(Context context)
    {
        needsPermissionDialog = buildTerminatingDialog(context, context.getResources().getString(R.string.permission_error));
        needsRootAccessDialog = buildTerminatingDialog(context, context.getResources().getString(R.string.root_access_error));
    }

    public void showNeedsPermissionDialog(AppCompatActivity activity)
    {
        UiUtils.showDialogInImmersiveMode(needsPermissionDialog, activity);
    }

    public void showNeedsRootAccessDialog(AppCompatActivity activity)
    {
        UiUtils.showDialogInImmersiveMode(needsRootAccessDialog, activity);
    }

    public void showGenericFatalErrorDialog(AppCompatActivity activity, String message)
    {
        AlertDialog fatalErrorDialog = buildTerminatingDialog(activity, message);
        UiUtils.showDialogInImmersiveMode(fatalErrorDialog, activity);
    }

    private AlertDialog buildTerminatingDialog(Context context, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(
                context.getResources().getString(R.string.terminating_dialog_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });

        return builder.create();
    }
}
