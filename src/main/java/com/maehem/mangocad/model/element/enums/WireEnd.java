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
 * WireEnd - indicates none, one, two or both ends of a wire.
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public enum WireEnd {
    NONE("none"), ONE("one"), TWO("two"), BOTH("both");

    private final String code;

    public static final String COMMAND_KEY = "wire-end";

    private WireEnd(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static WireEnd fromCode(String code) {
        if (code != null) {
            for (WireEnd g : WireEnd.values()) {
                if (code.equalsIgnoreCase(g.code())) {
                    return g;
                }
            }
        }
        return null;
    }

    public static List<String> asStringList() {
        ArrayList<String> list = new ArrayList<>();
        for (WireEnd a : values()) {
            list.add(a.code());
        }

        return list;
    }

}
