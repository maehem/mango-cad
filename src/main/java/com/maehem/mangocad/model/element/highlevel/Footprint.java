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
import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.PadSMD;
import com.maehem.mangocad.model.element.basic.PadTHD;
import com.maehem.mangocad.model.element.misc.Description;
import java.util.ArrayList;
import java.util.List;

/**
 * Eagle used the tern 'package' interchangeably with 3d package instances which was
 * confusing while writing this code.  So I changed 2D 'package' to footprint.
 * Still called 'package' in the XML files.
 * <pre>
 * package (description?, (polygon | wire | text | dimension | circle | rectangle | frame | hole | pad | smd)*)>
 *    ATTLIST
          name          %String;       #REQUIRED
          urn              %Urn;       ""
          locally_modified %Bool;      "no"
          library_version  %Int;       ""
          library_locally_modified %Bool; "no"

          library_version and library_locally_modified:
              Only in managed libraries inside boards or schematics

 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Footprint extends LibraryElement { // class name "Package" is reserved by Java.

    public static final String ELEMENT_NAME = "package";

     // There can only be one description.
    private final Description description = new Description();
    private final ArrayList<Element> elements = new ArrayList<>();

    //private String name;
    //private String urn;
    //private boolean locallyModified = false;
    //private int libraryVersion = 0;
    //private boolean libraryLocallyModified = false;

    public String getElementName() {
        return ELEMENT_NAME;
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
//    public void setUrn(String urn) {
//        this.urn = urn;
//    }
//
//    public String getUrn() {
//        return urn;
//    }

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
//
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

//    public Description getDescription() {
//        return description;
//    }

    public List<Element> getElements() {
        return elements;
    }

    public Element getPad(String padName) {
        for (Element el : getElements()) {
            if (el instanceof PadTHD e) {
                if (e.getName().equals(padName)) {
                    return e;
                }
            } else if (el instanceof PadSMD e) {
                if (e.getName().equals(padName)) {
                    return e;
                }
            }
        }
        return null;
    }

}
