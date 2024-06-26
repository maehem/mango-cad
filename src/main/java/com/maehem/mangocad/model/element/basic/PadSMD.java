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
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LayerNumberValue;
import com.maehem.mangocad.model.element.property.Rotation;


/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class PadSMD extends Element implements LayerNumberProperty, ElementValueListener {
    public static final String ELEMENT_NAME = "smd";

    //private int layer;
    public final LayerNumberValue layerValue = new LayerNumberValue(1);
    private String name;
    private double x;
    private double y;
    private double width;
    private double height;
    public final Rotation rotation = new Rotation();
    private int roundness = 0;
    private boolean stopmask = true;
    private boolean thermals = true;
    private boolean cream = true;

    public PadSMD() {

        layerValue.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public LayerNumberValue getLayerNumberProperty() {
        return layerValue;
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

    @Override
    public int getLayerNum() {
        return layerValue.get();
    }

    @Override
    public void setLayerNum(int layer) {
        layerValue.set(layer);
//        if (this.layer != layer) {
//            int oldVal = this.layer;
//            this.layer = layer;
//            notifyListeners(LayerNumberProperty.Field.LAYER, oldVal, this.layer);
//        }
    }

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(layerValue)) {
            notifyListeners(LayerNumberProperty.Field.LAYER, layerValue.getOldValue(), layerValue.get());
        }

    }

}
