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
import com.maehem.mangocad.model.ElementValueListener;
import com.maehem.mangocad.model.element.property.IntValue;
import com.maehem.mangocad.model.element.property.LockValue;
import com.maehem.mangocad.model.element.property.RealValue;
import com.maehem.mangocad.model.element.property.StringValue;
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.enums.TextAlign;
import com.maehem.mangocad.model.element.enums.TextFont;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LocationXYProperty;
import com.maehem.mangocad.model.element.property.RotationProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.util.Rotation;
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
public class ElementText extends Element
        implements
        LayerNumberProperty, LocationXYProperty, RotationProperty,
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

    private int layer;
    public final RealValue xProperty = new RealValue(0);
    public final RealValue yProperty = new RealValue(0);
    public final LockValue lockProperty = new LockValue();
    public final RealValue sizeProperty = new RealValue(1.778, 0.000001, 100000); // 0.7 inch
    private boolean selected = false;
    private ElementText snapshot = null;

    private TextFont font = TextFont.PROPORTIONAL;
    public final IntValue ratioProperty = new IntValue(8, 0, 31);
    private final Rotation rotation = new Rotation();
    private TextAlign align = TextAlign.BOTTOM_LEFT;
    private int distance = 50;  // Line to line distance

    public final StringValue valueProperty = new StringValue();  // #PCDATA Content

    private final ArrayList<String> grouprefs = new ArrayList<>();

    @SuppressWarnings("LeakingThisInConstructor")
    public ElementText() {
        rotation.setAllowMirror(true);
        rotation.setAllowSpin(true);

        xProperty.addListener(this);
        yProperty.addListener(this);
        sizeProperty.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x
     */
    @Override
    public double getX() {
        return xProperty.get();
    }

    /**
     * @param x the x to set
     */
    @Override
    public void setX(double x) {
        if (getX() != x) {
            double oldValue = getX();
            xProperty.set(x);
            notifyListeners(LocationXYProperty.Field.X, oldValue, getX());
        }
    }

    /**
     * @return the y
     */
    @Override
    public double getY() {
        return yProperty.get();
    }

    /**
     * @param y the y to set
     */
    @Override
    public void setY(double y) {
        if (getY() != y) {
            double oldValue = getY();
            yProperty.set(y);
            notifyListeners(LocationXYProperty.Field.Y, oldValue, getY());
        }
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
            notifyListeners(ElementText.Field.SIZE, oldValue, getSize());
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
            notifyListeners(ElementText.Field.DISTANCE, oldValue, this.distance);
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
            notifyListeners(ElementText.Field.RATIO, oldValue, getRatio());
        }
    }

    public double getDerivedStroke() {
        //return getRatio() * 0.01 * getSize() * 0.5;
        return getRatio() * 0.01 * getSize(); // From a percentange Integer value (0..100) --> (0.0..1.0)
    }

    /**
     * @return the rotation
     */
    @Override
    public Rotation getRotationProperty() {
        return rotation;
    }

    /**
     * Copy the values of source rotation into our rotation.
     *
     * @param rotation
     */
    public void setRotation(Rotation r) {
        //Rotation oldValue = this.getRotation();
        Rotation.copyValues(r, this.getRotationProperty());
        notifyListeners(RotationProperty.Field.VALUE, null, this.getRotationProperty());
    }

    @Override
    public double getRot() {
        return rotation.get();
    }

    @Override
    public void setRot(double rot) {
        if (getRot() != rot) {
            double oldValue = this.getRot();
            getRotationProperty().set(rot);
            notifyListeners(RotationProperty.Field.VALUE, oldValue, this.getRot());
        }
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
            notifyListeners(ElementText.Field.ALIGN, oldValue, this.align);
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
            notifyListeners(ElementText.Field.VALUE, oldValue, getValue());
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
            notifyListeners(ElementText.Field.FONT, oldValue, this.font);
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
    public boolean isSpun() {
        return rotation.isSpun();
    }

    @Override
    public void setSpin(boolean spin) {
        if (this.isSpin() != spin) {
            boolean oldValue = isSpin();
            rotation.setSpin(spin);
            notifyListeners(RotationProperty.Field.SPIN, oldValue, this.isSpin());
        }
    }

    @Override
    public boolean isSpin() {
        return rotation.isSpin();
    }

    @Override
    public void setAllowSpin(boolean allowSpin) {
        if (this.isSpinAllowed() != allowSpin) {
            boolean oldValue = isSpinAllowed();
            rotation.setAllowSpin(allowSpin);
            notifyListeners(RotationProperty.Field.ALLOW_SPIN, oldValue, this.isSpinAllowed());
        }
    }

    @Override
    public boolean isSpinAllowed() {
        return rotation.isSpinAllowed();
    }

    @Override
    public boolean isConstrained() {
        return getRotationProperty().isConstrained();
    }

    @Override
    public void setConstrained(boolean value) {
        if (this.isConstrained() != value) {
            boolean oldValue = this.isConstrained();
            rotation.setConstrained(value);
            notifyListeners(RotationProperty.Field.CONSTRAINED, oldValue, this.isConstrained());
        }
    }

    @Override
    public void setMirror(boolean value) {
        if (this.isMirrored() != value) {
            boolean oldValue = this.isMirrored();
            rotation.setMirror(value);
            notifyListeners(RotationProperty.Field.MIRROR, oldValue, this.isMirrored());
        }
    }

    @Override
    public boolean isMirrored() {
        return getRotationProperty().isMirror();
    }

    @Override
    public boolean isMirrorAllowed() {
        return getRotationProperty().isMirrorAllowed();
    }

    @Override
    public void setAllowMirror(boolean value) {
        if (this.isMirrorAllowed() != value) {
            boolean oldValue = this.isMirrorAllowed();
            rotation.setAllowMirror(value);
            notifyListeners(RotationProperty.Field.ALLOW_MIRROR, oldValue, this.isMirrorAllowed());
        }
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
            setAllowMirror(snapshot.isMirrorAllowed());
            setAllowSpin(snapshot.isSpinAllowed());
            setConstrained(snapshot.isConstrained());
            setDistance(snapshot.getDistance());
            setFont(snapshot.getFont());
            setLayerNum(snapshot.getLayerNum());
            setMirror(snapshot.isMirrored());
            setRatio(snapshot.getRatio());
            setRot(snapshot.getRot());
            setSize(snapshot.getSize());
            setSpin(snapshot.isSpin());
            setValue(snapshot.getValue());

            snapshot = null;
        } else {
            LOGGER.log(Level.SEVERE, "Tried to restore a NULL snapshot!");
        }
    }

    @Override
    public ElementText getSnapshot() {
        return snapshot;
    }

    public ElementText copy() {
        ElementText copy = new ElementText();

        copy.setX(getX());
        copy.setY(getY());
        copy.setAlign(align);
        copy.setAllowMirror(isMirrorAllowed());
        copy.setAllowSpin(isSpinAllowed());
        copy.setConstrained(isConstrained());
        copy.setDistance(distance);
        copy.setFont(font);
        copy.setLayerNum(getLayerNum());
        copy.setMirror(isMirrored());
        copy.setRatio(getRatio());
        copy.setRot(getRot());
        copy.setSize(getSize());
        copy.setSpin(isSpin());
        copy.setValue(getValue());

        // Copy GroupRefs
        for (String ref : grouprefs) {
            copy.grouprefs.add(ref);
        }
        Rotation.copyValues(getRotationProperty(), copy.getRotationProperty());

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
        if (newVal.equals(xProperty)) {
            notifyListeners(LocationXYProperty.Field.X, xProperty.getOldValue(), xProperty.get());
        } else if (newVal.equals(yProperty)) {
            notifyListeners(LocationXYProperty.Field.Y, yProperty.getOldValue(), yProperty.get());
        } else if (newVal.equals(sizeProperty)) {
            notifyListeners(Field.SIZE, sizeProperty.getOldValue(), sizeProperty.get());
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
            " x=\"" + xProperty.getPrecise(6) + "\"", // 0
            " y=\"" + yProperty.getPrecise(6) + "\"", // 1
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

//        StringBuilder sb = new StringBuilder("<text");
//        sb.append(" x=\"").append(xProperty.getPrecise(6)).append("\"").
//                append(" y=\"").append(yProperty.getPrecise(6)).append("\"");
//        if (isLocked()) {
//            sb.append(" locked=\"").append("yes").append("\"");
//        }
//        sb.append(" size=\"").append(sizeProperty.getPrecise(6)).
//                append(" layer=\"").append(getLayerNum());
//        if (getRatio() != 15) {
//            sb.append(" ratio=\"").append(getRatio());
//        }
//        if (getDistance() != 50) {
//            sb.append(" distance=\"").append(getDistance());
//        }
//
//        if (!getAlign().equals(TextAlign.BOTTOM_LEFT)) {
//            sb.append(" align=\"").append(getAlign().code()).append("\"");
//        }
//        if (!getFont().equals(TextFont.VECTOR)) {
//            sb.append(" font=\"").append(getFont().code()).append("\"");
//        }
//        if (getRot() != 0.0) {
//            sb.append(" rot=\"").append((int) getRot()).append("\"");
//        }
//        // TODO: GrouRefs
//
//        sb.append("/>");
//        return sb.toString();
    }
}
