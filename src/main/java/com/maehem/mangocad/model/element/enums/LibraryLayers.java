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
package com.maehem.mangocad.model.element.enums;

/**
 * Default @Library layer definitions.
 *
 * While new layers can be added to a @Library, these cannot be removed from a
 * design. Any new @Library will load all of these layers with these defaults.
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public enum LibraryLayers {
    TOP("Top", 1, 4, 1, true),
    BOTTOM("Bottom", 16, 1, 1, true),
    PADS("Pads", 17, 2, 1, true),
    VIAS("Vias", 18, 2, 1, true),
    UNROUTED("Unrouted", 19, 6, 1, true),
    DIMENSION("Dimension", 20, 24, 1, true),
    T_PLACE("tPlace", 21, 7, 1, true),
    B_PLACE("bPlace", 22, 7, 1, true),
    T_ORIGINS("tOrigins", 23, 15, 1, true),
    B_ORIGINS("bOrigins", 24, 15, 1, true),
    T_NAMES("tNames", 25, 7, 1, true),
    B_NAMES("bNames", 26, 7, 1, true),
    T_VALUES("tValues", 27, 7, 1, true),
    B_VALUES("bValues", 28, 7, 1, true),
    T_STOP("tStop", 29, 7, 3, false),
    B_STOP("bStop", 30, 7, 6, false),
    T_CREAM("tCream", 31, 7, 4, false),
    B_CREAM("bCream", 32, 7, 5, false),
    T_FINISH("tFinish", 33, 6, 3, false),
    B_FINISH("bFinish", 34, 6, 6, false),
    T_GLUE("tGlue", 35, 7, 4, false),
    B_GLUE("bGlue", 36, 7, 5, false),
    T_TEST("tTest", 37, 7, 1, false),
    B_TEST("bTest", 38, 7, 1, false),
    T_KEEPOUT("tKeepout", 39, 4, 11, true),
    B_KEEPOUT("bKeepout", 40, 1, 11, true),
    T_RESTRICT("tRestrict", 41, 4, 10, true),
    B_RESTRICT("bRestrict", 42, 1, 10, true),
    V_RESTRICT("vRestrict", 43, 2, 10, true),
    DRILLS("Drills", 44, 7, 1, false),
    HOLES("Holes", 45, 7, 1, false),
    MILLING("Milling", 46, 3, 1, false),
    MEASURES("Measures", 47, 7, 1, false),
    DOCUMENT("Document", 48, 7, 1, true),
    REFERENCE("Reference", 49, 7, 1, true),
    T_DOCU("tDocu", 51, 7, 1, true),
    B_DOCU("bDocu", 52, 7, 1, true),
    SIM_RESULTS("SimResults", 88, 9, 1, true),
    SIM_PROBES("SimProbes", 89, 9, 1, true),
    MODULES("Modules", 90, 5, 1, true),
    NETS("Nets", 91, 2, 1, true),
    BUSSES("Busses", 92, 1, 1, true),
    PINS("Pins", 93, 2, 1, false),
    SYMBOLS("Symbols", 94, 4, 1, true),
    NAMES("Names", 95, 7, 1, true),
    VALUES("Values", 96, 7, 1, true),
    INFO("Info", 97, 7, 1, true),
    GUIDE("Guide", 98, 6, 1, true);

    private final String code;
    private final int number;
    private final int fill;
    private final boolean showDefault;
    private final int color;

    private LibraryLayers(String code, int number, int color, int fill, boolean showDefault) {
        this.code = code;
        this.number = number;
        this.color = color;
        this.fill = fill;
        this.showDefault = showDefault;
    }

    public static boolean contains(int layerNumber) {
        for (LibraryLayers ll : LibraryLayers.values()) {
            if (ll.getNumber() == layerNumber) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the fill
     */
    public int getFill() {
        return fill;
    }

    /**
     * @return the showDefault
     */
    public boolean isShowDefault() {
        return showDefault;
    }

    /**
     * @return the color
     */
    public int getColor() {
        return color;
    }

    public boolean isActive(int layerNum) {
        // All layers are always active
        return true;
    }
}
