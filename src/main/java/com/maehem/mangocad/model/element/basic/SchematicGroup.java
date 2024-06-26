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
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.enums.TextFont;
import com.maehem.mangocad.model.element.enums.WireStyle;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LayerNumberValue;
import java.util.ArrayList;

/**
 * schematic_group name ID #REQUIRED selectable %Bool; #IMPLIED width
 * %Dimension; #IMPLIED titleSize %Dimension; #IMPLIED titleFont %TextFont;
 * #IMPLIED style %WireStyle; #IMPLIED showAnnotations %Bool; #IMPLIED layer
 * %Layer; #IMPLIED grouprefs IDREFS #IMPLIED
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SchematicGroup extends Element implements LayerNumberProperty, ElementValueListener {

    public static final String ELEMENT_NAME = "schematic_group";

    //private int layer;
    public final LayerNumberValue layerValue = new LayerNumberValue(1);
    private String name;
    private boolean selectable;
    private double width;
    private double titleSize;
    private TextFont titleFont;
    private WireStyle style;
    private boolean showAnnotations;
    private ArrayList<String> grouprefs = new ArrayList<>();

    public SchematicGroup() {

        layerValue.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public LayerNumberValue getLayerNumberProperty() {
        return layerValue;
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
     * @return the selectable
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * @param selectable the selectable to set
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * @return the titleSize
     */
    public double getTitleSize() {
        return titleSize;
    }

    /**
     * @param titleSize the titleSize to set
     */
    public void setTitleSize(double titleSize) {
        this.titleSize = titleSize;
    }

    /**
     * @return the titleFont
     */
    public TextFont getTitleFont() {
        return titleFont;
    }

    /**
     * @param titleFont the titleFont to set
     */
    public void setTitleFont(TextFont titleFont) {
        this.titleFont = titleFont;
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
        this.style = style;
    }

    /**
     * @return the showAnnotations
     */
    public boolean isShowAnnotations() {
        return showAnnotations;
    }

    /**
     * @param showAnnotations the showAnnotations to set
     */
    public void setShowAnnotations(boolean showAnnotations) {
        this.showAnnotations = showAnnotations;
    }

//    /**
//     * @return the layer
//     */
//    public int getLayer() {
//        return layer;
//    }
//
//    /**
//     * @param layer the layer to set
//     */
//    public void setLayer(int layer) {
//        this.layer = layer;
//    }
//
    /**
     * @return the grouprefs
     */
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }

    /**
     * @param grouprefs the grouprefs to set
     */
    public void setGrouprefs(ArrayList<String> grouprefs) {
        this.grouprefs = grouprefs;
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
        }
    }
}
