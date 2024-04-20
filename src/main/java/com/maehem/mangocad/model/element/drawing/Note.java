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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.enums.Severity;

/**
 * Autodesk added 'minversion' at some point.
 * 
 * <pre>
 * note (#PCDATA)>
 *    ATTLIST note
          version       %Real;         #REQUIRED
          minversion    %Real;         ""
          severity      %Severity;     #REQUIRED
          
          version: The EAGLE file version that introduced this compatibility note
 * </pre>
 * 
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Note extends Element {
    public static final String ELEMENT_NAME = "note";
    
    private String version = "";
    private String minVersion = "";
    private Severity severity = Severity.INFO;
    
    private String value = "";       // The #PCDATA text

    public Note() {
    }
    
    public Note(String value) {
        setValue(value);
    }

//    Note(String locale, String value) {
//        this(value);
//        setLocale(locale);
//    }
    
    @Override
    public String getElementName() {
        return ELEMENT_NAME;
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
    public final void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the severity
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * @param severity the severity to set
     */
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    /**
     * @return the version
     */
    public String getMinVersion() {
        return minVersion;
    }

    /**
     * @param version the version to set
     */
    public void setMinVersion(String version) {
        this.minVersion = version;
    }

}
