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

    public void dividePointWise(GainMap b)
    {
        Assert.assertEquals(numColumns, b.numColumns);
        Assert.assertEquals(numRows,    b.numRows);

        for (int row = 0; row < numRows; row++)
            for (int col = 0; col < numColumns; col++)
                values[row][col] /= b.values[row][col];
    }

    public void invertRows()
    {
        int halfRows = numRows / 2;
        float[] rowBuffer = new float[numColumns];
        float[] currentRow, invertedRow;
        for (int row = 0; row < halfRows; row++)
        {
            currentRow  = values[row];
            invertedRow = values[numRows - 1 - row];

            System.arraycopy(currentRow,  0, rowBuffer,   0, numColumns);
            System.arraycopy(invertedRow, 0, currentRow,  0, numColumns);
            System.arraycopy(rowBuffer,   0, invertedRow, 0, numColumns);
        }
    }

    public GainMap cloneMap()
    {
        GainMap result = new GainMap(numColumns, numRows);
        for (int row = 0; row < numRows; row++)
            System.arraycopy(values[row], 0, result.values[row], 0, numColumns);

        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("[numRows = " + numRows + "\nnumColumns:" + numColumns);
        result.append("\n");
        for (int row = 0; row < numRows; row++)
        {
            for (int col = 0; col < numColumns; col++)
                result.append(String.format(java.util.Locale.US,"%.2f", values[row][col])).append(" ");
            if (row != (numRows - 1)) result.append("\n");
        }
        result.append("]");
        return result.toString();
    }
}
