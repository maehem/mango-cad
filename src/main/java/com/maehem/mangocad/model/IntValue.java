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
package com.maehem.mangocad.model;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class IntValue extends ElementValue {

    private int value;
    private int oldValue;
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntValue(int value) {
        this.value = value;
    }

    public IntValue(int value, int min, int max) {
        this(value);
        this.min = min;
        this.max = max;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        oldValue = this.value;
        this.value = value;
        if (oldValue != this.value) {
            notifyValueChange();
        }
    }

    public int getMin() {
        return min;
    }

    public void setMin(int val) {
        this.min = val;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int val) {
        this.max = val;
    }

    public int getOldValue() {
        return oldValue;
    }

    public boolean isInRange(int val) {
        return (val > getMin() && val < getMax());
    }
}
