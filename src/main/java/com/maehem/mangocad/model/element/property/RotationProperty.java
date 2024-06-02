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

import com.maehem.mangocad.model.util.Rotation;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public interface RotationProperty {

    public enum Field {
        SPIN("spin", Boolean.class),
        CONSTRAINED("constrained", Boolean.class),
        ALLOW_SPIN("allowSpin", Boolean.class),
        MIRROR("mirror", Boolean.class),
        ALLOW_MIRROR("allowMirror", Boolean.class),
        VALUE("value", String.class);

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

    public Rotation getRotation();

    public double getRot();

    public void setRot(double value);

    public boolean isSpun();

    public void setSpin(boolean value);

    public boolean isSpin();

    public boolean isSpinAllowed();

    public void setAllowSpin(boolean value);

    public void setMirror(boolean value);

    public boolean isMirrored();

    public boolean isMirrorAllowed();

    public void setAllowMirror(boolean value);

    public boolean isConstrained();

    public void setConstrained(boolean value);

}
