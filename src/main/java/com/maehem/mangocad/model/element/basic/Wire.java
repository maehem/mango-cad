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
import com.maehem.mangocad.model.element.enums.WireCap;
import com.maehem.mangocad.model.element.enums.WireStyle;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * <pre>
 * wire EMPTY
 *    ATTLIST wire
 *      x1            %Coord;        #REQUIRED
 *      y1            %Coord;        #REQUIRED
 *      x2            %Coord;        #REQUIRED
 *      y2            %Coord;        #REQUIRED
 *      width         %Dimension;    #REQUIRED
 *      layer         %Layer;        #REQUIRED
 *      extent        %Extent;       #IMPLIED
 *      style         %WireStyle;    "continuous"
 *      curve         %WireCurve;    "0"
 *      cap           %WireCap;      "round"
 *      grouprefs     IDREFS         #IMPLIED
 *
 *      extent: Only applicable for airwires -->
 *      cap   : Only applicable if 'curve' is not zero -->
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Wire extends Element {

    public static final String ELEMENT_NAME = "wire";
    public static final int DEFAULT_LAYER = 94; // Nets
    
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private double width;
    private String extent = "";  // TODO: Store as 'extent' object. 
    private WireStyle style = WireStyle.CONTINUOUS;
    private double curve = 0.0;
    private WireCap cap = WireCap.ROUND;

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
    
    public double getAverageX() {
        return (x1+x2)/2.0;
    }

    public double getAverageY() {
        return (y1+y2)/2.0;
    }

    /**
     * @param y2 the y2 to set
     */
    public void setY2(double y2) {
        this.y2 = y2;
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
     * @return the curve
     */
    public double getCurve() {
        return curve;
    }

    /**
     * @param curve the curve to set
     */
    public void setCurve(double curve) {
        this.curve = curve;
    }

    /**
     * @return the cap
     */
    public WireCap getCap() {
        return cap;
    }

    /**
     * @param cap the cap to set
     */
    public void setCap(WireCap cap) {
        this.cap = cap;
    }


    /**
     * @return the extent
     */
    public String getExtent() {
        return extent;
    }

    /**
     * @param extent the extent to set
     */
    public void setExtent(String extent) {
        this.extent = extent;
    }

    /**
     * @return the style
     */
    public WireStyle getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(WireStyle style) {
        this.style = style;
    }

    /**
     * @return the grouprefs
     */
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }
    
    @Override
    public String toString() {
        MessageFormat mf = new MessageFormat("wire: {0},{1} to {2},{3}  avg:{4},{5} layer:{6}");
        Object[] o = new Object[]{
            getX1(), getY1(), getX2(), getY2(),
            getAverageX(), getAverageY(),
            getLayerNum()};
        return mf.format(o);
    }
}
