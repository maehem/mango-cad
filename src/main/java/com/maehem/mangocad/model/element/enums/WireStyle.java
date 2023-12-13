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

import java.util.ArrayList;
import java.util.List;

/**
 * WireStyle continuous | longdash | shortdash | dashdot
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public enum WireStyle {
    CONTINUOUS("continuous"), LONGDASH("longdash"), SHORTDASH("shortdash"), DASHDOT("dashdot");
    private final String code;

    public static List<Double> LONG_DASH_PATTERN = new ArrayList<>(List.of(5.0, 1.4));
    public static List<Double> SHORT_DASH_PATTERN = new ArrayList<>(List.of(1.2, 0.9));
    public static List<Double> DASH_DOT_PATTERN = new ArrayList<>(List.of(3.1, 0.8, 0.2, 0.8));

    private WireStyle(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static WireStyle fromCode(String code) {
        if (code != null) {
            for (WireStyle g : WireStyle.values()) {
                if (code.equalsIgnoreCase(g.code)) {
                    return g;
                }
            }
        }
        return null;
    }

    public List<Double> getPattern() {
        List<Double> rtn;
        switch (this) {
            case DASHDOT ->
                rtn = DASH_DOT_PATTERN;
            case LONGDASH ->
                rtn = LONG_DASH_PATTERN;
            case SHORTDASH ->
                rtn = SHORT_DASH_PATTERN;
            default ->
                rtn = null;
        }

        return rtn;
    }
}
