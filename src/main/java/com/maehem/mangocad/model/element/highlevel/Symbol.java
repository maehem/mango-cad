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

import com.maehem.mangocad.model.element.misc.Description;
import com.maehem.mangocad.model._AQuantum;
import java.util.ArrayList;
import java.util.List;

/**
 * symbol ( description?, (polygon | wire | text | dimension | pin | circle | rectangle | frame)* )
 *   attributes
 *         name          %String;       #REQUIRED
 *         urn              %Urn;       ""
 *         locally_modified %Bool;      "no"
 *         library_version  %Int;       ""
 *         library_locally_modified %Bool; "no"
 *         
 *         library_version and library_locally_modified: Only in managed libraries inside boards or schematics
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Symbol extends _AQuantum {

    public static final String ELEMENT_NAME = "symbol";
    
     // There can only be one description.
    private final Description description = new Description();
    private final ArrayList<_AQuantum> elements = new ArrayList<>();
    
    private String name;
    private String urn;
    private boolean locallyModified = false;
    private int libraryVersion;
    private boolean libraryLocallyModified = false;
    
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
     * @return the urn
     */
    public String getUrn() {
        return urn;
    }

    /**
     * @param urn the urn to set
     */
    public void setUrn(String urn) {
        this.urn = urn;
    }

    /**
     * @return the locallyModified
     */
    public boolean isLocallyModified() {
        return locallyModified;
    }

    /**
     * @param locallyModified the locallyModified to set
     */
    public void setLocallyModified(boolean locallyModified) {
        this.locallyModified = locallyModified;
    }

    /**
     * @return the libraryVersion
     */
    public int getLibraryVersion() {
        return libraryVersion;
    }

    /**
     * @param libraryVersion the libraryVersion to set
     */
    public void setLibraryVersion(int libraryVersion) {
        this.libraryVersion = libraryVersion;
    }

    /**
     * @return the libraryLocallyModified
     */
    public boolean isLibraryLocallyModified() {
        return libraryLocallyModified;
    }

    /**
     * @param libraryLocallyModified the libraryLocallyModified to set
     */
    public void setLibraryLocallyModified(boolean libraryLocallyModified) {
        this.libraryLocallyModified = libraryLocallyModified;
    }

    public List<_AQuantum> getElements() {
        return elements;
    }
    
    public Description getDescription() {        
        return description;
    }
}
