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

import com.maehem.mangocad.model.element.Element;

/**
 * pinref EMPTY>
 *   pinref
          part          %String;       #REQUIRED
          gate          %String;       #REQUIRED
          pin           %String;       #REQUIRED
 * 
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinRef extends Element {
    
    public static final String ELEMENT_NAME = "pinref";

    private String part;
    private String gate;
    private String pin;

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the part
     */
    public String getPart() {
        return part;
    }

    /**
     * @param part the part to set
     */
    public void setPart(String part) {
        this.part = part;
    }

    /**
     * @return the gate
     */
    public String getGate() {
        return gate;
    }

    /**
     * @param gate the gate to set
     */
    public void setGate(String gate) {
        this.gate = gate;
    }

    /**
     * @return the pin
     */
    public String getPin() {
        return pin;
    }

    /**
     * @param pin the pin to set
     */
    public void setPin(String pin) {
        this.pin = pin;
    }
    
}
