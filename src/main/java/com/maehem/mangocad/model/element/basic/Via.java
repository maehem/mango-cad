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

import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.enums.GridUnit;
import com.maehem.mangocad.model.element.enums.ViaShape;
import com.maehem.mangocad.model.element.misc.DesignRules;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.util.DrcDefs;
import java.util.ArrayList;
import java.util.logging.Logger;

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
public class Via extends Element implements GrouprefsProperty {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static final String ELEMENT_NAME = "via";
    //public static final double DEFAULT_FLASH = 0.51; // Calc dfault diameter ( DEF_FLASH + drill );
    public enum Layer {
        TOP, INNER, BOTTOM
    };

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
     * @return the diameter, usually 0(auto/DRC) unless overwritten by user.
     */
    public double getDiameter() {
        return diameter;
    }

    /**
     * Override the DRC calculated diameter by setting value greater than zero.
     *
     * @param diameter the diameter to set
     */
    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double getDerivedDiameter(DesignRules dr, Layer l) {
        // Example Default DRC
        //  - drill*0.25
        //  - or min 10mil
        //  - or max 20mil
        //  - or overridden by user in element.
        if (getDiameter() > 0.0) {
            return getDiameter(); // Use over-ridden value
        } else { // Apply Design Rule
            String drViaOuter = dr.getRule(DrcDefs.RV_VIA_OUTER); // rvViaOuter - % - percentage
            //LOGGER.log(Level.SEVERE, "drViaOuter string value: " + drViaOuter);
            Double viaOuterVal = Double.valueOf(drViaOuter);
            double flash = getDrill() * viaOuterVal;

            String drMinViaOuter = dr.getRule(DrcDefs.RL_MIN_VIA_OUTER); // rlMinViaOuter - unit mil/mm
            Double viaMinOuterVal = GridUnit.toMM(drMinViaOuter);

            String drMaxViaOuter = dr.getRule(DrcDefs.RL_MAX_VIA_OUTER);
            Double viaMaxOuterVal = GridUnit.toMM(drMaxViaOuter); // rlMaxViaOuter - unit mil/mm

            if (flash < viaMinOuterVal) {
                flash = viaMinOuterVal;
            }
            if (flash > viaMaxOuterVal) {
                flash = viaMaxOuterVal;
            }
            return getDrill() + flash * 2.0;
        }
    }

    public double getMaskDiameter(DesignRules dr, Layer l) {
        double padDia = getDerivedDiameter(dr, l);

        //Mask is MV_STOP_BASE % of the drill with min and max considered.
        // NOTE: For Via, ML_MAX_STOP_FRAME is the only param used.
        //       The ML_MIN_STOP_FRAME and MV_STOP_FRAME seem to be ignored.
        // TODO: If limit less than drill, return 0;
        // Add the DRC mask amount.
        String rMin = dr.getRule(DrcDefs.ML_MIN_STOP_FRAME); // in mm or mil. i.e. "0.4mm", "10mil"
        Double viaMinStopVal = GridUnit.toMM(rMin);
        String rMax = dr.getRule(DrcDefs.ML_MAX_STOP_FRAME);
        Double viaMaxStopVal = GridUnit.toMM(rMax);
        String rVal = dr.getRule(DrcDefs.MV_STOP_FRAME);
        Double viaStopVal = Double.valueOf(rVal); // in percent 0.0-1.0

        double maskBase = padDia * viaStopVal;

        //LOGGER.log(Level.SEVERE, "    Drll Size: " + getDrill());
        //LOGGER.log(Level.SEVERE, "      Pad Dia: " + padDia);
        //LOGGER.log(Level.SEVERE, "maskBase Size: " + maskBase);
        if (maskBase < viaMinStopVal) {
            maskBase = viaMinStopVal;
            //LOGGER.log(Level.SEVERE, "     * to min: " + maskBase);
        }
        if (maskBase > viaMaxStopVal) {
            maskBase = viaMaxStopVal;
            //LOGGER.log(Level.SEVERE, "     * to max: " + maskBase);
        }

        //LOGGER.log(Level.SEVERE, "Final Diameter: " + (padDia + 2 * maskBase));
        return padDia + 2 * maskBase;
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
    @Override
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }
}
