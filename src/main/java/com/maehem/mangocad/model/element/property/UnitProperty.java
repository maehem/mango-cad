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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public interface UnitProperty {

    public enum Field implements ElementField {
        VALUE("value", Unit.class);

        public final String fName;
        public final Class clazz;

        Field(String label, Class clazz) {
            this.fName = label;
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

    public enum Unit {
        MIC("mic", 1000.0), MM("mm", 1.0),
        MIL("mil", 39.3700787), INCH("inch", 0.0393700787);

        public final String label;
        public final double mult;

        private Unit(String label, double mult) {
            this.label = label;
            this.mult = mult;
        }

        public String code() {
            return label;
        }

        public static Unit fromCode(String code) {
            if (code != null) {
                for (Unit g : Unit.values()) {
                    if (code.equalsIgnoreCase(g.code())) {
                        return g;
                    }
                }
            }
            return null;
        }

        public static double convertUnit(double val, Unit srcUnit, Unit destUnit) {
            if (srcUnit.equals(destUnit)) {
                return val;
            }

            switch (destUnit) {
                case INCH -> {
                    switch (srcUnit) {
                        case MIC -> {
                            return val * INCH.mult / MIC.mult; // Mult by 25400
                        }
                        case MIL -> {
                            return val / MIC.mult;
                        }
                        case MM -> {
                            return val * INCH.mult;
                        }
                    }
                }

                case MIC -> {
                    switch (srcUnit) {
                        case INCH -> {
                            return val / (MIC.mult / INCH.mult); // divide by 25400
                        }
                        case MIL -> {
                            return val * INCH.mult; // Divide by 25.4 ( or mult by 0.039370...)
                        }
                        case MM -> {
                            return val / MIC.mult; // Divide by 1000.0
                        }
                    }
                }

                case MIL -> {
                    switch (srcUnit) {
                        case MIC -> {
                            return val / INCH.mult; // multiply by 25.4 ( convert INCH )
                        }
                        case INCH -> {
                            return val * MIC.mult; // multiply by 1000.0
                        }
                        case MM -> {
                            return val / MIL.mult; // divide by 39.37...
                        }
                    }
                }
                case MM -> {
                    switch (srcUnit) {
                        case MIC -> {
                            return val * MIC.mult;
                        }
                        case MIL -> {
                            return val * MIL.mult; // multiply by 39.37...
                        }
                        case INCH -> {
                            return val * INCH.mult; // div by 25.4 ( mult by 0.03937...)
                        }
                    }
                }
            }
            return val;
        }

        /**
         * Convert a string, possibly affixed with units to millimeters.
         * Example: "10mil" would return the converted Double value 0.254
         *
         * @param sVal
         * @return double value in mm, -1 if error.
         */
        public static Double toMM(String sVal) {
            if (sVal.endsWith(MIL.code())) {
                Double dVal = Double.valueOf(sVal.substring(0, sVal.length() - MIL.code().length()));

                return dVal / MIL.mult; // divide the  value by 39.370
            }
            if (sVal.endsWith(MM.code())) {
                Double dVal = Double.valueOf(sVal.substring(0, sVal.length() - MM.code().length()));
                return dVal / MM.mult;
            }
            if (sVal.endsWith(INCH.code())) {
                Double dVal = Double.valueOf(sVal.substring(0, sVal.length() - INCH.code().length()));
                return dVal / INCH.mult;
            }
            if (sVal.endsWith(MIC.code())) {
                Double dVal = Double.valueOf(sVal.substring(0, sVal.length() - MIC.code().length()));
                return dVal / MIC.mult;
            } else {
                Double dVal = Double.valueOf(sVal.substring(0, sVal.length()));
                return dVal;
            }
        }

        public static List<String> asCodeList() {
            ArrayList<String> list = new ArrayList<>();
            for (Unit u : Unit.values()) {
                list.add(u.code());
            }

            return list;
        }

        public String fName() {
            return label;
        }

        public Class clazz() {
            return Double.class;
        }

    }

    public UnitValue getUnitValue();

}
