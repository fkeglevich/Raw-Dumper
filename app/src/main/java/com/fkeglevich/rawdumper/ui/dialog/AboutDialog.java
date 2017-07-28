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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.ui.UiUtils;

/**
 * Represents the About this app dialog
 *
 * Created by Flávio Keglevich on 16/07/2017.
 */

public class AboutDialog
{
    private static final String TAG = "AboutDialog";

    private static final int TEXT_VIEW_PADDING = 8;
    private static final int TEXT_VIEW_MAX_LINES = 40;

    private AlertDialog alertDialog;

    public AboutDialog(Context context)
    {
        PackageInfo packageInfo = null;

        try
        {
            packageInfo = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {   Log.w(TAG, "Exception when the app tried to get the PackageInfo from itself. This should NOT happen!");   }

        String messageStr = context.getResources().getString(R.string.app_name);

        if (packageInfo != null)
            messageStr += " v" + packageInfo.versionName + "\n\n";

        messageStr += context.getResources().getString(R.string.about_message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final TextView messageView = new TextView(context);
        int paddingValue = UiUtils.dpToPixels(TEXT_VIEW_PADDING, context);
        messageView.setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
        final SpannableString spannableStr = new SpannableString(messageStr);
        Linkify.addLinks(spannableStr, Linkify.WEB_URLS);
        messageView.setMaxLines(TEXT_VIEW_MAX_LINES);
        messageView.setVerticalScrollBarEnabled(true);
        messageView.setMovementMethod(new ScrollingMovementMethod());
        messageView.setText(spannableStr);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setCancelable(false);
        builder.setTitle(context.getResources().getString(R.string.about_title));

        builder.setPositiveButton(
                context.getResources().getString(R.string.about_dismiss_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setView(messageView);

        alertDialog = builder.create();
    }

    public void showDialog(AppCompatActivity activity)
    {
        UiUtils.showDialogInImmersiveMode(alertDialog, activity);
    }
}