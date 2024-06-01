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

import com.maehem.mangocad.model.element.basic.Vertex;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public enum ElementPolygonField {
    WIDTH("width", Double.class),
    LAYER("layer", Integer.class),
    SELECTED("selected", Boolean.class),
    VERTEX("vertex", Vertex.class);

    private final String fName;
    private final Class clazz;

    private ElementPolygonField(String name, Class clazz) {
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
