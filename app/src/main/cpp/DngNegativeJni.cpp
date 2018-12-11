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
 * Native implementation of the DngNegative class
 *
 * Created by Flávio Keglevich on 01/11/2018.
 */

#include <jni.h>
#include <omp.h>
#include "dng_sdk/source/dng_host.h"
#include "dng_sdk/source/dng_simple_image.h"
#include "dng_sdk/source/dng_tag_values.h"
#include "dng_sdk/source/dng_matrix.h"
#include "dng_sdk/source/dng_exif.h"
#include "dng_sdk/source/dng_orientation.h"
#include "dng_sdk/source/dng_preview.h"
#include "dng_sdk/source/dng_render.h"
#include "dng_sdk/source/dng_color_space.h"
#include "dng_sdk/source/dng_image_writer.h"
#include "dng_sdk/source/dng_file_stream.h"
#include "dng_sdk/source/dng_tone_curve.h"
#include "dng_sdk/source/dng_camera_profile.h"
#include "dng_sdk/source/dng_point.h"
#include "dng_sdk/source/dng_gain_map.h"
#include "dng_sdk/source/dng_opcodes.h"
#include "dng_sdk/source/dng_memory_stream.h"
#include "dng_sdk/source/dng_area_task.h"
#include <android/log.h>
#include <sys/time.h>
#include <math.h>
#include <algorithm>

typedef unsigned long long timestamp_t;

static timestamp_t get_timestamp ()
{
    struct timeval now;
    gettimeofday (&now, NULL);
    return now.tv_usec + (timestamp_t) now.tv_sec * 1000000;
}

class OpenMPHost : public dng_host
{
    public:
        OpenMPHost(dng_memory_allocator *allocator = NULL, dng_abort_sniffer *sniffer = NULL) {}
        ~OpenMPHost(void) {}

    public:
        virtual void PerformAreaTask(dng_area_task &task, const dng_rect &area) override
        {
            dng_point tileSize (task.FindTileSize (area));

            int numThreads = PerformAreaTaskThreads();
            int widthPerThread = (int)ceil(((double) area.W()) / numThreads);

            timestamp_t t0 = get_timestamp();

            __android_log_print(ANDROID_LOG_DEBUG, "OpenMPHost", "num threads: %d", numThreads);
            __android_log_print(ANDROID_LOG_DEBUG, "OpenMPHost", "total area: %d %d %d %d", area.l, area.t, area.r, area.b);

            const int tileSizeH = tileSize.h, tileSizeV = tileSize.v;
            const int areaTop = area.t, areaWidth = area.W(), areaHeight = area.H();

            task.Start ((uint32) numThreads, tileSize, &Allocator (), Sniffer());

            #pragma omp parallel for num_threads(numThreads) schedule(static) default(shared)
            for (int x = 0; x < areaWidth; x += widthPerThread)
            {
                dng_rect taskArea(areaTop, x, areaHeight, (x + widthPerThread) < areaWidth ? (x + widthPerThread) : areaWidth);
                task.ProcessOnThread ((uint32) omp_get_thread_num(), taskArea, dng_point(tileSizeV, tileSizeH), Sniffer());
                __android_log_print(ANDROID_LOG_DEBUG, "OpenMPHost", "per thread area: %d %d %d %d", taskArea.l, taskArea.t, taskArea.r, taskArea.b);
            }

            task.Finish ((uint32) numThreads);

            timestamp_t t1 = get_timestamp();
            double secs = (t1 - t0) / 1000000.0L;
            __android_log_print(ANDROID_LOG_DEBUG, "OpenMPHost", "Compression only time: %f", secs * 1000.0);
        }

        uint32 PerformAreaTaskThreads() override
        {
            return std::min((uint32) omp_get_max_threads(), kMaxMPThreads);
        }
};

dng_matrix_3by3 get3x3Matrix(JNIEnv *env, jfloatArray matrix3x3)
{
    jfloat *raw = env->GetFloatArrayElements(matrix3x3, NULL);
    dng_matrix_3by3 dngMatrix(raw[0], raw[1], raw[2],
                              raw[3], raw[4], raw[5],
                              raw[6], raw[7], raw[8]);
    env->ReleaseFloatArrayElements(matrix3x3, raw, 0);
    return dngMatrix;
}

