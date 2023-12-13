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

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.basic.*;
import com.maehem.mangocad.model.element.drawing.Board;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Signal;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.library.LibraryElementNode;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class BoardPreview extends Group {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    private final Board board;

    public BoardPreview(Board board) {
        super();
        this.board = board;

        populateGrid();
        populateNode();
    }

    private void populateGrid() {
        // Grid
        for (int i = 0; i < 10; i++) {
            Wire gridline = new Wire();
            gridline.setX1(0);
            gridline.setY1(i * 1.27);
            gridline.setX2(25.40);
            gridline.setY2(i * 1.27);

            gridline.setWidth(0.01);
            getChildren().add(LibraryElementNode.createWireNode(gridline, new Color(0.2, 0.2, 0.2, 1.0), false));
        }
        for (int i = 0; i < 20; i++) {
            Wire gridline = new Wire();
            gridline.setX1(i * 1.27);
            gridline.setY1(0);
            gridline.setX2(i * 1.27);
            gridline.setY2(12.70);

            gridline.setWidth(0.01);
            getChildren().add(LibraryElementNode.createWireNode(gridline, new Color(0.2, 0.2, 0.2, 1.0), false));
        }

    }

    private void populateNode() {
        LayerElement[] layers = board.getParentDrawing().getLayers();
        ColorPalette palette = board.getParentDrawing().getPalette();
        ObservableList<Node> chld = getChildren();

        for (_AQuantum element : board.getPlain()) {
            int colorIndex = layers[element.getLayerNum()].getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));

            // polygon | wire | text | dimension | circle | spline | rectangle | frame | hole
            if (element instanceof ElementPolygon e) {
                chld.add(LibraryElementNode.createPolygon(e, c, false));
            } else if (element instanceof Wire e) {
                chld.add(LibraryElementNode.createWireNode(e, c, false));
            } else if (element instanceof ElementText e) {
                chld.add(LibraryElementNode.createText(e, c));
            } else if (element instanceof Dimension e) {
                chld.add(LibraryElementNode.createDimensionNode(
                        e, layers, palette)
                );
            } else if (element instanceof ElementCircle e) {
                chld.add(LibraryElementNode.createCircleNode(e, c, false));
            } else if (element instanceof Spline e) {
                // Only the plain group can have Spline and the Spline can only
                // be on the Dimension layer.

                //chld.add(LibraryElementNode.createSplineNode(e, c));
                LOGGER.log(Level.SEVERE, "TODO: Implement Spline Node");
            } else if (element instanceof ElementRectangle e) {
                chld.add(LibraryElementNode.createRectangle(e, c, false));
            } else if (element instanceof FrameElement e) {
                chld.add(LibraryElementNode.createFrameNode(e, c));
            } else if (element instanceof Hole e) {
                chld.add(LibraryElementNode.createHoleNode(e, c));
            } else {
                LOGGER.log(Level.SEVERE, "Encountered unhandled plain element: " + element.getElementName());
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
                pkgPreview.setLayoutX(element.getX());
                pkgPreview.setLayoutY(-element.getY());
                pkgPreview.getTransforms().add(new Rotate(-element.getRot()));
                chld.add(pkgPreview);
            } else {
                LOGGER.log(Level.SEVERE,
                        "Couldn''t find local library called: {0} for package: {1}",
                        new Object[]{element.getLibrary(), element.getFootprint()}
                );
            }
        }

        for (Signal sig : board.getSignals()) {
            LOGGER.log(Level.FINE, "Signal: {0} with netClass: {1}",
                    new Object[]{sig.getName(), sig.getNetClassNum()});
            for (_AQuantum el : sig.getElements()) {
                if (el instanceof Wire w) {
                    int colorIndex = layers[el.getLayerNum()].getColorIndex();
                    Color c = ColorUtils.getColor(palette.getHex(colorIndex));
                    LOGGER.log(Level.FINER, "    Element: {0} on layerNum: {1} at: {2},{3} to {4},{5} with width: {6}",
                            new Object[]{
                                w.getElementName(), w.getLayerNum(),
                                w.getX1(), w.getY1(), w.getX2(), w.getY2(),
                                w.getWidth()
                            });
                    chld.add(LibraryElementNode.createWireNode(w, c, false));

                } else if (el instanceof ContactRef cref) {

                    LOGGER.log(Level.SEVERE, "    ContactRef not handled: {0} pad:{1} routeTag:{2}",
                            new Object[]{
                                el.getElementName(),
                                cref.getPad(),
                                cref.getRouteTag()
                            }
                    );
                } else if (el instanceof Via v) {
                    Color padColor = ColorUtils.getColor(palette.getHex(layers[18].getColorIndex()));
                    Color maskColor = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                    chld.add(LibraryElementNode.createVia(v, padColor, maskColor));
                    //LOGGER.log(Level.SEVERE, "Via not handled: " + el.getElementName());
                } else if (el instanceof ElementPolygon ep) {
                    int colorIndex = layers[el.getLayerNum()].getColorIndex();
                    Color c = ColorUtils.getColor(palette.getHex(colorIndex));
                    chld.add(LibraryElementNode.createPolygon(ep, c, false));
                } else {
                    LOGGER.log(Level.SEVERE, "    Signal Element not handled: {0}", el.getElementName());
                }
            }
        }
    }

}
