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
package com.maehem.mangocad.model.element.enums;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public enum WireField {
    X1("x1", Double.class), Y1("y1", Double.class),
    X2("x2", Double.class), Y2("y2", Double.class),
    END("selectedEnd", WireEnd.class),
    //WIDTH("width", Double.class),
    LAYER("layer", Integer.class),
    EXTENT("extent", String.class),
    STYLE("style", WireStyle.class),
    CURVE("curve", Double.class),
    CAP("cap", WireCap.class),
    GROUP_REF("groupRef", String.class);

    private final String fName;
    private final Class clazz;

    private WireField(String name, Class clazz) {
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
