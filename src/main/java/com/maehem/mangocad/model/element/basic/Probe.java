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

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Probe extends TextElement {

    public static final String ELEMENT_NAME = "probe";
    public static final int PROBETYPE_VOLTAGE = 0;
    public static final int PROBETYPE_PHASE = 1;

    // probe ( no sub-nodes )
    //    ATTLIST probe
    //        x             %Coord;        #REQUIRED    (parent)
    //        y             %Coord;        #REQUIRED    (parent)
    //        size          %Dimension;    #REQUIRED    (parent)
    //        layer         %Layer;        #REQUIRED    (parent)
    //        font          %TextFont;     "proportional"  (parent)
    //        ratio         %Int;          "8"          (parent)
    //        rot           %Rotation;     "R0"         (parent)
    //        xref          %Bool;         "no"
    //        grouprefs     IDREFS         #IMPLIED     (parent)
    //
    //      Added recently:
    //        probetype    %Int             "0"  0=voltage, 1=phase
    //
    //        <!-- rot:  Only 0, 90, 180 or 270 -->
    //        <!-- xref: Only in <net> context -->

    private boolean xref = false;
    private int probetype = PROBETYPE_VOLTAGE;

    public Probe() {
        setSize(10.0);
        setLayerNum(89);
    }


    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the xref
     */
    public boolean isXref() {
        return xref;
    }

    /**
     * @param xref the xref to set
     */
    public void setXref(boolean xref) {
        this.xref = xref;
    }

    /**
     * @return the probetype
     */
    public int getProbeType() {
        return probetype;
    }

    /**
     * @param probetype the probetype to set
     */
    public void setProbeType(int probetype) {
        this.probetype = probetype;
    }

}
