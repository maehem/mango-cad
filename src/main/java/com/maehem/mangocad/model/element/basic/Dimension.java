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
import com.maehem.mangocad.model.ElementValue;
import com.maehem.mangocad.model.ElementValueListener;
import com.maehem.mangocad.model.IntValue;
import com.maehem.mangocad.model.LockValue;
import com.maehem.mangocad.model.RealValue;
import com.maehem.mangocad.model.UnitValue;
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.enums.DimensionType;
import com.maehem.mangocad.model.element.enums.GridUnit;
import com.maehem.mangocad.model.element.property.GridUnitProperty;
import com.maehem.mangocad.model.element.property.GridUnitProperty.Unit;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.VisibleProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import java.text.MessageFormat;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Dimension extends Element implements
        LayerNumberProperty, GrouprefsProperty, WidthProperty, VisibleProperty,
        ElementValueListener {

    public static final String ELEMENT_NAME = "dimension";

    //  dimension
    //      ATTLIST
    //          x1            %Coord;        #REQUIRED
    //          y1            %Coord;        #REQUIRED
    //          x2            %Coord;        #REQUIRED
    //          y2            %Coord;        #REQUIRED
    //          x3            %Coord;        #REQUIRED
    //          y3            %Coord;        #REQUIRED
    //          locked        %Bool;         "no"     // Not in DTD
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
    public enum Field implements ElementField {
        X1("x1", Double.class), Y1("y1", Double.class),
        X2("x2", Double.class), Y2("y2", Double.class),
        X3("x3", Double.class), Y3("y3", Double.class),
        D_TYPE("dType", DimensionType.class),
        EXTWIDTH("extwidth", Double.class),
        EXTOFFSET("extoffset", Double.class),
        TEXTSIZE("textsize", RealValue.class),
        TEXTRATIO("textratio", IntValue.class),
        UNIT("unit", GridUnit.class),
        PRECISION("precision", IntValue.class);

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

    private int layer;
    public final RealValue x1Property = new RealValue(0);
    public final RealValue y1Property = new RealValue(0);
    public final RealValue x2Property = new RealValue(0);
    public final RealValue y2Property = new RealValue(0);
    public final RealValue x3Property = new RealValue(0); // Text placement.
    public final RealValue y3Property = new RealValue(0);
    public final LockValue lockProperty = new LockValue();
    private DimensionType dtype = DimensionType.PARALLEL;
    public final RealValue widthProperty = new RealValue(0.13, 0.0, 200.0);
    public final RealValue extwidthProperty = new RealValue(0, 0.0, 200.0);
    public final RealValue extlengthProperty = new RealValue(0, 0.0, 200.0);
    public final RealValue extoffsetProperty = new RealValue(0, 0.0, 200.0);
    public final RealValue textsizeProperty = new RealValue(2.54, 0.000003125, 200.0); // TODO. get from  a sample ElementText
    public final IntValue textratioProperty = new IntValue(8, 0, 31);
    public final UnitValue unitProperty = new UnitValue(GridUnitProperty.Unit.MM);
    public final IntValue precisionProperty = new IntValue(2, 0, 6);
    private boolean visible = false;
    private final ArrayList<String> grouprefs = new ArrayList<>();

    public static final ObservableList<Integer> PRECISION_OPTIONS
            = FXCollections.observableArrayList(
                    0, 1, 2, 3, 4, 5, 6
            );

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
            notifyListeners(Dimension.Field.X1, oldVal, getX1());
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
            notifyListeners(Dimension.Field.Y1, oldVal, getY1());
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
            notifyListeners(Dimension.Field.X2, oldVal, getX2());
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
            notifyListeners(Dimension.Field.Y2, oldVal, getY2());
        }
    }

    /**
     * @return the x3
     */
    public double getX3() {
        return x3Property.get();
    }

    /**
     * @param x3 the x3 to set
     */
    public void setX3(double x3) {
        if (getX3() != x3) {
            double oldVal = getX3();
            x3Property.set(x3);
            notifyListeners(Dimension.Field.X3, oldVal, getX3());
        }
    }

    /**
     * @return the y3
     */
    public double getY3() {
        return y3Property.get();
    }

    /**
     * @param y3 the y3 to set
     */
    public void setY3(double y3) {
        if (getY3() != y3) {
            double oldVal = getY3();
            y3Property.set(y3);
            notifyListeners(Dimension.Field.Y3, oldVal, getY3());
        }
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
     * @return the widthProperty
     */
    @Override
    public double getWidth() {
        return widthProperty.get();
    }

    /**
     * @param width the widthProperty to set
     */
    @Override
    public void setWidth(double width) {
        this.widthProperty.set(width);
    }

    /**
     * @return the extwidthProperty
     */
    public double getExtwidth() {
        return extwidthProperty.get();
    }

    /**
     * @param extwidth the extwidthProperty to set
     */
    public void setExtwidth(double extwidth) {
        this.extwidthProperty.set(extwidth);
    }

    /**
     * @return the extlengthProperty
     */
    public double getExtlength() {
        return extlengthProperty.get();
    }

    /**
     * @param extlength the extlengthProperty to set
     */
    public void setExtlength(double extlength) {
        this.extlengthProperty.set(extlength);
    }

    /**
     * @return the extoffsetProperty
     */
    public double getExtoffset() {
        return extoffsetProperty.get();
    }

    /**
     * @param extoffset the extoffsetProperty to set
     */
    public void setExtoffset(double extoffset) {
        this.extoffsetProperty.set(extoffset);
    }

    /**
     * @return the textsizeProperty
     */
    public double getTextsize() {
        return textsizeProperty.get();
    }

    /**
     * @param textsize the textsizeProperty to set
     */
    public void setTextsize(double textsize) {
        this.textsizeProperty.set(textsize);
    }

    /**
     * @return the textratioProperty
     */
    public int getTextratio() {
        return textratioProperty.get();
    }

    /**
     * @param textratio the textratioProperty to set
     */
    public void setTextratio(int textratio) {
        this.textratioProperty.set(textratio);
    }

    /**
     * @return the unitProperty
     */
    public Unit getUnit() {
        return unitProperty.get();
    }

    /**
     * @param unit the unitProperty to set
     */
    public void setUnit(Unit unit) {
        this.unitProperty.set(unit);
    }

    /**
     * @return the precisionProperty
     */
    public int getPrecision() {
        return precisionProperty.get();
    }

    /**
     * @param precision the precisionProperty to set
     */
    public void setPrecision(int precision) {
        this.precisionProperty.set(precision);
    }

    /**
     * @return the visible
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    @Override
    public void setVisible(boolean visible) {
        if (isVisible() != visible) {
            boolean oldVal = isVisible();
            this.visible = visible;
            notifyListeners(VisibleProperty.Field.VISIBLE, oldVal, isVisible());
        }
    }

    /**
     * @return the grouprefs
     */
    @Override
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
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
        } else if (newVal.equals(x3Property)) {
            notifyListeners(Field.X3, x3Property.getOldValue(), x3Property.get());
        } else if (newVal.equals(y2Property)) {
            notifyListeners(Field.Y3, y3Property.getOldValue(), y3Property.get());
        } else if (newVal.equals(widthProperty)) {
            notifyListeners(WidthProperty.Field.WIDTH, widthProperty.getOldValue(), widthProperty.get());
        } else if (newVal.equals(textsizeProperty)) {
            notifyListeners(Field.TEXTSIZE, textsizeProperty.getOldValue(), textsizeProperty.get());
        } else if (newVal.equals(textratioProperty)) {
            notifyListeners(Field.TEXTRATIO, textratioProperty.getOldValue(), textratioProperty.get());
        } else if (newVal.equals(precisionProperty)) {
            notifyListeners(Field.PRECISION, precisionProperty.getOldValue(), precisionProperty.get());
        }
    }

    /**
     * <pre>
     * <dimension x1="22.86" y1="-7.62" x2="48.26" y2="2.54" x3="35.56" y3="10.16" textsize="1.778" layer="94" dtype="horizontal"/>
     * <dimension x1="68.58" y1="10.16" x2="58.42" y2="2.54" x3="78.74" y3="6.35" textsize="1.778" layer="94" dtype="vertical"/>
     * <dimension x1="22.86" y1="-10.16" x2="48.26" y2="-20.32" x3="35.56" y3="-15.24" textsize="1.778" layer="94" dtype="horizontal"/>
     * <dimension x1="25.4" y1="17.78" x2="30.48" y2="20.32" x3="33.02" y3="20.32" textsize="1.778" layer="94" dtype="leader" extlength="127"/>
     * <dimension x1="38.1" y1="2.54" x2="63.5" y2="2.54" x3="50.8" y3="15.24" locked="yes" textsize="1.778" layer="94" extwidth="0.127" extlength="2.54" extoffset="1.27" unit="mil" precision="6" visible="yes"/>
     * </pre>
     *
     * @return
     */
    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<dimension{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}{12}{13}{14}{15}{16}/>");

        Object[] args = {
            " x1=\"" + x1Property.getPrecise(6) + "\"", // 0
            " y1=\"" + y1Property.getPrecise(6) + "\"", // 1
            " x2=\"" + x2Property.getPrecise(6) + "\"", // 2
            " y2=\"" + y2Property.getPrecise(6) + "\"", // 3
            " x3=\"" + x3Property.getPrecise(6) + "\"", // 4
            " y3=\"" + y3Property.getPrecise(6) + "\"", // 5
            lockProperty.xmlValue(), // 6
            " textsize=\"" + textsizeProperty.getPrecise(3) + "\"", // 7
            " layer=\"" + getLayerNum() + "\"", // 8
            !getDtype().equals(DimensionType.PARALLEL) ? " dtype=\"" + getDtype().code() + "\"" : "", // 16
            getTextratio() != 8 ? " textratio=\"" + getTextratio() + "\"" : "", // 9
            getExtwidth() != 0.0 ? " extwidth=\"" + extwidthProperty.getPrecise(6) + "\"" : "", // 10
            getExtlength() != 0.0 ? " extlength=\"" + extlengthProperty.getPrecise(6) + "\"" : "", // 11
            getExtoffset() != 0.0 ? " extoffset=\"" + extoffsetProperty.getPrecise(6) + "\"" : "", // 12
            !getUnit().equals(Unit.MM) ? " unit=\"" + getUnit().code() + "\"" : "", // 13
            getPrecision() != 2 ? " precision=\"" + getPrecision() + "\"" : "", // 14
            isVisible() ? " visible=\"yes\"" : "" // 15
        };

        return mf.format(args);
    }

}
