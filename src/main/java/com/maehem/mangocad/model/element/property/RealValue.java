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

import com.maehem.mangocad.model.ElementValue;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RealValue extends ElementValue {

    public static final int DEFAULT_PREC = 6;

    private double value;
    private double oldValue;
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;
    private int prec = 6;

    public RealValue() {
        this(0.0);
    }

    public RealValue(double value) {
        this(value, DEFAULT_PREC);
    }

    public RealValue(double value, int precision) {
        this.value = value;
        this.prec = precision;
    }

    public RealValue(double value, double min, double max) {
        this(value);
        this.min = min;
        this.max = max;
    }

    public RealValue(double value, double min, double max, int precision) {
        this(value, precision);
        this.min = min;
        this.max = max;
    }

    public double get() {
        return round(value, prec);
    }

    public String getPrecise() {
        return getPrecise(prec);
    }

    public String getPrecise(int precision) {
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < precision; i++) {
            pattern.append("#");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());

        return df.format(get());
    }

    public void set(double value) {
        oldValue = this.value;
        if (value > getMax()) {
            value = getMax();
        }

        if (value < getMin()) {
            value = getMin();
        }
        this.value = value;

        if (oldValue != this.value) {
            notifyValueChange();
        }
    }

    public double getOldValue() {
        return oldValue;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double val) {
        this.min = val;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double val) {
        this.max = val;
    }

    public int getPrecision() {
        return this.prec;
    }

    public boolean isInRange(double val) {
        return (val > getMin() && val < getMax());
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        if (value != Double.NaN) {
            BigDecimal bd = BigDecimal.valueOf(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } else {
            return Double.MAX_VALUE;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getPrecise());
    }

}
