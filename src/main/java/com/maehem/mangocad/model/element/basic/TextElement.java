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
import com.maehem.mangocad.model.element.enums.TextAlign;
import com.maehem.mangocad.model.element.enums.TextFont;
import com.maehem.mangocad.model.element.property.CoordinateProperty;
import com.maehem.mangocad.model.element.property.CoordinateValue;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.IntValue;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LayerNumberValue;
import com.maehem.mangocad.model.element.property.LockValue;
import com.maehem.mangocad.model.element.property.RealValue;
import com.maehem.mangocad.model.element.property.Rotation;
import com.maehem.mangocad.model.element.property.RotationProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.StringValue;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * <pre>
 * text (#PCDATA)>
 *    ATTLIST text
 *      x             %Coord;        #REQUIRED
 *      y             %Coord;        #REQUIRED
 *      size          %Dimension;    #REQUIRED
 *      layer         %Layer;        #REQUIRED
 *      font          %TextFont;     "proportional"
 *      ratio         %Int;          "8"
 *      rot           %Rotation;     "R0"
 *      align         %Align;        "bottom-left"
 *      distance      %Int;          "50"
 *      grouprefs     IDREFS         #IMPLIED
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class TextElement extends Element
        implements
        LayerNumberProperty, CoordinateProperty, RotationProperty,
        SelectableProperty, GrouprefsProperty, ElementValueListener {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static final String ELEMENT_NAME = "text";

    public enum Field implements ElementField {
        //X("x", Double.class), Y("y", Double.class),
        //SELECTED("selected", Boolean.class),
        SIZE("size", Double.class),
        LAYER("layer", Integer.class),
        FONT("font", String.class),
        RATIO("ratio", Integer.class),
        //ROTATION("rotation", String.class),
        ALIGN("align", TextAlign.class),
        DISTANCE("distance", Integer.class),
        VALUE("value", String.class);

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

    public static final List<Double> SIZE_DEFAULT_OPTIONS
            = Arrays.asList(
                    -1.0,
                    1.0,
                    1.25,
                    2.5,
                    2.54,
                    3.0,
                    3.937008,
                    5.0,
                    5.08,
                    10.0
            );
    public static final ObservableList<Integer> RATIO_DEFAULT_OPTIONS
            = FXCollections.observableArrayList( // TODO: No JavaFX in model package!
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                    30, 31
            );

    //private int layer;
    public final LayerNumberValue layerValue = new LayerNumberValue(1);
    //public final RealValue xProperty = new RealValue(0);
    //public final RealValue yProperty = new RealValue(0);
    public final CoordinateValue coordinate = new CoordinateValue();
    public final LockValue lockProperty = new LockValue();
    public final RealValue sizeProperty = new RealValue(1.778, 0.000001, 100000); // 0.7 inch
    private boolean selected = false;
    private boolean picked = false;
    private TextElement snapshot = null;

    private TextFont font = TextFont.PROPORTIONAL;
    public final IntValue ratioProperty = new IntValue(8, 0, 31);
    public final Rotation rotation = new Rotation();
    private TextAlign align = TextAlign.BOTTOM_LEFT;
    private int distance = 50;  // Line to line distance

    public final StringValue valueProperty = new StringValue();  // #PCDATA Content

    private final ArrayList<String> grouprefs = new ArrayList<>();

    @SuppressWarnings("LeakingThisInConstructor")
    public TextElement() {
        rotation.setAllowMirror(true);
        rotation.setAllowSpin(true);

        layerValue.addListener(this);
        coordinate.addListener(this);
        rotation.addListener(this);
        sizeProperty.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public LayerNumberValue getLayerNumberProperty() {
        return layerValue;
    }

    @Override
    public CoordinateValue getCoordinateProperty() {
        return coordinate;
    }

    @Override
    public Rotation getRotationProperty() {
        return rotation;
    }

    /**
     * @return the x
     */
    public double getX() {
        return coordinate.x.get();
    }

    /**
     * @param val the x to set
     */
    public void setX(double val) {
        coordinate.x.set(val);
    }

    /**
     * @return the y
     */
    public double getY() {
        return coordinate.y.get();
    }

    /**
     * @param val the y to set
     */
    public void setY(double val) {
        coordinate.y.set(val);
    }

    /**
     * @return the size
     */
    public double getSize() {
        return sizeProperty.get();
    }

    /**
     * @param size the size to set
     */
    public void setSize(double size) {
        if (getSize() != size) {
            double oldValue = getSize();
            sizeProperty.set(size);
            notifyListeners(TextElement.Field.SIZE, oldValue, getSize());
        }
    }

    /**
     * @return the distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(int distance) {
        if (getDistance() != distance) {
            double oldValue = this.distance;
            this.distance = distance;
            notifyListeners(TextElement.Field.DISTANCE, oldValue, this.distance);
        }
    }

    /**
     * @return the ratio
     */
    public int getRatio() {
        return ratioProperty.get();
    }

    /**
     * @param ratio the ratio to set
     *
     * Maximum value is 31.
     */
    public void setRatio(int ratio) {
        if (getRatio() != ratio) {
            double oldValue = getRatio();
            ratioProperty.set(ratio);
            notifyListeners(TextElement.Field.RATIO, oldValue, getRatio());
        }
    }

    public double getDerivedStroke() {
        //return getRatio() * 0.01 * getSize() * 0.5;
        return getRatio() * 0.01 * getSize(); // From a percentange Integer value (0..100) --> (0.0..1.0)
    }

    /**
     * Copy the values of source rotation into our rotation.
     *
     * @param rotation
     */
    public void setRotation(Rotation r) {
        //Rotation oldValue = this.getRotation();
        Rotation.copyValues(r, rotation);
        notifyListeners(RotationProperty.Field.VALUE, null, rotation);
    }

    /**
     * @return the align
     */
    public TextAlign getAlign() {
        return align;
    }

    /**
     * @param align the align to set
     */
    public void setAlign(TextAlign align) {
        if (!this.align.equals(align)) {
            TextAlign oldValue = this.align;
            if (align != null) {
                this.align = align;
            }
            notifyListeners(TextElement.Field.ALIGN, oldValue, this.align);
        }
    }

    public void setAlign(String val) {
        TextAlign ta = TextAlign.fromCode(val);
        if (ta == null) {
            LOGGER.log(Level.SEVERE, "TextAlign: tried to set an alignment called \"{0}\"", val);
        } else {
            setAlign(ta);
        }
    }

    /**
     * @return the value
     */
    public String getValue() {
        return valueProperty.get();
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        if (!getValue().equals(value)) {
            String oldValue = getValue();
            valueProperty.set(value);
            notifyListeners(TextElement.Field.VALUE, oldValue, getValue());
        }
    }

    /**
     * @return the font
     */
    public TextFont getFont() {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(TextFont font) {
        if (!this.font.equals(font)) {
            TextFont oldValue = this.font;
            this.font = font;
            notifyListeners(TextElement.Field.FONT, oldValue, this.font);
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
    public void createSnapshot() {
        snapshot = copy();
    }

    @Override
    public void restoreSnapshot() {
        if (snapshot != null) {
            setX(snapshot.getX());
            setY(snapshot.getY());
            setAlign(snapshot.getAlign());
            rotation.setAllowMirror(snapshot.rotation.isMirrorAllowed());
            rotation.setAllowSpin(snapshot.rotation.isSpinAllowed());
            rotation.setConstrained(snapshot.rotation.isConstrained());
            setDistance(snapshot.getDistance());
            setFont(snapshot.getFont());
            setLayerNum(snapshot.getLayerNum());
            rotation.setMirror(snapshot.rotation.isMirror());
            setRatio(snapshot.getRatio());
            rotation.set(snapshot.rotation.get());
            setSize(snapshot.getSize());
            rotation.setSpin(snapshot.rotation.isSpin());
            setValue(snapshot.getValue());

            snapshot = null;
        } else {
            LOGGER.log(Level.SEVERE, "Tried to restore a NULL snapshot!");
        }
    }

    @Override
    public TextElement getSnapshot() {
        return snapshot;
    }

    public TextElement copy() {
        TextElement copy = new TextElement();

        copy.setX(getX());
        copy.setY(getY());
        copy.setAlign(align);
        copy.rotation.setAllowMirror(rotation.isMirrorAllowed());
        copy.rotation.setAllowSpin(rotation.isSpinAllowed());
        copy.rotation.setConstrained(rotation.isConstrained());
        copy.setDistance(distance);
        copy.setFont(font);
        copy.setLayerNum(getLayerNum());
        copy.rotation.setMirror(rotation.isMirror());
        copy.setRatio(getRatio());
        copy.rotation.set(rotation.get());
        copy.setSize(getSize());
        copy.rotation.setSpin(rotation.isSpin());
        copy.setValue(getValue());

        // Copy GroupRefs
        for (String ref : grouprefs) {
            copy.grouprefs.add(ref);
        }
        Rotation.copyValues(rotation, copy.rotation);

        return copy;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            boolean oldValue = this.selected;
            this.selected = selected;
            notifyListeners(SelectableProperty.Field.SELECTED, oldValue, this.selected);
        }
    }

    @Override
    public boolean isPicked() {
        return picked;
    }

    @Override
    public void setPicked(boolean picked) {
        if (this.picked != picked) {
            boolean oldValue = this.picked;
            this.picked = picked;
            notifyListeners(SelectableProperty.Field.PICKED, oldValue, this.picked);
        }
    }

    @Override
    public void modify(double xDist, double yDist, boolean ephemeral) {
        //LOGGER.log(Level.SEVERE, "Move elementXY.");
        //Element snapshot = es.getSnapshot();
        if (getSnapshot() instanceof CoordinateProperty snapXY) {
            //LOGGER.log(Level.SEVERE, "    Move relative to snapXY.");
            getCoordinateProperty().setX(snapXY.getCoordinateProperty().getX() + xDist);
            getCoordinateProperty().setY(snapXY.getCoordinateProperty().getY() + yDist);
        }
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
        } else if (newVal.equals(coordinate.x)) {
            notifyListeners(CoordinateProperty.Field.X, coordinate.x.getOldValue(), coordinate.x.get());
        } else if (newVal.equals(coordinate.y)) {
            notifyListeners(CoordinateProperty.Field.Y, coordinate.y.getOldValue(), coordinate.y.get());
        } else if (newVal.equals(sizeProperty)) {
            notifyListeners(Field.SIZE, sizeProperty.getOldValue(), sizeProperty.get());
        } else if (newVal.equals(rotation)) {
            notifyListeners(RotationProperty.Field.VALUE, rotation.getOldValue(), rotation.get());
        }

    }

    /**
     * <pre>
     * <text x="1.27" y="0" size="1.778" layer= "95" align="center"> & gt;NAME</text>
     * <text x="-1.27" y="-5.08" size="0.8128" layer= "96" align="center-left"> & gt;VALUE</text>
     * <text x="33.02" y="-2.54" locked="yes" size="1.778" layer="94" ratio="16" distance="55">HELLO</text>
     * <text x="33.02" y="-2.54" locked="yes" size="1.778" layer="94" ratio="16" distance="55" align="center">HELLO</text>
     *
     * </pre>
     *
     * @return
     */
    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<text{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}>{10}</text>");
        String rotStrValue = rotation.xmlValue();

        Object[] args = {
            " x=\"" + coordinate.x.getPrecise(6) + "\"", // 0
            " y=\"" + coordinate.y.getPrecise(6) + "\"", // 1
            lockProperty.xmlValue(), // 2
            " size=\"" + sizeProperty.getPrecise(6) + "\"", // 3
            " layer=\"" + getLayerNum() + "\"", // 4
            getRatio() != 8 ? " ratio=\"" + getRatio() + "\"" : "", // 5
            getDistance() != 50 ? " distance=\"" + getDistance() + "\"" : "", // 6
            !getAlign().equals(TextAlign.BOTTOM_LEFT) ? " align=\"" + getAlign().code() + "\"" : "", // 7
            !getFont().equals(TextFont.PROPORTIONAL) ? " font=\"" + getFont().code() + "\"" : "", // 8
            !rotStrValue.equals("R0") ? " rot=\"" + rotStrValue + "\"" : "", // 9
            // TODO Group Refs
            escapeHTML(getValue()) // 10  // TODO Format >  &  < and other XML symbols!
        };

        return mf.format(args);
    }
}
