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
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LayerNumberValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class FrameElement extends Element implements LayerNumberProperty, GrouprefsProperty, ElementValueListener {
    public static final String ELEMENT_NAME = "frame";

    //     frame ( no elements )
    //          x1            %Coord;       #REQUIRED
    //          y1            %Coord;       #REQUIRED
    //          x2            %Coord;       #REQUIRED
    //          y2            %Coord;       #REQUIRED
    //          columns       %Int;         #REQUIRED
    //          rows          %Int;         #REQUIRED
    //          layer         %Layer;       #REQUIRED
    //          border-left   %Bool;        "yes"
    //          border-top    %Bool;        "yes"
    //          border-right  %Bool;        "yes"
    //          border-bottom %Bool;        "yes"
    //          grouprefs     IDREFS         #IMPLIED
    //
    //private int layer;
    public final LayerNumberValue layerValue = new LayerNumberValue(97);
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private int columns;
    private int rows;
    private boolean borderLeft = true;
    private boolean borderTop  = true;
    private boolean borderRight = true;
    private boolean borderBottom = true;
    private final ArrayList<String> groupRefs = new ArrayList<>();

    public FrameElement() {

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
     * @return the x1
     */
    public double getX1() {
        return x1;
    }

    /**
     * @param x1 the x1 to set
     */
    public void setX1(double x1) {
        this.x1 = x1;
    }

    /**
     * @return the y1
     */
    public double getY1() {
        return y1;
    }

    /**
     * @param y1 the y1 to set
     */
    public void setY1(double y1) {
        this.y1 = y1;
    }

    /**
     * @return the x2
     */
    public double getX2() {
        return x2;
    }

    /**
     * @param x2 the x2 to set
     */
    public void setX2(double x2) {
        this.x2 = x2;
    }

    /**
     * @return the y2
     */
    public double getY2() {
        return y2;
    }

    /**
     * @param y2 the y2 to set
     */
    public void setY2(double y2) {
        this.y2 = y2;
    }

    /**
     * @return the columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return the borderLeft
     */
    public boolean isBorderLeft() {
        return borderLeft;
    }

    /**
     * @param borderLeft the borderLeft to set
     */
    public void setBorderLeft(boolean borderLeft) {
        this.borderLeft = borderLeft;
    }

    /**
     * @return the borderTop
     */
    public boolean isBorderTop() {
        return borderTop;
    }

    /**
     * @param borderTop the borderTop to set
     */
    public void setBorderTop(boolean borderTop) {
        this.borderTop = borderTop;
    }

    /**
     * @return the borderRight
     */
    public boolean isBorderRight() {
        return borderRight;
    }

    /**
     * @param borderRight the borderRight to set
     */
    public void setBorderRight(boolean borderRight) {
        this.borderRight = borderRight;
    }

    /**
     * @return the borderBottom
     */
    public boolean isBorderBottom() {
        return borderBottom;
    }

    /**
     * @param borderBottom the borderBottom to set
     */
    public void setBorderBottom(boolean borderBottom) {
        this.borderBottom = borderBottom;
    }

    public List<String> getGroupRefs() {
        return groupRefs;
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
    public ArrayList<String> getGrouprefs() {
        return groupRefs;
    }

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(layerValue)) {
            notifyListeners(LayerNumberProperty.Field.LAYER, layerValue.getOldValue(), layerValue.get());
        }
    }
}
