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

import com.maehem.mangocad.model._AQuantum;
import java.util.ArrayList;
import com.maehem.mangocad.model.element.basic.SchematicGroup;
import com.maehem.mangocad.model.element.misc.Description;
import com.maehem.mangocad.model.element.basic.Attribute;
import com.maehem.mangocad.model.element.basic.ElementElement;
import com.maehem.mangocad.model.element.basic.VariantDefinition;
import com.maehem.mangocad.model.element.highlevel.MfgPreviewColor;
import com.maehem.mangocad.model.element.highlevel.Signal;
import com.maehem.mangocad.model.element.misc.Approved;
import com.maehem.mangocad.model.element.misc.DesignRules;
import com.maehem.mangocad.model.element.misc.FusionSync;
import com.maehem.mangocad.model.element.misc.FusionTeam;
import com.maehem.mangocad.model.element.misc.NetClass;
import com.maehem.mangocad.model.element.misc.Pass;
import java.util.List;

/**
 * <pre>
 *  ELEMENT board (  ? == None or One
 *      description?,  X
 *      fusionsync?,   X
 *      fusionteam?,   X
 *      plain?,        X
 *      libraries?,    X
 *      attributes?,   X
 *      variantdefs?,  X
 *      classes?,      X
 *      designrules?,  X
 *      autorouter?,   X
 *      groups?,       X
 *      elements?,     X
 *      signals?,      X
 *      mfgpreviewcolors?, X
 *      errors?        X
 *   )
 *   ATTLIST
 *       limitedwidth  %Dimension;    #IMPLIED
 *        
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Board extends _AQuantum implements DesignObject {

    public static final String FILE_EXTENSION = "brd";
    public static final String ELEMENT_NAME = "board";

    // TODO: Should be a list of localized Descriptions.
    private final Description description = new Description(); // Might have locale setting.

    private final ArrayList<Library> libraries = new ArrayList<>();
    private final ArrayList<Attribute> attributes = new ArrayList<>();
    private final ArrayList<_AQuantum> plain = new ArrayList<>();
    private final ArrayList<VariantDefinition> variantDefs = new ArrayList<>();
    private final ArrayList<NetClass> netClasses = new ArrayList<>();
    private final ArrayList<Pass> autorouter = new ArrayList<>();
    private final ArrayList<SchematicGroup> groups = new ArrayList<>();
    private final ArrayList<ElementElement> elements = new ArrayList<>();
    private final ArrayList<Signal> signals = new ArrayList<>();
    private final ArrayList<MfgPreviewColor> mfgPreviewColors = new ArrayList<>();
    private final ArrayList<Approved> errors = new ArrayList<>();
    
    private DesignRules designRules = new DesignRules();
    private final FusionSync fusionSync = new FusionSync();
    private final FusionTeam fusionTeam = new FusionTeam();
    
    private double limitedWidth;
        
    private String filePath;
    private Drawing parentDrawing = null;

    public Board() {

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
     * @return the groups
     */
    public ArrayList<SchematicGroup> getGroups() {
        return groups;
    }

    /**
     * @return the errors
     */
    public ArrayList<Approved> getErrors() {
        return errors;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }
    
    public List<_AQuantum> getPlain() {
        return plain;
    }

    /**
     * @return the limitedWidth
     */
    public double getLimitedWidth() {
        return limitedWidth;
    }

    /**
     * @param limitedWidth the limitedWidth to set
     */
    public void setLimitedWidth(double limitedWidth) {
        this.limitedWidth = limitedWidth;
    }

    /**
     * @return the elements
     */
    public ArrayList<ElementElement> getElements() {
        return elements;
    }

    /**
     * @return the fusionSync
     */
    public FusionSync getFusionSync() {
        return fusionSync;
    }

    /**
     * @return the fusionTeam
     */
    public FusionTeam getFusionTeam() {
        return fusionTeam;
    }

    /**
     * @return the autorouter
     */
    public ArrayList<Pass> getAutorouter() {
        return autorouter;
    }

    /**
     * @return the designRules
     */
    public DesignRules getDesignRules() {
        return designRules;
    }

    /**
     * @param designRules the designRules to set
     */
    public void setDesignRules(DesignRules designRules) {
        this.designRules = designRules;
    }

    /**
     * @return the signals
     */
    public ArrayList<Signal> getSignals() {
        return signals;
    }

    /**
     * @return the mfgPreviewColors
     */
    public ArrayList<MfgPreviewColor> getMfgPreviewColors() {
        return mfgPreviewColors;
    }
    
}
