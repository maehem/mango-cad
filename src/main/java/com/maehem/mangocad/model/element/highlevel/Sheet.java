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

import com.maehem.mangocad.model.element.basic.Instance;
import com.maehem.mangocad.model.element.basic.ModuleInst;
import com.maehem.mangocad.model.element.misc.Description;
import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.basic.Part;
import com.maehem.mangocad.model.element.drawing.Schematic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * sheet (description?, plain?, moduleinsts?, instances?, busses?, nets?)
 * 
 * No Attributes.
 * 
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Sheet extends _AQuantum {
    
    public static final String ELEMENT_NAME = "sheet";   
        
    private final Description description = new Description();
    private final ArrayList<_AQuantum> plain = new ArrayList<>();
    private final ArrayList<ModuleInst> moduleInsts = new ArrayList<>();
    private final ArrayList<Instance> instances = new ArrayList<>();
    private final ArrayList<Net> busses = new ArrayList<>();
    private final ArrayList<Net> nets = new ArrayList<>();
    
    private Schematic parent = null;
    
    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }
    
    public Description getDescription() {
        return description;
    }
    
    public List<_AQuantum> getPlain() {
        return plain;
    }
    
    public List<ModuleInst> getModuleInsts() {
        return moduleInsts;
    }
    
    public List<Instance> getInststances() {
        return instances;
    }
    
    public List<Net> getBusInsts() {
        return busses;
    }
    
    public List<Net> getNetInsts() {
        return nets;
    }

    public void postIngest() {
        
    }

    public Schematic getParent() {
        return parent;
    }
    
    public void setParent(Schematic sch) {
        this.parent = sch;
    }
    
    
}
