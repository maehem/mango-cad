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
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.property.RealValue;
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.enums.WireCap;
import com.maehem.mangocad.model.element.enums.WireEnd;
import com.maehem.mangocad.model.element.enums.WireStyle;
import com.maehem.mangocad.model.element.property.CurveProperty;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
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
public class Wire extends Element implements LayerNumberProperty, SelectableProperty, GrouprefsProperty, ElementValueListener {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public enum Field implements ElementField {
        X1("x1", Double.class), Y1("y1", Double.class),
        X2("x2", Double.class), Y2("y2", Double.class),
        END("selectedEnd", WireEnd.class),
        //WIDTH("width", Double.class),
        //LAYER("layer", Integer.class),
        EXTENT("extent", String.class),
        STYLE("style", WireStyle.class),
        //CURVE("curve", Double.class),
        CAP("cap", WireCap.class),
        GROUP_REF("groupRef", String.class);

        private final String fName;
        private final Class clazz;

        private Field(String name, Class clazz) {
            this.fName = name;
            this.clazz = clazz;
        }

        @Override
        public String fName() {
            return fName;
        }

        @Override
        public Class clazz() {
            return clazz;
        }

    }

    // TODO: No JavaFX in model package!!!!
//    public static final ObservableList<Double> WIDTH_DEFAULT_OPTIONS
//            = FXCollections.observableArrayList(
//                    -1.0,
//                    0.0,
//                    0.01,
//                    0.0125,
//                    0.025,
//                    0.03937008,
//                    0.05,
//                    0.10,
//                    0.5,
//                    1.0,
//                    2.0,
//                    5.0,
//                    10.0
//            );
    public static final String ELEMENT_NAME = "wire";
    public static final int DEFAULT_LAYER = 94; // Nets

    private int layer = DEFAULT_LAYER;
    public final RealValue x1Property = new RealValue(0);
    public final RealValue y1Property = new RealValue(0);
    public final RealValue x2Property = new RealValue(0);
    public final RealValue y2Property = new RealValue(0);
    private WireEnd selectedEnd = WireEnd.NONE;
    public final WidthProperty widthProperty = new WidthProperty(0.254);
    public final CurveProperty curveProperty = new CurveProperty(0.0);
    private String extent = "";  // TODO: Store as 'extent' object.
    private WireStyle style = WireStyle.CONTINUOUS;
    private WireCap cap = WireCap.ROUND;
    private Wire snapshot = null;

    private final ArrayList<String> grouprefs = new ArrayList<>();

