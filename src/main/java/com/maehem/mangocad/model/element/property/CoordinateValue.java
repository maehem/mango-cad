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
package com.maehem.mangocad.model.element.property;

import com.maehem.mangocad.model.element.ElementValueListener;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class CoordinateValue extends ElementValue implements ElementValueListener {

    public final RealValue x = new RealValue();
    public final RealValue y = new RealValue();

    public CoordinateValue() {
        x.addListener(this);
        y.addListener(this);
    }

    public double getX() {
        return x.get();
    }

    public void setX(double val) {
        x.set(val);
    }

    public double getY() {
        return y.get();
    }

    public void setY(double val) {
        y.set(val);
    }

    public RealValue getXProperty() {
        return x;
    }

    public RealValue getYProperty() {
        return y;
    }

    @Override
    public void elementValueChanged(ElementValue newVal) {
        notifyValueChange(newVal);
    }

}
