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
import com.maehem.mangocad.model.ElementSelectable;
import com.maehem.mangocad.model.ElementXY;
import com.maehem.mangocad.model.element.enums.VertexField;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Vertex extends Element implements ElementXY, ElementSelectable {

    public static final String ELEMENT_NAME = "vertex";

    private double x;
    private double y;
    private double curve = 0;
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
        return x;
    }

    /**
     * @param x the x to set
     */
    @Override
    public void setX(double x) {
        if (this.x != x) {
            double oldVal = this.x;
            this.x = x;
            notifyListeners(VertexField.X, oldVal, this.x);
        }
    }

    /**
     * @return the y
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    @Override
    public void setY(double y) {
        if (this.y != y) {
            double oldVal = this.y;
            this.y = y;
            notifyListeners(VertexField.Y, oldVal, this.y);
        }
    }

    /**
     * @return the curve
     */
    public double getCurve() {
        return curve;
    }

    /**
     * @param curve the curve to set
     */
    public void setCurve(double curve) {
        this.curve = curve;
        if (this.curve != curve) {
            double oldVal = this.curve;
            this.curve = curve;
            notifyListeners(VertexField.CURVE, oldVal, this.curve);
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
            notifyListeners(VertexField.SELECTED, oldValue, this.selected);
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
        setCurve(snapshot.getCurve());
    }

    @Override
    public Element getSnapshot() {
        return snapshot;
    }

    public Vertex copy() {
        Vertex copyVertex = new Vertex();
        copyVertex.setX(getX());
        copyVertex.setY(getY());
        copyVertex.setCurve(getCurve());

        return copyVertex;
    }
}
