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

/**
 * Native implementation of the AWB (Automatic White Balance) algorithms
 *
 * Created by Flávio Keglevich on 09/12/2018.
 */

#include <jni.h>
#include <omp.h>
#include <android/log.h>

extern "C"
{
    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_raw_awb_GrayWorld_nativeCalculate(JNIEnv *env, jclass type,
                                                                        jint width, jint height,
                                                                        jint bpl, jbyteArray data_,
                                                                        jdoubleArray output_)
    {

        jbyte *data = env->GetByteArrayElements(data_, NULL);
        jshort *buffer = (jshort*) data;
        bpl /= 2;

        int t = 1;

        double cc0Avg = 0;
        double cc1Avg = 0;
        double cc2Avg = 0;
        double cc3Avg = 0;

        double linCc0;
        double linCc1;
        double linCc2;
        double linCc3;

        double blackLevel = 64.0;

        for (int row = 0; row < height; row += 2)
        {
            for (int col = 0; col < width; col += 2)
            {
                linCc0 = (buffer[row * bpl + col] -             blackLevel) / (1023.0 - blackLevel);
                linCc1 = (buffer[row * bpl + col + 1] -         blackLevel) / (1023.0 - blackLevel);
                linCc2 = (buffer[(row + 1) * bpl + col] -       blackLevel) / (1023.0 - blackLevel);
                linCc3 = (buffer[(row + 1) * bpl + col + 1] -   blackLevel) / (1023.0 - blackLevel);

                cc0Avg += (linCc0 - cc0Avg) / t;
                cc1Avg += (linCc1 - cc1Avg) / t;
                cc2Avg += (linCc2 - cc2Avg) / t;
                cc3Avg += (linCc3 - cc3Avg) / t;

                t++;
            }
        }

        env->ReleaseByteArrayElements(data_, data, 0);

        //MAJOR TODO: Update sensor pattern, black and white values

        //assuming GRBG:

        double G = (cc0Avg + cc3Avg) / 2;
        double R = cc1Avg / G;
        double B = cc2Avg / G;

        jdouble* output = env->GetDoubleArrayElements(output_, NULL);
        output[0] = R;
        output[1] = B;
        env->ReleaseDoubleArrayElements(output_, output, 0);

        __android_log_print(ANDROID_LOG_DEBUG, "AWB", "R: %f", R);
        __android_log_print(ANDROID_LOG_DEBUG, "AWB", "G: %f", 1.0);
        __android_log_print(ANDROID_LOG_DEBUG, "AWB", "B: %f", B);
    }
}