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

package com.fkeglevich.rawdumper.camera.data;

import java.util.Arrays;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public class PreviewArea
{
    public enum FlipType
    {
        NONE, HORIZONTAL, VERTICAL;
    }

    private static final List<Integer> VALID_DEGREES = Arrays.asList(0, 90, 180, 270);

    private final int x;
    private final int y;
    private final int viewWidth;
    private final int viewHeight;
    private final int touchSize;

    public static PreviewArea createTouchArea(int viewWidth, int viewHeight, int x, int y, int touchSize)
    {
        return new PreviewArea(x, y, viewWidth, viewHeight, touchSize);
    }

    private PreviewArea(int x, int y, int viewWidth, int viewHeight, int touchSize)
    {
        this.x = x;
        this.y = y;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.touchSize = touchSize;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getViewWidth()
    {
        return viewWidth;
    }

    public int getViewHeight()
    {
        return viewHeight;
    }

    public int getTouchSize()
    {
        return touchSize;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public PreviewArea rotate(int degrees)
    {
        if (!VALID_DEGREES.contains(degrees))
            throw new IllegalArgumentException();

        switch (degrees)
        {
            case 0:     return this;
            case 90:    return new PreviewArea(y, viewWidth - x, viewHeight, viewWidth, touchSize);
            case 180:   return new PreviewArea(viewWidth - x, viewHeight - y, viewWidth, viewHeight, touchSize);
            default:    return new PreviewArea(viewHeight - y, x, viewHeight, viewWidth, touchSize);
        }
    }

    public PreviewArea flip(FlipType type)
    {
        switch (type)
        {
            case HORIZONTAL: return new PreviewArea(viewWidth - x,  y, viewWidth, viewHeight, touchSize);
            case VERTICAL:   return new PreviewArea(x, viewHeight - y, viewWidth, viewHeight, touchSize);
            default:         return this;
        }
    }

    public PreviewArea fix(int margin)
    {
        int newX = x, newY = y;
        if ((x - touchSize - margin) < 0)          newX = touchSize + margin;
        if ((x + touchSize + margin) > viewWidth)  newX = viewWidth - touchSize - margin;

        if ((y - touchSize - margin) < 0)          newY = touchSize + margin;
        if ((y + touchSize + margin) > viewHeight) newY = viewHeight - touchSize - margin;

        return new PreviewArea(newX, newY, viewWidth, viewHeight, touchSize);
    }
}
