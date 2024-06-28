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

import com.maehem.mangocad.model.element.Element;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public interface SelectableProperty {

    public enum Field {
        SELECTED("selected", Boolean.class),
        PICKED("picked", Boolean.class);

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

    public void createSnapshot();

    public void restoreSnapshot();

    public Element getSnapshot();

    public boolean isSelected();

    public void setSelected(boolean selected);

    public boolean isPicked();

    public void setPicked(boolean picked);
}
