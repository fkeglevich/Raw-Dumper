package com.fkeglevich.rawdumper.dng1;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.util.Log;

import com.fkeglevich.rawdumper.raw.info.MknInfo;
import com.fkeglevich.rawdumper.raw.info.MknInfoExtractor;
import com.fkeglevich.rawdumper.raw.info.OV5670;
import com.fkeglevich.rawdumper.raw.info.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;
import com.fkeglevich.rawdumper.raw.info.T4K37;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by flavio on 18/01/2017.
 */

public class I3av4ToDngConverter
{
    private static final String     SOFTWARE_TAG = "RawDumper";
    private static final float[]    DEFAULT_AS_SHOT_NEUTRAL = new float[] {1f, 1f, 1f};
    private static final int        UNKNOWN_ILLUMINANT = 0;

    class SensorConfig
    {
        public RawImageSize rawImageSize;
        public SensorInfo sensorInfo;

        public SensorConfig(RawImageSize rawImageSize, SensorInfo sensorInfo)
        {
            this.rawImageSize = rawImageSize;
            this.sensorInfo = sensorInfo;
        }
    }

    private List<SensorConfig> sensorConfigs;

    public I3av4ToDngConverter()
    {
        initSensorConfigs();
    }

    private void insertSensorConfigsFrom(SensorInfo sensorInfo)
    {
        for (RawImageSize size : sensorInfo.getRawImageSizes())
            sensorConfigs.add(new SensorConfig(size, sensorInfo));
    }

    private void initSensorConfigs()
    {
        sensorConfigs = new ArrayList<>();
        insertSensorConfigsFrom(T4K37.getInstance());
        insertSensorConfigsFrom(OV5670.getInstance());
    }

    SensorConfig getBestSensorConfig(long i3av4Size)
    {
        long diff = Long.MAX_VALUE;
        long currentDiff;
        long currentLength;
        SensorConfig finalConfig = null;
        for (SensorConfig config : sensorConfigs)
        {
            currentLength = config.rawImageSize.getRawBufferLength();
            currentDiff = i3av4Size - currentLength;
            if (i3av4Size > currentLength && currentDiff < diff)
            {
                diff = currentDiff;
                finalConfig = config;
            }
        }
        return finalConfig;
    }

    public void convert(String i3av4Path, String dngPath) throws IOException
    {
        File i3av4File = new File(i3av4Path);
        RandomAccessFile i3av4RAFile = new RandomAccessFile(i3av4Path, "r");
        SensorConfig config = getBestSensorConfig(i3av4RAFile.length());
        long rawDataStart = i3av4RAFile.length() - config.rawImageSize.getRawBufferLength();
        DngWriterOptions options = new DngWriterOptions();
        options.makeTag = Build.BRAND;
        options.modelTag = "ZenFone 2 ZE551ML (back camera)";
        options.softwareTag = SOFTWARE_TAG;
        options.uniqueCameraModel = "Asus ZenFone 2 ZE551ML (back camera)";
        options.originalRawFileName = i3av4File.getName();

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(i3av4File.lastModified());
        options.dateTime = calendar;

        MknInfo info = MknInfoExtractor.extractFromI3AV4(i3av4RAFile, (int)rawDataStart);
        info.writeInfo(options);

        options.asShotNeutral = DEFAULT_AS_SHOT_NEUTRAL;
        options.whiteLevel = (long)Math.pow(2, config.sensorInfo.getRealNumOfBitsPerPixel()) - 1;
        options.calibrationIlluminant = UNKNOWN_ILLUMINANT;

        i3av4RAFile.seek(rawDataStart);
        DngWriter writer = new DngWriter();
        writer.write(i3av4RAFile, dngPath, config.rawImageSize, config.sensorInfo, options);
        i3av4RAFile.close();
    }
}
