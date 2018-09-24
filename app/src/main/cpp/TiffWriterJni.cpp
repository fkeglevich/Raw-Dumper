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
 * Native implementation of the TiffWriter class
 *
 * Created by Flávio Keglevich on 10/01/2017.
 */

#include <jni.h>
#include "tiff/libtiff/tiffio.h"

extern "C"
{

JNIEXPORT jlong JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_openNative(JNIEnv *env, jclass type, jstring filepath)
    {
        const char* filepathStr = env->GetStringUTFChars(filepath, 0);
        TIFF *tiffInstance = TIFFOpen(filepathStr, "w");
        env->ReleaseStringUTFChars(filepath, filepathStr);
        return (jlong)tiffInstance;
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_closeNative(JNIEnv *env, jobject instance, jlong pointer)
    {
        TIFFClose((TIFF*)pointer);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setIntFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jint value)
    {
        return TIFFSetField((TIFF*)pointer, tagName, (int)value);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setShortFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jshort value)
    {
        return TIFFSetField((TIFF*)pointer, tagName, (short)value);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setLongFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jlong value)
    {
        return TIFFSetField((TIFF*)pointer, tagName, (long long)value);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setByteFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jbyte value)
    {
        return TIFFSetField((TIFF*)pointer, tagName, (signed char)value);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setFloatFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jfloat value)
    {
        return TIFFSetField((TIFF*)pointer, tagName, (float)value);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setDoubleFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jdouble value)
    {
        return TIFFSetField((TIFF*)pointer, tagName, (double)value);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setStringFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jstring value)
    {
        const char* valueStr = env->GetStringUTFChars(value, 0);
        int result = TIFFSetField((TIFF*)pointer, tagName, valueStr);
        env->ReleaseStringUTFChars(value, valueStr);
        return result;
    }

    int setPointerField(jlong pointer, jint tagName, void* value, jboolean writeLength)
    {
        if (writeLength)
            return TIFFSetField((TIFF*)pointer, tagName, 1, value);
        else
            return TIFFSetField((TIFF*)pointer, tagName, value);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setIntPointerFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jint value, jboolean writeLength)
    {
        return setPointerField(pointer, tagName, &value, writeLength);
    }

    int setArrayField(JNIEnv *env, jlong pointer, jint tagName, jarray value, void* valueArr, jboolean writeLen)
    {
        int result;
        if (writeLen)
        {
            jsize len = env->GetArrayLength(value);
            result = TIFFSetField((TIFF *) pointer, tagName, len, valueArr);
        }
        else
        {
            result = TIFFSetField((TIFF *) pointer, tagName, valueArr);
        }
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setIntArrayFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jintArray value, jboolean writeLen)
    {
        jint* valueArr = env->GetIntArrayElements(value, 0);
        int result = setArrayField(env, pointer, tagName, value, valueArr, writeLen);
        env->ReleaseIntArrayElements(value, valueArr, 0);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setShortArrayFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jshortArray value, jboolean writeLen)
    {
        short* valueArr = env->GetShortArrayElements(value, 0);
        int result = setArrayField(env, pointer, tagName, value, valueArr, writeLen);
        env->ReleaseShortArrayElements(value, valueArr, 0);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setLongArrayFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jlongArray value, jboolean writeLen)
    {
        jlong* valueArr = env->GetLongArrayElements(value, 0);
        int result = setArrayField(env, pointer, tagName, value, valueArr, writeLen);
        env->ReleaseLongArrayElements(value, valueArr, 0);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setByteArrayFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jbyteArray value, jboolean writeLen)
    {
        signed char* valueArr = env->GetByteArrayElements(value, 0);
        int result = setArrayField(env, pointer, tagName, value, valueArr, writeLen);
        env->ReleaseByteArrayElements(value, valueArr, 0);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setFloatArrayFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jfloatArray value, jboolean writeLen)
    {
        float* valueArr = env->GetFloatArrayElements(value, 0);
        int result = setArrayField(env, pointer, tagName, value, valueArr, writeLen);
        env->ReleaseFloatArrayElements(value, valueArr, 0);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setDoubleArrayFieldNative(JNIEnv *env, jobject instance, jlong pointer, jint tagName, jdoubleArray value, jboolean writeLen)
    {
        double* valueArr = env->GetDoubleArrayElements(value, 0);
        int result = setArrayField(env, pointer, tagName, value, valueArr, writeLen);
        env->ReleaseDoubleArrayElements(value, valueArr, 0);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_writeScanlineNative(JNIEnv *env, jobject instance, jlong pointer, jbyteArray data, jint row)
    {
        signed char* dataArr = env->GetByteArrayElements(data, 0);
        int result = TIFFWriteScanline((TIFF*)pointer, dataArr, row, 0);
        env->ReleaseByteArrayElements(data, dataArr, 0);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_writeDirectoryNative(JNIEnv *env, jobject instance, jlong pointer)
    {
        return TIFFWriteDirectory((TIFF*)pointer);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_createEXIFDirectoryNative(JNIEnv *env, jobject instance, jlong pointer)
    {
        return TIFFCreateEXIFDirectory((TIFF*)pointer);
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_writeCustomDirectoryNative(JNIEnv *env, jobject instance, jlong pointer, jlongArray dirOffset, jint dirOffsetIndex)
    {
        jlong* dirOffsetArr = env->GetLongArrayElements(dirOffset, NULL);
        int result = TIFFWriteCustomDirectory((TIFF*)pointer, (uint64*)dirOffsetArr + dirOffsetIndex);
        env->ReleaseLongArrayElements(dirOffset, dirOffsetArr, 0);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_setDirectoryNative(JNIEnv *env, jobject instance, jlong pointer, jshort directory)
    {
        return TIFFSetDirectory((TIFF*)pointer, (uint16)directory);
    }

    JNIEXPORT jlong JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_writeRawTileNative(JNIEnv *env, jobject instance, jlong pointer, jint tile, jbyteArray data, jlong size)
    {
        jbyte* dataArr = env->GetByteArrayElements(data, 0);
        tmsize_t result = TIFFWriteRawTile((TIFF*)pointer, (uint32)tile, (void*)dataArr, size);
        env->ReleaseByteArrayElements(data, dataArr, 0);
        return result;
    }

    JNIEXPORT jlong JNICALL
    Java_com_fkeglevich_rawdumper_tiff_TiffWriter_writeRawStripNative(JNIEnv *env, jobject instance, jlong pointer, jint strip, jbyteArray data, jlong size)
    {
        jbyte* dataArr = env->GetByteArrayElements(data, 0);
        tmsize_t result = TIFFWriteRawStrip((TIFF*)pointer, (uint32)strip, (void*)dataArr, size);
        env->ReleaseByteArrayElements(data, dataArr, 0);
        return result;
    }
};