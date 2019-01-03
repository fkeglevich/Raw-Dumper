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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fkeglevich.rawdumper.R;
import com.fkeglevich.rawdumper.ui.UiUtil;
import com.fkeglevich.rawdumper.util.AppPackageUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Represents the About this app dialog
 *
 * Created by Flávio Keglevich on 16/07/2017.
 */

public class AboutDialog
{
    private final AlertDialog alertDialog;

    public AboutDialog(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(R.string.about_title);
        builder.setPositiveButton(
                context.getResources().getString(android.R.string.ok),
                (dialog, id) -> dialog.dismiss());
        builder.setView(createDialogView(context));
        alertDialog = builder.create();
    }

    @SuppressLint("InflateParams")
    @NonNull
    private View createDialogView(Context context)
    {
        String messageStr = context.getResources().getString(R.string.about_message, AppPackageUtil.getAppNameWithVersion());
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.about_view, null);
        TextView aboutText = view.findViewById(R.id.aboutText);
        aboutText.setText(messageStr);
        return view;
    }

    public void showDialog(AppCompatActivity activity)
    {
        UiUtil.showDialogInImmersiveMode(alertDialog, activity);
    }
}
