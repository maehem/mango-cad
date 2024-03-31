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
public enum SchematicLayers {
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

    private SchematicLayers(String code, int number, int color, int fill, boolean showDefault) {
        this.code = code;
        this.number = number;
        this.color = color;
        this.fill = fill;
        this.showDefault = showDefault;
    }

    public static boolean contains(int layerNumber) {
        for (SchematicLayers ll : SchematicLayers.values()) {
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
