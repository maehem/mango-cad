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
import com.maehem.mangocad.model.element.enums.TextAlign;
import com.maehem.mangocad.model.element.enums.TextFont;
import com.maehem.mangocad.model.element.property.Rotation;
import java.util.ArrayList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LabelElementOld extends Element {

    public static final String ELEMENT_NAME = "label";

    //label (no sub-nodes)
    //  ATTLIST label
    //          x             %Coord;        #REQUIRED
    //          y             %Coord;        #REQUIRED
    //          size          %Dimension;    #REQUIRED
    //          layer         %Layer;        #REQUIRED
    //          font          %TextFont;     "proportional"
    //          ratio         %Int;          "8"
    //          rot           %Rotation;     "R0"
    //          xref          %Bool;         "no"
    //          align         %Align;        "bottom-left"
    //          grouprefs     IDREFS         #IMPLIED
    //          >
    //          <!-- rot:  Only 0, 90, 180 or 270 -->
    //          <!-- xref: Only in <net> context -->
    private double x;
    private double y;
    private double size = 10;
    private TextFont font = TextFont.PROPORTIONAL;
    private int ratio = 8;
    private final Rotation rotation = new Rotation(Rotation.CONSTRAINED);
    private boolean xref = false;
    private TextAlign align = TextAlign.BOTTOM_LEFT;

    private final ArrayList<String> grouprefs = new ArrayList<>();

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the size
     */
    public double getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * @return the ratio
     */
    public int getRatio() {
        return ratio;
    }

    /**
     * @param ratio the ratio to set
     */
    public void setRatio(int ratio) {
        this.ratio = ratio;
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
     * @return the grouprefs
     */
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }

    /**
     * @return the font
     */
    public TextFont getFont() {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(TextFont font) {
        this.font = font;
    }

    public Rotation getRotation() {
        return rotation;
    }

    /**
     * @return the rot
     */
    public double getRot() {
        return rotation.get();
    }

    /**
     * @param val the rot to set 0,90,180,270. Values will be clamped.
     */
    public void setRot(double val) {
        this.rotation.set(val);
//        // Range checking. Round to nearest 90 degree angle.
//        if ( rotation >= 45.0 && rotation < 135.0 ) {
//            this.rotation = 90;
//        } else if ( rotation >= 135.0 && rotation < 225.0 ) {
//            this.rotation = 180;
//        } else if ( rotation >= 225.0 || rotation < 315.0 ) {
//            this.rotation = 270;
//        } else {
//            this.rotation = 0;
//        }
    }

    /**
     * @return the align
     */
    public TextAlign getAlign() {
        return align;
    }

    /**
     * @param align the align to set
     */
    public void setAlign(TextAlign align) {
        this.align = align;
    }

}
