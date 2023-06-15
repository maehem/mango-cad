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
import com.maehem.mangocad.model.element.enums.Severity;

/**
 * <pre>
 * note (#PCDATA)>
 *    ATTLIST note
          version       %Real;         #REQUIRED
          severity      %Severity;     #REQUIRED
          
          version: The EAGLE file version that introduced this compatibility note
 * </pre>
 * 
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Note extends _AQuantum {
    public static final String ELEMENT_NAME = "note";
    
    private double version = 0;
    private Severity severity = Severity.INFO;
    
    // A future feature?
    // private String locale = "us_EN"; // The i18n locale code for this text. No value = en_US
    private String value = "";       // The #PCDATA text
    //private int timestamp;           // When note created/modified (YYYYMMDDHHmmSS)

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

//    /**
//     * @return the locale
//     */
//    public String getLocale() {
//        return locale;
//    }

//    /**
//     * @param locale the locale to set
//     */
//    public final void setLocale(String locale) {
//        this.locale = locale;
//    }

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

//    /**
//     * @return the timestamp
//     */
//    public int getTimestamp() {
//        return timestamp;
//    }
//
//    /**
//     * @param timestamp the timestamp to set
//     */
//    public void setTimestamp(int timestamp) {
//        this.timestamp = timestamp;
//    }

    /**
     * @return the version
     */
    public double getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(double version) {
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
}
