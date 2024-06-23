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
package com.maehem.mangocad.model.element.drawing;

import com.maehem.mangocad.model.element.Element;

/**
 * High level Drawing thing called 'Filter'.  No idea what it does or how to use it.
 * I can find no documentation for this feature.  None of my text files store
 * this data. It might be something from older Eagle files and not used any more?
 * 
 * <pre>
    filter EMPTY>
        ATTLIST filter
          name          %String;       #REQUIRED
          expression    %String;       #REQUIRED
 * </pre>
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Filter extends Element {
    
    public static final String ELEMENT_NAME = "filter";

    private String name;
    private String expression;

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
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @param expression the expression to set
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }
    
}
