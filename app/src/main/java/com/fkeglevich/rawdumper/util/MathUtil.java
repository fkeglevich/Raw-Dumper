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

    public static float[] multiply3x3Matrices(float[] a, float[] b)
    {
        /*

        [0 1 2
         3 4 5 ]

        3 * i + j

         */
        float[] result = new float[9];
        int i, j, k;
        for(i = 0; i < 3; i++)
        {
            for(j = 0; j < 3; j++)
            {
                for(k = 0; k < 3; k++)
                {
                    result[3 * i + j] +=  a[3 * i + k] *  b[ 3 * k + j];
                }
            }
        }
        return result;
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
