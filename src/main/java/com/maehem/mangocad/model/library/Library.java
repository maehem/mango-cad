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
package com.maehem.mangocad.model.library;

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.LayerElement;
import java.util.ArrayList;
import com.maehem.mangocad.model.library.element.Description;
import com.maehem.mangocad.model.library.element.DeviceSet;
import com.maehem.mangocad.model.library.element.Note;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.Package3d;
import com.maehem.mangocad.model.library.element.Symbol;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Library {

    public static final String FILE_EXTENSION = "mclib";

    private final ColorPalette colorPalette = new ColorPalette(ColorPalette.Style.DARK);

    private ArrayList<Note> notes = new ArrayList<>();
    // Descriptions is a list of this library descriptions for each language
    // supported.
    private final LayerElement layers[] = new LayerElement[256];

    private ArrayList<Description> descriptions = new ArrayList<>();
    private ArrayList<Footprint> packages = new ArrayList<>();
    private ArrayList<Symbol> symbols = new ArrayList<>();
    private ArrayList<DeviceSet> deviceSets = new ArrayList<>();
    private ArrayList<Package3d> packages3d = new ArrayList<>();

    private String filePath;

    public Library() {
        layers[0] = new LayerElement();
    }

    public ColorPalette getPalette() {
        return colorPalette;
    }

    // TODO:  Add getDescription based on local.
    /**
     * @return the description
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

    /**
     * @param packages the packages to set
     */
    public void setPackages(ArrayList<Footprint> packages) {
        this.packages = packages;
    }

    /**
     * @return the symbols
     */
    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    /**
     * @param symbols the symbols to set
     */
    public void setSymbols(ArrayList<Symbol> symbols) {
        this.symbols = symbols;
    }

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

    /**
     * @param deviceSets the devices to set
     */
    public void setDeviceSets(ArrayList<DeviceSet> deviceSets) {
        this.deviceSets = deviceSets;
    }

    /**
     * @return the notes
     */
    public ArrayList<Note> getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public void addNote(String string) {
        getNotes().add(new Note(string));
    }

    /**
     * @return the deviceSets
     */
    public ArrayList<Package3d> getPackages3d() {
        return packages3d;
    }

    /**
     * @param packages3d the 3D Packages to set
     */
    public void setPackages3d(ArrayList<Package3d> packages3d) {
        this.packages3d = packages3d;
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

}
