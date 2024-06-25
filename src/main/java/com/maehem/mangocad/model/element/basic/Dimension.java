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
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.enums.DimensionType;
import com.maehem.mangocad.model.element.enums.GridUnit;
import com.maehem.mangocad.model.element.property.CoordinateValue;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.IntValue;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LockProperty;
import com.maehem.mangocad.model.element.property.LockValue;
import com.maehem.mangocad.model.element.property.RealValue;
import com.maehem.mangocad.model.element.property.UnitProperty;
import com.maehem.mangocad.model.element.property.UnitValue;
import com.maehem.mangocad.model.element.property.VisibleProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import com.maehem.mangocad.model.element.property.WidthValue;
import java.text.MessageFormat;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Dimension extends Element implements
        LayerNumberProperty, GrouprefsProperty, VisibleProperty,
        WidthProperty, UnitProperty, LockProperty,
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
        EXTLENGTH("extlength", Double.class),
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
    public final CoordinateValue coord1 = new CoordinateValue();
    public final CoordinateValue coord2 = new CoordinateValue();
    public final CoordinateValue coord3 = new CoordinateValue();
    public final LockValue lockProperty = new LockValue();
    private DimensionType dtype = DimensionType.PARALLEL;
    public final WidthValue widthProperty = new WidthValue(0.13);
    public final RealValue extwidthProperty = new RealValue(0, 0.0, 200.0);
    public final RealValue extlengthProperty = new RealValue(0, 0.0, 200.0);
    public final RealValue extoffsetProperty = new RealValue(0, 0.0, 200.0);
    public final RealValue textsizeProperty = new RealValue(2.54, 0.000003125, 200.0); // TODO. get from  a sample ElementText
    public final IntValue textratioProperty = new IntValue(8, 0, 31);
    public final UnitValue unitProperty = new UnitValue(UnitValue.Unit.MM);
    public final IntValue precisionProperty = new IntValue(2, 0, 6);
    private boolean visible = false;
    private final ArrayList<String> grouprefs = new ArrayList<>();

    public static final ObservableList<Integer> PRECISION_OPTIONS
            = FXCollections.observableArrayList(
                    0, 1, 2, 3, 4, 5, 6
            );

    @SuppressWarnings("LeakingThisInConstructor")
    public Dimension() {

        coord1.addListener(this);
        coord2.addListener(this);
        coord3.addListener(this);
        lockProperty.addListener(this);
        widthProperty.addListener(this);
        extwidthProperty.addListener(this);
        extlengthProperty.addListener(this);
        extoffsetProperty.addListener(this);
        textratioProperty.addListener(this);
        unitProperty.addListener(this);
        precisionProperty.addListener(this);

    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public WidthValue getWidthProperty() {
        return widthProperty;
    }

    @Override
    public UnitValue getUnitProperty() {
        return unitProperty;
    }

    @Override
    public LockValue getLockProperty() {
        return lockProperty;
    }

    /**
     * @return the x1
     */
    public double getX1() {
        return coord1.x.get();
    }

    /**
     * @param val the x1 to set
     */
    public void setX1(double val) {
        coord1.x.set(val);
    }

    /**
     * @return the y1
     */
    public double getY1() {
        return coord1.y.get();
    }

    /**
     * @param val the y1 to set
     */
    public void setY1(double val) {
        coord1.y.set(val);
    }

    /**
     * @return the x2
     */
    public double getX2() {
        return coord2.x.get();
    }

    /**
     * @param val the x2 to set
     */
    public void setX2(double val) {
        coord2.x.set(val);
    }

    /**
     * @return the y2
     */
    public double getY2() {
        return coord2.y.get();
    }

    /**
     * @param val the y2 to set
     */
    public void setY2(double val) {
        coord2.y.set(val);
    }

    /**
     * @return the x3
     */
    public double getX3() {
        return coord3.x.get();
    }

    /**
     * @param val the x3 to set
     */
    public void setX3(double val) {
        coord3.x.set(val);
    }

    /**
     * @return the y3
     */
    public double getY3() {
        return coord3.y.get();
    }

    /**
     * @param val the y3 to set
     */
    public void setY3(double val) {
        coord3.y.set(val);
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
    public double getWidth() {
        return widthProperty.get();
    }

    /**
     * @param width the widthProperty to set
     */
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
    public UnitValue.Unit getUnit() {
        return unitProperty.get();
    }

    /**
     * @param unit the unitProperty to set
     */
    public void setUnit(UnitValue.Unit unit) {
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
        if (newVal.equals(coord1.x)) {
            notifyListeners(Field.X1, coord1.x.getOldValue(), coord1.x.get());
        } else if (newVal.equals(coord1.y)) {
            notifyListeners(Field.Y1, coord1.y.getOldValue(), coord1.y.get());
        } else if (newVal.equals(coord2.x)) {
            notifyListeners(Field.X2, coord2.x.getOldValue(), coord2.x.get());
        } else if (newVal.equals(coord2.y)) {
            notifyListeners(Field.Y2, coord2.y.getOldValue(), coord2.y.get());
        } else if (newVal.equals(coord3.x)) {
            notifyListeners(Field.X3, coord3.x.getOldValue(), coord3.x.get());
        } else if (newVal.equals(coord3.y)) {
            notifyListeners(Field.Y3, coord3.y.getOldValue(), coord3.y.get());
        } else if (newVal.equals(widthProperty)) {
            notifyListeners(WidthValue.Field.WIDTH, widthProperty.getOldValue(), widthProperty.get());
        } else if (newVal.equals(extwidthProperty)) {
            notifyListeners(Field.EXTWIDTH, extwidthProperty.getOldValue(), extwidthProperty.get());
        } else if (newVal.equals(extlengthProperty)) {
            notifyListeners(Field.EXTLENGTH, extlengthProperty.getOldValue(), extlengthProperty.get());
        } else if (newVal.equals(extoffsetProperty)) {
            notifyListeners(Field.EXTOFFSET, extoffsetProperty.getOldValue(), extoffsetProperty.get());
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
            " x1=\"" + coord1.x.getPrecise(6) + "\"", // 0
            " y1=\"" + coord1.y.getPrecise(6) + "\"", // 1
            " x2=\"" + coord2.x.getPrecise(6) + "\"", // 2
            " y2=\"" + coord2.y.getPrecise(6) + "\"", // 3
            " x3=\"" + coord3.x.getPrecise(6) + "\"", // 4
            " y3=\"" + coord3.y.getPrecise(6) + "\"", // 5
            lockProperty.xmlValue(), // 6
            " textsize=\"" + textsizeProperty.getPrecise(3) + "\"", // 7
            " layer=\"" + getLayerNum() + "\"", // 8
            !getDtype().equals(DimensionType.PARALLEL) ? " dtype=\"" + getDtype().code() + "\"" : "", // 16
            getTextratio() != 8 ? " textratio=\"" + getTextratio() + "\"" : "", // 9
            getExtwidth() != 0.0 ? " extwidth=\"" + extwidthProperty.getPrecise(6) + "\"" : "", // 10
            getExtlength() != 0.0 ? " extlength=\"" + extlengthProperty.getPrecise(6) + "\"" : "", // 11
            getExtoffset() != 0.0 ? " extoffset=\"" + extoffsetProperty.getPrecise(6) + "\"" : "", // 12
            !getUnit().equals(UnitValue.Unit.MM) ? " unit=\"" + getUnit().code() + "\"" : "", // 13
            getPrecision() != 2 ? " precision=\"" + getPrecision() + "\"" : "", // 14
            isVisible() ? " visible=\"yes\"" : "" // 15
        };

        return mf.format(args);
    }

}
