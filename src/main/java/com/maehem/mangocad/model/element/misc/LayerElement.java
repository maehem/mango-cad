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
package com.maehem.mangocad.model.element.misc;

import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.property.VisibleProperty;

/**
 *
 * <layer number="1" name="Top" color="4" fill="1" visible="yes" active="yes"/>
 *
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LayerElement implements VisibleProperty {

    private int number = 0;
    private String name = "???";
    private int colorIndex = 0;
    private int fill = 1;
    private boolean visible = true;
    private boolean active = true;
    private boolean allowDelete = true;
    public final Layers parent;

    private boolean editorVisible = true;

    public LayerElement(Layers parent) {
        this.parent = parent;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
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
     * @return the colorIndex
     */
    public int getColorIndex() {
        return colorIndex;
    }

    /**
     * @param colorIndex the colorIndex to set
     */
    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    /**
     * @return the fill
     */
    public int getFill() {
        return fill;
    }

    /**
     * @param fill the fill to set
     */
    public void setFill(int fill) {
        this.fill = fill;
    }

    /**
     * @return the visible
     */
    @Override
    public final boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    @Override
    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Used to tell editor if item should appear in comboBox menu. Not written
     * to file. Determined at run time based on editor: Symbol, Schematic,
     * Footprint, Board.
     *
     * @return the visible
     */
    public final boolean isEditorVisible() {
        return visible;
    }

    /**
     * Used to tell editor if item should appear in comboBox menu.
     *
     * @param visible the visible to set
     */
    public final void setEditorVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAllowDelete() {
        return allowDelete;
    }

    public void setAllowDelete(boolean value) {
        allowDelete = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": "
                + "[" + number + "]"
                + " name: " + name
                + " index:" + colorIndex
                + " fill: " + fill
                + " visible: " + (visible ? "Y" : "N")
                + " active: " + (active ? "Y" : "N");

    }
}
