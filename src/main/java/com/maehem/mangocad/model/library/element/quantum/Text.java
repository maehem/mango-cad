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

import com.maehem.mangocad.model.library.element.quantum.enums.TextAlign;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Text extends _AQuantum {
    // No FONT.  We only support line-font right now.
    private double size;
    private int    distance = 50;  // Line to line distance
    private double width = 8.0; // Eagle called it ratio
    private double rotation = 0.0;
    private TextAlign align = TextAlign.BOTTOM_LEFT;
    private String value;

    /**
     * @return the size
     */
    public double getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * @return the distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
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
     * @return the align
     */
    public TextAlign getAlign() {
        return align;
    }

    /**
     * @param align the align to set
     */
    public void setAlign(TextAlign align) {
        this.align = align;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

//    @Override
//    public Rectangle2D getBounds() {
//        Rectangle2D bounds = super.getBounds();
//        bounds.add( getX()+(getValue().length()*getSize()*0.7),
//                getY()+getSize()
//        );
//        return bounds; //To change body of generated methods, choose Tools | Templates.        
//    }

    
}
