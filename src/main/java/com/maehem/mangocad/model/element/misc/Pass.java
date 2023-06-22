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
package com.maehem.mangocad.model.element.misc;

import com.maehem.mangocad.model._AQuantum;

/**
 * <pre>
 * ELEMENT pass (param)*>
 *    ATTLIST pass
          name          %String;       #REQUIRED
          refer         %String;       #IMPLIED
          active        %Bool;         "yes"
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Pass extends _AQuantum {

    public static final String ELEMENT_NAME = "pass";

    private String name;
    private String refer;
    private boolean active = true;

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the refer
     */
    public String getRefer() {
        return refer;
    }

    /**
     * @param refer the refer to set
     */
    public void setRefer(String refer) {
        this.refer = refer;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
