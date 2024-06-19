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

import com.maehem.mangocad.model.RealValue;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class CurveProperty extends RealValue {

    public static final double MIN = -359.9;
    public static final double MAX = 359.9;


    public CurveProperty(double value) {
        super(value, MIN, MAX);
    }

    public enum Field {
        VALUE("value", Double.class);

        private final String fName;
        private final Class clazz;

        private Field(String name, Class clazz) {
            this.fName = name;
            this.clazz = clazz;
        }

        public String fName() {
            return fName;
        }

        public Class clazz() {
            return clazz;
        }

    }

    public String toXML() {
        return get() != 0.0 ? " curve=\"" + getPrecise(1) + "\"" : "";
    }
}
