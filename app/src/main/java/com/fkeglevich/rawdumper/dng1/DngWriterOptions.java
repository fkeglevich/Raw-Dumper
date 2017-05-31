package com.fkeglevich.rawdumper.dng1;

import java.util.GregorianCalendar;

/**
 * Created by flavio on 17/01/2017.
 */

public class DngWriterOptions
{
    public String makeTag =             "Dummy make";
    public String modelTag =            "Dummy model";
    public String softwareTag =         "Dummy software";
    public String uniqueCameraModel =   "Dummy unique camera model";
    public String originalRawFileName = "dummyRawFileName.raw";
    public GregorianCalendar dateTime = new GregorianCalendar(2010, 10, 10, 10, 10, 10);

    public float[] colorMatrix1 = new float[]{  1, 0, 0,
                                                0, 1, 0,
                                                0, 0, 1};

    public float[] asShotNeutral =      new float[]{1f, 1f, 1f};
    public long whiteLevel =            0xFFFF;
    public int calibrationIlluminant =  21;
    public float blackLevel =           64;
    public byte[] makerNote;

    public float[] asShotWhiteXY = new float[] {0.33411f, 0.34877f};

    public double cct;

    public int ISO;
    public double shutterSpeed;

    public DngWriterOptions()
    {   }
}
