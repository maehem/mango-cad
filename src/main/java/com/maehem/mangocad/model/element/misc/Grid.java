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

import com.maehem.mangocad.model._AQuantum;
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
public class Grid extends _AQuantum {
    public static final String ELEMENT_NAME = "grid";
    
    private double distance;
    private GridUnit unitDist;
    private GridUnit unit;
    private GridStyle style = GridStyle.LINES;
    private int multiple = 1;
    private boolean display = false;
    private double altDistance;
    private GridUnit altUnitDist;
    private GridUnit altUnit;
     
    public Grid() {
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * @return the unitDist
     */
    public GridUnit getUnitDist() {
        return unitDist;
    }

    /**
     * @param unitDist the unitDist to set
     */
    public void setUnitDist(GridUnit unitDist) {
        this.unitDist = unitDist;
    }

    /**
     * @return the unit
     */
    public GridUnit getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(GridUnit unit) {
        this.unit = unit;
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
    public double getAltDistance() {
        return altDistance;
    }

    /**
     * @param altDistance the altDistance to set
     */
    public void setAltDistance(double altDistance) {
        this.altDistance = altDistance;
    }

    /**
     * @return the altUnitDist
     */
    public GridUnit getAltUnitDist() {
        return altUnitDist;
    }

    /**
     * @param altUnitDist the altUnitDist to set
     */
    public void setAltUnitDist(GridUnit altUnitDist) {
        this.altUnitDist = altUnitDist;
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

}
