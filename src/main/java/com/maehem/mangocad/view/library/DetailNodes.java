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
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.Symbol;
import com.maehem.mangocad.model.library.element.quantum.ElementCircle;
import com.maehem.mangocad.model.library.element.quantum.ElementPolygon;
import com.maehem.mangocad.model.library.element.quantum.ElementRectangle;
import com.maehem.mangocad.model.library.element.quantum.ElementText;
import com.maehem.mangocad.model.library.element.quantum.Gate;
import com.maehem.mangocad.model.library.element.quantum.PadSMD;
import com.maehem.mangocad.model.library.element.quantum.PadTHD;
import com.maehem.mangocad.model.library.element.quantum.Pin;
import com.maehem.mangocad.model.library.element.quantum.Wire;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.ControlPanel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DetailNodes {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    public static Node descriptionNode(String content) {
        TextArea ta = new TextArea(content);
        return ta;
    }

    public static Node devicePreview(DeviceSet devSet, Library lib ) {
        SplitPane pane = new SplitPane();
        pane.setOrientation(Orientation.HORIZONTAL);
        SplitPane pkgPane = new SplitPane();
        pkgPane.setOrientation(Orientation.VERTICAL);
        Pane footprintPreview = footprintPreview(
                lib.getPackage(devSet.getDevices().get(0).getPackage()), 
                lib
        );
        StackPane footprintPane = new StackPane(footprintPreview);
        
        pkgPane.getItems().add(footprintPane);
        pkgPane.getItems().add( new BorderPane(
                new Text("details, details...")
        ));
        
        Node gsPreview = gateSetPreview(devSet.getGates(), lib);
        
        Platform.runLater(() -> {
            double gsScale = pane.getBoundsInLocal().getWidth()/gsPreview.getBoundsInLocal().getWidth();
            gsPreview.setScaleX(gsScale);
            gsPreview.setScaleY(gsScale);
            
            double footScale = footprintPane.getBoundsInLocal().getHeight()/footprintPreview.getBoundsInLocal().getHeight();
            footprintPreview.setScaleX(footScale);
            footprintPreview.setScaleY(footScale);
            
            pane.getDividers().get(0).setPosition(0.5);
            pkgPane.getDividers().get(0).setPosition(0.5);
        });
        
        StackPane gsPane = new StackPane(gsPreview);
        
        pane.getItems().add(gsPane);
        pane.getItems().add(pkgPane);
        
        pane.getDividers().get(0).positionProperty().addListener(((ov, t, t1) -> {
            double position = pane.getDividers().get(0).getPosition();
            //if ( position <= 0.5 ) {
                gsPane.setScaleX(position);
                gsPane.setScaleY(position);
            //}
        }));
        
        pkgPane.getDividers().get(0).positionProperty().addListener((o) -> {
            double position = pkgPane.getDividers().get(0).getPosition();
            footprintPane.setScaleX(position);
            footprintPane.setScaleY(position);
        });
        
        return pane;
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
    public static Pane symbolPreview(Symbol symbol, Library lib ) {
        LayerElement[] layers = lib.getLayers();
        ColorPalette palette = lib.getPalette();
        
        Group g = new Group();
        StackPane pane = new StackPane(g);

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

        Bounds bounds = pane.getBoundsInLocal();

        pane.setPrefSize(bounds.getWidth(), bounds.getHeight());
        pane.setMaxSize(bounds.getWidth(), bounds.getHeight());

        return pane;
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
    public static Pane footprintPreview(Footprint footprint, Library lib ) {
        LayerElement[] layers = lib.getLayers();
        ColorPalette palette = lib.getPalette();
        
        Group g = new Group();
        StackPane pane = new StackPane(g);
        
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

        Bounds bounds = pane.getBoundsInLocal();
        pane.setPrefSize(bounds.getWidth(), bounds.getHeight());
        pane.setMaxSize(bounds.getWidth(), bounds.getHeight());

        return pane;
    }

    public static Pane scaleGauge() {
        final Color COLOR = new Color(0.5, 0.7, 1.0, 0.5);
        final double FONT_SIZE = 2.0;
        double mmNum = 10.0;
        double inNum = 10* 1.27;
        Group g = new Group();
        
        Line left = new Line(0, FONT_SIZE*1.4, 0, -FONT_SIZE*1.4);
        Line center = new Line(0, 0, inNum, 0);
        Line mm = new Line(mmNum, 0, mmNum, -FONT_SIZE);
        Line in = new Line(  inNum, 0, inNum, FONT_SIZE);
                
        g.getChildren().addAll(left,center,mm,in);
        
        Text mmText = new Text("10mm");
        mmText.setFont(Font.font(FONT_SIZE));
        mmText.setFill(COLOR.brighter().desaturate());
        mmText.setLayoutX(1.0);
        mmText.setLayoutY(-FONT_SIZE*0.4);
        Text inText = new Text("0.50in");
        inText.setFont(Font.font(FONT_SIZE));
        inText.setFill(COLOR.brighter().desaturate());
        inText.setLayoutX(1.0);
        inText.setLayoutY(FONT_SIZE*1);
        
        g.getChildren().addAll(mmText, inText);
        
        for ( Node n: g.getChildren() ) {
            if ( n instanceof Line l) {
                l.setStroke(COLOR);
                l.setStrokeWidth(FONT_SIZE*0.1);
            }
        }
        left.setStrokeWidth(FONT_SIZE*0.16);
                
        StackPane sp = new StackPane(g);
        Bounds bounds = sp.getBoundsInLocal();
        sp.setPrefSize(bounds.getWidth(), bounds.getHeight());
        sp.setMaxSize(bounds.getWidth(), bounds.getHeight());
        return sp;
    }
    
    private static Node gateSetPreview( List<Gate> gates, Library lib) {
        Group g = new Group();
        
        gates.forEach((gate) -> {
            Node n = symbolPreview(lib.getSymbol(gate.getSymbol()), lib);
            n.setLayoutX(gate.getX());
            n.setLayoutY(-gate.getY());
            
            g.getChildren().add(n);
        });
        
        return g;
    }
}
