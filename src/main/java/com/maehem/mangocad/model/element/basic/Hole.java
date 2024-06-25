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
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.enums.GridUnit;
import com.maehem.mangocad.model.element.misc.DesignRules;
import com.maehem.mangocad.model.element.property.CoordinateValue;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LocationXYProperty;
import com.maehem.mangocad.model.util.DrcDefs;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Hole extends Element implements LocationXYProperty, GrouprefsProperty {
    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");
    public static final String ELEMENT_NAME = "hole";

    @Override
    public CoordinateValue getCoordinateProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public enum Field implements ElementField {
        DRILL("drill", Double.class);

        private final String fName;
        private final Class clazz;

        private Field(String name, Class clazz) {
            this.fName = name;
            this.clazz = clazz;
        }

        @Override
        public String fName() {
            return fName;
        }

        @Override
        public Class clazz() {
            return clazz;
        }

    }

    public final CoordinateValue coordinate = new CoordinateValue();
    //private double x;
    //private double y;
    private double drill;
    private final ArrayList<String> groupRefs = new ArrayList<>();


    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x
     */
    public double getX() {
        return coordinate.x.get();
    }

    /**
     * @param x the x to set
     */
    public void setX(double val) {
        coordinate.x.set(val);
    }

    /**
     * @return the y
     */
    public double getY() {
        return coordinate.y.get();
    }

    /**
     * @param val the y to set
     */
    public void setY(double val) {
        coordinate.y.set(val);
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

    public double getMaskDiameter(DesignRules dr) {

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

        double maskBase = getDrill() * viaStopVal;

        //LOGGER.log(Level.SEVERE, "    Hole Size: " + getDrill());
        //LOGGER.log(Level.SEVERE, "maskBase Size: " + maskBase);
        if (maskBase < viaMinStopVal) {
            maskBase = viaMinStopVal;
            //LOGGER.log(Level.SEVERE, "     * to min: " + maskBase);
        }
        if (maskBase > viaMaxStopVal) {
            maskBase = viaMaxStopVal;
            //LOGGER.log(Level.SEVERE, "     * to max: " + maskBase);
        }

        //LOGGER.log(Level.SEVERE, "Final Diameter: " + (getDrill() + 2 * maskBase));
        return getDrill() + 2 * maskBase;
    }

    @Override
    public ArrayList<String> getGrouprefs() {
        return groupRefs;
    }

}
