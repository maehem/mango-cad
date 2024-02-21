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
import com.maehem.mangocad.model.element.misc.DesignRules;
import com.maehem.mangocad.model.element.misc.Grid;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.util.DrcDefs;
import com.maehem.mangocad.model.util.Units;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.library.LibraryElementNode;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
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
        Grid grid = board.getParentDrawing().getGrid();
        // Calc grid settings from here.
        //populateNodeAsDesign(); // Get bounds as input for grid.
        populateNodeAsMfgPreview();

//        Group g = populateGrid(
//                getBoundsInLocal().getWidth(),
//                getBoundsInLocal().getHeight(),
//                grid.getDistance() * (grid.getUnit() == GridUnit.INCH ? 0.0393701 : 1)
//        );
//        getChildren().add(g);
    }

    private Group populateGrid(double bW, double bH, double gridSize) {
        Group g = new Group();

        Color gridColor = new Color(1.0, 1.0, 1.0, 0.2);
        // Grid
        for (int i = 0; i < (int) (bH / gridSize); i++) {
            Wire gridline = new Wire();
            gridline.setX1(0.0);
            gridline.setY1(i * gridSize);
            gridline.setX2(bW);
            gridline.setY2(i * gridSize);

            gridline.setWidth(0.01);
            g.getChildren().add(LibraryElementNode.createWireNode(
                    gridline, gridColor, false
            ));
        }
        for (int i = 0; i < (int) (bW / gridSize); i++) {
            Wire gridline = new Wire();
            gridline.setX1(i * gridSize);
            gridline.setY1(0.0);
            gridline.setX2(i * gridSize);
            gridline.setY2(bH);

            gridline.setWidth(0.01);
            g.getChildren().add(LibraryElementNode.createWireNode(
                    gridline, gridColor, false
            ));
        }

        return g;
    }

    private void populateNodeAsDesign() {
        LayerElement[] layers = board.getParentDrawing().getLayers();
        ColorPalette palette = board.getParentDrawing().getPalette();
        ObservableList<Node> chld = getChildren();

        for (_AQuantum element : board.getPlain()) {
            int colorIndex = layers[element.getLayerNum()].getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));

            // polygon | wire | text | dimension | circle | spline | rectangle | frame | hole
            if (element instanceof ElementPolygon e) {
                chld.add(LibraryElementNode.createPolygonCurved(e, c, false));
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
                int ocColorIndex = layers[20].getColorIndex();
                Color oc = ColorUtils.getColor(palette.getHex(ocColorIndex));
                int tmColorIndex = layers[29].getColorIndex();
                Color tm = ColorUtils.getColor(palette.getHex(tmColorIndex));
                int bmColorIndex = layers[30].getColorIndex();
                Color bm = ColorUtils.getColor(palette.getHex(bmColorIndex));
                chld.add(LibraryElementNode.createHoleNode(
                        e, 0.04,
                        oc, tm, bm,
                        board.getDesignRules()
                ));
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
                    Color tm = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                    Color bm = ColorUtils.getColor(palette.getHex(layers[30].getColorIndex()));
                    chld.add(LibraryElementNode.createVia(
                            v,
                            padColor, tm, bm,
                            board.getDesignRules()
                    ));
                } else if (el instanceof ElementPolygon ep) {
                    int colorIndex = layers[el.getLayerNum()].getColorIndex();
                    Color c = ColorUtils.getColor(palette.getHex(colorIndex));
                    chld.add(LibraryElementNode.createPolygonCurved(ep, c, false));
                } else {
                    LOGGER.log(Level.SEVERE, "    Signal Element not handled: {0}", el.getElementName());
                }
            }
        }
    }

    /**
     * Simplified version of PCB representing a manufactured facsimile.
     *
     */
    private void populateNodeAsMfgPreview() {
        // Set default colors
        Color solderMaskColor = new Color(0.0, 0.8, 0.0, 0.3); // Green
        Color silkScreenColor = new Color(0.9, 0.9, 0.9, 1.0); // Almost White
        Color backgroundColor = new Color(0.1, 0.1, 0.1, 1.0); // Grey
        Color copperColor = new Color(0.6, 0.5, 0.0, 1.0); // Copper
        Color substrateColor = new Color(0.2, 0.2, 0.0, 0.8); // Dark Brown-Yellow

        LayerElement[] layers = board.getParentDrawing().getLayers();
        ColorPalette palette = board.getParentDrawing().getPalette();

        ObservableList<Node> chld = getChildren();

        ArrayList<Node> silkNodes = new ArrayList<>();
        ArrayList<Node> maskNodes = new ArrayList<>();

        ArrayList<ArrayList<Node>> restrictNodes = new ArrayList<>();
        restrictNodes.add(new ArrayList<>());
        restrictNodes.add(new ArrayList<>());
        restrictNodes.add(new ArrayList<>());
        restrictNodes.add(new ArrayList<>());
        restrictNodes.add(new ArrayList<>());
        restrictNodes.add(new ArrayList<>());
        restrictNodes.add(new ArrayList<>());

        // Copper is ranked.
        // Polygons have isolate (1..6) but all other wires/pads are isolate (0)
        ArrayList<ArrayList<Node>> rank = new ArrayList<>();
        rank.add(new ArrayList<>());
        rank.add(new ArrayList<>());
        rank.add(new ArrayList<>());
        rank.add(new ArrayList<>());
        rank.add(new ArrayList<>());
        rank.add(new ArrayList<>());
        rank.add(new ArrayList<>());

        ArrayList<ArrayList<Node>> isolate = new ArrayList<>();
        isolate.add(new ArrayList<>());
        isolate.add(new ArrayList<>());
        isolate.add(new ArrayList<>());
        isolate.add(new ArrayList<>());
        isolate.add(new ArrayList<>());
        isolate.add(new ArrayList<>());
        isolate.add(new ArrayList<>());

        ArrayList<Node> holeNodes = new ArrayList<>();
        ArrayList<Wire> substrateWires = new ArrayList<>(); // Wires that should make a closed shape.

        // Board file overrides any mfg colors
        //    <mfgpreviewcolors>
        //    <mfgpreviewcolor name="soldermaskcolor" color="0xC8008000"/>
        //    <mfgpreviewcolor name="silkscreencolor" color="0xFFFEFEFE"/>
        //    <mfgpreviewcolor name="backgroundcolor" color="0xFF282828"/>
        //    <mfgpreviewcolor name="coppercolor" color="0xFFFFBF00"/>
        //    <mfgpreviewcolor name="substratecolor" color="0xFF786E46"/>
        //    </mfgpreviewcolors>
        //
        //   Eagle is like Web color but alpha is first and we need to strip it.
//        for (MfgPreviewColor mfgC : board.getMfgPreviewColors()) {
//            Color color = ColorUtils.getColor(mfgC.getColor());
//            switch (mfgC.getName()) {
//                case "soldermaskcolor":
//                    solderMaskColor = color;
//                    break;
//                case "silkscreencolor":
//                    silkScreenColor = color;
//                    break;
//                case "backgroundcolor":
//                    backgroundColor = color;
//                    break;
//                case "coppercolor":
//                    copperColor = color;
//                    break;
//                case "substratecolor":
//                    substrateColor = color;
//                    break;
//            }
//        }
        // Decide Top or bottom
        // Draw background
        // Draw substrate
        // Draw Pads and Copper
        // Draw Soldermask
        // Draw SilkScreen
        // Add Computed Dimensions
        for (_AQuantum element : board.getPlain()) {
            int colorIndex = layers[element.getLayerNum()].getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));
            int l = element.getLayerNum();

            // Skip if not a layer we want to draw. Speeds up draw.
            // 1,16           Top or Bottom Copper
            // 21,22,25,26    Silk Layer
            // 29,30          Mask layer
            // 0              Holes
