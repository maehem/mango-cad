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
package com.maehem.mangocad.model.library.element.quantum;

import com.maehem.mangocad.model.library.element.quantum.enums.PinDirection;
import com.maehem.mangocad.model.library.element.quantum.enums.PinFunction;
import com.maehem.mangocad.model.library.element.quantum.enums.PinLength;
import com.maehem.mangocad.model.library.element.quantum.enums.PinVisible;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Pin extends _AQuantum {
    // 'layer' is not used.
    
    private String name;
    private String padValue = null;
    private PinVisible visible = PinVisible.BOTH;
    private PinLength length = PinLength.LONG;
    private PinDirection direction = PinDirection.IO;
    private PinFunction function = PinFunction.NONE;
    private int swapLevel = 0;
    private double rotation = 0;

    /**
     * Pad Value, usually pin number assigned in DeviceSet
     * Might be null when viewed as symbol.
     * 
     * @return 
     */
    public String getPadValue() {
        return padValue;
    }
    
    public void setPadValue( String value ) {
        this.padValue = value;
    }
    
//    /**
//     * @return the visible
//     */
//    public String getVisible() {
//        return visible.code();
//    }
    public PinVisible getVisible() {
        return visible;
    }

//    /**
//     * @param visible the visible to set
//     */
//    public void setVisible(String visible) {
//        this.visible = PinVisible.fromCode(visible);
//    }
    
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

    /**
     * @return the rotation
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * @param rotation the rotation to set
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
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

    @Override
    public int getLayerNum() {
        return 94; // Same as symbol color  // TODO: get from static table.
    }

}
