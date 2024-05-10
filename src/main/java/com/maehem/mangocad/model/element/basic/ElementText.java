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
import com.maehem.mangocad.model.ElementRotation;
import com.maehem.mangocad.model.ElementXY;
import com.maehem.mangocad.model.element.enums.ElementTextField;
import com.maehem.mangocad.model.element.enums.RotationField;
import com.maehem.mangocad.model.element.enums.TextAlign;
import com.maehem.mangocad.model.element.enums.TextFont;
import com.maehem.mangocad.model.util.Rotation;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class ElementText extends Element implements ElementXY, ElementRotation {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static final String ELEMENT_NAME = "text";

    private double x;
    private double y;
    private double size;
    private double[] snapshot = {0, 0};

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
        return x;
    }

    /**
     * @param x the x to set
     */
    @Override
    public void setX(double x) {
        double oldValue = this.x;
        this.x = x;
        notifyListeners(ElementTextField.X, oldValue, this.x);
    }

    /**
     * @return the y
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    @Override
    public void setY(double y) {
        double oldValue = this.y;
        this.y = y;
        notifyListeners(ElementTextField.Y, oldValue, this.y);
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
        double oldValue = this.size;
        this.size = size;
        notifyListeners(ElementTextField.SIZE, oldValue, this.size);
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
        double oldValue = this.distance;
        this.distance = distance;
        notifyListeners(ElementTextField.DISTANCE, oldValue, this.distance);
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
        if (ratio > 31) {
            ratio = 31;
        }
        double oldValue = this.ratio;
        this.ratio = ratio;
        notifyListeners(ElementTextField.RATIO, oldValue, this.ratio);
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
        notifyListeners(ElementTextField.ROTATION, null, this.getRotation());
    }

    @Override
    public double getRot() {
        return rotation.getValue();
    }

    @Override
    public void setRot(double rot) {
        double oldValue = this.getRot();
        getRotation().setValue(rot);
        notifyListeners(ElementTextField.ROTATION, oldValue, this.getRot());
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
        TextAlign oldValue = this.align;
        if (align != null) {
            this.align = align;
        }
        notifyListeners(ElementTextField.ALIGN, oldValue, this.align);
    }

    public void setAlign(String val) {
        TextAlign oldValue = this.align;
        TextAlign ta = TextAlign.fromCode(val);
        if (ta == null) {
            LOGGER.log(Level.SEVERE, "TextAlign: tried to set an alignment called \"{0}\"", val);
        } else {
            setAlign(ta);
        }
        notifyListeners(ElementTextField.ALIGN, oldValue, this.align);
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
        notifyListeners(ElementTextField.VALUE, oldValue, this.value);
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
        TextFont oldValue = this.font;
        this.font = font;
        notifyListeners(ElementTextField.FONT, oldValue, this.font);
    }

    /**
     * @return the grouprefs
     */
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }

    public ElementText copy() {
        ElementText copy = new ElementText();

        copy.setX(x);
        copy.setY(y);
        copy.setAlign(align);
        copy.setDistance(distance);
        copy.setFont(font);
        copy.setLayer(getLayerNum());
        copy.setRatio(ratio);
        copy.setSize(size);
        copy.setValue(value);

        // Copy GroupRefs
        for (String ref : grouprefs) {
            copy.grouprefs.add(ref);
        }
        Rotation.copyValues(getRotation(), copy.getRotation());

        return copy;
    }

    @Override
    public boolean isSpun() {
        return rotation.isSpun();
    }

    @Override
    public void setSpin(boolean spin) {
        boolean oldValue = isSpin();
        rotation.setSpin(spin);
        notifyListeners(RotationField.SPIN, oldValue, this.isSpin());
    }

    @Override
    public boolean isSpin() {
        return rotation.isSpin();
    }

    @Override
    public void setAllowSpin(boolean allowSpin) {
        boolean oldValue = isSpinAllowed();
        rotation.setAllowSpin(allowSpin);
        notifyListeners(RotationField.ALLOW_SPIN, oldValue, this.isSpinAllowed());
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
        boolean oldValue = this.isConstrained();
        rotation.setConstrained(value);
        notifyListeners(RotationField.CONSTRAINED, oldValue, this.isConstrained());
    }

    @Override
    public void setMirror(boolean value) {
        boolean oldValue = this.isMirrored();
        rotation.setMirror(value);
        notifyListeners(RotationField.MIRROR, oldValue, this.isMirrored());
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
        boolean oldValue = this.isMirrorAllowed();
        rotation.setAllowMirror(value);
        notifyListeners(RotationField.ALLOW_MIRROR, oldValue, this.isMirrorAllowed());
    }

    @Override
    public void createSnapshot() {
        snapshot[0] = getX();
        snapshot[1] = getY();
    }

    @Override
    public double[] getSnapshot() {
        return snapshot;
    }

}
