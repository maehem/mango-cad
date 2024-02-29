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

import com.maehem.mangocad.model._AQuantum;
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
public class ElementText extends _AQuantum {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static final String ELEMENT_NAME = "text";

    private double x;
    private double y;
    private double size;

    private TextFont font = TextFont.PROPORTIONAL;
    private int ratio = 8; // Eagle called it ratio
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
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
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
        this.size = size;
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
        this.distance = distance;
    }

    /**
     * @return the ratio
     */
    public int getRatio() {
        return ratio;
    }

    /**
     * @param ratio the ratio to set
     */
    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public double getDerivedStroke() {
        return getRatio() * 0.01 * getSize() * 0.5;
    }

    /**
     * @return the rotation
     */
    public Rotation getRotation() {
        return rotation;
    }

    public double getRot() {
        return rotation.getValue();
    }

    public void setRot(double rot) {
        getRotation().setValue(rot);
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
        if (align != null) {
            this.align = align;
        }
    }

    public void setAlign(String val) {
        TextAlign ta = TextAlign.fromCode(val);
        setAlign(ta);
        if (ta == null) {
            LOGGER.log(Level.SEVERE, "TextAlign: tried to set an alignment called \"{0}\"", val);
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
        this.value = value;
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
        this.font = font;
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

}
