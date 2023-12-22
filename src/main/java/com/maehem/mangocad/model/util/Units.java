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
package com.maehem.mangocad.model.util;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Units {

    public static final String MIL = "mil";
    public static final String MM = "mm";

    /**
     * Convert a string, possibly affixed with units to millimeters. Example:
     * "10mil" would return the converted Double value 0.254
     *
     * @param sVal
     * @return double value in mm, -1 if error.
     */
    public static Double toMM(String sVal) {
        if (sVal.endsWith(MIL)) {
            Double dVal = Double.valueOf(sVal.substring(0, sVal.length() - MIL.length()));

            return dVal / 39.370; // divide the  value by 39.370
        }
        if (sVal.endsWith(MM)) {
            Double dVal = Double.valueOf(sVal.substring(0, sVal.length() - MM.length()));
            return dVal;
        } else {
            Double dVal = Double.valueOf(sVal.substring(0, sVal.length()));
            return dVal;
        }

    }
}