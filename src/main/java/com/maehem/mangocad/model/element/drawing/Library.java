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

import java.util.ArrayList;
import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.misc.Description;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Package3d;
import com.maehem.mangocad.model.element.highlevel.Symbol;

/**
 * library ( description?, packages?, packages3d?, symbols?, devicesets? )
 *    name          %String;       #REQUIRED
 *    urn           %Urn;          ""
 *    
 *    name: Only in libraries used inside boards or schematics
 *    urn:  Only in online libraries used inside boards or schematics
 * 
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Library extends _AQuantum implements DesignObject {

    public static final String FILE_EXTENSION = "lbr";
    public static final String ELEMENT_NAME = "library";

    //private final ColorPalette colorPalette = new ColorPalette(ColorPalette.Style.DARK);

    //private final Grid grid = new Grid();
    //private final ArrayList<Setting> settings = new ArrayList<>();
    
    //private final ArrayList<Note> notes = new ArrayList<>();
    //private final LayerElement layers[] = new LayerElement[256];
    //private final ArrayList<Filter> filters = new ArrayList<>();

    private final ArrayList<Description> descriptions = new ArrayList<>();
    private final ArrayList<Footprint> packages = new ArrayList<>();
    private final ArrayList<Package3d> packages3d = new ArrayList<>();
    private final ArrayList<DeviceSet> deviceSets = new ArrayList<>();
    private final ArrayList<Symbol> symbols = new ArrayList<>();

    private String filePath;
    private Drawing parentDrawing = null;
    
    private String name;// Only in libraries used inside boards or schematics
    private String urn; // Only in online libraries used inside boards or schematics

    public Library() {
        //layers[0] = new LayerElement();
    }

    @Override
    public Drawing getParentDrawing() {
        return parentDrawing;
    }

    @Override
    public void setParentDrawing(Drawing parent) {
        this.parentDrawing = parent;
    }

    
    
//    public ColorPalette getPalette() {
//        return colorPalette;
//    }
//
//    public Grid getGrid() {
//        return grid;
//    }
//    
    // TODO:  Add getDescription based on local.
    /**
     * @return the description
     */
    public ArrayList<Description> getDescriptions() {
        return descriptions;
    }

//    /**
//     * @param descriptions the descriptions to set
//     */
//    public void setDescriptions(ArrayList<Description> descriptions) {
//        this.descriptions = descriptions;
//    }
//
    public String getDescription() {
        if (!getDescriptions().isEmpty()) {
            return getDescriptions().get(0).getValue();
        } else {
            return "";
        }
    }

    public static String getDescriptionShort(String longDesc) {
        String[] s1 = longDesc.split("\\n");
        String[] s2 = s1[0].split("<br\\s*\\/?>");
        return s2[0];
    }

//    /**
//     * @return the layers
//     */
//    public LayerElement[] getLayers() {
//        return layers;
//    }
//
//    public int getIndexForLayer(int layerNum) {
//        LayerElement layer = layers[layerNum];
//
//        if (layer != null) {
//            return layer.getColorIndex();
//        } else {
//            return -1;
//        }
//    }
//
    /**
     * @return the packages
     */
    public ArrayList<Footprint> getPackages() {
        return packages;
    }

    public Footprint getPackage(String pkgName) {
        for (Footprint fp : getPackages()) {
            if (fp.getName().equals(pkgName)) {
                return fp;
            }
        }
        return null;
    }

//    /**
//     * @param packages the packages to set
//     */
//    public void setPackages(ArrayList<Footprint> packages) {
//        this.packages = packages;
//    }
//
    /**
     * @return the symbols
     */
    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

//    /**
//     * @param symbols the symbols to set
//     */
//    public void setSymbols(ArrayList<Symbol> symbols) {
//        this.symbols = symbols;
//    }
//
    public Symbol getSymbol(String symbolName) {
        for (Symbol s : getSymbols()) {
            if (s.getName().equals(symbolName)) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return the deviceSets
     */
    public ArrayList<DeviceSet> getDeviceSets() {
        return deviceSets;
    }
    
    public DeviceSet getDeviceSet( String name ) {
        for ( DeviceSet ds: getDeviceSets() ) {
            if ( ds.getName().equals(name) ) {
                return ds;
            }
        }
        return null;
    }

//    /**
//     * @param deviceSets the devices to set
//     */
//    public void setDeviceSets(ArrayList<DeviceSet> deviceSets) {
//        this.deviceSets = deviceSets;
//    }
//
//    /**
//     * @return the notes
//     */
//    public ArrayList<Note> getNotes() {
//        return notes;
//    }

//    /**
//     * @param notes the notes to set
//     */
//    public void setNotes(ArrayList<Note> notes) {
//        this.notes = notes;
//    }

//    public void addNote(String string) {
//        getNotes().add(new Note(string));
//    }

    /**
     * @return the deviceSets
     */
    public ArrayList<Package3d> getPackages3d() {
        return packages3d;
    }
    
    public Package3d getPackage3D( String pkgName ) {
        for ( Package3d pkg: getPackages3d() ) {
            if (pkg.getName().equals(pkgName )) {
                return pkg;
            }
        }
        
        return null;
    }

//    /**
//     * @param packages3d the 3D Packages to set
//     */
//    public void setPackages3d(ArrayList<Package3d> packages3d) {
//        this.packages3d = packages3d;
//    }
//
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

//    public List<Setting> getSettings() {
//        return settings;
//    }
//    
//    public List<Filter> getFilters() {
//        return filters;
//    }
    

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }
}
