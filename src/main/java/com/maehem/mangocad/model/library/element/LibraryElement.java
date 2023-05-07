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
package com.maehem.mangocad.model.library.element;

import java.util.ArrayList;
import java.util.List;
import com.maehem.mangocad.model.library.element.quantum._AQuantum;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public abstract class LibraryElement {
    private String name;
    private ArrayList<Description> descriptions = new ArrayList<>();
    private List<_AQuantum> elements = new ArrayList<>();
    private String displayUnits = "mm";

    /**
     * @return the displayUnits
     */
    public String getDisplayUnits() {
        return displayUnits;
    }

    /**
     * @param displayUnits the displayUnits to set
     */
    public void setDisplayUnits(String displayUnits) {
        this.displayUnits = displayUnits;
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

    public String getDescription() {
        if ( !getDescriptions().isEmpty() ) {
            return getDescriptions().get(0).getValue();
        } else {
            return "";
        }
    }
    
    /**
     * @return the descriptions
     */
    public ArrayList<Description> getDescriptions() {
        return descriptions;
    }

    /**
     * @param descriptions the descriptions to set
     */
    public void setDescriptions(ArrayList<Description> descriptions) {
        this.descriptions = descriptions;
    }
    
    /**
     * @return the elements
     */
    public List<_AQuantum> getElements() {
        return elements;
    }

    /**
     * @param elements the elements to set
     */
    public void setElements(List<_AQuantum> elements) {
        this.elements = elements;
    }
}
