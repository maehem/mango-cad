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
package com.maehem.mangocad.model.element.misc;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.enums.GridStyle;
import com.maehem.mangocad.model.element.enums.GridUnit;

/**
 * <pre>
 * grid EMPTY
 *    ATTRIBUTES
          distance      %Real;         #IMPLIED
          unitdist      %GridUnit;     #IMPLIED
          unit          %GridUnit;     #IMPLIED
          style         %GridStyle;    "lines"
          multiple      %Int;          "1"
          display       %Bool;         "no"
          altdistance   %Real;         #IMPLIED
          altunitdist   %GridUnit;     #IMPLIED
          altunit       %GridUnit;     #IMPLIED
 * </pre>
 *
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Grid extends Element {

    public static final String ELEMENT_NAME = "grid";

    public static final GridUnit DEFAULT_DIST_UNIT = GridUnit.INCH;
    public static final double DEFAULT_DIST = 0.10;
    public static final double DEFAULT_ALT = 0.01;
    public static final GridUnit DEFAULT_ALT_UNIT = GridUnit.INCH;
    public static final int DEFAULT_MULT = 1;
    public static final boolean DEFAULT_DISPLAY = false;

    private double distance = DEFAULT_DIST;  // "Size" in UI
    private GridUnit distanceStoredUnit = DEFAULT_DIST_UNIT; // Store 'distance' as these type units.
    private GridUnit distanceUnit = DEFAULT_DIST_UNIT;  // UI selected unit. UI ComboBox.
    private GridStyle style = GridStyle.LINES;
    private int multiple = DEFAULT_MULT;
    private boolean display = DEFAULT_DISPLAY;
    private double altSize = DEFAULT_ALT;
    private GridUnit altStoredUnit = DEFAULT_ALT_UNIT; // Store 'altDistance' as these type units.
    private GridUnit altUnit = DEFAULT_ALT_UNIT;    // UI selected unit. UI ComboBox.

    public Grid() {
    }

    /**
     * @return the distance
     */
    public double getSize() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setSize(double distance) {
        this.distance = distance;
    }

    /**
     * @param distUnit the distanceStoredUnit to set
     */
    public void setSizeStoredUnit(GridUnit distUnit) {
        this.distanceStoredUnit = distUnit;
    }

    /**
     * @return the distanceStoredUnit
     */
    public GridUnit getSizeStoredUnit() {
        return distanceStoredUnit;
    }

    /**
     * @param distUnit the distanceStoredUnit to set
     */
    public void setSizeUnit(GridUnit distUnit) {
        this.distanceUnit = distUnit;
    }

    /**
     * @return the distanceUnit
     */
    public GridUnit getSizeUnit() {
        return distanceUnit;
    }

    /**
     * @return the style
     */
    public GridStyle getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(GridStyle style) {
        this.style = style;
    }

    /**
     * @return the multiple
     */
    public int getMultiple() {
        return multiple;
    }

    /**
     * @param multiple the multiple to set
     */
    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    /**
     * @return the display
     */
    public boolean isDisplay() {
        return display;
    }

    /**
     * @param display the display to set
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * @return the altDistance
     */
    public double getAltSize() {
        return altSize;
    }

    /**
     * @param altSize the altDistance to set
     */
    public void setAltSize(double altSize) {
        this.altSize = altSize;
    }

    /**
     * @return the altStoredUnit
     */
    public GridUnit getAltStoredUnit() {
        return altStoredUnit;
    }

    /**
     * @param altSizeUnit the altStoredUnit to set
     */
    public void setAltStoredUnit(GridUnit altSizeUnit) {
        this.altStoredUnit = altSizeUnit;
    }

    /**
     * @return the altUnit
     */
    public GridUnit getAltUnit() {
        return altUnit;
    }

    /**
     * @param altUnit the altUnit to set
     */
    public void setAltUnit(GridUnit altUnit) {
        this.altUnit = altUnit;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    public boolean isDots() {
        return getStyle().equals(GridStyle.DOTS);
    }

}
