/*
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF
    licenses this file to you under the Apache License, Version 2.0
    (the "License"), you may not use this file except in compliance with the
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
    License for the specific language governing permissions and limitations
    under the License.
 */
package com.maehem.mangocad.model.element.misc;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class WireWidthDefaults {

    public static Double[] values() {
        return new Double[]{
            0.762, // 0.003 inch (3 mil)
            0.1016,// 0.004 inch (4 mil)
            0.127, // 0.005 inch (5 mil)
            0.15, // 0.15  mm
            0.1524,// 0.006 inch (6 mil)
            0.2, // 0.2   mm
            0.2032,// 0.008 inch (8mil)
            0.254, // 0.01  inch (10 mil)
            0.3048,// 0.012 inch
            0.4064,// 0.016 inch
            0.508, // 0.02  inch
            0.6096,// 0.24  inch
            0.8128,// 0.32  inch
            1.016, // 0.04  inch
            1.27, // 0.05  inch
            2.54 // 0.1   inch
        };
    }
}
