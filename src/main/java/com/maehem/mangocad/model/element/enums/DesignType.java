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
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public enum DesignType {
    Library("Library", "lbr"), Board("Board", "brd"), Schematic("Schematic", "sch");

    private final String code;
    private final String fileExt;

    private DesignType(String code, String fileExt) {
        this.code = code;
        this.fileExt = fileExt;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the fileExt
     */
    public String getFileExt() {
        return fileExt;
    }

}
