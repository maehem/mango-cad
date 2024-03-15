/*
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF
    licenses this file to you under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with the
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
    License for the specific language governing permissions and limitations
    under the License.
 */
package com.maehem.mangocad.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ColorPalette {

    public enum Style {
        DARK, LIGHT, COLORED, PRINT_BW, PRINT_GREY, PRINT_COLOR
    };

    private Style style;

    private String[] darkHex = new String[]{
        "0xFF000000",
        "0xFF3232C8",
        "0xFF32C832",
        "0xFF32C8C8",
        "0xFFC83232", //  0xFFC83232
        "0xFFC832C8", //  0xFFC832C8
        "0xFFC8C832",
        "0xFFC8C8C8", // tStop
        "0xFF646464", // bStop
        "0xFF0000FF",
        "0xFF00FF00",
        "0xFF00FFFF",
        "0xFFFF0000",
        "0xFFFF00FF",
        "0xFFFFFF00",
        "0xFFFFFFFF",
        // Group-2 Normal Color
        "0xFFCC6600",
        "0xFFCC9900",
        "0xFF336600",
        "0xFF666633",
        "0xFF669966",
        "0xFF336666",
        "0xFF009966",
        "0xFF006699",
        //# Group-2 Hi-Light
        "0xFFFF9900",
        "0xFFFFCC33",
        "0xFF669900",
        "0xFF999966",
        "0xFF99CC99",
        "0xFF669999",
        "0xFF33CC99",
        "0xFF0099CC",
        // Group-2 Normal Color
        "0xFF996699",
        "0xFFCC9999",
        "0xFFCC6666",
        "0xFF660033",
        "0xFF663366",
        "0xFF996666",
        "0xFF336699",
        "0xFF339999",
        // Group-2 Hi-Light
        "0xFFCC99CC",
        "0xFFFFCCCC",
        "0xFFFF9999",
        "0xFF990033",
        "0xFF996699",
        "0xFFCC9999",
        "0xFF6699CC",
        "0xFF66CCCC",
        // Group-3 Normal Color
        "0xFFFBAF5D",
        "0xFF000000",
        "0xFF000000",
        "0xFF000000",
        "0xFF000000",
        "0xFF000000",
        "0xFF000000",
        "0xFF000000",
        // Group-3 Hi-Light
        "0xFFFDC689",
        "0xFFFFFFFF",
        "0xFFFFFFFF",
        "0xFFFFFFFF",
        "0xFFFFFFFF",
        "0xffFfFf00",
        "0xc895918C",
        "0xffFFFFFF"

    };

    private String[] lightHex = new String[]{
        "0xB4000080",
        "0xB4008000",
        "0xB4008080",
        "0xB4800000",
        "0xB4800080",
        "0xB4808000",
        "0xB4808080",
        "0xB4C0C0C0",
        "0xB40000FF",
        "0xB400FF00",
        "0xB400FFFF",
        "0xB4FF0000",
        "0xB4FF00FF",
        "0xB4FFFF00",
        "0xB4000000",
        // Group-2 Normal Color

        "0xC8CC6600",
        "0xC8CC9900",
        "0xC8336600",
        "0xC8666633",
        "0xC8669966",
        "0xC8336666",
        "0xC8009966",
        "0xC8006699",
        // Group-2 Hi-Light

        "0xC8FF9900",
        "0xC8FFCC33",
        "0xC8669900",
        "0xC8999966",
        "0xC899CC99",
        "0xC8669999",
        "0xC833CC99",
        "0xC80099CC",
        // Group-2 Normal Color

        "0xC8996699",
        "0xC8CC9999",
        "0xC8CC6666",
        "0xC8660033",
        "0xC8663366",
        "0xC8996666",
        "0xC8336699",
        "0xC8339999",
        // Group-2 Hi-Light

        "0xC8CC99CC",
        "0xC8FFCCCC",
        "0xC8FF9999",
        "0xC8990033",
        "0xC8996699",
        "0xC8CC9999",
        "0xC86699CC",
        "0xC866CCCC",
        // Group-3 Normal Color

        "0xC8FBAF5D",
        "0xC8000000",
        "0xC8000000",
        "0xC8000000",
        "0xC8000000",
        "0xC8000000",
        "0xC8000000",
        "0xC8000000",
        // Group-3 Hi-Light

        "0xC8FDC689",
        "0xC8FFFFFF",
        "0xC8FFFFFF",
        "0xC8FFFFFF",
        "0xC8FFFFFF",
        "0xC8fFFFFF",
        "0xC8FFFFFF",
        "0xC8FFFFFF"
    };

    private ArrayList<String> dark = new ArrayList<>(Arrays.asList(darkHex));
    private ArrayList<String> light = new ArrayList<>(Arrays.asList(lightHex));

    public ColorPalette(Style style) {
        this.style = style;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public String getHex(int index) {
        return getPalette(style).get(index);
    }

    public ArrayList<String> getPalette(Style s) {
        switch (s) {
            case DARK -> {
                return dark;
            }
            case PRINT_BW -> {
                return light;
            }
            case PRINT_COLOR -> {
                return light;
            }
            default -> {
                return dark;
            }
        }
    }

}

/*
Lifted from:
https://community.element14.com/products/eagle/f/eagle-user-chat-english/26919/color-scheme-script

set palette black;

set palette  0 0xFF000000;
set palette  1 0xB43232C8;
set palette  2 0xB432C832;
set palette  3 0xB432C8C8;
set palette  4 0xB4C83232;
set palette  5 0xB4C832C8;
set palette  6 0xB4C8C832;
set palette  7 0xB4C8C8C8;
set palette  8 0xB4646464;
set palette  9 0xB40000FF;
set palette 10 0xB400FF00;
set palette 11 0xB400FFFF;
set palette 12 0xB4FF0000;
set palette 13 0xB4FF00FF;
set palette 14 0xB4FFFF00;
set palette 15 0xB4FFFFFF;

# Group-2 Normal Color

SET PALETTE 16 0xC8CC6600;
SET PALETTE 17 0xC8CC9900;
SET PALETTE 18 0xC8336600;
SET PALETTE 19 0xC8666633;
SET PALETTE 20 0xC8669966;
SET PALETTE 21 0xC8336666;
SET PALETTE 22 0xC8009966;
SET PALETTE 23 0xC8006699;

# Group-2 Hi-Light

SET PALETTE 24 0xC8FF9900;
SET PALETTE 25 0xC8FFCC33;
SET PALETTE 26 0xC8669900;
SET PALETTE 27 0xC8999966;
SET PALETTE 28 0xC899CC99;
SET PALETTE 29 0xC8669999;
SET PALETTE 30 0xC833CC99;
SET PALETTE 31 0xC80099CC;

# Group-2 Normal Color

SET PALETTE 32 0xC8996699;
SET PALETTE 33 0xC8CC9999;
SET PALETTE 34 0xC8CC6666;
SET PALETTE 35 0xC8660033;
SET PALETTE 36 0xC8663366;
SET PALETTE 37 0xC8996666;
SET PALETTE 38 0xC8336699;
SET PALETTE 39 0xC8339999;

# Group-2 Hi-Light

SET PALETTE 40 0xC8CC99CC;
SET PALETTE 41 0xC8FFCCCC;
SET PALETTE 42 0xC8FF9999;
SET PALETTE 43 0xC8990033;
SET PALETTE 44 0xC8996699;
SET PALETTE 45 0xC8CC9999;
SET PALETTE 46 0xC86699CC;
SET PALETTE 47 0xC866CCCC;

# Group-3 Normal Color

SET PALETTE 48 0xC8FBAF5D;
SET PALETTE 49 0xC8000000;
SET PALETTE 50 0xC8000000;
SET PALETTE 51 0xC8000000;
SET PALETTE 52 0xC8000000;
SET PALETTE 53 0xC8000000;
SET PALETTE 54 0xC8000000;
SET PALETTE 55 0xC8000000;

# Group-3 Hi-Light

SET PALETTE 56 0xC8FDC689;
SET PALETTE 57 0xC8FFFFFF;
SET PALETTE 58 0xC8FFFFFF;
SET PALETTE 59 0xC8FFFFFF;
SET PALETTE 60 0xC8FFFFFF;
SET PALETTE 61 0xffFfFf00;
SET PALETTE 62 0xc895918C;
SET PALETTE 63 0xffFFFFFF;


set palette colored;


set palette  0 0xFFEEEECE;
set palette  1 0xB4000080;
set palette  2 0xB4008000;
set palette  3 0xB4008080;
set palette  4 0xB4800000;
set palette  5 0xB4800080;
set palette  6 0xB4808000;
set palette  7 0xB4808080;
set palette  8 0xB4C0C0C0;
set palette  9 0xB40000FF;
set palette 10 0xB400FF00;
set palette 11 0xB400FFFF;
set palette 12 0xB4FF0000;
set palette 13 0xB4FF00FF;
set palette 14 0xB4FFFF00;
set palette 15 0xB4000000;

# Group-2 Normal Color

SET PALETTE 16 0xC8CC6600;
SET PALETTE 17 0xC8CC9900;
SET PALETTE 18 0xC8336600;
SET PALETTE 19 0xC8666633;
SET PALETTE 20 0xC8669966;
SET PALETTE 21 0xC8336666;
SET PALETTE 22 0xC8009966;
SET PALETTE 23 0xC8006699;

# Group-2 Hi-Light

SET PALETTE 24 0xC8FF9900;
SET PALETTE 25 0xC8FFCC33;
SET PALETTE 26 0xC8669900;
SET PALETTE 27 0xC8999966;
SET PALETTE 28 0xC899CC99;
SET PALETTE 29 0xC8669999;
SET PALETTE 30 0xC833CC99;
SET PALETTE 31 0xC80099CC;

# Group-2 Normal Color

SET PALETTE 32 0xC8996699;
SET PALETTE 33 0xC8CC9999;
SET PALETTE 34 0xC8CC6666;
SET PALETTE 35 0xC8660033;
SET PALETTE 36 0xC8663366;
SET PALETTE 37 0xC8996666;
SET PALETTE 38 0xC8336699;
SET PALETTE 39 0xC8339999;

# Group-2 Hi-Light

SET PALETTE 40 0xC8CC99CC;
SET PALETTE 41 0xC8FFCCCC;
SET PALETTE 42 0xC8FF9999;
SET PALETTE 43 0xC8990033;
SET PALETTE 44 0xC8996699;
SET PALETTE 45 0xC8CC9999;
SET PALETTE 46 0xC86699CC;
SET PALETTE 47 0xC866CCCC;

# Group-3 Normal Color

SET PALETTE 48 0xC8FBAF5D;
SET PALETTE 49 0xC8000000;
SET PALETTE 50 0xC8000000;
SET PALETTE 51 0xC8000000;
SET PALETTE 52 0xC8000000;
SET PALETTE 53 0xC8000000;
SET PALETTE 54 0xC8000000;
SET PALETTE 55 0xC8000000;

# Group-3 Hi-Light

SET PALETTE 56 0xC8FDC689;
SET PALETTE 57 0xC8FFFFFF;
SET PALETTE 58 0xC8FFFFFF;
SET PALETTE 59 0xC8FFFFFF;
SET PALETTE 60 0xC8FFFFFF;
SET PALETTE 61 0xC8fFFFFF;
SET PALETTE 62 0xC8FFFFFF;
SET PALETTE 63 0xC8FFFFFF;


set palette white;


set palette  1 0xB4000080;
set palette  2 0xB4008000;
set palette  3 0xB4008080;
set palette  4 0xB4800000;
set palette  5 0xB4800080;
set palette  6 0xB4808000;
set palette  7 0xB4808080;
set palette  8 0xB4C0C0C0;
set palette  9 0xB40000FF;
set palette 10 0xB400FF00;
set palette 11 0xB400FFFF;
set palette 12 0xB4FF0000;
set palette 13 0xB4FF00FF;
set palette 14 0xB4FFFF00;
set palette 15 0xB4000000;

# Group-2 Normal Color

SET PALETTE 16 0xC8CC6600;
SET PALETTE 17 0xC8CC9900;
SET PALETTE 18 0xC8336600;
SET PALETTE 19 0xC8666633;
SET PALETTE 20 0xC8669966;
SET PALETTE 21 0xC8336666;
SET PALETTE 22 0xC8009966;
SET PALETTE 23 0xC8006699;

# Group-2 Hi-Light

SET PALETTE 24 0xC8FF9900;
SET PALETTE 25 0xC8FFCC33;
SET PALETTE 26 0xC8669900;
SET PALETTE 27 0xC8999966;
SET PALETTE 28 0xC899CC99;
SET PALETTE 29 0xC8669999;
SET PALETTE 30 0xC833CC99;
SET PALETTE 31 0xC80099CC;

# Group-2 Normal Color

SET PALETTE 32 0xC8996699;
SET PALETTE 33 0xC8CC9999;
SET PALETTE 34 0xC8CC6666;
SET PALETTE 35 0xC8660033;
SET PALETTE 36 0xC8663366;
SET PALETTE 37 0xC8996666;
SET PALETTE 38 0xC8336699;
SET PALETTE 39 0xC8339999;

# Group-2 Hi-Light

SET PALETTE 40 0xC8CC99CC;
SET PALETTE 41 0xC8FFCCCC;
SET PALETTE 42 0xC8FF9999;
SET PALETTE 43 0xC8990033;
SET PALETTE 44 0xC8996699;
SET PALETTE 45 0xC8CC9999;
SET PALETTE 46 0xC86699CC;
SET PALETTE 47 0xC866CCCC;

# Group-3 Normal Color

SET PALETTE 48 0xC8FBAF5D;
SET PALETTE 49 0xC8000000;
SET PALETTE 50 0xC8000000;
SET PALETTE 51 0xC8000000;
SET PALETTE 52 0xC8000000;
SET PALETTE 53 0xC8000000;
SET PALETTE 54 0xC8000000;
SET PALETTE 55 0xC8000000;

# Group-3 Hi-Light

SET PALETTE 56 0xC8FDC689;
SET PALETTE 57 0xC8FFFFFF;
SET PALETTE 58 0xC8FFFFFF;
SET PALETTE 59 0xC8FFFFFF;
SET PALETTE 60 0xC8FFFFFF;
SET PALETTE 61 0xC8fFFFFF;
SET PALETTE 62 0xC8FFFFFF;
SET PALETTE 63 0xC8FFFFFF;

 */
