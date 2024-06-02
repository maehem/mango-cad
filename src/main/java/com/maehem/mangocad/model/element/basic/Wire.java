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
import com.maehem.mangocad.model.element.enums.WireEnd;
import com.maehem.mangocad.model.element.enums.WireField;
import com.maehem.mangocad.model.element.enums.WireStyle;
import com.maehem.mangocad.model.element.property.CurveProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LocationDualXYProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class Wire extends Element implements LayerNumberProperty, LocationDualXYProperty, SelectableProperty, WidthProperty, CurveProperty {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static final String ELEMENT_NAME = "wire";
    public static final int DEFAULT_LAYER = 94; // Nets

    private int layer = DEFAULT_LAYER;
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private WireEnd selectedEnd = WireEnd.NONE;
    private double width = 0.254;
    private double curve = 0.0;
    private String extent = "";  // TODO: Store as 'extent' object.
    private WireStyle style = WireStyle.CONTINUOUS;
    private WireCap cap = WireCap.ROUND;
    private Wire snapshot = null;

    private final ArrayList<String> grouprefs = new ArrayList<>();

    public Wire() {
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x1
     */
    @Override
    public double getX1() {
        return x1;
    }

    /**
     * @param x1 the x1 to set
     */
    @Override
    public void setX1(double x1) {
        if (this.x1 != x1) {
            double oldVal = this.x1;
            this.x1 = x1;
            notifyListeners(WireField.X1, oldVal, this.x1);
        }
    }

    /**
     * @return the y1
     */
    @Override
    public double getY1() {
        return y1;
    }

    /**
     * @param y1 the y1 to set
     */
    @Override
    public void setY1(double y1) {
        if (this.y1 != y1) {
            double oldVal = this.y1;
            this.y1 = y1;
            notifyListeners(WireField.Y1, oldVal, this.y1);
        }
    }

    /**
     * @return the x2
     */
    @Override
    public double getX2() {
        return x2;
    }

    /**
     * @param x2 the x2 to set
     */
    @Override
    public void setX2(double x2) {
        if (this.x2 != x2) {
            double oldVal = this.x2;
            this.x2 = x2;
            notifyListeners(WireField.X2, oldVal, this.x2);
        }
    }

    /**
     * @return the y2
     */
    @Override
    public double getY2() {
        return y2;
    }

    /**
     * @param y2 the y2 to set
     */
    @Override
    public void setY2(double y2) {
        if (this.y2 != y2) {
            double oldVal = this.y2;
            this.y2 = y2;
            notifyListeners(WireField.Y2, oldVal, this.y2);
        }
    }

    public double getAverageX() {
        return (x1 + x2) / 2.0;
    }

    public double getAverageY() {
        return (y1 + y2) / 2.0;
    }

    public double getLength() {
        double ac = Math.abs(y2 - y1);
        double cb = Math.abs(x2 - x1);

        return Math.hypot(ac, cb);
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
        if (getWidth() != width) {
            double oldVal = this.width;
            this.width = width;
            notifyListeners(WidthProperty.Field.WIDTH, oldVal, this.width);
        }
    }

    /**
     * @return the curve
     */
    @Override
    public double getCurve() {
        return curve;
    }

    /**
     * @param curve the curve to set
     */
    @Override
    public void setCurve(double curve) {
        if (getCurve() != curve) {
            double oldVal = this.curve;
            this.curve = curve;
            notifyListeners(CurveProperty.Field.CURVE, oldVal, this.curve);
        }
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
        if (getCap() != cap) {
            WireCap oldVal = this.cap;
            this.cap = cap;
            notifyListeners(WireField.CAP, oldVal, this.cap);
        }
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
        if (!getExtent().equals(extent)) {
            String oldVal = this.extent;
            this.extent = extent;
            notifyListeners(WireField.EXTENT, oldVal, this.extent);
        }
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
        if (!getStyle().equals(style)) {
            WireStyle oldVal = this.style;
            this.style = style;
            notifyListeners(WireField.STYLE, oldVal, this.style);
        }
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

    @Override
    public void setSelectedEnd(WireEnd end) {
        if (!getSelectedEnd().equals(end)) {
            WireEnd oldVal = this.getSelectedEnd();
            this.selectedEnd = end;
            notifyListeners(WireField.END, oldVal, this.selectedEnd);
        }
    }

    @Override
    public WireEnd getSelectedEnd() {
        return selectedEnd;
    }

    @Override
    public void createSnapshot() {
        snapshot = copy();
    }

    @Override
    public void restoreSnapshot() {
        if (snapshot != null) {
            setX1(snapshot.getX1());
            setY1(snapshot.getY1());
            setX2(snapshot.getX2());
            setY2(snapshot.getY2());
            setLayerNum(snapshot.getLayerNum());
            setWidth(snapshot.getWidth());
            setStyle(snapshot.getStyle());
            setCurve(snapshot.getCurve());
            setCap(snapshot.getCap());

            snapshot = null;
        } else {
            LOGGER.log(Level.SEVERE, "Wire: restore, snapshot was null.");
        }
    }

    @Override
    public Element getSnapshot() {
        return snapshot;
    }

    @Override
    public boolean isSelected() {
        return selectedEnd != WireEnd.NONE;
    }

    @Override
    public void setSelected(boolean selected) {
        LOGGER.log(Level.SEVERE, "Wire.setSelected() mis-used.  Use setSelectedEnd() instead!");
    }

    public Wire copy() {
        Wire copyWire = new Wire();

        copyWire.setX1(getX1());
        copyWire.setY1(getY1());
        copyWire.setX2(getX2());
        copyWire.setY2(getY2());
        copyWire.setLayerNum(getLayerNum());
        copyWire.setWidth(getWidth());
        copyWire.setStyle(getStyle());
        copyWire.setCurve(getCurve());

        return copyWire;
    }

    @Override
    public int getLayerNum() {
        return layer;
    }

    @Override
    public void setLayerNum(int layer) {
        if (this.layer != layer) {
            int oldVal = this.layer;
            this.layer = layer;
            notifyListeners(LayerNumberProperty.Field.LAYER, oldVal, this.layer);
        }
    }

}
