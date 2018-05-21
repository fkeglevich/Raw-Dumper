/*
 * Copyright 2018, Flávio Keglevich
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

package com.fkeglevich.rawdumper.raw.gain;

import junit.framework.Assert;

/**
 * Created by flavio on 24/03/18.
 */

public class GainMap
{
    public final int numColumns;
    public final int numRows;

    public final float[][] values;

    public GainMap(int numColumns, int numRows)
    {
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.values = new float[numRows][numColumns];
    }

    public void add(GainMap b)
    {
        Assert.assertEquals(numColumns, b.numColumns);
        Assert.assertEquals(numRows,    b.numRows);

        for (int row = 0; row < numRows; row++)
            for (int col = 0; col < numColumns; col++)
                values[row][col] += b.values[row][col];
    }

    public void divideByScalar(float value)
    {
        for (int row = 0; row < numRows; row++)
            for (int col = 0; col < numColumns; col++)
                values[row][col] /= value;
    }

    public void multiplyByScalar(float value)
    {
        for (int row = 0; row < numRows; row++)
            for (int col = 0; col < numColumns; col++)
                values[row][col] *= value;
    }
}
