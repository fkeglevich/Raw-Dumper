package com.fkeglevich.rawdumper.dng1;

import android.util.Log;

//import com.drew.metadata.Directory;
//import com.fkeglevich.rawdumper.lj92.LJ92Encoder;
import com.fkeglevich.rawdumper.raw.BayerPattern;
import com.fkeglevich.rawdumper.raw.info.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;
import com.fkeglevich.rawdumper.tiff.ExifTag;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;
//import com.fkeglevich.rawdumper.util.ColorUtil;
//import com.fkeglevich.rawdumper.util.Matrix3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by flavio on 12/01/2017.
 */

public class DngWriter
{
    // Thumbnail Header tag values
    private static final int    THUMB_SUB_FILE_TYPE =       TiffTag.FILETYPE_REDUCEDIMAGE;
    private static final int    THUMB_BITS_PER_SAMPLE =     8;
    private static final int    THUMB_COMPRESSION =         TiffTag.COMPRESSION_NONE;
    private static final int    THUMB_PHOTOMETRIC =         TiffTag.PHOTOMETRIC_RGB;
    private static final int    THUMB_SAMPLES_PER_PIXEL =   3;
    private static final int    THUMB_PLANAR_CONFIG =       TiffTag.PLANARCONFIG_CONTIG;

    private static final int DEFAULT_ORIENTATION =          TiffTag.ORIENTATION_TOPLEFT;

    private static final String DATE_PATTERN =          "yyyy:MM:dd HH:mm:ss";
    private static final byte[] DNG_VERSION =           new byte[] {1, 3, 0, 0};
    private static final byte[] DNG_BACKWARD_VERSION =  new byte[] {1, 3, 0, 0};

    // Raw Image Header tag values
    private static final int RAW_SUB_FILE_TYPE =     0;
    private static final int RAW_PHOTOMETRIC =       TiffTag.PHOTOMETRIC_CFA;
    private static final int RAW_SAMPLES_PER_PIXEL = 1;
    private static final int RAW_PLANAR_CONFIG =     TiffTag.PLANARCONFIG_CONTIG;
    private static final short[] RAW_CFA_REPEAT_PATTERN_DIM = new short[] {2, 2};

    // Formatted Bayer Patterns
    private static final byte RED   = 0;
    private static final byte GREEN = 1;
    private static final byte BLUE  = 2;
    private static final byte[] FORMATTED_BGGR = new byte[] {BLUE,  GREEN,  GREEN,  RED};
    private static final byte[] FORMATTED_RGGB = new byte[] {RED,   GREEN,  GREEN,  BLUE};
    private static final byte[] FORMATTED_GBRG = new byte[] {GREEN, BLUE,   RED,    GREEN};
    private static final byte[] FORMATTED_GRBG = new byte[] {GREEN, RED,    BLUE,   GREEN};

    public DngWriter()
    {   }

