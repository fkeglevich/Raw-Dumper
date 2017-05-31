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

package com.fkeglevich.rawdumper.tiff;

/**
 * Collection of values of some TIFF tags
 *
 * Created by Flávio Keglevich on 11/01/2017.
 */

public class TiffTag
{
    public static final int TIFFTAG_SUBFILETYPE             = 254;
        public static final int FILETYPE_FULLIMAGE          = 0x0;
        public static final int FILETYPE_REDUCEDIMAGE       = 0x1;
        public static final int FILETYPE_PAGE               = 0x2;
        public static final int FILETYPE_MASK               = 0x4;

    public static final int TIFFTAG_IMAGEWIDTH              = 256;
    public static final int TIFFTAG_IMAGELENGTH             = 257;
    public static final int TIFFTAG_BITSPERSAMPLE           = 258;

    public static final int TIFFTAG_COMPRESSION             = 259;
        public static final int COMPRESSION_NONE            = 1;

    public static final int TIFFTAG_PHOTOMETRIC             = 262;
        public static final int PHOTOMETRIC_RGB             = 2;
        public static final int PHOTOMETRIC_CFA             = 32803;

    public static final int TIFFTAG_MAKE                    = 271;
    public static final int TIFFTAG_MODEL                   = 272;
    public static final int TIFFTAG_ORIENTATION             = 274;
        public static final int ORIENTATION_TOPLEFT         = 1;
        public static final int ORIENTATION_TOPRIGHT        = 2;
        public static final int ORIENTATION_BOTRIGHT        = 3;
        public static final int ORIENTATION_BOTLEFT         = 4;
        public static final int ORIENTATION_LEFTTOP         = 5;
        public static final int ORIENTATION_RIGHTTOP        = 6;
        public static final int ORIENTATION_RIGHTBOT        = 7;
        public static final int ORIENTATION_LEFTBOT         = 8;

    public static final int TIFFTAG_SAMPLESPERPIXEL         = 277;

    public static final int TIFFTAG_PLANARCONFIG            = 284;
        public static final int PLANARCONFIG_CONTIG         = 1;
        public static final int PLANARCONFIG_SEPARATE       = 2;

    public static final int TIFFTAG_SOFTWARE                = 305;
    public static final int TIFFTAG_DATETIME                = 306;
    public static final int TIFFTAG_SUBIFD                  = 330;
    public static final int TIFFTAG_DNGVERSION              = 50706;
    public static final int TIFFTAG_DNGBACKWARDVERSION      = 50707;
    public static final int TIFFTAG_UNIQUECAMERAMODEL       = 50708;
    public static final int TIFFTAG_LOCALIZEDCAMERAMODEL	= 50709;

    public static final int TIFFTAG_CFAPLANECOLOR           = 50710;
    public static final int TIFFTAG_CFALAYOUT               = 50711;

    public static final int TIFFTAG_COLORMATRIX1            = 50721;
    public static final int TIFFTAG_COLORMATRIX2            = 50722;

    public static final int TIFFTAG_CAMERACALIBRATION1      = 50723;
    public static final int TIFFTAG_CAMERACALIBRATION2      = 50724;

    public static final int TIFFTAG_ANALOGBALANCE           = 50727;
    public static final int TIFFTAG_ASSHOTNEUTRAL           = 50728;
    public static final int TIFFTAG_ASSHOTWHITEXY           = 50729;

    public static final int TIFFTAG_CALIBRATIONILLUMINANT1  = 50778;
    public static final int TIFFTAG_CALIBRATIONILLUMINANT2  = 50779;

    public static final int TIFFTAG_ORIGINALRAWFILENAME     = 50827;

    public static final int TIFFTAG_CFAREPEATPATTERNDIM     = 33421;
    public static final int TIFFTAG_CFAPATTERN              = 33422;

    public static final int TIFFTAG_WHITELEVEL              = 50717;
    public static final int TIFFTAG_BLACKLEVELREPEATDIM     = 50713;
    public static final int TIFFTAG_BLACKLEVEL              = 50714;

    public static final int TIFFTAG_EXIFIFD                 = 34665;

    public static final int TIFFTAG_FORWARDMATRIX1          = 50964;
    public static final int TIFFTAG_FORWARDMATRIX2          = 50965;
    public static final int TIFFTAG_NOISEPROFILE            = 51041;

    public static final int TIFFTAG_OPCODELIST1             = 51008;
    public static final int TIFFTAG_OPCODELIST2             = 51009;
    public static final int TIFFTAG_OPCODELIST3             = 51022;

    public static final int TIFFTAG_TILEWIDTH               = 322;
    public static final int TIFFTAG_TILELENGTH              = 323;
}
