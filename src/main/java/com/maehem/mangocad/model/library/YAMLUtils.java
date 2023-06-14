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
package com.maehem.mangocad.model.library;

import com.maehem.mangocad.model.element.drawing.Library;
import com.esotericsoftware.yamlbeans.YamlConfig;
import com.maehem.mangocad.model.element.misc.Description;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.LibraryElement;
import com.maehem.mangocad.model.element.drawing.Note;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.basic.Attribute;
import com.maehem.mangocad.model.element.basic.Connection;
import com.maehem.mangocad.model.element.highlevel.Device;
import com.maehem.mangocad.model.element.basic.Technology;
import com.maehem.mangocad.model.element.basic.ElementCircle;
import com.maehem.mangocad.model.element.basic.Gate;
import com.maehem.mangocad.model.element.basic.Hole;
import com.maehem.mangocad.model.element.basic.PadSMD;
import com.maehem.mangocad.model.element.basic.PadTHD;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.ElementPolygon;
import com.maehem.mangocad.model.element.basic.ElementRectangle;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.Vertex;
import com.maehem.mangocad.model.element.basic.Via;
import com.maehem.mangocad.model.element.basic.Wire;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class YAMLUtils {

    public static void setConfig(YamlConfig config) {
            config.setClassTag("library", Library.class);
            config.setClassTag("description", Description.class);
            config.setClassTag("note", Note.class);
            config.setClassTag("package", Footprint.class);
            config.setClassTag("symbol", Symbol.class);
            config.setClassTag("hole", Hole.class);
            config.setClassTag("circle", ElementCircle.class);
            config.setClassTag("padthd", PadTHD.class);
            config.setClassTag("padsmd", PadSMD.class);
            config.setClassTag("polygon", ElementPolygon.class);
            config.setClassTag("rectangle", ElementRectangle.class);
            config.setClassTag("text", ElementText.class);
            config.setClassTag("vertex", Vertex.class);
            config.setClassTag("via", Via.class);
            config.setClassTag("wire", Wire.class);
            config.setClassTag("pin", Pin.class);
            config.setClassTag("device", Device.class);
            config.setClassTag("connection", Connection.class);
            config.setClassTag("technology", Technology.class);
            config.setClassTag("attribute", Attribute.class);
            config.setClassTag("gate", Gate.class);
            config.setPropertyElementType(Library.class, "notes", Note.class);
            config.setPropertyElementType(Library.class, "descriptions", Description.class);
            config.setPropertyElementType(Library.class, "packages", Footprint.class);
            config.setPropertyElementType(Library.class, "symbols", Symbol.class);
            config.setPropertyElementType(Library.class, "deviceSets", DeviceSet.class);
            config.setPropertyElementType(LibraryElement.class, "descriptions", Description.class);

    }
    
}
