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
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.enums.PinDirection;
import java.util.ArrayList;
import java.util.List;

/**
 * symbol ( description?, (polygon | wire | text | dimension | pin | circle | rectangle | frame)* )
 *   attributes
 *         name          %String;       #REQUIRED
 *         urn              %Urn;       ""
 *         locally_modified %Bool;      "no"
 *         library_version  %Int;       ""
 *         library_locally_modified %Bool; "no"
 *
 *         library_version and library_locally_modified: Only in managed libraries inside boards or schematics
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Symbol extends LibraryElement {

    public static final String ELEMENT_NAME = "symbol";
    public static final int LAYER_NUMBER = 94;

    private final ArrayList<Element> elements = new ArrayList<>();

    public Symbol() {
    }

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public List<Element> getElements() {
        return elements;
    }

    /**
     *
     * @return VALUE of pin if Supply or null if symbol is not a Supply pin.
     */
    public String supplyPin() {
        List<Element> list = elements.stream().filter((el) -> ( el instanceof Pin)  ).toList();

        if ( list.size() == 1 ) {
            Pin pin = (Pin)(list.get(0));
            if ( pin.getDirection().equals(PinDirection.SUPPLY) ) {
                return pin.getName();
            }
        }

        return null;
    }

    public int getLayerNum() {
        return LAYER_NUMBER;
    }
}
