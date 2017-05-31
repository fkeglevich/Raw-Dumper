package com.fkeglevich.rawdumper.raw.info;

import android.util.Log;

import com.fkeglevich.rawdumper.util.ByteArrayUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by flavio on 12/01/2017.
 */

public class MknInfoExtractor
{
    private static final byte[] CCMBytes = {0x43, 0x43, 0x4d, 0x3d, 0x20, 0x0a};
    private static final byte[] GrassBytes = {0x67, 0x72, 0x61, 0x73, 0x73, 0x3d};
    private static final byte[] DualFlashBytes = {0x20, 0x44, 0x75, 0x61, 0x6c, 0x20, 0x46, 0x6c, 0x61, 0x73, 0x68, 0x20};

    private static final int DEFAULT_MKN_SIZE = 12000;

    private static final byte LF = 0x0a;
    private static final byte SPACE = 0x20;

    public static MknInfo extractFromI3AV4(RandomAccessFile file, int mknSize) throws IOException
    {
        /*The basic ASCII chars containing some maker note information:

        grass=1
        sky=0
        cw=1
        sat=10
        sat_dyn=0
        prefer= 1
        CCM=
        3.7371 -2.6759 -0.0608
        -0.2805 1.8338 -0.5526
        0.1502 -2.3446 3.1941
        0=11 1=70 2=272 3=449 4=222 5=0
         Dual Flash = 0 100
        [some bytes]Front Camera AWB Calibration:
        use golden AWB gain
        Rear Camera AWB Calibration:
        parm_golden_at_lab:522 812 581
        factory_golden_at_lab:522 812 581
        factory_golden_at_ems:510 811 571
        dut_at_ems:475 795 575
        LSC= -1 [R] 2.66992 2.49992
        LSCDecreaseRate= 0
        [some bytes]current: 1 1 1 1 (1333/3072)
        first fail: 0 1 1 1 (83/3072)
         (0) 0 : 0
         */

        byte[] mknBytes = new byte[mknSize];

        Pattern exposureHelper = Pattern.compile("first\\s+fail:\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+\\s+[(]\\s*\\d+\\s*[/]\\s*\\d+\\s*[)]\\s*[(]\\s*\\d+\\s*[)]\\s+\\d+\\s+[:]\\s+\\d+");

        file.seek(0);
        file.read(mknBytes);
        String mknStringBytes = null;
        try
        {
            mknStringBytes = new String(mknBytes, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        int ISO = 0;
        double shutterSpeed = 0;

        if (mknStringBytes != null)
        {
            Matcher matcher = exposureHelper.matcher(mknStringBytes);
            int lastEnd = -1;
            while (matcher.find()) {
                lastEnd = matcher.end();
            }
            int firstExposureIndex = lastEnd + 46 + (16*29);
            ByteBuffer wrapped = ByteBuffer.wrap(mknBytes, firstExposureIndex, 16);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);

            int firstExposure = wrapped.getInt();
            wrapped.getInt();
            int isoIndex = firstExposureIndex + 8;
            float iso = wrapped.getFloat();

            ISO = (int)Math.round(50.0 * iso);
            shutterSpeed = firstExposure / 1000000.0;
        }

        int grassIndex = ByteArrayUtil.indexOf(mknBytes, GrassBytes);

        if (grassIndex == -1)
            return null;

        int dualBytesIndex = ByteArrayUtil.indexOf(mknBytes, DualFlashBytes, grassIndex);
        if (dualBytesIndex == -1)
            return null;

        int mknStringLength = dualBytesIndex - grassIndex;
        String mknString = new String(mknBytes, grassIndex, dualBytesIndex - grassIndex, "UTF-8");
        String[] lines = mknString.split("\n");

        String CCMLine1 = lines[7];
        String CCMLine2 = lines[8];
        String CCMLine3 = lines[9];
        String CCMLine4 = lines[10];

        double cct = decodeWhiteBalance(lines[10]);

        float[] wbXy = cctToXY(cct);
        float[] CMMFinal = decodeCCM(lines);

        MknInfo result = new MknInfo(CMMFinal, wbXy, cct, ISO, shutterSpeed, mknBytes);
        return result;
    }

    public static MknInfo extractFromI3AV4(RandomAccessFile file) throws IOException
    {
        return extractFromI3AV4(file, DEFAULT_MKN_SIZE);
    }

    private static float[] decodeCCM(String[] mknLines)
    {
        return decodeCCMLines(mknLines[7] + " " + mknLines[8] + " " + mknLines[9]);
    }

    private static float[] decodeCCMLines(String line)
    {
        String[] values = line.split(" ");
        float[] results = new float[values.length];
        for (int i = 0; i < values.length; i++)
            results[i] = Float.parseFloat(values[i]);

        return results;
    }

    private static double decodeWhiteBalance(String line)
    {
        String[] spaceSeparated = line.trim().split(" ");

        int[] illuminantIds = new int[spaceSeparated.length];
        int[] illuminantValues = new int[spaceSeparated.length];
        String[] mappingString;
        String item;
        for (int i = 0; i < spaceSeparated.length; i++)
        {
            item = spaceSeparated[i];
            mappingString = item.split("=");
            illuminantIds[i] = Integer.parseInt(mappingString[0].trim());
            illuminantValues[i] = Integer.parseInt(mappingString[1].trim());
        }

        float[] xCoords = new float[6];
        float[] yCoords = new float[6];
        double[] temps = new double[6];
        /*
        0 = incandescente 		2856 K	(A)			(0.45117, 0.40594)
        1 = shade				7504 K	(D75)		(0.29968, 0.31740)
        2 = fluorescente		4000 K	(TL84)		(0.38541, 0.37123)
        3 = daylight			5503 K	(D55)		(0.33411, 0.34877)
        4 = cloudy				6504 K	(D65)		(0.31382, 0.33100)
        5 = "dark shade"		9000 K	(?)			(0.28693, 0.29559)
         */

        //0 - Tungsten
        xCoords[0] = 0.45117f;
        yCoords[0] = 0.40594f;
        temps[0]   = 2856; //2495 (0.477444, 0.413735)

        //1 - Shade
        xCoords[1] = 0.29968f;
        yCoords[1] = 0.31740f;
        temps[1]   = 7504; //4343 (0.366479, 0.367567)

        //2 - Fluorescent
        xCoords[2] = 0.38541f;
        yCoords[2] = 0.37123f;
        temps[2]   = 4000; //2996 (0.437217, 0.404172)

        //3 - Daylight
        xCoords[3] = 0.33411f;
        yCoords[3] = 0.34877f;
        temps[3]   = 5503; //3662 (0.396588, 0.386165)

        //4 - Cloudy
        xCoords[4] = 0.31382f;
        yCoords[4] = 0.33100f;
        temps[4]   = 6504; //4034 (0.378955, 0.375816)

        //5 - Dark Shade
        xCoords[5] = 0.28693f;
        yCoords[5] = 0.29559f;
        temps[5]   = 9000; //4710 (0.353778, 0.358366)

        double xMean = 0;
        double yMean = 0;
        double tMean = 0;

        for (int i = 0; i < xCoords.length; i++)
        {
            xMean += xCoords[i] * (illuminantValues[i] / 1024.0);
            yMean += yCoords[i] * (illuminantValues[i] / 1024.0);
            tMean += temps[i] * (illuminantValues[i] / 1024.0);
        }

        return tMean;
    }

    private static float[] cctToXY(double cct)
    {
        double x, y;
        double cct_squared = cct*cct;
        double cct_cubic = cct_squared*cct;

        if (cct <= 7000)
            x = (-4607000000.0/cct_cubic) + (2967800.0/cct_squared) + (99.11/cct) + 0.244063;
        else
            x = (-2006400000.0/cct_cubic) + (1901800.0/cct_squared) + (247.48/cct) + 0.237040;

        y = (-3.0*(x*x)) + (2.870*x) - 0.275;
        return new float[]{(float)x, (float)y};
    }
}
