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
import com.maehem.mangocad.model.element.enums.ElementCircleField;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LocationXYProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class ElementCircle extends Element implements LayerNumberProperty, LocationXYProperty, SelectableProperty, WidthProperty {

    public static final String ELEMENT_NAME = "circle";

    private int layer;
    private double x;
    private double y;
    private double radius = 2.54;
    private double width = 0.254;
    private final ArrayList<String> grouprefs = new ArrayList<>();

    private boolean selected = false;
    private ElementCircle snapshot = null;

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        double oldValue = this.radius;
        this.radius = radius;
        notifyListeners(ElementCircleField.RADIUS, oldValue, this.radius);
    }

    /**
     * @return the width
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    @Override
    public void setWidth(double width) {
        double oldValue = this.width;
        this.width = width;
        notifyListeners(ElementCircleField.WIDTH, oldValue, this.width);
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
        double oldValue = this.x;
        this.x = x;
        notifyListeners(ElementCircleField.X, oldValue, this.x);
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
        double oldValue = this.y;
        this.y = y;
        notifyListeners(ElementCircleField.Y, oldValue, this.y);
    }

    /**
     * @return the grouprefs
     */
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
            setWidth(snapshot.getWidth());
            setLayerNum(snapshot.getLayerNum());
            setRadius(snapshot.getRadius());

            snapshot = null;
        } else {
            LOGGER.log(Level.SEVERE, "ElementCircle: Tried to restore a NULL snapshot!");
        }
    }

    public ElementCircle copy() {
        ElementCircle copy = new ElementCircle();

        copy.setX(getX());
        copy.setY(getY());
        copy.setWidth(getWidth());
        copy.setLayerNum(getLayerNum());
        copy.setRadius(getRadius());

        return copy;
    }

    @Override
    public ElementCircle getSnapshot() {
        return snapshot;
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
            notifyListeners(ElementCircleField.SELECTED, oldValue, this.selected);
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