    @SuppressWarnings("LeakingThisInConstructor")
    public Wire() {

        x1Property.addListener(this);
        y1Property.addListener(this);
        x2Property.addListener(this);
        y2Property.addListener(this);
        widthProperty.addListener(this);
        curveProperty.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x1
     */
    public double getX1() {
        return x1Property.get();
    }

    /**
     * @param x1 the x1 to set
     */
    public void setX1(double x1) {
        if (getX1() != x1) {
            double oldVal = getX1();
            x1Property.set(x1);
            notifyListeners(Wire.Field.X1, oldVal, getX1());
        }
    }

    /**
     * @return the y1
     */
    public double getY1() {
        return y1Property.get();
    }

    /**
     * @param y1 the y1 to set
     */
    public void setY1(double y1) {
        if (getY1() != y1) {
            double oldVal = getY1();
            y1Property.set(y1);
            notifyListeners(Wire.Field.Y1, oldVal, getY1());
        }
    }

    /**
     * @return the x2
     */
    public double getX2() {
        return x2Property.get();
    }

    /**
     * @param x2 the x2 to set
     */
    public void setX2(double x2) {
        if (getX2() != x2) {
            double oldVal = getX2();
            x2Property.set(x2);
            notifyListeners(Wire.Field.X2, oldVal, getX2());
        }
    }

    /**
     * @return the y2
     */
    public double getY2() {
        return y2Property.get();
    }

    /**
     * @param y2 the y2 to set
     */
    public void setY2(double y2) {
        if (getY2() != y2) {
            double oldVal = getY2();
            y2Property.set(y2);
            notifyListeners(Wire.Field.Y2, oldVal, getY2());
        }
    }

    public double getAverageX() {
        return (getX1() + getX2()) / 2.0;
    }

    public double getAverageY() {
        return (getY1() + getY2()) / 2.0;
    }

    public double getLength() {
        double ac = Math.abs(getY2() - getY1());
        double cb = Math.abs(getX2() - getX1());

        return Math.hypot(ac, cb);
    }

    /**
     * @return the width
     */
    //@Override
    public double getWidth() {
        return widthProperty.get();
    }

    /**
     * @param width the width to set
     */
    //@Override
    public void setWidth(double width) {
//        if (getWidth() != width) {
//            double oldVal = getWidth();
            widthProperty.set(width);
//            notifyListeners(WidthProperty.Field.WIDTH, oldVal, getWidth());
//        }
    }

    /**
     * @param curve the curve to set
     */
    public void setCurve(double curve) {
        if (curveProperty.get() != curve) {
            double oldVal = curveProperty.get();
            curveProperty.set(curve);
            notifyListeners(CurveProperty.Field.VALUE, oldVal, curveProperty.get());
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
            notifyListeners(Wire.Field.CAP, oldVal, this.cap);
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
            notifyListeners(Wire.Field.EXTENT, oldVal, this.extent);
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
            notifyListeners(Wire.Field.STYLE, oldVal, this.style);
        }
    }

    public double getRadius() {
        double radius;
        if (curveProperty.get() == 0.0) {
            radius = 10000.0; // Big number that makes it look straight.
        } else {
            radius = (getLength() * 0.5)
                    / Math.sin(Math.toRadians(curveProperty.get() * 0.5));
        }

        return radius;
    }

    /**
     * @return the grouprefs
     */
    @Override
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

    public void setSelectedEnd(WireEnd end) {
        if (!getSelectedEnd().equals(end)) {
            WireEnd oldVal = this.getSelectedEnd();
            this.selectedEnd = end;
            notifyListeners(Wire.Field.END, oldVal, this.selectedEnd);
        }
    }

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
            setCurve(snapshot.curveProperty.get());
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
        copyWire.setCurve(curveProperty.get());

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

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(x1Property)) {
            notifyListeners(Field.X1, x1Property.getOldValue(), x1Property.get());
        } else if (newVal.equals(y1Property)) {
            notifyListeners(Field.Y1, y1Property.getOldValue(), y1Property.get());
        } else if (newVal.equals(x2Property)) {
            notifyListeners(Field.X2, x2Property.getOldValue(), x2Property.get());
        } else if (newVal.equals(y2Property)) {
            notifyListeners(Field.Y2, y2Property.getOldValue(), y2Property.get());
        } else if (newVal.equals(curveProperty)) {
            notifyListeners(CurveProperty.Field.VALUE, curveProperty.getOldValue(), curveProperty.get());
        } else if (newVal.equals(widthProperty)) {
            notifyListeners(WidthProperty.Field.WIDTH, widthProperty.getOldValue(), widthProperty.get());
        }
    }

    /**
     * <pre>
     * <wire x1="-3.048" y1="3.81" x2="-3.048" y2="-3.81" width="0.6096" layer="94" curve="-60"/>
     * <wire x1="-3.048" y1="-3.81" x2="0" y2="-3.81" width="0.6096" layer="94"/>
     * <wire x1="1.016" y1="-16.51" x2="16.256" y2="-16.51" width="0.254" layer="97" style="shortdash"/>
     * </pre>
     *
     * @return
     */
    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<wire{0}{1}{2}{3}{4}{5}{6}/>");

        Object[] args = {
            " x1=\"" + x1Property.getPrecise(6) + "\"", // 0
            " y1=\"" + y1Property.getPrecise(6) + "\"", // 1
            " x2=\"" + x2Property.getPrecise(6) + "\"", // 2
            " y2=\"" + y2Property.getPrecise(6) + "\"", // 3
            " width=\"" + widthProperty.getPrecise(6) + "\"", // 4
            " layer=\"" + getLayerNum() + "\"", // 5
            curveProperty.get() != 0.0 ? " curve=\"" + curveProperty.getPrecise(6) + "\"" : "" // 6
        };

        return mf.format(args);
    }

}
