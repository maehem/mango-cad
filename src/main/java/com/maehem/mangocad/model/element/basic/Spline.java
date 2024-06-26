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
import java.util.ArrayList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Spline extends Element implements LayerNumberProperty, ElementValueListener {

    public static final String ELEMENT_NAME = "spline";

    private ArrayList<Vertex> vertices = new ArrayList<>();

    //  spline (vertex)
    //  Four simple (non-curve) vertices define the control points of a degree-3 spline curve
    //  ATTLIST spline
    //          width          %Dimension;    #REQUIRED

    //   ADDED BY AUTODESK RECENTLY
    //     layer           %Int%       IMPLIED    (new to 9.7)
    //     locked          %Bool%      IMPLIED    (new to 9.7)
    private double width;
    public final LayerNumberValue layerValue = new LayerNumberValue(1);
    //private int layer;
    private boolean locked = false;

    public Spline() {

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
     * @return the part
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
     * @return the vertices
     */
    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
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
