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
import com.maehem.mangocad.model.element.enums.GridField;
import com.maehem.mangocad.model.element.enums.GridStyle;
import com.maehem.mangocad.model.element.enums.GridUnit;
import com.maehem.mangocad.model.util.Units;

/**
 * <pre>
 * grid EMPTY
 *    ATTRIBUTES
 * distance      %Real;         #IMPLIED
 * unitdist      %GridUnit;     #IMPLIED
 * unit          %GridUnit;     #IMPLIED
 * style         %GridStyle;    "lines"
 * multiple      %Int;          "1"
 * display       %Bool;         "no"
 * altdistance   %Real;         #IMPLIED
 * altunitdist   %GridUnit;     #IMPLIED
 * altunit       %GridUnit;     #IMPLIED
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
     * Get the raw size and convert to mm if needed.
     *
     * @return
     */
    public double getSizeMM() {
        switch (distanceStoredUnit) {
            case INCH -> {
                // Convert to mm
                return distance / Units.INCH.mult;
            }
            case MIC -> {
                // Convert to mm.
                return distance / Units.MIC.mult;
            }
            case MIL -> {
                // Convert to mm.
                return distance / Units.MIL.mult;
            }

        }
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setSize(double distance) {
        if (this.distance != distance) {
            double oldVal = this.distance;
            this.distance = distance;
            notifyListeners(GridField.DISTANCE, oldVal, this.distance);
        }
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
        if (this.distanceUnit != distUnit) {
            GridUnit oldVal = this.distanceUnit;
            this.distanceUnit = distUnit;
            notifyListeners(GridField.DISTANCE_UNIT, oldVal, this.distanceUnit);
        }
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
        if (this.style != style) {
            GridStyle oldVal = this.style;
            this.style = style;
            notifyListeners(GridField.STYLE, oldVal, this.style);
        }
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
        if (this.multiple != multiple) {
            int oldVal = this.multiple;
            this.multiple = multiple;
            notifyListeners(GridField.MULTIPLE, oldVal, this.multiple);
        }
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
        if (this.display != display) {
            boolean oldVal = this.display;
            this.display = display;
            notifyListeners(GridField.DISPLAY, oldVal, this.display);
        }
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
        if (this.altSize != altSize) {
            double oldVal = this.altSize;
            this.altSize = altSize;
            notifyListeners(GridField.ALT_SIZE, oldVal, this.altSize);
        }
    }

    /**
     * @return the altStoredUnit
     */
    public GridUnit getAltStoredUnit() {
        return altStoredUnit;
    }

    /**
     * @param altSizeStoredUnit the altStoredUnit to set
     */
    public void setAltStoredUnit(GridUnit altSizeStoredUnit) {
        if (this.altStoredUnit != altSizeStoredUnit) {
            GridUnit oldVal = this.altStoredUnit;
            this.altStoredUnit = altSizeStoredUnit;
            notifyListeners(GridField.ALT_STORED_UNIT, oldVal, this.altStoredUnit);
        }
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
        if (this.altUnit != altUnit) {
            GridUnit oldVal = altUnit;
            this.altUnit = altUnit;
            notifyListeners(GridField.ALT_UNIT, oldVal, this.altUnit);
        }
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    public boolean isDots() {
        return getStyle().equals(GridStyle.DOTS);
    }

}
