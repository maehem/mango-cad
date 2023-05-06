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

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.maehem.mangocad.model.library.element.Description;
import com.maehem.mangocad.model.library.element.DeviceSet;
import com.maehem.mangocad.model.library.element.LibraryElement;
import com.maehem.mangocad.model.library.element.Note;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.Symbol;
import com.maehem.mangocad.model.library.element.device.Attribute;
import com.maehem.mangocad.model.library.element.device.Connection;
import com.maehem.mangocad.model.library.element.device.Device;
import com.maehem.mangocad.model.library.element.device.Technology;
import com.maehem.mangocad.model.library.element.quantum.Circle;
import com.maehem.mangocad.model.library.element.quantum.Gate;
import com.maehem.mangocad.model.library.element.quantum.Hole;
import com.maehem.mangocad.model.library.element.quantum.PadSMD;
import com.maehem.mangocad.model.library.element.quantum.PadTHD;
import com.maehem.mangocad.model.library.element.quantum.Pin;
import com.maehem.mangocad.model.library.element.quantum.Polygon;
import com.maehem.mangocad.model.library.element.quantum.Rectangle;
import com.maehem.mangocad.model.library.element.quantum.Text;
import com.maehem.mangocad.model.library.element.quantum.Vertex;
import com.maehem.mangocad.model.library.element.quantum.Via;
import com.maehem.mangocad.model.library.element.quantum.Wire;

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
            config.setClassTag("circle", Circle.class);
            config.setClassTag("padthd", PadTHD.class);
            config.setClassTag("padsmd", PadSMD.class);
            config.setClassTag("polygon", Polygon.class);
            config.setClassTag("rectangle", Rectangle.class);
            config.setClassTag("text", Text.class);
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
