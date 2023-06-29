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
package com.maehem.mangocad.view.board;

import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.basic.*;
import com.maehem.mangocad.model.element.drawing.Board;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.library.LibraryElementNode;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class BoardPreview extends Group {

    private static final Logger LOGGER = Logger.getLogger(ControlPanel.class.getSimpleName());

    private final Board board;

    public BoardPreview(Board board) {
        super();
        this.board = board;

        populateNode();
    }

    private void populateNode() {

        for (_AQuantum element : board.getPlain()) {
            // polygon | wire | text | dimension | circle | spline | rectangle | frame | hole
            if (element instanceof ElementPolygon e) {
                getChildren().add(LibraryElementNode.createPolygon(e, Color.CORAL, false));
            } else if (element instanceof Wire e) {
                getChildren().add(LibraryElementNode.createWireNode(e, Color.RED, false));
            } else if (element instanceof ElementText e) {
                getChildren().add(LibraryElementNode.createText(e, Color.CORAL));
            } else if (element instanceof Dimension e) {
                //getChildren().add(LibraryElementNode.createDimensionNode(e, Color.RED));
                LOGGER.log(Level.SEVERE, "TODO: Implement Dimension Node");
            } else if (element instanceof ElementCircle e) {
                getChildren().add(LibraryElementNode.createCircleNode(e, Color.CORAL, false));
            } else if (element instanceof Spline e) {
                //getChildren().add(LibraryElementNode.createSplineNode(e, Color.CORAL));
                LOGGER.log(Level.SEVERE, "TODO: Implement Spline Node");
            } else if (element instanceof ElementRectangle e) {
                getChildren().add(LibraryElementNode.createRectangle(e, Color.RED, false));
            } else if (element instanceof FrameElement e) {
                getChildren().add(LibraryElementNode.createFrameNode(e, Color.RED));
            } else if (element instanceof Hole e) {
                //getChildren().add(LibraryElementNode.createHoleNode(e, Color.RED));
                LOGGER.log(Level.SEVERE, "TODO: Implement Hole Node");
            }

        }

        for (ElementElement element : board.getElements()) {

            Optional<Library> libSearch = board.getLibraries().stream().filter(
                    library -> library.getName().equals(element.getLibrary())
            ).findFirst();

            if (!libSearch.isEmpty()) {
                Footprint pkg = libSearch.get().getPackage(element.getFootprint());
                Node pkgPreview = LibraryElementNode.createPackageNode(
                        pkg, board.getParentDrawing().getLayers(), 
                        board.getParentDrawing().getPalette()
                );
                //Node pkgPreview = DetailNodes.footprintPreview(pkg, libSearch.get(), false);
                pkgPreview.setLayoutX(element.getX());
                pkgPreview.setLayoutY(-element.getY());
                pkgPreview.setRotate(element.getRot());
                getChildren().add(pkgPreview);
            } else {
                LOGGER.log(Level.SEVERE,
                        "Couldn''t find local library called: {0} for package: {1}",
                        new Object[]{element.getLibrary(), element.getFootprint()}
                );
            }
//            board.getLibraries().forEach((lib) -> {
//                if (lib.getName().equals(element.getLibrary())) {
//                    Footprint pkg = lib.getPackage(element.getFootprint());
//                    Node pkgPreview = DetailNodes.footprintPreview(pkg, lib, false);
//                    pkgPreview.setLayoutX(element.getX());
//                    pkgPreview.setLayoutY(-element.getY());
//                    pkgPreview.setRotate(element.getRot());
//                    getChildren().add(pkgPreview);

//                    DeviceSet deviceSet = lib.getDeviceSet(p.getDeviceSet());
//                    deviceSet.getGates().forEach((gate) -> {
//                        if (element.getGate().equals(gate.getName())) {
//                            Symbol symbol = lib.getSymbol(gate.getSymbol());
//                            LayerElement[] layers = lib.getParentDrawing().getLayers();
//                            ColorPalette palette = lib.getParentDrawing().getPalette();
//
//                            Node symbolPreview = LibraryElementNode.createSymbolNode(symbol, layers, palette);
//                            symbolPreview.setLayoutX(element.getX());
//                            symbolPreview.setLayoutY(-element.getY());
//                            symbolPreview.setRotate(element.getRot());
//                            getChildren().add(symbolPreview);
//                        }
//                    });
//                }
//            });
        }
    }

}
