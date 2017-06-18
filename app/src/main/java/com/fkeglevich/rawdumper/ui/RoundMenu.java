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
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.fkeglevich.rawdumper.R;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;

/**
 * Created by Flávio Keglevich on 03/05/2017.
 * TODO: Add a class header comment!
 */

public class RoundMenu extends FrameLayout
{
    private ImageButton button;
    private ImageButton closeButton;
    private FrameLayout maskLayout;
    private LinearLayout innerLayout;
    private OnClickListener openCloseListener;

    private ChangeBounds moveTransition = new ChangeBounds();
    private Fade fadeTransition = new Fade();

    private int numButtons = 0;
    private boolean closed = true;

    private int orientation;
    private int direction;

    public RoundMenu(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context);
        TypedArray styledAttrs = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundMenu, 0, 0);

        openCloseListener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closed = !closed;
                doTransitions();
                updateClosedStatus(v.getContext());
            }
        };
        try
        {
            setupMaskLayout(context, styledAttrs);
            setupInnerLayout(context, styledAttrs);
            setupButton(context, styledAttrs);
            setupCloseButton(context, styledAttrs);
            addButton(context, styledAttrs);
            addButton(context, styledAttrs);
            addButton(context, styledAttrs);
            updateClosedStatus(context);
        }
        finally
        {
            styledAttrs.recycle();
        }
        setClipChildren(false);
    }

    private ImageButton createRoundButton(Context context, Drawable buttonIcon, Drawable background)
    {
        ImageButton roundButton = new ImageButton(context);
        roundButton.setBackground(background);
        //roundButton.setPadding(UiUtils.dpToPixels(8, context), UiUtils.dpToPixels(8, context), UiUtils.dpToPixels(8, context), UiUtils.dpToPixels(8, context));
        UiUtils.setDpPaddingFromDimension(R.dimen.round_button_padding, roundButton);
        roundButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        roundButton.setImageDrawable(buttonIcon);
        return roundButton;
    }

    private ImageButton createRoundButton(Context context, Drawable buttonIcon)
    {
        return createRoundButton(context, buttonIcon, ContextCompat.getDrawable(context, R.drawable.round_button));
    }

    private void setupButton(Context context, TypedArray styledAttrs)
    {
        button = createRoundButton(context, styledAttrs.getDrawable(R.styleable.RoundMenu_buttonIcon));
        button.setOnClickListener(openCloseListener);
        addView(button);
    }

    private void setupMaskLayout(Context context, TypedArray styledAttrs)
    {
        maskLayout = new FrameLayout(context);
        maskLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(maskLayout);
    }

    // Needs this!;
    private void setupInnerLayout(Context context, TypedArray styledAttrs)
    {
        innerLayout = new LinearLayout(context);
        //orientation = styledAttrs.getInteger(R.styleable.RoundMenu_menuOrientation, 0) == 0 ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL;
        //innerLayout.setOrientation(orientation);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.round_menu_background));

        innerLayout.setPadding(0, UiUtils.dpToPixels(40, context), 0, 0);
        maskLayout.addView(innerLayout);
    }

    private void setupCloseButton(Context context, TypedArray styledAttrs)
    {
        closeButton = createRoundButton(context, ContextCompat.getDrawable(context, R.drawable.ic_clear_black_24dp));
        closeButton.setOnClickListener(openCloseListener);
        addView(closeButton);
    }

    private void addButton(Context context, TypedArray styledAttrs)
    {
        ImageButton newButton = createRoundButton(context, ContextCompat.getDrawable(context, R.drawable.ic_clear_black_24dp), ContextCompat.getDrawable(context, R.drawable.round_button_transparent));
        innerLayout.addView(newButton);
        numButtons++;
        updateClipBounds(context);
    }

    private void updateClipBounds(Context context)
    {
        int y = 20;
        int x = 0;
        int width = 40;

        maskLayout.setClipBounds(new Rect(UiUtils.dpToPixels(0, context), UiUtils.dpToPixels(20, context), UiUtils.dpToPixels(40, context), UiUtils.dpToPixels(40*numButtons + 40, context)));
    }

    private void updateClosedStatus(Context context)
    {
        if (closed)
        {
            closeButton.setVisibility(INVISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)innerLayout.getLayoutParams();
            params.topMargin = UiUtils.dpToPixels(-40*numButtons, context);
            innerLayout.setLayoutParams(params);
        }
        else
        {
            closeButton.setVisibility(VISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)innerLayout.getLayoutParams();
            params.topMargin = UiUtils.dpToPixels(0, context);
            innerLayout.setLayoutParams(params);
        }
    }

    private void doTransitions()
    {
        TransitionManager.beginDelayedTransition(this, fadeTransition);
        TransitionManager.beginDelayedTransition(innerLayout, moveTransition);
    }
}
