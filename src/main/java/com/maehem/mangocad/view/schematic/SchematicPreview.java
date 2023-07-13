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
package com.maehem.mangocad.view.schematic;

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.basic.*;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.drawing.Schematic;
import com.maehem.mangocad.model.element.highlevel.Device;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Net;
import com.maehem.mangocad.model.element.highlevel.Sheet;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.library.LibraryElementNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SchematicPreview extends Group {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private final Schematic schematic;
    private int index;

    public SchematicPreview(Schematic schematic, int index) {
        super();
        this.schematic = schematic;
        this.index = index;

        if (schematic.getSheets().get(index) != null) {
            populateNode(schematic, index);
        } else {
            Text t = new Text("No sheet to show! No sheet found at index " + index);
            getChildren().add(t);
        }
    }

    private void populateNode(Schematic schem, int index) {
        LayerElement[] layers = schem.getParentDrawing().getLayers();
        ColorPalette palette = schem.getParentDrawing().getPalette();
        //LOGGER.log(Level.SEVERE, "Populate Page: " + (index+1));
        Sheet sheet = schem.getSheets().get(index);
        Map<String, String> vars = new HashMap();
        vars.putAll(schem.getParentDrawing().getVars());
        vars.put("SHEET", (index + 1) + "/" + schem.getSheets().size());

//        if (sheet.getPlain().isEmpty()) {
//            LOGGER.log(Level.SEVERE, "No <plain> nodes found!");
//        }
        for (_AQuantum element : sheet.getPlain()) {
            LayerElement layer = layers[element.getLayerNum()];
            Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));
            // polygon | wire | text | dimension | circle | spline | rectangle | frame | hole
            if (element instanceof ElementPolygon e) {
                getChildren().add(LibraryElementNode.createPolygon(e, c, false));
            } else if (element instanceof Wire e) {
                getChildren().add(LibraryElementNode.createWireNode(e, c, false));
            } else if (element instanceof ElementText e) {
                getChildren().add(LibraryElementNode.createText(e, c));
            } else if (element instanceof Dimension e) {
                getChildren().add(LibraryElementNode.createDimensionNode(e, layers, palette));
            } else if (element instanceof ElementCircle e) {
                getChildren().add(LibraryElementNode.createCircleNode(e, c, false));
            } else if (element instanceof Spline e) {
                //getChildren().add(LibraryElementNode.createSplineNode(e, c));
                LOGGER.log(Level.SEVERE, "Spline not supported in Schematics!");
            } else if (element instanceof ElementRectangle e) {
                getChildren().add(LibraryElementNode.createRectangle(e, c, false));
            } else if (element instanceof FrameElement e) {
                getChildren().add(LibraryElementNode.createFrameNode(e, c));
            } else if (element instanceof Hole e) {
                //getChildren().add(LibraryElementNode.createHoleNode(e, c));
                LOGGER.log(Level.SEVERE, "TODO: Implement Hole Node");
            }
        }

        // Instances
        for (Instance inst : sheet.getInststances()) {
            String partName = inst.getPart();
            Optional<Part> lookupPart = schem.lookupPart(partName);
            if (lookupPart.isPresent()) {
                Part part = lookupPart.get();
                Optional<Library> lookupLibrary = schem.lookupLibrary(part.getLibrary());
                if (lookupLibrary.isPresent()) {
                    Library lib = lookupLibrary.get();
                    DeviceSet deviceSet = lib.getDeviceSet(part.getDeviceSet());
                    Device device = deviceSet.lookupDevice(part.getDevice());
                    deviceSet.getGates().forEach((gate) -> {
                        
                        if (inst.getGate().equals(gate.getName())) {
                            Symbol symbol = lib.getSymbol(gate.getSymbol());
                            // Pass attribute key/value list to symbol preview.
                            String val;
                            if (part.getValue() == null) {
                                String supplyPin = symbol.supplyPin();
                                if (supplyPin != null) {
                                    val = supplyPin;
                                } // Check if it's a global value
                                // >LAST_DATE_TIME  -  File mod date
                                // >SHEET (n of 99)  -  sheet num / num sheets
                                // >DRAWING_NAME   - file.getName()
                                // Match any attributes:
                                // >DOCUMENT_NUMBER - schem.attribute
                                // >FOO    schem.attribute
                                // >BAR    schem.attribute
                                else {
                                    val = gate.getName(); // Maybe it's the gate name?
                                }
                            } else {
                                val = part.getValue();
                            }
                            vars.put("VALUE", val);
                            Node symbolPreview = LibraryElementNode.createSymbolNode(
                                    device, symbol, inst, part, vars, layers, palette
                            );
                            symbolPreview.setLayoutX(inst.getX());
                            symbolPreview.setLayoutY(-inst.getY());
                            // Can't just rotate part since sub-texts need to be upright.
                            // Rotation direction is negative versus Eagle.
                            //symbolPreview.getTransforms().add(new Rotate(-inst.getRot(), 0, 0));
                            getChildren().add(symbolPreview);
                        }
                    });
                }
            }

        }
        // Nets
        for (Net inst : sheet.getNetInsts()) {
            //LOGGER.log(Level.SEVERE, "Draw Net: " + inst.getName());
            // segment (pinref | portref | wire | junction | label | probe)*
            //    'pinref' and 'junction' are only valid in a <net> context
            inst.getSegments().forEach((seg) -> {
                //LOGGER.log(Level.SEVERE, "Draw Seg");

                seg.forEach((element) -> {
                    int colorIndex = layers[element.getLayerNum()].getColorIndex();
                    Color c = ColorUtils.getColor(palette.getHex(colorIndex));
                    if (element instanceof PinRef e) {
                        // Might not have any visual element.
                        //        LOGGER.log(Level.SEVERE, "TODO: Draw PinRef Node");
                        //getChildren().add(LibraryElementNode.createPinNode(e., Color.PALEGREEN));
                    } else if (element instanceof PortRef e) {
                        LOGGER.log(Level.SEVERE, "TODO: Draw PortRef Node");
                        //getChildren().add(LibraryElementNode.createPortNode(e., Color.PALEGREEN));
                    } else if (element instanceof Wire e) {
                        getChildren().add(LibraryElementNode.createWireNode(e, c, false));
                    } else if (element instanceof Junction e) {
                        getChildren().add(LibraryElementNode.createJunctionNode(e, c));
                    } else if (element instanceof LabelElement e) {
                        getChildren().add(LibraryElementNode.createLabelNode(e, c));
                    } else if (element instanceof Probe e) {
                        getChildren().add(LibraryElementNode.createProbeNode(e, c, seg));
                    } else {
                        LOGGER.log(Level.SEVERE, "Unknown Element in Segment List: " + element.getElementName());
                    }

                });

            });
        }
    }
}
