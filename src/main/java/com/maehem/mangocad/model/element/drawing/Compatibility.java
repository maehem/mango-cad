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
package com.maehem.mangocad.model.element.drawing;

import com.maehem.mangocad.model._AQuantum;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
    compatibility (note)
        NO ATTRIBUTES
 * </pre>
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Compatibility extends _AQuantum {
    
    public static final String ELEMENT_NAME = "compatibility";

    private final ArrayList<Note> notes  = new ArrayList<>();

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    public List<Note> getNotes() {
        return notes;
    }
}