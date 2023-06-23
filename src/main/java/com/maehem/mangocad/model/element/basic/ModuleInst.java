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

import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.util.Rotation;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.robot.Robot;

/**
     * moduleinst ( attribute(s) )
          name          %String;       #REQUIRED
          module        %String;       #REQUIRED
          modulevariant %String;       ""
          x             %Coord;        #REQUIRED
          y             %Coord;        #REQUIRED
          offset        %Int;          "0"
          smashed       %Bool;         "no"
          rot           %Rotation;     "R0"
          >
          <!-- rot: Only 0, 90, 180 or 270 -->
 * 
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ModuleInst extends _AQuantum {
    
    public static final String ELEMENT_NAME = "moduleinst";
    
    private final ArrayList<Attribute> attributes = new ArrayList<>();
    
    private String name;
    private String module;
    private String moduleVariant = "";
    private double x;
    private double y;
    private int offset = 0;
    private boolean smashed = false;
    private final Rotation rotation = new Rotation(Rotation.CONSTRAINED);

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * @return the moduleVariant
     */
    public String getModuleVariant() {
        return moduleVariant;
    }

    /**
     * @param moduleVariant the moduleVariant to set
     */
    public void setModuleVariant(String moduleVariant) {
        this.moduleVariant = moduleVariant;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
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
        return rotation.getValue();
    }

    /**
     *
     * @param rot the rot to set Only 0, 90, 180 or 270
     */
    public void setRot(double rot) {
        rotation.setValue(rot);
//        // Range checking. Round to nearest 90 degree angle.
//        if ( rot >= 45.0 && rot < 135.0 ) {
//            this.rot = 90;
//        } else if ( rot >= 135.0 && rot < 225.0 ) {
//            this.rot = 180;
//        } else if ( rot >= 225.0 || rot < 315.0 ) {
//            this.rot = 270;
//        } else {
//            this.rot = 0;
//        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
