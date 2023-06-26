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

import com.maehem.mangocad.model.util.Rotation;

/**
 * <pre>
 * label (no sub-nodes)
 *   ATTLIST label
 *          x             %Coord;        #REQUIRED
 *          y             %Coord;        #REQUIRED
 *          size          %Dimension;    #REQUIRED
 *          layer         %Layer;        #REQUIRED
 *          font          %TextFont;     "proportional"
 *          ratio         %Int;          "8"
 *          rot           %Rotation;     "R0"
 *          xref          %Bool;         "no"
 *          align         %Align;        "bottom-left"
 *          grouprefs     IDREFS         #IMPLIED
 *
 *           <!-- rot:  Only 0, 90, 180 or 270 -->
 * //          <!-- xref: Only in <net> context --> *
 * </pre>
 *
 * Almost same attributes as TextElement except: - we don't use 'distance' -
 * rotation is CONSTRAINED - we add 'xref'
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LabelElement extends ElementText {

    public static final String ELEMENT_NAME = "label";

    private boolean xRef = false;

    public LabelElement() {
        getRotation().setConstrained(Rotation.CONSTRAINED);
    }

    public boolean isXref() {
        return xRef;
    }

    public void setXref(boolean val) {
        xRef = val;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

}
