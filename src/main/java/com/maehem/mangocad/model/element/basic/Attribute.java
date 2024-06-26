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
import com.maehem.mangocad.model.element.enums.TextAlign;
import com.maehem.mangocad.model.element.enums.TextFont;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LayerNumberValue;
import com.maehem.mangocad.model.element.property.Rotation;
import java.util.ArrayList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 * <pre>
 * [[attribute]]
 *      name %String; #REQUIRED
 *      value %String; #IMPLIED
 *      x %Coord;  #IMPLIED
 *      y %Coord; #IMPLIED
 *      size %Dimension; #IMPLIED
 *      layer %Layer; #IMPLIED
 *      font %TextFont; #IMPLIED
 *      ratio %Int; #IMPLIED
 *      rot %Rotation; "R0"
 *      display %AttributeDisplay; "value"
 *      constant %Bool; "no"
 *      align %Align; "bottom-left"
 *      grouprefs IDREFS #IMPLIED >
 *
 *      <!-- display: Only in <element> or <instance> context -->
 *      <!-- constant:Only in <device> context -->
 * </pre>
 */
public class Attribute extends Element implements LayerNumberProperty, GrouprefsProperty {

    public static final String ELEMENT_NAME = "attribute";

    //private int layer;
    public final LayerNumberValue layerNumber = new LayerNumberValue(97);
    private String name;
    private double x;
    private double y;
    private String value;
    private double size;
    private final Rotation rotation = new Rotation();  // rot
    private int ratio = 8;
    private String display = "value";
    private boolean constant = false;
    private TextAlign align = TextAlign.BOTTOM_LEFT;
    private TextFont font = TextFont.VECTOR;

    // Added in more recent Eagle DTD
    private boolean locked = false;

    private final ArrayList<String> grouprefs = new ArrayList<>();

    // TODO: Needs Listener setup
    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the ratio (for text)
     */
    public int getRatio() {
        return ratio;
    }

    /**
     * @param the text ratio to set
     */
    public void setRatio(int r) {
        this.ratio = r;
    }

    /**
     * @return the rotation
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * @param value
     */
    public void setRotation(double value) {
        this.rotation.set(value);
    }

    /**
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @param display the display to set
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * @return the constant
     */
    public boolean isConstant() {
        return constant;
    }

    /**
     * @param constant the constant to set
     */
    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    /**
     * @return the name
     */
    public TextAlign getAlign() {
        return align;
    }

    /**
     * @param align the alignment to set
     */
    public void setAlign(TextAlign align) {
        this.align = align;
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
     * @return the constant
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked the value to set
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
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
        return "Attribute: " + getName() + ":" + getValue() + " :: " + getX() + "," + getY() + " . " + getAlign().code();
    }

    @Override
    public int getLayerNum() {
        return layerNumber.get();
    }

    @Override
    public void setLayerNum(int layer) {
        layerNumber.set(layer);
//        if (this.layer != layer) {
//            int oldVal = this.layer;
//            this.layer = layer;
//            notifyListeners(LayerNumberProperty.Field.LAYER, oldVal, this.layer);
//        }
    }

    @Override
    public LayerNumberValue getLayerNumberProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
