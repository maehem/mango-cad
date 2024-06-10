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
import com.maehem.mangocad.model.RealValue;
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.enums.TextAlign;
import com.maehem.mangocad.model.element.enums.TextFont;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LocationXYProperty;
import com.maehem.mangocad.model.element.property.RotationProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.util.Rotation;
import java.util.ArrayList;
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
public class ElementText extends Element implements LayerNumberProperty, LocationXYProperty, RotationProperty, SelectableProperty, GrouprefsProperty {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static final String ELEMENT_NAME = "text";

    public enum Field implements ElementField {
        //X("x", Double.class), Y("y", Double.class),
        //SELECTED("selected", Boolean.class),
        SIZE("size", Double.class),
        LAYER("layer", Integer.class),
        FONT("font", String.class),
        RATIO("ratio", Integer.class),
        ROTATION("rotation", String.class),
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

    public static final ObservableList<Integer> TEXT_RATIO_DEFAULT_OPTIONS
            = FXCollections.observableArrayList(
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                    30, 31
            );

    private int layer;
    public final RealValue xProperty = new RealValue(0);
    public final RealValue yProperty = new RealValue(0);
    private double size = 1.778; // 0.7 inch
    private boolean selected = false;
    private ElementText snapshot = null;

    private TextFont font = TextFont.PROPORTIONAL;
    private int ratio = 8;
    private final Rotation rotation = new Rotation();
    private TextAlign align = TextAlign.BOTTOM_LEFT;
    private int distance = 50;  // Line to line distance

    private String value;  // #PCDATA Content

    private final ArrayList<String> grouprefs = new ArrayList<>();

    public ElementText() {
        rotation.setAllowMirror(true);
        rotation.setAllowSpin(true);
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
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(double size) {
        if (getSize() != size) {
            double oldValue = this.size;
            this.size = size;
            notifyListeners(ElementText.Field.SIZE, oldValue, this.size);
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
        return ratio;
    }

    /**
     * @param ratio the ratio to set
     *
     * Maximum value is 31.
     */
    public void setRatio(int ratio) {
        if (getRatio() != ratio) {
            if (ratio > 31) {
                ratio = 31;
            }
            double oldValue = this.ratio;
            this.ratio = ratio;
            notifyListeners(ElementText.Field.RATIO, oldValue, this.ratio);
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
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Copy the values of source rotation into our rotation.
     *
     * @param rotation
     */
    public void setRotation(Rotation r) {
        //Rotation oldValue = this.getRotation();
        Rotation.copyValues(r, this.getRotation());
        notifyListeners(ElementText.Field.ROTATION, null, this.getRotation());
    }

    @Override
    public double getRot() {
        return rotation.getValue();
    }

    @Override
    public void setRot(double rot) {
        if (getRot() != rot) {
            double oldValue = this.getRot();
            getRotation().setValue(rot);
            notifyListeners(ElementText.Field.ROTATION, oldValue, this.getRot());
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
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        String oldValue = this.value;
        this.value = value;
        notifyListeners(ElementText.Field.VALUE, oldValue, this.value);
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
        return rotation.isAllowSpin();
    }

    @Override
    public boolean isConstrained() {
        return getRotation().isConstrained();
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
        return getRotation().isMirror();
    }

    @Override
    public boolean isMirrorAllowed() {
        return getRotation().isAllowMirror();
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
        copy.setRatio(ratio);
        copy.setRot(getRot());
        copy.setSize(size);
        copy.setSpin(isSpin());
        copy.setValue(value);

        // Copy GroupRefs
        for (String ref : grouprefs) {
            copy.grouprefs.add(ref);
        }
        Rotation.copyValues(getRotation(), copy.getRotation());

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

}
