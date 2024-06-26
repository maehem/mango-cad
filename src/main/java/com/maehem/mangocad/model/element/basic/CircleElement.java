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
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.property.CoordinateProperty;
import com.maehem.mangocad.model.element.property.CoordinateValue;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.IntValue;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LockProperty;
import com.maehem.mangocad.model.element.property.LockValue;
import com.maehem.mangocad.model.element.property.RealValue;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import com.maehem.mangocad.model.element.property.WidthValue;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class CircleElement extends Element implements
        LayerNumberProperty, CoordinateProperty, SelectableProperty,
        WidthProperty, LockProperty, GrouprefsProperty,
        ElementValueListener {

    public static final String ELEMENT_NAME = "circle";

    public enum Field implements ElementField {
        RADIUS("raduis", Double.class);
        //WIDTH("width", Double.class);

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

    //private int layer;
    private final IntValue layerValue = new IntValue(0);
    //public final RealValue xProperty = new RealValue(0);
    //public final RealValue yProperty = new RealValue(0);
    public final CoordinateValue coordinate = new CoordinateValue();

    public final LockValue lockProperty = new LockValue();
    public final RealValue radiusProperty = new RealValue(2.54, 0.0, Double.MAX_VALUE, 9);
    public final WidthValue widthProperty = new WidthValue(0.254);

    private final ArrayList<String> grouprefs = new ArrayList<>();

    private boolean selected = false;
    private CircleElement snapshot = null;

    public CircleElement() {

        layerValue.addListener(this);
        coordinate.addListener(this);
        widthProperty.addListener(this);
        radiusProperty.addListener(this);
        lockProperty.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public CoordinateValue getCoordinateProperty() {
        return coordinate;
    }

    @Override
    public WidthValue getWidthProperty() {
        return widthProperty;
    }

    @Override
    public LockValue getLockProperty() {
        return lockProperty;
    }

    public IntValue getLayerNumberProperty() {
        return layerValue;
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
            notifyListeners(CircleElement.Field.RADIUS, oldValue, getRadius());
        }
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return widthProperty.get();
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        widthProperty.set(width);
    }

    /**
     * @return the x
     */
    public double getX() {
        return coordinate.x.get();
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        coordinate.x.set(x);
    }

    /**
     * @return the y
     */
    public double getY() {
        return coordinate.y.get();
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        coordinate.y.set(y);
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
            setWidth(snapshot.widthProperty.get());
            setLayerNum(snapshot.getLayerNum());
            setRadius(snapshot.getRadius());

            snapshot = null;
        } else {
            LOGGER.log(Level.SEVERE, "ElementCircle: Tried to restore a NULL snapshot!");
        }
    }

    public CircleElement copy() {
        CircleElement copy = new CircleElement();

        copy.setX(getX());
        copy.setY(getY());
        copy.setWidth(getWidth());
        copy.setLayerNum(getLayerNum());
        copy.setRadius(getRadius());

        return copy;
    }

    @Override
    public CircleElement getSnapshot() {
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
        if (newVal.equals(coordinate.x)) {
            notifyListeners(CoordinateProperty.Field.X, coordinate.x.getOldValue(), coordinate.x.get());
        } else if (newVal.equals(coordinate.y)) {
            notifyListeners(CoordinateProperty.Field.Y, coordinate.y.getOldValue(), coordinate.y.get());
        } else if (newVal.equals(radiusProperty)) {
            notifyListeners(Field.RADIUS, radiusProperty.getOldValue(), radiusProperty.get());
        } else if (newVal.equals(widthProperty)) {
            notifyListeners(WidthProperty.Field.WIDTH, widthProperty.getOldValue(), widthProperty.get());
        } else if (newVal.equals(lockProperty)) {
            notifyListeners(LockProperty.Field.LOCKED, lockProperty.getOldValue(), lockProperty.isLocked());
        } else if (newVal.equals(layerValue)) {
            notifyListeners(LayerNumberProperty.Field.LAYER, layerValue.getOldValue(), layerValue.get());
        }
    }

    /**
     * <code>  Examples:
     * <circle x="-6.096" y="-7.62" radius="0.762" width="0.3048" layer="94"/>
     * <circle x="-19.558" y="5.08" radius="0.762" width="0.3048" layer="94"/>
     * <circle x="-12.7" y="-7.62" radius="0.254" width="0.381" layer="94"/>
     * <circle x="6.096" y="-7.62" radius="0.762" width="0.3048" layer="94"/>
     * </code>
     *
     * @return
     */
    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<circle{0}{1}{2}{3}{4}{5}/>");

        Object[] args = {
            " x=\"" + coordinate.x.getPrecise(6) + "\"", // 0
            " y=\"" + coordinate.y.getPrecise(6) + "\"", // 1
            lockProperty.xmlValue(), // 2
            " radius=\"" + radiusProperty.getPrecise(9) + "\"", // 3
            " width=\"" + widthProperty.getPrecise(6) + "\"", // 4
            " layer=\"" + getLayerNum() + "\"" // 5
        };

        return mf.format(args);
    }
}
