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
import java.util.ArrayList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class ElementCircle extends Element {
    public static final String ELEMENT_NAME = "circle";

    private double x;
    private double y;
    private double radius;
    private double width;
    private final ArrayList<String> grouprefs = new ArrayList<>();

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
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
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

}
