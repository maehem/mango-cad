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
import com.maehem.mangocad.model.library.element.quantum.Pin;
import com.maehem.mangocad.model.library.element.quantum.Vertex;
import com.maehem.mangocad.model.library.element.quantum.Wire;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.ControlPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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
                LOGGER.log(Level.SEVERE, "No Layer for: " + e.getLayerNum());
            }
            int colorIndex = le.getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));
            
            if (e instanceof Wire) {
                g.getChildren().add(LibraryElementNode.createWireNode((Wire) e, c));
            } else
            if (e instanceof ElementRectangle) {
                g.getChildren().add(LibraryElementNode.createRectangle((ElementRectangle) e, c));
            } else if (e instanceof ElementText) {
                g.getChildren().add(LibraryElementNode.createText((ElementText) e, c));
                g.getChildren().add(LibraryElementNode.crosshairs(e.getX(), -e.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if( e instanceof ElementPolygon ) {
                g.getChildren().add(LibraryElementNode.createPolygon((ElementPolygon) e));
            } else if (e instanceof Pin) {
                g.getChildren().add(LibraryElementNode.createPinNode((Pin) e));
            } else if ( e instanceof ElementCircle ) {
                g.getChildren().add(LibraryElementNode.createCircleNode((ElementCircle) e, c));
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

            if (e instanceof PadSMD) {
                g.getChildren().add(LibraryElementNode.createSmd((PadSMD) e));
            } else
            if (e instanceof Wire) {
                g.getChildren().add(LibraryElementNode.createWireNode((Wire) e, c));
            } else
            if (e instanceof ElementRectangle) {
                g.getChildren().add(LibraryElementNode.createRectangle((ElementRectangle) e, c));
            } else 
            if (e instanceof ElementText) {
                g.getChildren().add(LibraryElementNode.createText((ElementText) e, c));
                g.getChildren().add(LibraryElementNode.crosshairs(e.getX(), -e.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if( e instanceof ElementPolygon ) {
                g.getChildren().add(LibraryElementNode.createPolygon((ElementPolygon) e));
            }
        });
        g.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.05, Color.RED
        ));

         
        symbolGroup.setScaleX(40.0);
        symbolGroup.setScaleY(40.0);

        return symbolGroup;
    }

}
