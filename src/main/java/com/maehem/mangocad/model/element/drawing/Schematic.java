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

import com.maehem.mangocad.model.ColorPalette;
import java.util.ArrayList;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.element.basic.Part;
import com.maehem.mangocad.model.element.basic.SchematicGroup;
import com.maehem.mangocad.model.element.misc.Description;
import com.maehem.mangocad.model.element.basic.Attribute;
import com.maehem.mangocad.model.element.basic.VariantDefinition;
import com.maehem.mangocad.model.element.highlevel.Sheet;
import com.maehem.mangocad.model.element.misc.Approved;
import com.maehem.mangocad.model.element.misc.Grid;
import com.maehem.mangocad.model.element.misc.NetClass;
import com.maehem.mangocad.model.element.misc.Setting;
import java.util.List;

/**
 *  ELEMENT schematic (
 *      description?, 
 *      libraries?, 
 *      attributes?, 
 *      variantdefs?, 
 *      classes?, 
 *      modules?, 
 *      groups?, 
 *      parts?, 
 *      sheets?, 
 *      errors?
 *  )
 *  ATTLIST schematic
 *        xreflabel     %String;       #IMPLIED
 *        xrefpart      %String;       #IMPLIED
 * 
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Schematic {

    public static final String FILE_EXTENSION = "sch";

    private final ColorPalette colorPalette = new ColorPalette(ColorPalette.Style.DARK);

    private final Grid grid = new Grid();
    private final ArrayList<Setting> settings = new ArrayList<>();
    
    private final ArrayList<Note> notes = new ArrayList<>();
    private final LayerElement layers[] = new LayerElement[256];
    private final ArrayList<Filter> filters = new ArrayList<>();
    
    // TODO: Should be a list of localized Descriptions.
    private final Description description = new Description(); // Might have locale setting.

    private final ArrayList<Library> libraries = new ArrayList<>();
    private final ArrayList<Attribute> attributes = new ArrayList<>();
    
    private final ArrayList<VariantDefinition> variantDefs = new ArrayList<>();
    private final ArrayList<NetClass> netClasses = new ArrayList<>();
    private final ArrayList<CircuitModule> modules = new ArrayList<>();
    private final ArrayList<SchematicGroup> groups = new ArrayList<>();
    private final ArrayList<Part> parts = new ArrayList<>();
    private final ArrayList<Sheet> sheets = new ArrayList<>();
    private final ArrayList<Approved> errors = new ArrayList<>();
    
    private String xRefLabel;
    private String xRefPart;
    
    private String filePath;

    public Schematic() {
        layers[0] = new LayerElement();
    }
    
    public Grid getGrid() {
        return grid;
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description.setValue(description);
    }

    public Description getDescription() {
        return description;
    }
    
    /**
     * @return the layers
     */
    public LayerElement[] getLayers() {
        return layers;
    }

    public int getIndexForLayer(int layerNum) {
        LayerElement layer = layers[layerNum];

        if (layer != null) {
            return layer.getColorIndex();
        } else {
            return -1;
        }
    }

    public List<Filter> getFilters() {
        return filters;
    }
    
     /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the notes
     */
    public ArrayList<Note> getNotes() {
        return notes;
    }

    // Should not need this.
//    /**
//     * @param notes the notes to set
//     */
//    public void setNotes(ArrayList<Note> notes) {
//        this.notes = notes;
//    }

    public void addNote(String string) {
        getNotes().add(new Note(string));
    }

    /**
     * @return the libraries
     */
    public ArrayList<Library> getLibraries() {
        return libraries;
    }

    /**
     * @return the attributes
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @return the variantDefs
     */
    public ArrayList<VariantDefinition> getVariantDefs() {
        return variantDefs;
    }

    /**
     * @return the netClasses
     */
    public ArrayList<NetClass> getNetClasses() {
        return netClasses;
    }

    /**
     * @return the modules
     */
    public ArrayList<CircuitModule> getModules() {
        return modules;
    }

    /**
     * @return the groups
     */
    public ArrayList<SchematicGroup> getGroups() {
        return groups;
    }

    /**
     * @return the parts
     */
    public ArrayList<Part> getParts() {
        return parts;
    }

    /**
     * @return the sheets
     */
    public ArrayList<Sheet> getSheets() {
        return sheets;
    }

    /**
     * @return the errors
     */
    public ArrayList<Approved> getErrors() {
        return errors;
    }

    public String getXrefLabel() {
        return xRefLabel;
    }
    
    public void setXrefLabel(String value) {
        this.xRefLabel = value;
    }

    public String getXrefPart() {
        return xRefPart;
    }
    public void setXrefPart(String value) {
        this.xRefPart = value;
    }

    public List<Setting> getSettings() {
        return settings;
    }
}
