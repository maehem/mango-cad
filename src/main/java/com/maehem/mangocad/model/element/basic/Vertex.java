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
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.property.RealValue;
import com.maehem.mangocad.model.element.property.CurveProperty;
import com.maehem.mangocad.model.element.property.LocationXYProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import java.text.MessageFormat;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Vertex extends Element implements
        LocationXYProperty, SelectableProperty, ElementValueListener {

    public static final String ELEMENT_NAME = "vertex";

    public final RealValue xProperty = new RealValue(0);
    public final RealValue yProperty = new RealValue(0);

    public final CurveProperty curveProperty = new CurveProperty(0);

    private boolean selected = false;
    private Vertex snapshot = null;

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
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
        if (xProperty.get() != x) {
            double oldVal = xProperty.get();
            xProperty.set(x);
            notifyListeners(LocationXYProperty.Field.X, oldVal, xProperty.get());
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
        if (yProperty.get() != y) {
            double oldVal = yProperty.get();
            yProperty.set(y);
            notifyListeners(LocationXYProperty.Field.Y, oldVal, yProperty.get());
        }
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
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(xProperty)) {
            notifyListeners(LocationXYProperty.Field.X, xProperty.getOldValue(), xProperty.get());
        } else if (newVal.equals(yProperty)) {
            notifyListeners(LocationXYProperty.Field.Y, yProperty.getOldValue(), yProperty.get());
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
            " x=\"" + xProperty.getPrecise(6) + "\"", // 0
            " y=\"" + yProperty.getPrecise(6) + "\"", // 1
            curveProperty.toXML() // 2
        };

        return mf.format(args);
    }

}
