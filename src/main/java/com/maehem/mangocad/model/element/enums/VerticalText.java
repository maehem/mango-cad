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
package com.maehem.mangocad.model.element.enums;

/**
 * verticaltext       up | down
 * 
 * @author Mark J Koch ( @maehem on GitHub)
 */
public enum VerticalText {
    UP("up"), DOWN("down");
    private final String code;
 
    private VerticalText(String code) {
        this.code = code;
    }
 
    public String code() {
        return code;
    }
 
    public static VerticalText fromCode(String code) {
        if (code != null) {
            for (VerticalText g : VerticalText.values()) {
                if (code.equalsIgnoreCase(g.code)) {
                    return g;
                }
            }
        }
        return null;
    }
    
}
