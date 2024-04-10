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
 * PinFunction       none | dot | clk | dotclk
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public enum PinFunction {
    NONE("none"), DOT("dot"), CLK("clk"), DOTCLK("dotclk");

    private final String code;

    public static final String COMMAND_KEY = "function";

    private PinFunction(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static PinFunction fromCode(String code) {
        if (code != null) {
            for (PinFunction g : PinFunction.values()) {
                if (code.equalsIgnoreCase(g.code())) {
                    return g;
                }
            }
        }
        return null;
    }

}
