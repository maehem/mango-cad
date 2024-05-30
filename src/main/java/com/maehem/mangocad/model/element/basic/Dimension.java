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
import com.maehem.mangocad.model.FieldWidth;
import com.maehem.mangocad.model.element.enums.DimensionType;
import com.maehem.mangocad.model.element.enums.GridUnit;
import java.util.ArrayList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Dimension extends Element implements FieldWidth {

    public static final String ELEMENT_NAME = "dimension";

    //  dimension
    //      ATTLIST
    //          x1            %Coord;        #REQUIRED
    //          y1            %Coord;        #REQUIRED
    //          x2            %Coord;        #REQUIRED
    //          y2            %Coord;        #REQUIRED
    //          x3            %Coord;        #REQUIRED
    //          y3            %Coord;        #REQUIRED
    //          layer         %Layer;        #REQUIRED
    //          dtype         %DimensionType; "parallel"
    //          width         %Dimension;    "0.13"
    //          extwidth      %Dimension;    "0"
    //          extlength     %Dimension;    "0"
    //          extoffset     %Dimension;    "0"
    //          textsize      %Dimension;    #REQUIRED
    //          textratio     %Int;          "8"
    //          unit          %GridUnit;     "mm"
    //          precision     %Int;          "2"
    //          visible       %Bool;         "no"
    //          grouprefs     IDREFS         #IMPLIED

    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private double x3;
    private double y3;
    private DimensionType dtype = DimensionType.PARALLEL;
    private double width = 0.13;
    private double extwidth = 0;
    private double extlength = 0;
    private double extoffset = 0;
    private double textsize = 10;
    private int textratio = 8;
    private GridUnit unit = GridUnit.MM;
    private int precision = 2;
    private boolean visible = false;
    private final ArrayList<String> grouprefs = new ArrayList<>();


    @Override
    public String getElementName() {
        return ELEMENT_NAME;
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
     * @return the x3
     */
    public double getX3() {
        return x3;
    }

    /**
     * @param x3 the x3 to set
     */
    public void setX3(double x3) {
        this.x3 = x3;
    }

    /**
     * @return the y3
     */
    public double getY3() {
        return y3;
    }

    /**
     * @param y3 the y3 to set
     */
    public void setY3(double y3) {
        this.y3 = y3;
    }

    /**
     * @return the dtype
     */
    public DimensionType getDtype() {
        return dtype;
    }

    /**
     * @param dtype the dtype to set
     */
    public void setDtype(DimensionType dtype) {
        this.dtype = dtype;
    }

    /**
     * @return the width
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    @Override
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * @return the extwidth
     */
    public double getExtwidth() {
        return extwidth;
    }

    /**
     * @param extwidth the extwidth to set
     */
    public void setExtwidth(double extwidth) {
        this.extwidth = extwidth;
    }

    /**
     * @return the extlength
     */
    public double getExtlength() {
        return extlength;
    }

    /**
     * @param extlength the extlength to set
     */
    public void setExtlength(double extlength) {
        this.extlength = extlength;
    }

    /**
     * @return the extoffset
     */
    public double getExtoffset() {
        return extoffset;
    }

    /**
     * @param extoffset the extoffset to set
     */
    public void setExtoffset(double extoffset) {
        this.extoffset = extoffset;
    }

    /**
     * @return the textsize
     */
    public double getTextsize() {
        return textsize;
    }

    /**
     * @param textsize the textsize to set
     */
    public void setTextsize(double textsize) {
        this.textsize = textsize;
    }

    /**
     * @return the textratio
     */
    public int getTextratio() {
        return textratio;
    }

    /**
     * @param textratio the textratio to set
     */
    public void setTextratio(int textratio) {
        this.textratio = textratio;
    }

    /**
     * @return the unit
     */
    public GridUnit getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(GridUnit unit) {
        this.unit = unit;
    }

    /**
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the grouprefs
     */
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }

}
