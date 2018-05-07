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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.fkeglevich.rawdumper.camera.data.PreviewArea;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 11/11/17.
 */

public class TouchFocusView extends View
{
    public static final int FOCUS_METERING         = 0xFFCCCCCC;
    public static final int FOCUS_METERING_SUCCESS = 0xFF33FF33;
    public static final int FOCUS_METERING_FAIL    = 0xFFFF3333;
    public static final int STROKE_WIDTH_DP = 1;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public TouchFocusView(Context context)
    {
        super(context);
        init();
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public TouchFocusView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public TouchFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint paint;
    private PreviewArea meteringArea = null;
    private Rect drawingRect = new Rect();

    private void init()
    {
        paint = new Paint();
        paint.setColor(FOCUS_METERING_SUCCESS);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(UiUtil.dpToPixels(STROKE_WIDTH_DP, getContext()));
    }

    public void setMeteringArea(PreviewArea area, int color)
    {
        meteringArea = area;
        paint.setColor(color);
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        if (meteringArea != null)
        {
            int x = meteringArea.getX(), y = meteringArea.getY();
            int touchSize = meteringArea.getTouchSize();

            drawingRect.set(x - touchSize, y - touchSize, x + touchSize, y + touchSize);
            canvas.drawRect(drawingRect, paint);
        }
    }
}
