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

import com.maehem.mangocad.model.library.element.quantum._AQuantum;
import java.util.ArrayList;

/**
 * segment (pinref | portref | wire | junction | label | probe)*>
 *         'pinref' and 'junction' are only valid in a <net> context.
 * 
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Segment extends ArrayList<_AQuantum> {

    public static final String ELEMENT_NAME = "segment";

    @Override
    public boolean add(_AQuantum e) {
        return switch (e.getElementName()) {
            case "pinref", "portref", "wire", "junction", "label", "probe" -> super.add(e);
            default -> false;
        };
    }
    
    
}
