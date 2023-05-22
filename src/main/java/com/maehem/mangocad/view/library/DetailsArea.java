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
package com.maehem.mangocad.view.library;

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.LayerElement;
import com.maehem.mangocad.model.library.Library;
import com.maehem.mangocad.model.library.element.DeviceSet;
import com.maehem.mangocad.model.library.element.ElementType;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.Package3d;
import com.maehem.mangocad.model.library.element.Symbol;
import com.maehem.mangocad.model.library.element.quantum.ElementCircle;
import com.maehem.mangocad.model.library.element.quantum.ElementPolygon;
import com.maehem.mangocad.model.library.element.quantum.ElementRectangle;
import com.maehem.mangocad.model.library.element.quantum.ElementText;
import com.maehem.mangocad.model.library.element.quantum.PadSMD;
import com.maehem.mangocad.model.library.element.quantum.PadTHD;
import com.maehem.mangocad.model.library.element.quantum.Pin;
import com.maehem.mangocad.model.library.element.quantum.Wire;
import com.maehem.mangocad.model.library.element.quantum.enums.TextAlign;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.ControlPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DetailsArea extends SplitPane {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final double PANE_H = 0.25;

    public DetailsArea() {
        setOrientation(Orientation.VERTICAL);
        setDividerPosition(0, 1 * PANE_H);
        setDividerPosition(1, 2 * PANE_H);
        setDividerPosition(2, 3 * PANE_H);
    }

    void setItem(Library lib, ElementType src, String newValue) {

        getItems().clear();

        switch (src) {
            case DEVICE -> {
                // Preview
                // Description
                for (DeviceSet s : lib.getDeviceSets()) {
                    if (s.getName().equals(newValue)) {
                        getItems().add(descriptionNode(s.getDescription()));
                        return;
                    }
                }
                // Variants
            }
            case FOOTPRINT -> {
                // Preview
                // Description
                for (Footprint footprint : lib.getPackages()) {
                    if (footprint.getName().equals(newValue)) {
                        getItems().add(footprintPreview(footprint, lib.getLayers(), lib.getPalette()));
                        getItems().add(descriptionNode(footprint.getDescription()));
                        return;
                    }
                }
            }
            case PACKAGE3D -> {
                // Preview
                // Description
                for (Package3d s : lib.getPackages3d()) {
                    if (s.getName().equals(newValue)) {
                        getItems().add(descriptionNode(s.getDescription()));
                        return;
                    }
                }
            }
            case SYMBOL -> {
                // Preview
                // Description
                for (Symbol s : lib.getSymbols()) {
                    if (s.getName().equals(newValue)) {
                        
                        getItems().add(symbolPreview(s, lib.getLayers(), lib.getPalette() ));
                        getItems().add(descriptionNode(s.getDescription()));
                        return;
                    }
                }
            }
        }
    }

    private Node descriptionNode(String content) {
        TextArea ta = new TextArea(content);
        return ta;
    }

    /**
     * Render a preview of the symbols.
     *
     * NOTE: Eagle Y coordinates are reversed. Up is positive, Y origin at
     * bottom.
     *
     * @param symbol
     * @return
     */
    private Node symbolPreview(Symbol symbol, LayerElement[] layers, ColorPalette palette ) {
        Group g = new Group();
        StackPane symbolGroup = new StackPane(g);
        

        
        symbol.getElements().forEach((e) -> {
            LayerElement le = layers[e.getLayerNum()];
            if ( le == null ) {
                LOGGER.log(Level.SEVERE, "No Layer for: {0}", e.getLayerNum());
            }
            int colorIndex = le.getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));
            
            if (e instanceof Wire wire) {
                g.getChildren().add(LibraryElementNode.createWireNode(wire, c));
            } else if (e instanceof ElementRectangle elementRectangle) {
                g.getChildren().add(LibraryElementNode.createRectangle(elementRectangle, c));
            } else if (e instanceof ElementText elementText) {
                g.getChildren().add(LibraryElementNode.createText(elementText, c));
                g.getChildren().add(LibraryElementNode.crosshairs(e.getX(), -e.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if( e instanceof ElementPolygon elementPolygon ) {
                g.getChildren().add(LibraryElementNode.createPolygon(elementPolygon, c));
            } else if (e instanceof Pin pin) {
                g.getChildren().add(LibraryElementNode.createPinNode(pin, c));
            } else if ( e instanceof ElementCircle elementCircle ) {
                g.getChildren().add(LibraryElementNode.createCircleNode(elementCircle, c));
            }
        });
        g.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.05, Color.RED
        ));

         
        symbolGroup.setScaleX(10.0);
        symbolGroup.setScaleY(10.0);

        return symbolGroup;
    }
    
    /**
     * Render a preview of the footprint.
     *
     * NOTE: Eagle Y coordinates are reversed. Up is positive, Y origin at
     * bottom.
     *
     * @param footprint
     * @return
     */
    private Node footprintPreview(Footprint footprint, LayerElement[] layers, ColorPalette palette ) {
        Group g = new Group();
        StackPane symbolGroup = new StackPane(g);
        
        
        footprint.getElements().forEach((e) -> {
            LayerElement le = layers[e.getLayerNum()];
            int colorIndex = le.getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));

            if (e instanceof PadSMD padSMD) {
                Color maskColor = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                Node n = LibraryElementNode.createSmd(padSMD, c, maskColor);
                g.getChildren().add(n);
                n.toBack();
            } else if (e instanceof PadTHD padTHD) {
                Color maskColor = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                Node n = LibraryElementNode.createThd(padTHD, c, maskColor);
                g.getChildren().add(n);
                n.toBack();
            } else if (e instanceof Wire wire) {
                g.getChildren().add(LibraryElementNode.createWireNode(wire, c));
            } else if (e instanceof ElementRectangle elementRectangle) {
                g.getChildren().add(LibraryElementNode.createRectangle(elementRectangle, c));
            } else  if (e instanceof ElementText elementText) {
                g.getChildren().add(LibraryElementNode.createText(elementText, c));
                g.getChildren().add(LibraryElementNode.crosshairs(e.getX(), -e.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if (e instanceof ElementRectangle elementRectangle) {
                g.getChildren().add(LibraryElementNode.createRectangle(elementRectangle, c));
            } else if( e instanceof ElementPolygon elementPolygon ) {
                g.getChildren().add(LibraryElementNode.createPolygon(elementPolygon, c));
            } else if ( e instanceof ElementCircle elementCircle ) {
                g.getChildren().add(LibraryElementNode.createCircleNode(elementCircle, c));
            }
        });
        g.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.05, Color.RED
        ));

         
        symbolGroup.setScaleX(30.0);
        symbolGroup.setScaleY(30.0);

        return symbolGroup;
    }

}
