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
import com.maehem.mangocad.model.element.property.CoordinateProperty;
import com.maehem.mangocad.model.element.property.CoordinateValue;
import com.maehem.mangocad.model.element.property.CurveProperty;
import com.maehem.mangocad.model.element.property.CurveValue;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import java.text.MessageFormat;

/**
 * <code>
 * ELEMENT vertex (no sub-elements)
 *      x             %Coord;        #REQUIRED
 *      y             %Coord;        #REQUIRED
 *      curve         %WireCurve;    "0"
 *
 *      curve: The curvature from this vertex to the next one
 * </code>
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Vertex extends Element implements
        CoordinateProperty, SelectableProperty, CurveProperty,
        ElementValueListener {

    public static final String ELEMENT_NAME = "vertex";

    public final CoordinateValue coordinate = new CoordinateValue();
    public final CurveValue curveProperty = new CurveValue(0);

    private boolean selected = false;
    private boolean picked = false;
    private Vertex snapshot = null;

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x
     */
    public double getX() {
        return coordinate.x.get();
    }

    /**
     * @param val the x to set
     */
    public void setX(double val) {
        coordinate.x.set(val);
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
     * @param curve the curve to set
     */
    public void setCurve(double curve) {
        if (curveProperty.get() != curve) {
            double oldVal = curveProperty.get();
            curveProperty.set(curve);
            notifyListeners(CurveProperty.Field.VALUE, oldVal, curveProperty.get());
        }
    }

    @Override
    public CurveValue getCurveProperty() {
        return curveProperty;
    }

    /**
     * @return the selected
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    @Override
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            boolean oldValue = this.selected;
            this.selected = selected;
            notifyListeners(SelectableProperty.Field.SELECTED, oldValue, this.selected);
        }
    }

    @Override
    public boolean isPicked() {
        return picked;
    }

    @Override
    public void setPicked(boolean picked) {
        if (this.picked != picked) {
            boolean oldValue = this.picked;
            this.picked = picked;
            notifyListeners(SelectableProperty.Field.PICKED, oldValue, this.picked);
        }
    }

    @Override
    public void modify(double xDist, double yDist, boolean ephemeral) {
        //LOGGER.log(Level.SEVERE, "Move elementXY.");
        //Element snapshot = es.getSnapshot();
        if (getSnapshot() instanceof CoordinateProperty snapXY) {
            //LOGGER.log(Level.SEVERE, "    Move relative to snapXY.");
            getCoordinateProperty().setX(snapXY.getCoordinateProperty().getX() + xDist);
            getCoordinateProperty().setY(snapXY.getCoordinateProperty().getY() + yDist);
        }
    }

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(coordinate.x)) {
            notifyListeners(CoordinateProperty.Field.X, coordinate.x.getOldValue(), coordinate.x.get());
        } else if (newVal.equals(coordinate.y)) {
            notifyListeners(CoordinateProperty.Field.Y, coordinate.y.getOldValue(), coordinate.y.get());
        } else if (newVal.equals(curveProperty)) {
            notifyListeners(CurveProperty.Field.VALUE, curveProperty.getOldValue(), curveProperty.get());
        }
    }

    @Override
    public void createSnapshot() {
        snapshot = copy();

    }

    @Override
    public void restoreSnapshot() {
        setX(snapshot.getX());
        setY(snapshot.getY());
        setCurve(snapshot.curveProperty.get());
    }

    @Override
    public Element getSnapshot() {
        return snapshot;
    }

    public Vertex copy() {
        Vertex copyVertex = new Vertex();
        copyVertex.setX(getX());
        copyVertex.setY(getY());
        copyVertex.setCurve(curveProperty.get());

        return copyVertex;
    }

    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<vertex{0}{1}{2}/>");

        Object[] args = {
            " x=\"" + coordinate.x.getPrecise(6) + "\"", // 0
            " y=\"" + coordinate.y.getPrecise(6) + "\"", // 1
            curveProperty.toXML() // 2
        };

        return mf.format(args);
    }

    @Override
    public CoordinateValue getCoordinateProperty() {
        return coordinate;
    }

}
