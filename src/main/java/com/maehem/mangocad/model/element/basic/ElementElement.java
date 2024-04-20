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
package com.maehem.mangocad.model.element.basic;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.util.Rotation;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * ELEMENT element (attribute*, variant*)
 *    variant* is accepted only for compatibility with EAGLE 6.x files
 *    ATTLIST element
 *      name          %String;       #REQUIRED
 *      library       %String;       #REQUIRED
 *      library_urn   %Urn;          ""
 *      package       %String;       #REQUIRED
 *      package3d_urn %Urn;          ""
 *      override_package3d_urn %Urn; ""
 *      override_package_urn %Urn;    ""
 *      override_locally_modified %Bool; "no"
 *      value         %String;       #REQUIRED
 *      x             %Coord;        #REQUIRED
 *      y             %Coord;        #REQUIRED
 *      locked        %Bool;         "no"
 *      populate      %Bool;         "yes"
 *      smashed       %Bool;         "no"
 *      rot           %Rotation;     "R0"
 *      grouprefs     IDREFS         #IMPLIED
 *
 *      library_urn: Only in parts from online libraries
 *
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ElementElement extends Element {

    public static final String ELEMENT_NAME = "element";

    private String name;
    private String library;
    private Library libraryObj;
    private String libraryUrn;
    private String footprint;
    private Footprint footprintPkg;
    private String overridePackageUrn;
    private String package3dUrn;
    private String overridePackage3dUrn;
    private boolean overrideLocallyModified;
    private String value;
    private double x;
    private double y;
    private boolean locked;
    private boolean populate;
    private boolean smashed;
    private final Rotation rot = new Rotation(Rotation.UNCONSTRAINED);
    private final ArrayList<String> grouprefs = new ArrayList<>();

    private final List<Attribute> attributes = new ArrayList<>();

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
     * @return the library
     */
    public String getLibrary() {
        return library;
    }

    /**
     * @param library the library to set
     */
    public void setLibrary(String library) {
        this.library = library;
    }

    /**
     * @return the library
     */
    public Library getLibraryObj() {
        return libraryObj;
    }

    /**
     * @param library the library to set
     */
    public void setLibraryObj(Library lib) {
        this.libraryObj = lib;
    }

    /**
     * @return the libraryUrn
     */
    public String getLibraryUrn() {
        return libraryUrn;
    }

    /**
     * @param libraryUrn the libraryUrn to set
     */
    public void setLibraryUrn(String libraryUrn) {
        this.libraryUrn = libraryUrn;
    }

    /**
     * @return the package3dUrn
     */
    public String getPackage3dUrn() {
        return package3dUrn;
    }

    /**
     * @param package3dUrn the package3dUrn to set
     */
    public void setPackage3dUrn(String package3dUrn) {
        this.package3dUrn = package3dUrn;
    }

    /**
     * @return the overridePackage3dUrn
     */
    public String getOverridePackage3dUrn() {
        return overridePackage3dUrn;
    }

    /**
     * @param overridePackage3dUrn the overridePackage3dUrn to set
     */
    public void setOverridePackage3dUrn(String overridePackage3dUrn) {
        this.overridePackage3dUrn = overridePackage3dUrn;
    }

    /**
     * @return the overridePackageUrn
     */
    public String getOverridePackageUrn() {
        return overridePackageUrn;
    }

    /**
     * @param overridePackageUrn the overridePackageUrn to set
     */
    public void setOverridePackageUrn(String overridePackageUrn) {
        this.overridePackageUrn = overridePackageUrn;
    }

    /**
     * @return the overrideLocallyModified
     */
    public boolean isOverrideLocallyModified() {
        return overrideLocallyModified;
    }

    /**
     * @param overrideLocallyModified the overrideLocallyModified to set
     */
    public void setOverrideLocallyModified(boolean overrideLocallyModified) {
        this.overrideLocallyModified = overrideLocallyModified;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the grouprefs
     */
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }

    public Rotation getRotation() {
        return rot;
    }
    /**
     * @return the rot
     */
    public double getRot() {
        return rot.getValue();
    }

    /**
     * @param val the rot to set
     */
    public void setRot(double val) {
        this.rot.setValue(val);

    }

    /**
     * @return the footprint
     */
    public String getFootprint() {
        return footprint;
    }

    /**
     * @param footprint the footprint to set
     */
    public void setFootprint(String footprint) {
        this.footprint = footprint;
    }

    /**
     * @return the footprint package object
     */
    public Footprint getFootprintPkg() {
        return getLibraryObj().getPackage(getFootprint());
    }

//    /**
//     * @param footprint the footprint to set
//     */
//    public void setFootprintPkg(Footprint pkg) {
//        this.footprintPkg = pkg;
//    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * @return the populate
     */
    public boolean isPopulate() {
        return populate;
    }

    /**
     * @param populate the populate to set
     */
    public void setPopulate(boolean populate) {
        this.populate = populate;
    }

    /**
     * @return the smashed
     */
    public boolean isSmashed() {
        return smashed;
    }

    /**
     * @param smashed the smashed to set
     */
    public void setSmashed(boolean smashed) {
        this.smashed = smashed;
    }

    /**
     * @return the attributes
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

}
