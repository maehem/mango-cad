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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.util.Rotation;
import java.util.ArrayList;

/**
 * instance ( attribute(s) )
 *   instance
 *         part          %String;       #REQUIRED
 *         gate          %String;       #REQUIRED
 *         x             %Coord;        #REQUIRED
 *         y             %Coord;        #REQUIRED
 *         smashed       %Bool;         "no"
 *         rot           %Rotation;     "R0"
 *         grouprefs     IDREFS         #IMPLIED
 *
 *         rot: Only 0, 90, 180 or 270
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Instance extends Element implements GrouprefsProperty {

    public static final String ELEMENT_NAME = "instance";

    private final ArrayList<Attribute> attributes = new ArrayList<>();
    private final ArrayList<String> grouprefs = new ArrayList<>();

    private String part;
    private String gate;
    private double x;
    private double y;
    private boolean smashed = false;
    private final Rotation rotation = new Rotation(Rotation.CONSTRAINED);

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the smashed
     */
    public boolean isSmashed() {
        return smashed;
    }

    /**
     * @param smashed the smashed to set
     */
    public void setSmashed(boolean smashed) {
        this.smashed = smashed;
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
     *
     * @param rot the rot to set Only 0, 90, 180 or 270
     */
    public void setRot(double rot) {
        rotation.set(rot);
    }

    /**
     * @return the part
     */
    public String getPart() {
        return part;
    }

    /**
     * @param part the part to set
     */
    public void setPart(String part) {
        this.part = part;
    }

    /**
     * @return the gate
     */
    public String getGate() {
        return gate;
    }

    /**
     * @param gate the gate to set
     */
    public void setGate(String gate) {
        this.gate = gate;
    }

    @Override
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
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

}
