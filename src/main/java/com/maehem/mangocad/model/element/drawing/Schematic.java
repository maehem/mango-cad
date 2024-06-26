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
import com.maehem.mangocad.model.element.basic.Attribute;
import com.maehem.mangocad.model.element.basic.Part;
import com.maehem.mangocad.model.element.basic.SchematicGroup;
import com.maehem.mangocad.model.element.basic.VariantDefinition;
import com.maehem.mangocad.model.element.enums.DesignType;
import com.maehem.mangocad.model.element.highlevel.Sheet;
import com.maehem.mangocad.model.element.misc.Approved;
import com.maehem.mangocad.model.element.misc.Description;
import com.maehem.mangocad.model.element.misc.NetClass;
import java.util.ArrayList;
import java.util.Optional;

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
public class Schematic extends Element implements DesignObject {

    //public static final String FILE_EXTENSION = "sch";
    public static final String ELEMENT_NAME = "schematic";

    // TODO: Should be a list of localized Descriptions.
    private final Description description = new Description(); // Might have locale setting.

    private final ArrayList<Library> libraries = new ArrayList<>();

    // This does not seem to be used in Eagle nor is there a way to
    // affect Schematic attributes.
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
    private Drawing parentDrawing = null;

    public Schematic() {
        //layers[0] = new LayerElement();
    }

    @Override
    public Drawing getParentDrawing() {
        return parentDrawing;
    }

    @Override
    public void setParentDrawing(Drawing parent) {
        this.parentDrawing = parent;
        libraries.forEach((lib) -> {
            lib.setParentDrawing(parent);
        });
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
     * @return the libraries
     */
    public ArrayList<Library> getLibraries() {
        return libraries;
    }

    public Optional<Library> lookupLibrary( String libName ) {
        return libraries.stream().filter((library) -> library.getName().equals(libName)).findFirst();
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

    public Optional<Part> lookupPart( String partName ) {
        return parts.stream().filter((part) -> part.getName().equals(partName)).findFirst();
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

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getFileExtension() {
        return getType().getFileExt();
    }

    @Override
    public DesignType getType() {
        return DesignType.Schematic;
    }
}
