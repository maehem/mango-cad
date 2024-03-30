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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * <layer number="1" name="Top" color="4" fill="1" visible="yes" active="yes"/>
 *
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LayerElement {

    private final IntegerProperty number = new SimpleIntegerProperty(0);
    private final StringProperty name = new SimpleStringProperty("???");
    private final IntegerProperty colorIndex = new SimpleIntegerProperty(0);
    //private int colorIndex = 0;
    private final IntegerProperty fill = new SimpleIntegerProperty(0);
    //private int fill = 1;
    private final BooleanProperty visible = new SimpleBooleanProperty(true);
    //private boolean visible = true;
    private final BooleanProperty active = new SimpleBooleanProperty(true);
    //private boolean active = true;

    public LayerElement() {
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number.get();
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number.set(number);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name.get();
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * @return the colorIndex
     */
    public int getColorIndex() {
        return colorIndex.get();
    }

    /**
     * @param colorIndex the colorIndex to set
     */
    public void setColorIndex(int colorIndex) {
        this.colorIndex.set(colorIndex);
    }

    /**
     * @return the fill
     */
    public int getFill() {
        return fill.get();
    }

    /**
     * @param fill the fill to set
     */
    public void setFill(int fill) {
        this.fill.set(fill);
    }

    public final BooleanProperty visibleProperty() {
        return visible;
    }

    /**
     * @return the visible
     */
    public final boolean isVisible() {
        return visible.get();
    }

    /**
     * @param visible the visible to set
     */
    public final void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active.get();
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active.set(active);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": "
                + "[" + number + "]"
                + " name: " + name
                + " index:" + colorIndex
                + " fill: " + fill
                + " visible: " + (visible.get() ? "Y" : "N")
                + " active: " + (active.get() ? "Y" : "N");

    }
}
