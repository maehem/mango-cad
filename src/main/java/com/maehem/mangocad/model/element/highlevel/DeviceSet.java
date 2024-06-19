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

import com.maehem.mangocad.model.LibraryElement;
import com.maehem.mangocad.model.element.basic.Gate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class DeviceSet extends LibraryElement {

    public static final String ELEMENT_NAME = "deviceset";
    //  deviceset (description?, gates, devices, spice?)>
    //      ATTLIST deviceset
    //          name          %String;       #REQUIRED
    //          urn              %Urn;       ""
    //          locally_modified %Bool;      "no"
    //          library_version  %Int;       ""
    //          library_locally_modified %Bool; "no"

    //          prefix        %String;       ""
    //          uservalue     %Bool;         "no"
    //          >
    //          <!-- library_version and library_locally_modified: Only in managed libraries inside boards or schematics -->

//    private String name;
//    private String urn;
//    private boolean locallyModified = false;
//
    private boolean needsSave = false;

    private String prefix = "";
    private boolean uservalue = false;

//    private int libraryVersion = 0;
//    private boolean libraryLocallyModified = false;

//    private final List<Description> descriptions = new ArrayList<>();
    private final List<Gate> gates = new ArrayList<>();
    private final List<Device> devices = new ArrayList<>();
    //private List<Spice> spice = new ArrayList<>();

    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the userValue
     */
    public boolean getUservalue() {
        return isUservalue();
    }

    /**
     * @param userValue the userValue to set
     */
    public void setUservalue(boolean userValue) {
        this.uservalue = userValue;
    }

    /**
     * @return the gates
     */
    public List<Gate> getGates() {
        return gates;
    }

    /**
     * @return the devices
     */
    public List<Device> getDevices() {
        return devices;
    }

//    /**
//     * @return the name
//     */
//    public String getName() {
//        return name;
//    }
//
//    /**
//     * @param name the name to set
//     */
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    /**
//     * @return the urn
//     */
//    public String getUrn() {
//        return urn;
//    }
//
//    /**
//     * @param urn the urn to set
//     */
//    public void setUrn(String urn) {
//        this.urn = urn;
//    }
//
//    /**
//     * @return the locallyModified
//     */
//    public boolean isLocallyModified() {
//        return locallyModified;
//    }
//
//    /**
//     * @param locallyModified the locallyModified to set
//     */
//    public void setLocallyModified(boolean locallyModified) {
//        this.locallyModified = locallyModified;
//    }

    /**
     * @return the uservalue
     */
    public boolean isUservalue() {
        return uservalue;
    }

//    /**
//     * @return the libraryVersion
//     */
//    public int getLibraryVersion() {
//        return libraryVersion;
//    }
//
//    /**
//     * @param libraryVersion the libraryVersion to set
//     */
//    public void setLibraryVersion(int libraryVersion) {
//        this.libraryVersion = libraryVersion;
//    }
//
//    /**
//     * @return the libraryLocallyModified
//     */
//    public boolean isLibraryLocallyModified() {
//        return libraryLocallyModified;
//    }
//
//    /**
//     * @param libraryLocallyModified the libraryLocallyModified to set
//     */
//    public void setLibraryLocallyModified(boolean libraryLocallyModified) {
//        this.libraryLocallyModified = libraryLocallyModified;
//    }

//    /**
//     * @return the descriptions
//     */
//    public List<Description> getDescriptions() {
//        return descriptions;
//    }
//
//    public String getDescription() {
//        if ( descriptions.isEmpty() ) {
//            return "";
//        }
//        // TODO: Return based on user's locale if avaialble.
//        return descriptions.get(0).getValue();
//    }

    public Device getNamedDevice(String named) {
        for ( Device device : devices ) {
            if ( device.getName().equals(named) ) {
                return device;
            }
        }
        return null;
    }

    public Device lookupDevice(String devName) {
        return devices.stream().
                filter(device -> devName.equals(device.getName())).
                findAny().orElse(null);
    }

    public static boolean isValidName(Object nameResult) {
        // TODO: Check for invalid things like characters in name, or name starts with a digit.
        return true;
    }

    /**
     * @return the needsSave
     */
    public boolean isNeedsSave() {
        return needsSave;
    }

    /**
     * @param needsSave the needsSave to set
     */
    public void setNeedsSave(boolean needsSave) {
        this.needsSave = needsSave;
    }

    @Override
    public String toXML() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
