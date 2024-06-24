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

import com.maehem.mangocad.model.element.ElementField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class WidthValue extends RealValue {

    public static final ObservableList<Double> WIDTH_DEFAULT_OPTIONS
            = FXCollections.observableArrayList(
                    -1.0,
                    0.0,
                    0.01,
                    0.0125,
                    0.025,
                    0.03937008,
                    0.05,
                    0.10,
                    0.5,
                    1.0,
                    2.0,
                    5.0,
                    10.0
            );

    public enum Field implements ElementField {
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

    public WidthValue() {
        super(0.254, 0, Double.POSITIVE_INFINITY);
    }

    public WidthValue(double val) {
        super(val);
    }

    //public double getWidth();
    //public void setWidth(double x);
}
