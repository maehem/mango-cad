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
import com.maehem.mangocad.model.LockValue;
import com.maehem.mangocad.model.RealValue;
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LocationXYProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class ElementCircle extends Element implements LayerNumberProperty, LocationXYProperty, SelectableProperty, GrouprefsProperty, WidthProperty {

    public static final String ELEMENT_NAME = "circle";

    public enum Field implements ElementField {
        //X("x", Double.class), Y("y", Double.class),
        //SELECTED("selected", Boolean.class),
        RADIUS("raduis", Double.class),
        WIDTH("width", Double.class);

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

    private int layer;
    public final RealValue xProperty = new RealValue(0);
    public final RealValue yProperty = new RealValue(0);

    public final LockValue lockProperty = new LockValue();
    public final RealValue radiusProperty = new RealValue(2.54, 0.0, Double.MAX_VALUE, 9);
    public final RealValue widthProperty = new RealValue(0.254);

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
        return radiusProperty.get();
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        if (getRadius() != radius) {
            double oldValue = getRadius();
            radiusProperty.set(radius);
            notifyListeners(ElementCircle.Field.RADIUS, oldValue, getRadius());
        }
    }

    /**
     * @return the width
     */
    @Override
    public double getWidth() {
        return widthProperty.get();
    }

    /**
     * @param width the width to set
     */
    @Override
    public void setWidth(double width) {
        if (getWidth() != width) {
            double oldValue = getWidth();
            widthProperty.set(width);
            notifyListeners(ElementCircle.Field.WIDTH, oldValue, getWidth());
        }
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

    /**
     *
     * <circle x="-6.096" y="-7.62" radius="0.762" width="0.3048" layer="94"/>
     * <circle x="-19.558" y="5.08" radius="0.762" width="0.3048" layer="94"/>
     * <circle x="-12.7" y="-7.62" radius="0.254" width="0.381" layer="94"/>
     * <circle x="6.096" y="-7.62" radius="0.762" width="0.3048" layer="94"/>
     *
     * @return
     */
    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<circle{0}{1}{2}{3}{4}{5}/>");

        Object[] args = {
            " x=\"" + xProperty.getPrecise(6) + "\"", // 0
            " y=\"" + yProperty.getPrecise(6) + "\"", // 1
            lockProperty.xmlValue(), // 2
            " radius=\"" + radiusProperty.getPrecise(9) + "\"", // 3
            " width=\"" + widthProperty.getPrecise(6) + "\"", // 4
            " layer=\"" + getLayerNum() + "\"" // 5
        };

        return mf.format(args);
    }
}