//            if (l != 1 && l != 16 && l != 20 && l != 21 && l != 22 && l != 25 && l != 26 && l != 45 ) {
//                continue; // Next element.
//            }
            // polygon | wire | text | dimension | circle | spline | rectangle | frame | hole
            if (element instanceof ElementPolygon e) {
                switch (l) {
                    case 21 /*, 22 */, 25 /*, 26 */ -> {
                        Node n = LibraryElementNode.createPolygonCurved(e, silkScreenColor, false);
                        silkNodes.add(n);
                    }
                    case 1 /*, 16 */ -> {
                        // Text in etch.
                        // TODO: Toggle top or bottom.
                        Node n = LibraryElementNode.createPolygonCurved(e, copperColor, false);
                        rank.get(0).add(n);
                    }
                    case 29 -> { // StopMask Top
                        // Text in etch.
                        // TODO: Toggle top or bottom.
                        Node n = LibraryElementNode.createPolygonCurved(e, solderMaskColor, false);
                        maskNodes.add(n);
                    }
                    default -> {
                        //chld.add(LibraryElementNode.createPolygonCurved(e, c, false));
                    }
                }
            } else if (element instanceof Wire e) {
                switch (l) {
                    case 1 /* , 16 */ -> {
                        // Text in etch.
                        // TODO: Toggle top or bottom.
                        Node n = LibraryElementNode.createWireNode(e, copperColor, false);
                        rank.get(0).add(n);
                    }
                    case 20 -> { // Dimension
                        //Node n = LibraryElementNode.createWireNode(e, Color.YELLOW, false);
                        substrateWires.add(e);
                    }
                    case 21 /*, 22 */, 25 /*, 26 */ -> { // Silk Screen
                        Node n = LibraryElementNode.createWireNode(e, silkScreenColor, false);
                        silkNodes.add(n);
                    }
                    case 29 -> { // Stop Mask TOP
                        Node n = LibraryElementNode.createWireNode(e, solderMaskColor, false);
                        maskNodes.add(n);
                    }
                    default -> {
                    }
                }
            } else if (element instanceof ElementText e) {
                switch (l) {
                    case 1 /*, 16 */ -> {
                        // Text in etch.
                        // TODO: Toggle top or bottom.
                        Node n = LibraryElementNode.createText(e, null, copperColor, null, false);
                        rank.get(0).add(n);
                    }
                    case 21 /*, 22 */, 25 /*, 26 */ -> {
                        Node n = LibraryElementNode.createText(e, null, silkScreenColor, null, false);
                        silkNodes.add(n);
                    }
                    case 29 -> {
                        Node n = LibraryElementNode.createText(e, null, solderMaskColor, null, false);
                        maskNodes.add(n);
                    }
                    default -> {
                        //Node n = LibraryElementNode.createText(e, c);
                        //                    chld.add(n);
                    }
                }
            } else if (element instanceof Dimension e) {
//                chld.add(LibraryElementNode.createDimensionNode(
//                        e, layers, palette)
//                );
            } else if (element instanceof ElementCircle e) {
                switch (l) {
                    case 1 /*, 16 */ -> {
                        // Text in etch.
                        // TODO: Toggle top or bottom.
                        Node n = LibraryElementNode.createCircleNode(e, copperColor, false);
                        rank.get(0).add(n);
                    }
                    case 21 /*, 22 */, 25 /*, 26 */ -> {
                        Node n = LibraryElementNode.createCircleNode(e, silkScreenColor, false);
                        silkNodes.add(n);
                    }
                    case 29 -> {
                        Node n = LibraryElementNode.createCircleNode(e, solderMaskColor, false);
                        maskNodes.add(n);
                    }
                    default -> {
                        //Node n = LibraryElementNode.createCircleNode(e, c, false);
                        //                      chld.add(n);
                    }
                }
            } else if (element instanceof Spline e) {
                // Only the plain group can have Spline and the Spline can only
                // be on the Dimension layer.

                //chld.add(LibraryElementNode.createSplineNode(e, c));
                LOGGER.log(Level.SEVERE, "TODO: Implement Spline Node");
            } else if (element instanceof ElementRectangle e) {
                switch (l) {
                    case 1 /*, 16 */ -> {
                        // Text in etch.
                        // TODO: Toggle top or bottom.
                        Node n = LibraryElementNode.createRectangle(e, copperColor, false);
                        rank.get(0).add(n);
                    }
                    case 21 /*, 22 */, 25 /*, 26 */ -> {
                        Node n = LibraryElementNode.createRectangle(e, silkScreenColor, false);
                        silkNodes.add(n);
                    }
                    case 29 -> {
                        Node n = LibraryElementNode.createRectangle(e, solderMaskColor, false);
                        maskNodes.add(n);
                    }
                    default -> {
                    }
                }
            } else if (element instanceof FrameElement e) { // No Frame for MFG preview
            } else if (element instanceof Hole e) {
                boolean mirror = false;
                //LOGGER.log(Level.SEVERE, "Hole is on Layer: " + e.getLayerNum());
                Circle circ = new Circle(mirror ? -e.getX() : e.getX(), -e.getY(), e.getDrill() / 2.0, backgroundColor);
                circ.setStroke(null);

                holeNodes.add(circ);
            } else {
                LOGGER.log(Level.SEVERE, "Encountered unhandled plain element: " + element.getElementName());
            }
        }

        Shape dimOutline = null;
        Shape dimMask = null;
        DesignRules dr = board.getDesignRules();
        String drWire2Wire = dr.getRule(DrcDefs.MD_WIRE2WIRE); // Wire to Wire
        Double wireIsolate = Units.toMM(drWire2Wire);
        String drDim2Wire = dr.getRule(DrcDefs.MD_COPPER2DIMENSION);
        Double dimIsolate = Units.toMM(drDim2Wire);

        for (ElementElement element : board.getElements()) {

            Optional<Library> libSearch = board.getLibraries().stream().filter(
                    library -> library.getName().equals(element.getLibrary())
            ).findFirst();

            if (!libSearch.isEmpty()) {
                Footprint pkg = libSearch.get().getPackage(element.getFootprint());

                Node pkgCopper = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 1, copperColor, 0);
                pkgCopper.setLayoutX(element.getX());
                pkgCopper.setLayoutY(-element.getY());
                pkgCopper.getTransforms().add(new Rotate(-element.getRot()));
                rank.get(0).add(pkgCopper);

                // Isolate Items into iso[0]
                Node pkgIso = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 1, substrateColor, wireIsolate);
                pkgIso.setLayoutX(element.getX());
                pkgIso.setLayoutY(-element.getY());
                pkgIso.getTransforms().add(new Rotate(-element.getRot()));
                isolate.get(0).add(pkgIso);

                // Drills
                Node pkgHole = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 45, backgroundColor, 0);
                pkgHole.setLayoutX(element.getX());
                pkgHole.setLayoutY(-element.getY());
                pkgHole.getTransforms().add(new Rotate(-element.getRot()));
                holeNodes.add(pkgHole);

                // Outlines (ex. cyber-1 card pkg)
                // Silk Items -- Name
                Node silkName = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 25, silkScreenColor, 0);
                silkName.setLayoutX(element.getX());
                silkName.setLayoutY(-element.getY());
                silkName.getTransforms().add(new Rotate(-element.getRot()));
                silkNodes.add(silkName);

                // Silk Items -- Place
                Node silkPlace = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 21, silkScreenColor, 0);
                silkPlace.setLayoutX(element.getX());
                silkPlace.setLayoutY(-element.getY());
                silkPlace.getTransforms().add(new Rotate(-element.getRot()));
                silkNodes.add(silkPlace);

                // Mask Items
                Node stopMask = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 29, solderMaskColor, 0);
                stopMask.setLayoutX(element.getX());
                stopMask.setLayoutY(-element.getY());
                stopMask.getTransforms().add(new Rotate(-element.getRot()));
                maskNodes.add(stopMask);

            } else {
                LOGGER.log(Level.SEVERE,
                        "Couldn''t find local library called: {0} for package: {1}",
                        new Object[]{element.getLibrary(), element.getFootprint()}
                );
            }
        }

        // Add our drawabales in the correct order over the default layer.
        if (!substrateWires.isEmpty()) {
            Shape dimShape = LibraryElementNode.createPath(substrateWires, substrateColor, false);
            chld.add(dimShape);

            dimOutline = LibraryElementNode.createPath(substrateWires, null, false);
            dimOutline.setStrokeWidth(0.2);
            dimOutline.setStrokeType(StrokeType.OUTSIDE);
            dimOutline.setStroke(substrateColor.brighter());

            dimMask = LibraryElementNode.createPath(substrateWires, null, false);
            dimMask.setStrokeWidth(dimIsolate);
            dimMask.setStrokeType(StrokeType.INSIDE);
            dimMask.setStroke(Color.RED);
            dimMask.setFill(solderMaskColor);

//            Shape pp = LibraryElementNode.createPath(substrateWires, null, false);
//            pp.setStrokeWidth(dimIsolate);
//            pp.setStrokeType(StrokeType.INSIDE);
//            pp.setStroke(substrateColor);
//            isolate.get(0).add(pp);
        }

        for (Signal sig : board.getSignals()) {

            for (_AQuantum el : sig.getElements()) {
                if (el.getLayerNum() != 1 && !(el instanceof Via)) {
                    continue; // Only layer 1 for now.
                }
                if (el instanceof Wire w) {
                    rank.get(0).add(LibraryElementNode.createWireNode(w, copperColor, false));

                    Shape isoWire = LibraryElementNode.createWireNode(w, substrateColor, false);
                    isoWire.setStrokeWidth(w.getWidth() + (wireIsolate * 2.0));
                    isolate.get(0).add(isoWire);
                } else if (el instanceof ContactRef cref) { // Not a drawable thing
//
//                    LOGGER.log(Level.SEVERE, "    ContactRef not handled: {0} pad:{1} routeTag:{2}",
//                            new Object[]{
//                                el.getElementName(),
//                                cref.getPad(),
//                                cref.getRouteTag()
//                            }
//                    );
                } else if (el instanceof Via v) {
                    Circle viaC = new Circle(v.getDerivedDiameter(dr, Via.Layer.TOP) / 2.0, copperColor);
                    viaC.setLayoutX(v.getX());
                    viaC.setLayoutY(-v.getY());
                    rank.get(0).add(viaC);

                    Circle isoC = new Circle(v.getDerivedDiameter(dr, Via.Layer.TOP) / 2.0 + wireIsolate, substrateColor);
                    isoC.setLayoutX(v.getX());
                    isoC.setLayoutY(-v.getY());
                    isolate.get(0).add(isoC);

                    Circle drlC = new Circle(v.getDrill() / 2.0, backgroundColor);
                    drlC.setLayoutX(v.getX());
                    drlC.setLayoutY(-v.getY());
                    holeNodes.add(drlC);
                } else if (el instanceof ElementPolygon ep) {
                    Shape poly = LibraryElementNode.createPolygonCurved(ep, copperColor, false);
                    poly.toBack();
                    rank.get(ep.getRank()).add(poly);

                    Shape isoShape = LibraryElementNode.createPolygonCurved(ep, null, false);
                    isoShape.setStrokeWidth(wireIsolate);
                    isoShape.setStrokeType(StrokeType.OUTSIDE);
                    isoShape.setStrokeLineJoin(StrokeLineJoin.ROUND);
                    isoShape.setStroke(substrateColor);
                    isolate.get(ep.getRank()).add(isoShape);
                } else {
                    LOGGER.log(Level.SEVERE, "    Signal Element not handled: {0}", el.getElementName());
                }
            }
        }

        // Copper
        // Ranks 7..1  Back to Front
        for (int i = rank.size() - 1; i >= 0; i--) {
            //if (i < isolate.length) {
            isolate.get(i).forEach(node -> {
                chld.add(node);
            });

            rank.get(i).forEach(node -> {
                chld.add(node);
            });
        }

        // If board outline is not closed, then assume a mask area.
        if (dimMask == null) {
            dimMask = new Rectangle(getBoundsInLocal().getWidth(), getBoundsInLocal().getHeight(), solderMaskColor);
            dimMask.setLayoutY(-dimMask.getBoundsInLocal().getHeight());
        }

        // maskNodes leafs are shapes to be cut from a main mask.
        // Nodes can be nested in multiple Groups and Panes
        // TODO: Clean up if possible.
        for (Node n : maskNodes) {
            if ((n instanceof Parent parent)) {
                for (Node t : parent.getChildrenUnmodifiable()) {
                    if (t instanceof Shape s) {
                        dimMask = Shape.subtract(dimMask, s);
                    } else if (t instanceof Pane p) {
                        for (Node tt : p.getChildren()) {
                            if (tt instanceof Shape ss) {
                                dimMask = Shape.subtract(dimMask, ss);
                            }
                        }
                    } else if (t instanceof Group p) {
                        for (Node tt : p.getChildren()) {
                            if (tt instanceof Shape ss) {
                                dimMask = Shape.subtract(dimMask, ss);
                            } else if (tt instanceof Pane) {
                                for (Node ttt : p.getChildren()) {
                                    if (ttt instanceof Shape ss) {
                                        dimMask = Shape.subtract(dimMask, ss);
                                    } else if (ttt instanceof Pane pp) {
                                        for (Node tttt : pp.getChildren()) {
                                            if (tttt instanceof Shape ss) {
                                                dimMask = Shape.subtract(dimMask, ss);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (n instanceof Shape s) {
                dimMask = Shape.subtract(dimMask, s);
            }
        }

        dimMask.setFill(solderMaskColor);
        chld.add(dimMask);

        // Silk
        for (Node n : silkNodes) {
            chld.add(n);
        }

        // Holes
        for (Node n : holeNodes) {
            chld.add(n);
        }

        // TODO:  Clip outside dimension shape.
        // Finally add the dimension outline
        if (dimOutline != null) {
            chld.add(dimOutline);
        }
    }
}
