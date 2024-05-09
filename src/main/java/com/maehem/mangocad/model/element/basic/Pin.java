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
import com.maehem.mangocad.model.ElementRotation;
import com.maehem.mangocad.model.ElementXY;
import com.maehem.mangocad.model.element.enums.PinDirection;
import com.maehem.mangocad.model.element.enums.PinField;
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
public class Pin extends Element implements ElementXY, ElementRotation {

    public static final String ELEMENT_NAME = "pin";
    // 'layer' is not used.

    public static final String SWAPLEVEL_KEY = "swaplevel"; // TODO Use PinField enum.
    public static final List<String> BASIC_ROTATIONS = Arrays.asList("R0", "R90", "R180", "R270"); // TODO: Make a fixed-ROT enum

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

    public Pin() {
        getRotation().setConstrained(true);
    }

    @Override
    public int getLayerNum() {
        return 94; // Same as symbol color  // TODO: get from static table.
    }

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
        String oldName = this.name;
        this.name = name;
        notifyListeners(PinField.NAME, oldName, name);
    }

    /**
     * @return the x
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    @Override
    public void setX(double x) {
        if (this.x != x) {
            double oldX = this.x;
            this.x = x;
            notifyListeners(PinField.X, oldX, x);
        }
    }

    /**
     * @return the y
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    @Override
    public void setY(double y) {
        if (this.y != y) {
            double oldY = this.y;
            this.y = y;
            notifyListeners(PinField.Y, oldY, y);
        }
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
        String oldVal = this.padValue;
        this.padValue = value;
        notifyListeners(PinField.PAD_VALUE, oldVal, padValue);
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
     *
     * @param visible
     */
    public void setVisible(PinVisible visible) {
        PinVisible oldVisible = this.visible;
        this.visible = visible;
        notifyListeners(PinField.VISIBLE, oldVisible, this.visible);
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
        PinLength oldVal = this.length;
        this.length = length;
        notifyListeners(PinField.LENGTH, oldVal, this.length);
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
        PinDirection oldVal = this.direction;
        this.direction = direction;
        notifyListeners(PinField.DIRECTION, oldVal, this.direction);
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
        PinFunction oldVal = this.function;
        this.function = function;
        notifyListeners(PinField.FUNCTION, oldVal, this.function);
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
        int oldVal = this.swapLevel;
        this.swapLevel = swapLevel;
        notifyListeners(PinField.SWAPLEVEL, oldVal, this.swapLevel);
    }

    @Override
    public final Rotation getRotation() {
        return rotation;
    }

    /**
     * @return the rotation
     */
    @Override
    public double getRot() {
        return rotation.getValue();
    }

    /**
     * @param val the rotation to set
     */
    @Override
    public void setRot(double val) {
        double oldVal = this.getRot();
        this.rotation.setValue(val);
        notifyListeners(PinField.ROTATION, oldVal, this.rotation.getValue());
    }

    @Override
    public boolean isSpun() {
        return false;
    }

    @Override
    public void setSpin(boolean value) {
        // Not applicable
    }

    @Override
    public boolean isSpin() {
        return false;
    }

    @Override
    public boolean isSpinAllowed() {
        return false;
    }

    @Override
    public void setAllowSpin(boolean value) {
        // Not applicable.
    }

    @Override
    public void setMirror(boolean value) {
        setRot(getRot() + 180);
    }

    @Override
    public boolean isMirrored() {
        return getRot() == 180 || getRot() == 270;
    }

    @Override
    public boolean isMirrorAllowed() {
        return false;
    }

    @Override
    public void setAllowMirror(boolean value) {
        // Not applicable.
    }

    @Override
    public boolean isConstrained() {
        return true;
    }

    @Override
    public void setConstrained(boolean value) {
        // Only true allowed.
    }

}
