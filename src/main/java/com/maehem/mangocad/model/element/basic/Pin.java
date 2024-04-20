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
import com.maehem.mangocad.model.element.enums.PinDirection;
import com.maehem.mangocad.model.element.enums.PinFunction;
import com.maehem.mangocad.model.element.enums.PinLength;
import com.maehem.mangocad.model.element.enums.PinVisible;
import com.maehem.mangocad.model.util.Rotation;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * pin EMPTY
 *    ATTR
 *      name          %String;       #REQUIRED
 *      x             %Coord;        #REQUIRED
 *      y             %Coord;        #REQUIRED
 *      visible       %PinVisible;   "both"
 *      length        %PinLength;    "long"
 *      direction     %PinDirection; "io"
 *      function      %PinFunction;  "none"
 *      swaplevel     %Int;          "0"
 *      rot           %Rotation;     "R0"
 * </pre>
 *
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Pin extends Element {

    public static final String ELEMENT_NAME = "pin";
    // 'layer' is not used.

    public static final String SWAPLEVEL_KEY = "swaplevel";
    public static final List<String> BASIC_ROTATIONS = Arrays.asList("R0", "R90", "R180", "R270");

    private String name;
    private double x;
    private double y;
    private PinVisible visible = PinVisible.BOTH;
    private PinLength length = PinLength.LONG;
    private PinDirection direction = PinDirection.IO;
    private PinFunction function = PinFunction.NONE;
    private int swapLevel = 0;
    private final Rotation rotation = new Rotation(Rotation.CONSTRAINED);

    // Lookup
    private String padValue = null;

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

    /**
     * Pad Value, usually pin number assigned in DeviceSet Might be null when
     * viewed as symbol.
     *
     * @return
     */
    public String getPadValue() {
        return padValue;
    }

    public void setPadValue(String value) {
        this.padValue = value;
    }

    /**
     *
     * @return code for visible
     */
    public PinVisible getVisible() {
        return visible;
    }

    /**
     * Set pin/pad visibility
     * @param visible
     */
    public void setVisible(PinVisible visible) {
        this.visible = visible;
    }

    /**
     * @return the length
     */
    public PinLength getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(PinLength length) {
        this.length = length;
    }

    /**
     * @return the direction
     */
    public PinDirection getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(PinDirection direction) {
        this.direction = direction;
    }

    /**
     * @return the function
     */
    public PinFunction getFunction() {
        return function;
    }

    /**
     * @param function the function to set
     */
    public void setFunction(PinFunction function) {
        this.function = function;
    }

    /**
     * @return the swapLevel
     */
    public int getSwapLevel() {
        return swapLevel;
    }

    /**
     * @param swapLevel the swapLevel to set
     */
    public void setSwapLevel(int swapLevel) {
        this.swapLevel = swapLevel;
    }

    public Rotation getRotation() {
        return rotation;
    }

    /**
     * @return the rotation
     */
    public double getRot() {
        return rotation.getValue();
    }

    /**
     * @param val the rotation to set
     */
    public void setRot(double val) {
        this.rotation.setValue(val);
    }

    public int getLayerNum() {
        return 94; // Same as symbol color  // TODO: get from static table.
    }

}
