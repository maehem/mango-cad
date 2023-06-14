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

import com.maehem.mangocad.model._AQuantum;

/**
// *  part  (attribute*, variant*, spice?)
//          name          %String;       #REQUIRED
//          library       %String;       #REQUIRED
//          library_urn   %Urn;          ""
//          deviceset     %String;       #REQUIRED
//          device        %String;       #REQUIRED
//          package3d_urn %Urn;          ""
//          override_package3d_urn %Urn; ""
//          override_package_urn %Urn;    ""
//          override_locally_modified %Bool; "no"
//          technology    %String;       ""
//          value         %String;       #IMPLIED
//
//          library_urn: Only in parts from online libraries
 * 
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Part extends _AQuantum {
    public static final String ELEMENT_NAME = "part";   
    
    private String name;
    private String library;
    private String libraryUrn;
    private String deviceSet;
    private String device;
    private String package3dUrn;
    private String overridePackage3dUrn;
    private String overridePackageUrn;
    private boolean overrideLocallyModified;
    private String technology;
    private String value;
    

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
     * @return the deviceSet
     */
    public String getDeviceSet() {
        return deviceSet;
    }

    /**
     * @param deviceSet the deviceSet to set
     */
    public void setDeviceSet(String deviceSet) {
        this.deviceSet = deviceSet;
    }

    /**
     * @return the device
     */
    public String getDevice() {
        return device;
    }

    /**
     * @param device the device to set
     */
    public void setDevice(String device) {
        this.device = device;
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
     * @return the technology
     */
    public String getTechnology() {
        return technology;
    }

    /**
     * @param technology the technology to set
     */
    public void setTechnology(String technology) {
        this.technology = technology;
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

     
}