    private String formatCalendar(Calendar calendar)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }

    private byte[] formatBayerPattern(BayerPattern pattern)
    {
        switch (pattern)
        {
            case BGGR:          return FORMATTED_BGGR;
            case RGGB:          return FORMATTED_RGGB;
            case GBRG:          return FORMATTED_GBRG;
            case GRBG: default: return FORMATTED_GRBG;
        }
    }

    public static byte[] opCode3 = null;

    private TiffWriter writeThumbnailHeader(String dngPath, DngWriterOptions options, RawImageSize rawImageSize)
    {
        TiffWriter tiffWriter = TiffWriter.open(dngPath);
        tiffWriter.setField(TiffTag.TIFFTAG_SUBFILETYPE,    THUMB_SUB_FILE_TYPE);
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGEWIDTH,     rawImageSize.getRawBufferWidth()  >> 4);
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGELENGTH,    rawImageSize.getRawBufferHeight() >> 4);
        tiffWriter.setField(TiffTag.TIFFTAG_BITSPERSAMPLE,      THUMB_BITS_PER_SAMPLE);
        tiffWriter.setField(TiffTag.TIFFTAG_COMPRESSION,        THUMB_COMPRESSION);
        tiffWriter.setField(TiffTag.TIFFTAG_PHOTOMETRIC,        THUMB_PHOTOMETRIC);
        tiffWriter.setField(TiffTag.TIFFTAG_SAMPLESPERPIXEL,    THUMB_SAMPLES_PER_PIXEL);
        tiffWriter.setField(TiffTag.TIFFTAG_PLANARCONFIG,       THUMB_PLANAR_CONFIG);
        tiffWriter.setField(TiffTag.TIFFTAG_SUBIFD,             new long[]{0}, true);
        tiffWriter.setField(TiffTag.TIFFTAG_ORIENTATION,        DEFAULT_ORIENTATION);
        tiffWriter.setField(TiffTag.TIFFTAG_DNGVERSION,         DNG_VERSION, false);
        tiffWriter.setField(TiffTag.TIFFTAG_DNGBACKWARDVERSION, DNG_BACKWARD_VERSION, false);
        tiffWriter.setField(TiffTag.TIFFTAG_MAKE,                   options.makeTag);
        tiffWriter.setField(TiffTag.TIFFTAG_MODEL,                  options.modelTag);
        tiffWriter.setField(TiffTag.TIFFTAG_SOFTWARE,               options.softwareTag);
        tiffWriter.setField(TiffTag.TIFFTAG_UNIQUECAMERAMODEL,      options.uniqueCameraModel);
        tiffWriter.setField(TiffTag.TIFFTAG_ORIGINALRAWFILENAME,    options.originalRawFileName.getBytes(Charset.forName("UTF-8")), true);
        tiffWriter.setField(TiffTag.TIFFTAG_DATETIME,               formatCalendar(options.dateTime));

        float[] defaultMatrix3 = new float[]{1.1741f, -0.2862f, -0.0448f, -0.1778f, 0.9912f, 0.2184f, 0.0223f, 0.2117f, 0.532f};
        tiffWriter.setField(TiffTag.TIFFTAG_COLORMATRIX1,           defaultMatrix3, true);

        float[] m4 = defaultMatrix3.clone();

        double sx = options.asShotWhiteXY[0];
        double sy = options.asShotWhiteXY[1];
        double Y = 1;

        double[] whiteXYZ = new double[] {(sx*Y)/sy, Y, ((1-sx-sy)*Y)/sy};
        double[] w = whiteXYZ;

        double[] ASN = new double[]{    w[0]*m4[0] + w[1]*m4[1] + w[2]*m4[2],
                                        w[0]*m4[3] + w[1]*m4[4] + w[2]*m4[5],
                                        w[0]*m4[6] + w[1]*m4[7] + w[2]*m4[8]};

        double calR = 0.8119342866848235;
        double calB = 1.0597582922258328;

        float[] neutral = new float[] {(float)(ASN[0]/ASN[1] * calR), (float)(ASN[1]/ASN[1] * 0.95), (float)(ASN[2]/ASN[1] * calB)};
        //float[] neutral = new float[] {(float)(ASN[0]/ASN[1]), (float)(ASN[1]/ASN[1]), (float)(ASN[2]/ASN[1])};
        //float[] neutral = new float[] {(float)(1/1.240181), (float)(1/1.056), (float)(1/1.757178)};

        tiffWriter.setField(TiffTag.TIFFTAG_ASSHOTNEUTRAL,           neutral, true);
        tiffWriter.setField(TiffTag.TIFFTAG_CAMERACALIBRATION1,     new float[]{(float)calR, 0, 0, 0, 1, 0, 0, 0, (float)calB}, true);
        tiffWriter.setField(TiffTag.TIFFTAG_CALIBRATIONILLUMINANT1, 20);
        return tiffWriter;
    }

    private void writeBlackThumbnail(TiffWriter tiffWriter, RawImageSize rawImageSize, DngWriterOptions options)
    {
        byte[] pixelRow = new byte[rawImageSize.getRawBufferWidth()];
        for (int row = 0; row < (rawImageSize.getRawBufferHeight() >> 4); row++)
            tiffWriter.writeScanline(pixelRow, row);

        tiffWriter.writeDirectory();
    }

    private void writeMainHeader(TiffWriter tiffWriter, RawImageSize rawImageSize, SensorInfo sensorInfo, DngWriterOptions options)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_SUBFILETYPE,    RAW_SUB_FILE_TYPE);
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGEWIDTH,     rawImageSize.getRawBufferWidth());
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGELENGTH,    rawImageSize.getRawBufferHeight());
        tiffWriter.setField(TiffTag.TIFFTAG_BITSPERSAMPLE,  sensorInfo.getNumOfBitsPerPixel());
        tiffWriter.setField(TiffTag.TIFFTAG_PHOTOMETRIC,        RAW_PHOTOMETRIC);
        tiffWriter.setField(TiffTag.TIFFTAG_SAMPLESPERPIXEL,    RAW_SAMPLES_PER_PIXEL);
        tiffWriter.setField(TiffTag.TIFFTAG_PLANARCONFIG,       RAW_PLANAR_CONFIG);
        tiffWriter.setField(TiffTag.TIFFTAG_CFAREPEATPATTERNDIM, RAW_CFA_REPEAT_PATTERN_DIM, false);
        tiffWriter.setField(TiffTag.TIFFTAG_CFAPATTERN, formatBayerPattern(sensorInfo.getBayerPattern()), false);
        tiffWriter.setField(TiffTag.TIFFTAG_WHITELEVEL, new long[]{1023}, true);
        tiffWriter.setField(TiffTag.TIFFTAG_BLACKLEVELREPEATDIM, new short[]{2, 2}, false);


        if (!sensorInfo.getSensorName().equals("T4K37"))
        {
            options.blackLevel = 16;
            if (opCode3 != null)
                tiffWriter.setField(TiffTag.TIFFTAG_OPCODELIST3, opCode3, true);
        }
        else
        {
            options.blackLevel = 64;
        }
        tiffWriter.setField(TiffTag.TIFFTAG_BLACKLEVEL, new float[]{options.blackLevel, options.blackLevel, options.blackLevel, options.blackLevel}, true);
    }

    private void writeMainImageFromMemory(TiffWriter tiffWriter, byte[] rawData, RawImageSize rawImageSize)
    {
        byte[] buffer = new byte[rawImageSize.getRawBufferWidthBytes()];
        for (int row = 0; row < rawImageSize.getRawBufferHeight(); row++)
        {
            System.arraycopy(rawData, rawImageSize.getRawBufferAlignedWidth() * row, buffer, 0, rawImageSize.getRawBufferWidthBytes());
            tiffWriter.writeScanline(buffer, row);
        }
        tiffWriter.writeDirectory();
    }

    private void writeMainImageFromFile(TiffWriter tiffWriter, RandomAccessFile file, RawImageSize rawImageSize) throws IOException
    {
        byte[] buffer = new byte[rawImageSize.getRawBufferWidthBytes()];
        long initPos = file.getFilePointer();
        for (int row = 0; row < rawImageSize.getRawBufferHeight(); row++)
        {
            file.seek(initPos + rawImageSize.getRawBufferAlignedWidth() * row);
            file.read(buffer);
            tiffWriter.writeScanline(buffer, row);
        }
        tiffWriter.writeDirectory();
    }

    private TiffWriter writeThumbAndMainHeader(String dngPath, RawImageSize rawImageSize, SensorInfo sensorInfo, DngWriterOptions options)
    {
        TiffWriter tiffWriter = writeThumbnailHeader(dngPath, options, rawImageSize);
        writeBlackThumbnail(tiffWriter, rawImageSize, options);
        writeMainHeader(tiffWriter, rawImageSize, sensorInfo, options);
        return tiffWriter;
    }

    private double log2(double x)
    {
        return Math.log(x) / Math.log(2.0);
    }

    private double apexEx(double x)
    {
        return  -1.0 * log2(x);
    }

    private void writeExifTags(TiffWriter tiffWriter, DngWriterOptions options)
    {
        tiffWriter.createEXIFDirectory();
        tiffWriter.setField(ExifTag.EXIFTAG_EXPOSURETIME, options.shutterSpeed);
        tiffWriter.setField(ExifTag.EXIFTAG_SHUTTERSPEEDVALUE, apexEx(options.shutterSpeed));
        tiffWriter.setField(ExifTag.EXIFTAG_MAKERNOTE, options.makerNote, true);
        tiffWriter.setField(ExifTag.EXIFTAG_ISOSPEEDRATINGS, new short[]{(short)options.ISO}, true);
        long[] dirOffset = {0};
        tiffWriter.writeCustomDirectory(dirOffset, 0);
        tiffWriter.setDirectory((short)0);
        tiffWriter.setField(TiffTag.TIFFTAG_EXIFIFD, dirOffset[0]);
    }

    public void write(byte[] rawData, String dngPath, RawImageSize rawImageSize, SensorInfo sensorInfo, DngWriterOptions options)
    {
        TiffWriter tiffWriter = writeThumbAndMainHeader(dngPath, rawImageSize, sensorInfo, options);
        writeMainImageFromMemory(tiffWriter, rawData, rawImageSize);
        tiffWriter.close();
    }

    public void write(RandomAccessFile file, String dngPath, RawImageSize rawImageSize, SensorInfo sensorInfo, DngWriterOptions options) throws IOException
    {
        TiffWriter tiffWriter = writeThumbAndMainHeader(dngPath, rawImageSize, sensorInfo, options);
        writeMainImageFromFile(tiffWriter, file, rawImageSize);
        writeExifTags(tiffWriter, options);
        tiffWriter.close();
    }
}
