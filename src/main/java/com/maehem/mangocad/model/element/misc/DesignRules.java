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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.util.DrcDefs;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 * ELEMENT designrules (description*, param*)>
 *     ATTLIST designrules
 * name          %String;       #REQUIRED
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DesignRules extends Element {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static final String ELEMENT_NAME = "designrules";

    private final ArrayList<Description> descriptions = new ArrayList<>();
    private final ArrayList<Param> params = new ArrayList<>();

    private String name;

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
     * @return a list of the descriptions
     */
    public ArrayList<Description> getDescriptions() {
        return descriptions;
    }

    /**
     * @return a list of the params
     */
    public ArrayList<Param> getParams() {
        return params;
    }

    public String getRule(DrcDefs rule) {
        for (Param p : getParams()) {
            if (p.getName().equals(rule.label)) {
                return p.getValue();
            }
        }
        LOGGER.log(Level.SEVERE, "DesignRules.getRule() could not find a rule called: {0}", rule.label);
        return "";
    }

}
