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
package com.maehem.mangocad.model.element.basic;

import com.maehem.mangocad.model._AQuantum;

/**
 * <pre>
 * variant EMPTY
 *      variant
 *      name          %String;       #REQUIRED
 *      populate      %Bool;         "yes"
 *      value         %String;       #IMPLIED
 *      technology    %String;       #IMPLIED
 * 
 *      technology: Only in part context
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Variant extends _AQuantum {

    public static final String ELEMENT_NAME = "variant";

    private String name;
    private boolean populate = true;
    private String value;
    private String technology;

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
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the populate
     */
    public boolean isPopulate() {
        return populate;
    }

    /**
     * @param populate the populate to set
     */
    public void setPopulate(boolean populate) {
        this.populate = populate;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the technology
     */
    public String getTechnology() {
        return technology;
    }

    /**
     * @param technology the technology to set
     */
    public void setTechnology(String technology) {
        this.technology = technology;
    }

}
