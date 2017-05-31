package com.fkeglevich.rawdumper.raw.info;

import com.fkeglevich.rawdumper.dng1.DngWriterOptions;

/**
 * Created by flavio on 12/01/2017.
 */

public class MknInfo
{
    private final float[] CCM;
    private final float[] xyCoords;
    private final double cct;
    private final int ISO;
    private final double shutterSpeed;
    private byte[] makerNote;


    public MknInfo(float[] CCM, float[] xyCoords, double cct, int ISO, double shutterSpeed, byte[] makerNote)
    {
        this.CCM = CCM.clone();
        this.xyCoords = xyCoords.clone();
        this.cct = cct;
        this.ISO = ISO;
        this.shutterSpeed = shutterSpeed;
        this.makerNote =  makerNote;
    }

    public float[] getCCM()
    {
        return CCM.clone();
    }

    public float[] getXyCoords()
    {
        return xyCoords.clone();
    }

    public double getCct()
    {
        return cct;
    }

    public double getShutterSpeed()
    {
        return shutterSpeed;
    }

    public int getISO()
    {
        return ISO;
    }

    public void writeInfo(DngWriterOptions options)
    {
        options.colorMatrix1 = CCM.clone();
        options.asShotWhiteXY = xyCoords.clone();
        options.cct = cct;
        options.ISO = ISO;
        options.shutterSpeed = shutterSpeed;
        options.makerNote = makerNote.clone();
    }
}
