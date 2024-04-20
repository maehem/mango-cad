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
package com.maehem.mangocad.model.element.highlevel;

import com.maehem.mangocad.model.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * ELEMENT signal (contactref | polygon | wire | via)*>
 *   ATTLIST signal
 *      name          %String;       #REQUIRED
 *      class         %Class;        "0"
 *      airwireshidden %Bool;        "no"
 * </pre>

 Does not use Element.layerNum! Signal sub-elements (wire, polygon, etc.)
 each define their own layer number.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Signal extends Element {

    public static final String ELEMENT_NAME = "signal";

    private String name;
    private int netClass = 0;
    private boolean airwiresHidden = false;

    private final ArrayList<Element> elements = new ArrayList<>();


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
     * @return the netClass
     */
    public int getNetClassNum() {
        return netClass;
    }

    /**
     * @param netClass the netClass to set
     */
    public void setNetClass(int netClass) {
        this.netClass = netClass;
    }

    /**
     * @return the airwiresHidden
     */
    public boolean isAirwiresHidden() {
        return airwiresHidden;
    }

    /**
     * @param airwiresHidden the airwiresHidden to set
     */
    public void setAirwiresHidden(boolean airwiresHidden) {
        this.airwiresHidden = airwiresHidden;
    }

    /**
     * @return the element of type (contactref | polygon | wire | via)
     */
    public List<Element> getElements() {
        return elements;
    }
}
