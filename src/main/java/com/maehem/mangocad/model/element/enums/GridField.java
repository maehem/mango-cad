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
public enum GridField {
    DISTANCE("distance", Double.class),
    DISTANCE_STORED_UNIT("distanceStoredUnit", GridUnit.class),
    DISTANCE_UNIT("distanceUnit", GridUnit.class),
    STYLE("style", GridStyle.class),
    MULTIPLE("multiple", Integer.class),
    DISPLAY("display", Boolean.class),
    ALT_SIZE("altSize", Double.class),
    ALT_STORED_UNIT("altStoredUnit", GridUnit.class),
    ALT_UNIT("altUnit", GridUnit.class);

    private final String fName;
    private final Class clazz;

    private GridField(String name, Class clazz) {
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
