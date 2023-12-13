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
package com.maehem.mangocad.model.element.basic;

import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.enums.ViaShape;
import java.util.ArrayList;

/**
 * <pre>
 * via EMPTY>
 *    ATTLIST via
 *      x             %Coord;        #REQUIRED
 *      y             %Coord;        #REQUIRED
 *      extent        %Extent;       #REQUIRED
 *      drill         %Dimension;    #REQUIRED
 *      diameter      %Dimension;    "0"
 *      shape         %ViaShape;     "round"
 *      alwaysstop    %Bool;         "no"
 *      grouprefs     IDREFS         #IMPLIED
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Via extends _AQuantum {

    public static final String ELEMENT_NAME = "via";
    //public static final double DEFAULT_FLASH = 0.51; // Calc dfault diameter ( DEF_FLASH + drill );

    private double x;
    private double y;
    private String extent;
    private double drill;
    private double diameter = 0.0;
    private ViaShape shape = ViaShape.ROUND;
    private boolean alwaysstop = false;

    private final ArrayList<String> grouprefs = new ArrayList<>();

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the drill
     */
    public double getDrill() {
        return drill;
    }

    /**
     * @param drill the drill to set
     */
    public void setDrill(double drill) {
        this.drill = drill;
    }

    /**
     * @return the extent
     */
    public String getExtent() {
        return extent;
    }

    /**
     * @param extent the extent to set
     */
    public void setExtent(String extent) {
        this.extent = extent;
    }

    /**
     * @return the diameter
     */
    public double getDiameter() {
        return diameter;
    }

    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double getDerivedDiameter() {
        // Default DRC
        //  - drill*0.25
        //  - or min 10mil
        //  - or max 20mil
        //  - or overridden.
        // TODO:  These numbers need to come from any installed DRC rule set.
        if (getDiameter() == 0.0) { // Use default
            double flash = getDrill() * 0.25;
            if (flash < 0.254) {
                flash = 0.254;
            }
            if (flash > 0.508) {
                flash = 0.508;
            }
            return getDrill() + flash * 2.0;
        }

        return getDiameter();
    }

    /**
     * @return the shape
     */
    public ViaShape getShape() {
        return shape;
    }

    /**
     * @param shape the shape to set
     */
    public void setShape(ViaShape shape) {
        this.shape = shape;
    }

    /**
     * @return the alwaysstop
     */
    public boolean isAlwaysstop() {
        return alwaysstop;
    }

    /**
     * @param alwaysstop the alwaysstop to set
     */
    public void setAlwaysstop(boolean alwaysstop) {
        this.alwaysstop = alwaysstop;
    }

    /**
     * @return the grouprefs
     */
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }
}
