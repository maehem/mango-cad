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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.enums.PadShape;
import static com.maehem.mangocad.model.element.enums.PadShape.ROUND;
import com.maehem.mangocad.model.util.Rotation;


/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class PadTHD extends Element {

    public static final String ELEMENT_NAME = "thd";
    public static final double DEFAULT_FLASH = 0.51; // Calc dfault diameter ( DEF_FLASH + drill );

    private String name;
    private double x;
    private double y;
    private final Rotation rotation = new Rotation();
    private double drill;
    private PadShape shape = ROUND;
    private boolean stopmask = true;
    private boolean thermals = true;
    private double diameter = 0.0; // 0.0 == auto diamteter
    private boolean first;


    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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

    public Rotation getRotation() {
        return rotation;
    }

    /**
     * @return the rotation
     */
    public double getRot() {
        return rotation.get();
    }

    /**
     * @param val the rotation to set
     */
    public void setRot(double val) {
        this.rotation.set(val);
    }

    /**
     * @return the stopMask
     */
    public boolean isStopmask() {
        return stopmask;
    }

    /**
     * @param stopmask the stopMask to set
     */
    public void setStopmask(boolean stopmask) {
        this.stopmask = stopmask;
    }

    /**
     * @return the thermals
     */
    public boolean isThermals() {
        return thermals;
    }

    /**
     * @param thermals the thermals to set
     */
    public void setThermals(boolean thermals) {
        this.thermals = thermals;
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
     * @return the shape
     */
    public PadShape getShape() {
        return shape;
    }

    /**
     * @param shape the shape to set
     */
    public void setShape(PadShape shape) {
        this.shape = shape;
    }

    /**
     * @return the diameter
     */
    public double getDiameter() {
        return diameter;
    }

    public double getDerivedDiameter() {
        if ( getDiameter() == 0.0 ) { // Use default
            return getDrill() + DEFAULT_FLASH;
        }

        return getDiameter();
    }

    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

//    /**
//     *
//     * @return layer number
//     */
//    @Override
//    public int getLayerNum() {
//        return 17; // TODO: get from static table.
//    }
//
//    @Override
//    public void setLayerNum(int layer) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
//
    /**
     * @return the first
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * @param first
     */
    public void setFirst(boolean first) {
        this.first = first;
    }


}