dng_tone_curve getToneCurve(JNIEnv *env, jfloatArray toneCurve)
{
    dng_tone_curve result; result.SetInvalid();
    int len = env->GetArrayLength(toneCurve);
    if (len % 2 != 0)
    {
        result.SetNull();
        return result;
    }

    jfloat *raw = env->GetFloatArrayElements(toneCurve, NULL);

    for (int i = 0; i < len; i += 2)
    {
        dng_point_real64 point(raw[i + 1], raw[i]);
        result.fCoord.push_back(point);
    }

    env->ReleaseFloatArrayElements(toneCurve, raw, 0);
    return result;
}

extern "C"
{
    OpenMPHost globalHost;
    bool hostWasInitialized = false;

    void initializeHost()
    {
        if (!hostWasInitialized)
        {
            globalHost.SetSaveLinearDNG(false);
            hostWasInitialized = true;
        }
    }

    void setDefaultValues(dng_negative* negative)
    {
        negative->SetColorChannels(3);
        negative->SetColorKeys(colorKeyRed, colorKeyGreen, colorKeyBlue);
        negative->SetDefaultScale(dng_urational(1, 1), dng_urational(1, 1));
        negative->SetNoiseReductionApplied(dng_urational(0, 1));
        negative->SetBaselineExposure(0.0);
        negative->SetBaselineNoise(1.0);
        negative->SetBaselineSharpness(1.0);
        negative->SetAntiAliasStrength(dng_urational(100, 100));
        negative->SetLinearResponseLimit(1.0);
        negative->SetAnalogBalance(dng_vector_3(1.0, 1.0, 1.0));
        negative->SetShadowScale(dng_urational(1, 1));
    }

    JNIEXPORT jlong JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_nativeConstructor(JNIEnv *env, jobject instance)
    {
        initializeHost();
        dng_negative *negative = globalHost.Make_dng_negative();
        setDefaultValues(negative);
        return (jlong) negative;
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_nativeDispose(JNIEnv *env, jobject instance,
                                                                       jlong pointer)
    {
        delete ((dng_negative*) pointer);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setModelNative(JNIEnv *env, jobject instance,
                                                                        jlong pointer, jstring model_)
    {
        const char *model = env->GetStringUTFChars(model_, 0);
        ((dng_negative*) pointer)->SetModelName(model);
        env->ReleaseStringUTFChars(model_, model);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setOriginalRawFileNameNative(JNIEnv *env, jobject instance,
                                                                                      jlong pointer,
                                                                                      jstring fileName_)
    {
        const char *fileName = env->GetStringUTFChars(fileName_, 0);
        ((dng_negative*) pointer)->SetOriginalRawFileName(fileName);
        env->ReleaseStringUTFChars(fileName_, fileName);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setSensorInfoNative(JNIEnv *env, jobject instance,
                                                                             jlong pointer,
                                                                             jint whiteLevel,
                                                                             jfloatArray blackLevels_,
                                                                             jint bayerPhase)
    {
        jfloat *blackLevels = env->GetFloatArrayElements(blackLevels_, NULL);
        ((dng_negative*) pointer)->SetWhiteLevel((uint32) whiteLevel);
        ((dng_negative*) pointer)->SetQuadBlacks(blackLevels[0], blackLevels[1], blackLevels[2], blackLevels[3]);
        ((dng_negative*) pointer)->SetBayerMosaic((uint32) bayerPhase);
        env->ReleaseFloatArrayElements(blackLevels_, blackLevels, 0);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setCameraNeutralNative(JNIEnv *env, jobject instance,
                                                                                jlong pointer,
                                                                                jdoubleArray cameraNeutral_)
    {
        jdouble *cameraNeutral = env->GetDoubleArrayElements(cameraNeutral_, NULL);
        dng_vector_3 vector3(cameraNeutral[0], cameraNeutral[1], cameraNeutral[2]);
        ((dng_negative*) pointer)->SetCameraNeutral(vector3);
        env->ReleaseDoubleArrayElements(cameraNeutral_, cameraNeutral, 0);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setImageSizeAndOrientationNative(JNIEnv *env, jobject instance,
                                                                              jlong pointer,
                                                                              jint width,
                                                                              jint height,
                                                                              jint orientationExifCode)
    {
        dng_rect rect((uint32) height, (uint32) width);
        dng_orientation orientation; orientation.SetTIFF((uint32) orientationExifCode);

        ((dng_negative*) pointer)->SetDefaultCropOrigin(0, 0);
        ((dng_negative*) pointer)->SetDefaultCropSize((uint32) width, (uint32) height);
        ((dng_negative*) pointer)->SetActiveArea(rect);
        ((dng_negative*) pointer)->SetBaseOrientation(orientation);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setCameraCalibrationNative(JNIEnv *env, jobject instance,
                                                                                    jlong pointer,
                                                                                    jfloatArray cameraCalibration1_,
                                                                                    jfloatArray cameraCalibration2_)
    {
        if (cameraCalibration1_ != NULL)
            ((dng_negative*) pointer)->SetCameraCalibration1(get3x3Matrix(env, cameraCalibration1_));
        if (cameraCalibration2_ != NULL)
            ((dng_negative*) pointer)->SetCameraCalibration2(get3x3Matrix(env, cameraCalibration2_));
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_addColorProfileNative(JNIEnv *env, jobject instance,
                                                                               jlong pointer,
                                                                               jstring name_,
                                                                               jfloatArray colorMatrix1_,
                                                                               jfloatArray colorMatrix2_,
                                                                               jfloatArray forwardMatrix1_,
                                                                               jfloatArray forwardMatrix2_,
                                                                               jint calibrationIlluminant1,
                                                                               jint calibrationIlluminant2,
                                                                               jfloatArray toneCurve_)
    {
        AutoPtr<dng_camera_profile> profile(new dng_camera_profile);

        const char *name = env->GetStringUTFChars(name_, 0);
        profile->SetName(name);
        env->ReleaseStringUTFChars(name_, name);

        if (colorMatrix1_ != NULL)
            profile->SetColorMatrix1(get3x3Matrix(env, colorMatrix1_));

        if (colorMatrix2_ != NULL)
            profile->SetColorMatrix2(get3x3Matrix(env, colorMatrix2_));

        if (forwardMatrix1_ != NULL)
            profile->SetForwardMatrix1(get3x3Matrix(env, forwardMatrix1_));

        if (forwardMatrix2_ != NULL)
            profile->SetForwardMatrix2(get3x3Matrix(env, forwardMatrix2_));

        if (calibrationIlluminant1 != lsUnknown)
            profile->SetCalibrationIlluminant1((uint32) calibrationIlluminant1);

        if (calibrationIlluminant2 != lsUnknown)
            profile->SetCalibrationIlluminant2((uint32) calibrationIlluminant2);

        if (toneCurve_ != NULL)
            profile->SetToneCurve(getToneCurve(env, toneCurve_));

        profile->SetWasReadFromDNG(true);

        ((dng_negative*) pointer)->AddProfile(profile);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setAsShotProfileNameNative(JNIEnv *env,
                                                                                    jobject instance,
                                                                                    jlong pointer,
                                                                                    jstring name_)
    {
        const char *name = env->GetStringUTFChars(name_, 0);
        ((dng_negative*) pointer)->SetAsShotProfileName(name);
        env->ReleaseStringUTFChars(name_, name);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setOpcodeListNative(JNIEnv *env, jobject instance,
                                                                              jlong pointer,
                                                                              jbyteArray bytes_,
                                                                              jint listType)
    {
        int numBytes = env->GetArrayLength(bytes_);

        jbyte *bytes = env->GetByteArrayElements(bytes_, NULL);
        dng_memory_stream opcodeListStream(globalHost.Allocator());
        opcodeListStream.Put(bytes, (uint32) numBytes);
        env->ReleaseByteArrayElements(bytes_, bytes, 0);

        switch (listType)
        {
            case 1:
                ((dng_negative*) pointer)->OpcodeList1().Parse(globalHost, opcodeListStream, (uint32) numBytes, 0);
                break;
            case 2:
                ((dng_negative*) pointer)->OpcodeList2().Parse(globalHost, opcodeListStream, (uint32) numBytes, 0);
                break;
            default:
                ((dng_negative*) pointer)->OpcodeList3().Parse(globalHost, opcodeListStream, (uint32) numBytes, 0);
        }
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_setNoiseProfileNative(JNIEnv *env, jobject instance,
                                                                               jlong pointer,
                                                                               jdoubleArray noiseProfile)
    {
        dng_std_vector<dng_noise_function> noiseFunctions;
        int len = env->GetArrayLength(noiseProfile);
        if (len % 2 != 0)
            return;

        jdouble *raw = env->GetDoubleArrayElements(noiseProfile, NULL);

        for (int i = 0; i < len; i += 2)
        {
            dng_noise_function function(raw[i], raw[i + 1]);
            noiseFunctions.push_back(function);
        }

        env->ReleaseDoubleArrayElements(noiseProfile, raw, 0);

        ((dng_negative*) pointer)->SetNoiseProfile(dng_noise_profile(noiseFunctions));
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_exif_DngExifTagWriter_writeMakerNoteTagNative(JNIEnv *env, jobject instance,
                                                                                jlong nativeHandle,
                                                                                jbyteArray makerNote_)
    {
        int numBytes = env->GetArrayLength(makerNote_);
        jbyte *makerNote = env->GetByteArrayElements(makerNote_, NULL);

        AutoPtr<dng_memory_stream> mknStream(new dng_memory_stream(globalHost.Allocator()));
        mknStream->SetBigEndian();                      //The stream should be big endian
        mknStream->Put("Adobe\0MakN", 10);              //These chars mark the makernotes
        mknStream->Put_uint32((uint32) numBytes);       //Write the original length of the makernotes
        mknStream->Put("II", 2);                        //Assuming the makernote is always small endian
        mknStream->Put_uint32(0);                       //Assuming the makernote has no offset
        mknStream->Put(makerNote, (uint32) numBytes);   //Put the actual makernote data

        AutoPtr<dng_memory_block> mknBlock(mknStream->AsMemoryBlock(globalHost.Allocator()));
        ((dng_negative*) nativeHandle)->SetPrivateData(mknBlock);

        env->ReleaseByteArrayElements(makerNote_, makerNote, 0);
    }

    JNIEXPORT void JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_writeImageToFileNative(JNIEnv *env, jobject instance,
                                                                                jlong pointer,
                                                                                jstring fileName_,
                                                                                jint width,
                                                                                jint height,
                                                                                jint bpl,
                                                                                jboolean shouldInvertRows,
                                                                                jbyteArray imageData_,
                                                                                jboolean uncompressed,
                                                                                jboolean calculateDigest)
    {

        timestamp_t t0 = get_timestamp();

        jbyte *imageData = env->GetByteArrayElements(imageData_, NULL);

        dng_rect imageRect((uint32) height, ((uint32) width));
        dng_simple_image* image = new dng_simple_image(imageRect, 1, ttShort, globalHost.Allocator());

        dng_pixel_buffer buffer; image->GetPixelBuffer(buffer);

        size_t widthBytes = width * sizeof(uint16);

        uint16 *targetBuffer = (uint16*) buffer.fData;
        if (shouldInvertRows)
        {
            for (int row = 0; row < height; row++)
                memcpy(targetBuffer + (row * width), imageData + ((height - 1 - row) * bpl), widthBytes);
        }
        else
        {
            for (int row = 0; row < height; row++)
                memcpy(targetBuffer + (row * width), imageData + (row * bpl), widthBytes);
        }

        env->ReleaseByteArrayElements(imageData_, imageData, 0);

        timestamp_t t1 = get_timestamp();
        double secs = (t1 - t0) / 1000000.0L;
        __android_log_print(ANDROID_LOG_DEBUG, "Perf", "memcpy time: %f", secs * 1000.0);

        AutoPtr<dng_image> castImage(dynamic_cast<dng_image*>(image));
        ((dng_negative*) pointer)->SetStage1Image(castImage);

        t0 = get_timestamp();
        ((dng_negative*) pointer)->SynchronizeMetadata();
        t1 = get_timestamp();
        secs = (t1 - t0) / 1000000.0L;
        __android_log_print(ANDROID_LOG_DEBUG, "Perf", "SynchronizeMetadata time: %f", secs * 1000.0);

        t0 = get_timestamp();
        const char *fileName = env->GetStringUTFChars(fileName_, 0);
        dng_image_writer writer;
        dng_file_stream stream(fileName, true);
        writer.WriteDNG(globalHost, stream, *((dng_negative*) pointer) , NULL, dngVersion_SaveDefault, uncompressed, calculateDigest);
        env->ReleaseStringUTFChars(fileName_, fileName);
        t1 = get_timestamp();
        secs = (t1 - t0) / 1000000.0L;
        __android_log_print(ANDROID_LOG_DEBUG, "Perf", "WriteDNG time: %f", secs * 1000.0);
    }

    JNIEXPORT jlong JNICALL
    Java_com_fkeglevich_rawdumper_dng_writer_DngNegative_getExifHandleNative(JNIEnv *env, jobject instance,
                                                                             jlong pointer)
    {
        return (jlong ) ((dng_negative*) pointer)->GetExif();
    }
}