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
/*
 * EagleCAD's Pin Direction;  
 * http://blog.ilektronx.com/2012/04/advanced-eagle-layout-tutorial-building.html
 *
 *
 */
package com.maehem.mangocad.model.element.enums;

/**
 * PinDirection      nc | in | out | io | oc | pwr | pas | hiz | sup)
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public enum PinDirection {
    NO_CONNECT("nc"), INPUT("in"), OUTPUT("out"), IO("io"),
    OPEN_COLLECTOR("oc"), POWER("pwr"), PASSIVE("pas"), 
    HI_IMPEDANCE("hiz"), SUPPLY("sup");
    
    private final String code;
 
    private PinDirection(String code) {
        this.code = code;
    }
 
    public String code() {
        return code;
    }
 
    public static PinDirection fromCode(String code) {
        if (code != null) {
            for (PinDirection g : PinDirection.values()) {
                if (code.equalsIgnoreCase(g.code())) {
                    return g;
                }
            }
        }
        return null;
    }
    
}
