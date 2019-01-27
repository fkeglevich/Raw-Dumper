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

package com.fkeglevich.rawdumper.util;

import androidx.annotation.NonNull;

/**
 * Created by Flávio Keglevich on 22/04/2017.
 * TODO: Add a class header comment!
 */

public class MathUtil
{
    public static int gcd(int a, int b)
    {
        int rem;
        while(b != 0)
        {
            rem = a % b;
            a = b;
            b = rem;
        }
        return a;
    }

    public static double log2(double x)
    {
        return Math.log(x) / Math.log(2.0);
    }

    public static double[] multiply3x3MatrixToVector3(double[] m, double[] v)
    {
        return new double[]{    v[0]*m[0] + v[1]*m[1] + v[2]*m[2],
                                v[0]*m[3] + v[1]*m[4] + v[2]*m[5],
                                v[0]*m[6] + v[1]*m[7] + v[2]*m[8]};
    }

    public static double[] scalarMultiply(double[] m, double s)
    {
        double[] result = m.clone();
        for (int i = 0; i < result.length; i++)
            result[i] *= s;

        return result;
    }

    public static double[] sum(double[] m, double[] m2)
    {
        double[] result = m.clone();
        for (int i = 0; i < result.length; i++)
            result[i] += m2[i];

        return result;
    }

    @NonNull
    public static float[] multiply3x3Matrices(@NonNull float[] a, @NonNull float[] b)
    {
        float[] result = new float[9];
        int i, j, k;

        for(i = 0; i < 3; i++)
            for (j = 0; j < 3; j++)
                for (k = 0; k < 3; k++)
                    result[3*i + j] += a[3*i + k] * b[3*k + j];

        return result;
    }

    @NonNull
    public static double[] multiply3x3Matrices(@NonNull double[] a, @NonNull double[] b)
    {
        double[] result = new double[9];
        int i, j, k;

        for(i = 0; i < 3; i++)
            for (j = 0; j < 3; j++)
                for (k = 0; k < 3; k++)
                    result[3*i + j] += a[3*i + k] * b[3*k + j];

        return result;
    }

    public static double[] invert3x3Matrix(double[] matrix)
    {
        double a00 = matrix[0];
        double a01 = matrix[1];
        double a02 = matrix[2];
        double a10 = matrix[3];
        double a11 = matrix[4];
        double a12 = matrix[5];
        double a20 = matrix[6];
        double a21 = matrix[7];
        double a22 = matrix[8];

        double[] temp = new double[9];

        temp[0] = a11 * a22 - a21 * a12;
        temp[1] = a21 * a02 - a01 * a22;
        temp[2] = a01 * a12 - a11 * a02;
        temp[3] = a20 * a12 - a10 * a22;
        temp[4] = a00 * a22 - a20 * a02;
        temp[5] = a10 * a02 - a00 * a12;
        temp[6] = a10 * a21 - a20 * a11;
        temp[7] = a20 * a01 - a00 * a21;
        temp[8] = a00 * a11 - a10 * a01;

        double det = (a00 * temp [0] +
                      a01 * temp [3] +
                      a02 * temp [6]);

        if (Math.abs(det) < 1.0E-10)
            throw new ArithmeticException("Matrix cannot be inverted!");

        double[] result = new double[9];
        for (int i = 0; i < temp.length; i++)
            result[i] = temp[i] / det;

        return result;
    }

    public static double[] normalizeRows(double[] m)
    {
        double sum0 = m[0] + m[1] + m[2];
        double sum1 = m[3] + m[4] + m[5];
        double sum2 = m[6] + m[7] + m[8];

        return new double[] { m[0] / sum0, m[1] / sum0, m[2] / sum0,
                              m[3] / sum1, m[4] / sum1, m[5] / sum1,
                              m[6] / sum2, m[7] / sum2, m[8] / sum2 };
    }

    public static double[] floatArrayToDouble(float[] array)
    {
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i];

        return result;
    }

    public static float[] doubleArrayToFloat(double[] array)
    {
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = (float)array[i];

        return result;
    }

    public static int clamp(int value, int min, int max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float clamp(float value, float min, float max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double clamp(double value, double min, double max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
