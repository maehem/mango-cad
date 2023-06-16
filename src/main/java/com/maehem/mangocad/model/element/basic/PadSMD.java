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
import com.maehem.mangocad.model.util.Rotation;


/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class PadSMD extends _AQuantum {
    public static final String ELEMENT_NAME = "smd";
    
    private String name;
    private double x;
    private double y;
    private double width;
    private double height;
    private Rotation rotation = new Rotation();
    private int roundness = 0;
    private boolean stopmask = true;
    private boolean thermals = true;
    private boolean cream = true;

    
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
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the rotation
     */
    public double getRotation() {
        return rotation.getValue();
    }

    /**
     * @param val the rotation to set
     */
    public void setRotation(double val) {
        this.rotation.setValue(val);
    }

    /**
     * @return the roundness
     */
    public int getRoundness() {
        return roundness;
    }

    /**
     * @param roundness the roundness to set
     */
    public void setRoundness(int roundness) {
        this.roundness = roundness;
    }

    /**
     * @return the stopMask
     */
    public boolean isStopmask() {
        return stopmask;
    }

    /**
     * @param stopMask the stopMask to set
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
     * @return the cream
     */
    public boolean isCream() {
        return cream;
    }

    /**
     * @param cream the cream to set
     */
    public void setCream(boolean cream) {
        this.cream = cream;
    }

    
}
