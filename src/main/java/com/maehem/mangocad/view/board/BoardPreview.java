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
import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.*;
import com.maehem.mangocad.model.element.drawing.Board;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.enums.GridUnit;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Signal;
import com.maehem.mangocad.model.element.misc.DesignRules;
import com.maehem.mangocad.model.element.misc.Grid;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.util.DrcDefs;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.library.LibraryElementNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
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

//        Group copper = populateGrid(
//                getBoundsInLocal().getWidth(),
//                getBoundsInLocal().getHeight(),
//                grid.getDistance() * (grid.getUnit() == GridUnit.INCH ? 0.0393701 : 1)
//        );
//        getChildren().add(copper);
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
        //LayerElement[] layers = board.getParentDrawing().getLayers();
        Layers layers = board.getParentDrawing().getLayers();
        ColorPalette palette = board.getParentDrawing().getPalette();
        ObservableList<Node> chld = getChildren();

        for (Element element : board.getPlain()) {
            Color c;
            if (element instanceof LayerNumberProperty lp) {
                int colorIndex = layers.get(lp.getLayerNum()).getColorIndex();
                c = ColorUtils.getColor(palette.getHex(colorIndex));
            } else {
                c = Color.GRAY;
            }

            // polygon | wire | text | dimension | circle | spline | rectangle | frame | hole
            if (element instanceof ElementPolygon e) {
                chld.add(LibraryElementNode.createPolygonCurved(e, c, false));
            } else if (element instanceof Wire e) {
                chld.add(LibraryElementNode.createWireNode(e, c, false));
            } else if (element instanceof ElementText e) {
                chld.addAll(LibraryElementNode.createText2(e, c));
            } else if (element instanceof Dimension e) {
                chld.add(LibraryElementNode.createDimensionNode(
                        e, layers, palette)
                );
            } else if (element instanceof ElementCircle e) {
                chld.add(LibraryElementNode.createCircleNode(e, c, false));
            } else if (element instanceof Spline) {
                // Only the plain group can have Spline and the Spline can only
                // be on the Dimension layer.
            } else if (element instanceof ElementRectangle e) {
                chld.add(LibraryElementNode.createRectangle(e, c, false));
            } else if (element instanceof FrameElement e) {
                chld.add(LibraryElementNode.createFrameNode(e, c));
            } else if (element instanceof Hole e) {
                int ocColorIndex = layers.get(20).getColorIndex();
                Color oc = ColorUtils.getColor(palette.getHex(ocColorIndex));
                int tmColorIndex = layers.get(29).getColorIndex();
                Color tm = ColorUtils.getColor(palette.getHex(tmColorIndex));
                int bmColorIndex = layers.get(30).getColorIndex();
                Color bm = ColorUtils.getColor(palette.getHex(bmColorIndex));
                chld.add(LibraryElementNode.createHoleNode(
                        e, 0.04,
                        oc, tm, bm,
                        board.getDesignRules()
                ));
            } else {
                LOGGER.log(Level.SEVERE, "Encountered unhandled plain element: {0}", element.getElementName());
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
            for (Element el : sig.getElements()) {
                if (el instanceof Wire w) {
                    int colorIndex = layers.get(w.getLayerNum()).getColorIndex();
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
                    Color padColor = ColorUtils.getColor(palette.getHex(layers.get(18).getColorIndex()));
                    Color tm = ColorUtils.getColor(palette.getHex(layers.get(29).getColorIndex()));
                    Color bm = ColorUtils.getColor(palette.getHex(layers.get(30).getColorIndex()));
                    chld.add(LibraryElementNode.createVia(
                            v,
                            padColor, tm, bm,
                            board.getDesignRules()
                    ));
                } else if (el instanceof ElementPolygon ep) {
                    int colorIndex = layers.get(ep.getLayerNum()).getColorIndex();
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
        Color substrateColor = new Color(0.3, 0.3, 0.0, 0.9); // Dark Brown-Yellow

        // Board file overrides any mfg colors
        //    <mfgpreviewcolors>
        //    <mfgpreviewcolor name="soldermaskcolor" color="0xC8008000"/>
        //    <mfgpreviewcolor name="silkscreencolor" color="0xFFFEFEFE"/>
        //    <mfgpreviewcolor name="backgroundcolor" color="0xFF282828"/>
        //    <mfgpreviewcolor name="coppercolor" color="0xFFFFBF00"/>
        //    <mfgpreviewcolor name="substratecolor" color="0xFF786E46"/>
        //    </mfgpreviewcolors>
        //
//        for (MfgPreviewColor mfgC : board.getMfgPreviewColors()) {
//            Color color = ColorUtils.getColor(mfgC.getColor()); // Convert Eagle color to JavaFX Color
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
//        LayerElement[] layers = board.getParentDrawing().getLayers();
//        ColorPalette palette = board.getParentDrawing().getPalette();
        ObservableList<Node> chld = getChildren();

        ArrayList<Node> silkNodes = new ArrayList<>();
        ArrayList<Node> maskNodes = new ArrayList<>();

        ArrayList<Shape> restrict = new ArrayList<>();

        //Map<String, List<Person>> groups = new HashMap<String, List<Person>>();
        Map<String, ArrayList<Shape>> copperSignals2 = new HashMap<>();
        Map<String, ArrayList<Shape>> isolationSignals2 = new HashMap<>(); // Isolation except own signal.
        Map<String, ArrayList<Shape>> restricts2 = new HashMap<>(); // Isolation but always.

        //ArrayList<ArrayList<Shape>> copperSignals = new ArrayList<>();
        //ArrayList<ArrayList<Shape>> isolationSignals = new ArrayList<>();
        ArrayList<ArrayList<SignalPolygon>> signalPolys = new ArrayList<>();
        signalPolys.add(new ArrayList<>());
        signalPolys.add(new ArrayList<>());
        signalPolys.add(new ArrayList<>());
        signalPolys.add(new ArrayList<>());
        signalPolys.add(new ArrayList<>());
        signalPolys.add(new ArrayList<>());
        signalPolys.add(new ArrayList<>());

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

        DesignRules dr = board.getDesignRules();
        String drMinWire = dr.getRule(DrcDefs.MS_WIDTH); // Wire Width
        Double wireMin = GridUnit.toMM(drMinWire);
        String drWire2Wire = dr.getRule(DrcDefs.MD_WIRE2WIRE); // Wire to Wire
        Double wireIsolate = GridUnit.toMM(drWire2Wire);
//        String drDim2Wire = dr.getRule(DrcDefs.MD_COPPER2DIMENSION);
//        Double dimIsolate = Units.toMM(drDim2Wire);
        String drThIso = dr.getRule(DrcDefs.SL_THERMAL_ISOLATE); // Thermal Isolation
        Double thermalIsolate = GridUnit.toMM(drThIso);
//        String mlStopMax = dr.getRule(DrcDefs.ML_MAX_STOP_FRAME); // Thermal Isolation
//        Double viaStopMask = Units.toMM(mlStopMax);
        String mlViaStopLimit = dr.getRule(DrcDefs.ML_VIA_STOP_LIMIT); // Thermal Isolation
        Double viaStopLimit = GridUnit.toMM(mlViaStopLimit);

        // Decide Top or bottom
        // Draw background
        // Draw substrate
        // Draw Pads and Copper
        // Draw Soldermask
        // Draw SilkScreen
        // Add Computed Dimensions
        for (Element element : board.getPlain()) {
            //int colorIndex = layers[element.getLayerNum()].getColorIndex();
            //Color c = ColorUtils.getColor(palette.getHex(colorIndex));
            //int l = element.getLayerNum();

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
                switch (e.getLayerNum()) {
                    case 21 /*, 22 */, 25 /*, 26 */ -> {
                        Node n = LibraryElementNode.createPolygonCurved(e, silkScreenColor, false);
                        silkNodes.add(n);
                    }
                    case 1 /*, 16 */ -> { // TODO: Plain Polygons should not appear in Layer1-16.
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
                switch (e.getLayerNum()) {
                    case 1 /* , 16 */ -> { // TODO: Plain Wire should not appear in Layer1-16.
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
                switch (e.getLayerNum()) {
                    case 1 /*, 16 */ -> {
                        // Text in etch.
                        // TODO: Toggle top or bottom.
                        List<Shape> n = LibraryElementNode.createText2(e, null, copperColor, null, false);
                        rank.get(0).addAll(n);

                        ElementText textIsolate = e.copy();
                        int ratio = textIsolate.getRatio();
                        double size = textIsolate.getSize();
                        double stroke = textIsolate.getDerivedStroke();
                        double thick = stroke + wireIsolate * 2.0;
                        double newRatio = thick / (size * 0.01);

                        //textIsolate.setRatio((int) newRatio);
                        textIsolate.setRatio((int) newRatio);
                        LOGGER.log(Level.SEVERE,
                                "Old ratio: {0}  new ratio: {1} oldStroke: {2}   new Stroke:{3}",
                                new Object[]{ratio, newRatio, stroke, textIsolate.getDerivedStroke()}
                        );

                        List<Shape> rn = LibraryElementNode.createText2(textIsolate, null, copperColor, null, false);
                        restrict.addAll(rn);
                    }
                    case 21 /*, 22 */, 25 /*, 26 */ -> {
                        List<Shape> n = LibraryElementNode.createText2(e, null, silkScreenColor, null, true);
                        silkNodes.addAll(n);
                    }
                    case 29 -> {
                        List<Shape> n = LibraryElementNode.createText2(e, null, solderMaskColor, null, false);
                        maskNodes.addAll(n);
                    }
                    default -> {
                        //Node n = LibraryElementNode.createText(e, c);
                        //                    chld.add(n);
                    }
                }
            } else if (element instanceof Dimension) {
//                chld.add(LibraryElementNode.createDimensionNode(
//                        e, layers, palette)
//                );
            } else if (element instanceof ElementCircle e) {
                switch (e.getLayerNum()) {
                    case 1 /*, 16 */ -> {
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
                    }
                }
            } else if (element instanceof Spline e) {
                // Only the plain group can have Spline and the Spline can only
                // be on the Dimension layer.
            } else if (element instanceof ElementRectangle e) {
                switch (e.getLayerNum()) {
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
            } else if (element instanceof FrameElement) { // No Frame for MFG preview
            } else if (element instanceof Hole e) {
                boolean mirror = false;
                //LOGGER.log(Level.SEVERE, "Hole is on Layer: " + e.getLayerNum());
                Circle circ = new Circle(mirror ? -e.getX() : e.getX(), -e.getY(), e.getDrill() / 2.0, backgroundColor);
                circ.setStroke(null);

                holeNodes.add(circ);
            } else {
                LOGGER.log(Level.SEVERE, "Encountered unhandled plain element: {0}", element.getElementName());
            }
        }

        Shape substrate = null;
        Shape dimOutline = null;
        Shape dimMask = null;
        Shape pcbClip = null;

        LOGGER.log(Level.SEVERE, "Do Elements.");
        for (ElementElement element : board.getElements()) { // Component Packages
            Library lib = board.getLibrary(element.getLibrary());
            //if (lib != null) {
            Footprint pkg = lib.getPackage(element.getFootprint());
            if (pkg != null) {
                List<Shape> unusedPads = LibraryElementNode.createPackageUnusedPads(pkg, board, element, 1, copperColor, 0);

                // TODO: Apply layoutX/Y in createPackage method
                for (Shape s : unusedPads) {
                    s.setLayoutX(element.getX());
                    s.setLayoutY(-element.getY());
                    s.getTransforms().add(new Rotate(-element.getRot()));
                }
                rank.get(0).addAll(unusedPads);

                List<Shape> unusedPadsIso = LibraryElementNode.createPackageUnusedPads(pkg, board, element, 1, copperColor, wireIsolate);
                for (Shape s : unusedPadsIso) {
                    s.setLayoutX(element.getX());
                    s.setLayoutY(-element.getY());
                    s.getTransforms().add(new Rotate(-element.getRot()));
                }
                restrict.addAll(unusedPadsIso);

                // Layer 1 other plain things
                List<Shape> topPlain = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 1, copperColor, 0);
//                topPlain.setLayoutX(element.getX());
//                topPlain.setLayoutY(-element.getY());
//                topPlain.getTransforms().add(new Rotate(-element.getRot()));
                rank.get(0).addAll(topPlain);

                List<Shape> topPlainIso = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 1, copperColor, wireIsolate);
                restrict.addAll(topPlainIso);

//                // Drills
//                Node pkgHole = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 45, backgroundColor, 0);
//                pkgHole.setLayoutX(element.getX());
//                pkgHole.setLayoutY(-element.getY());
//                pkgHole.getTransforms().add(new Rotate(-element.getRot()));
//                holeNodes.add(pkgHole);
                // Drills
                List<Shape> pkgDrills = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 45, backgroundColor, 0);
                holeNodes.addAll(pkgDrills);

                // Outlines (ex. cyber-1 card pkg)
                // Silk Items -- Shapes only
                List<Shape> silkShapes = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 25, silkScreenColor, 0);
//                silkName.setLayoutX(element.getX());
//                silkName.setLayoutY(-element.getY());
//                silkName.getTransforms().add(new Rotate(-element.getRot()));
                silkNodes.addAll(silkShapes);

                // Silk Items -- Place
                List<Shape> silkPlace = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 21, silkScreenColor, 0);
//                silkPlace.setLayoutX(element.getX());
//                silkPlace.setLayoutY(-element.getY());
//                silkPlace.getTransforms().add(new Rotate(-element.getRot()));
                silkNodes.addAll(silkPlace);

                // Text Nodes Silk Place
                List<Node> silkTxtPlace = LibraryElementNode.createPackageMfgPreviewTxtNode(pkg, element, 21, silkScreenColor, 0);
                silkNodes.addAll(silkTxtPlace);

                // Text Nodes Silk Name
                List<Node> silkTxtName = LibraryElementNode.createPackageMfgPreviewTxtNode(pkg, element, 25, silkScreenColor, 0);
                silkNodes.addAll(silkTxtName);

                // Mask Items
                List<Shape> stopMask = LibraryElementNode.createPackageMfgPreviewNode(pkg, element, 29, solderMaskColor, 0);
//                for (Shape s : stopMask) {
//                    s.setLayoutX(element.getX());
//                    s.setLayoutY(-element.getY());
//                    s.getTransforms().add(new Rotate(-element.getRot()));
//                }
                maskNodes.addAll(stopMask);

                // Text Mask Nodes
                List<Node> maskTxt = LibraryElementNode.createPackageMfgPreviewTxtNode(pkg, element, 29, silkScreenColor, 0);
                maskNodes.addAll(maskTxt);

                // Text Top Copper Nodes
                List<Node> topCopperText = LibraryElementNode.createPackageMfgPreviewTxtNode(pkg, element, 1, silkScreenColor, 0);
                rank.get(0).addAll(topCopperText);

                // TODO:  FIX ME!!!
//                // Text Restrict Nodes
//                List<Node> restrictText = LibraryElementNode.createPackageMfgPreviewTxtNode(pkg, element, 21, silkScreenColor, 0);
//                restrict.addAll(restrictText);
            } else {
                LOGGER.log(Level.SEVERE,
                        "Couldn''t find local library called: {0} for package: {1}",
                        new Object[]{element.getLibrary(), element.getFootprint()}
                );
            }
        }

        // Polys first so that crefs can detect overlap for thermals.
        for (Signal sig : board.getSignals()) {

            ArrayList<Shape> copper = new ArrayList<>();
            ArrayList<Shape> isolation = new ArrayList<>();
            ArrayList<Shape> restricts = new ArrayList<>();

            for (Element el : sig.getElements()) {
                if (el instanceof SignalPolygon ep) {
                    if (ep.getLayerNum() != 1 && (!((el instanceof Via) || (el instanceof ContactRef)))) { // TODO: Via uses 'extent'
                        continue; // Only layer 1 for now.
                    }

                    signalPolys.get(ep.getRank()).add(ep);

                    Shape poly2 = LibraryElementNode.createPolygonCurved(ep, copperColor, false);
                    copper.add(poly2);

                    Shape isoShape2 = LibraryElementNode.createPolygonCurved(ep, null, false);
                    isoShape2.setStrokeWidth(wireIsolate);
                    isoShape2.setStrokeType(StrokeType.OUTSIDE);
                    isoShape2.setStrokeLineJoin(StrokeLineJoin.ROUND);
                    isoShape2.setStroke(substrateColor);
                    isolation.add(isoShape2);
                }
            }
            copperSignals2.put(sig.getName(), copper);
            isolationSignals2.put(sig.getName(), isolation);
            restricts2.put(sig.getName(), restricts);
        }

        // TODO:
        //     Gather pins from the same element and perform a union on the isolation/restrict
        //     before adding to the main iso layer.
        for (Signal sig : board.getSignals()) {
            ArrayList<Shape> copper = copperSignals2.get(sig.getName());
            ArrayList<Shape> isolation = isolationSignals2.get(sig.getName());
            ArrayList<Shape> restricts = restricts2.get(sig.getName());

            ArrayList<Shape> sigCopper = new ArrayList<>();

            for (Element el : sig.getElements()) {
//                if (el.getLayerNum() != 1 &&
//                        (
//                        !( (el instanceof Via) || (el instanceof ContactRef))
//                        )
//                    ) { // TODO: Via uses 'extent'
//                    continue; // Only layer 1 for now.
//                }
                if (el instanceof Wire w) {
                    if (w.getLayerNum() != 1) {
                        continue; // Only top for now.
                    }
                    sigCopper.add(LibraryElementNode.createWireNode(w, copperColor, false));

                    Shape isoWire2 = LibraryElementNode.createWireNode(w, substrateColor, false);
                    isoWire2.setStrokeWidth(w.getWidth() + (wireIsolate * 2.0));
                    isolation.add(isoWire2);
                } else if (el instanceof ContactRef cref) { // Look up pads with this signal
                    //LOGGER.log(Level.SEVERE, "ContactRef: {0}.{1}", new Object[]{cref.getElement(), cref.getPad()});
                    // Will generate thermals for signal polygons.
                    ElementElement elm = board.getElement(cref.getElement());
                    Element pad = elm.getFootprintPkg().getPad(cref.getPad());

                    if (pad == null) { // Do nothing
                    } else if (pad instanceof PadTHD p) {
                        Shape thdShape = LibraryElementNode.createThdPad(p, copperColor);
                        thdShape.setLayoutX(cref.getElementO().getX());
                        thdShape.setLayoutY(-cref.getElementO().getY());
                        thdShape.getTransforms().add(new Rotate(-cref.getElementO().getRot()));

                        Shape thdIsoShape = LibraryElementNode.createThdPad(p, substrateColor);
                        thdIsoShape.setStrokeWidth(wireIsolate);
                        thdIsoShape.setStrokeType(StrokeType.OUTSIDE);
                        thdIsoShape.setStroke(solderMaskColor);
                        thdIsoShape.setLayoutX(cref.getElementO().getX());
                        thdIsoShape.setLayoutY(-cref.getElementO().getY());
                        thdIsoShape.getTransforms().add(new Rotate(-cref.getElementO().getRot()));
                        if (sig.getName().equals("GPIO0")) {
                            int a = 0; /// Debug stop point
                        }
                        if (p.isThermals()) {
                            double polyLineW = 0;
                            boolean doThermals = false;
                            for (Shape s : copper) {
                                if ((s instanceof Path) || (s instanceof Polygon)) {
                                    Shape intersect = Shape.intersect(s, thdShape);
                                    if (intersect.getBoundsInLocal().getWidth() != -1) {
                                        doThermals = true;
                                        polyLineW = s.getStrokeWidth();
                                        break;
                                    }
                                }
                            }
                            if (doThermals) {
                                ArrayList<Shape> therms = LibraryElementNode.createThdThermal(
                                        p, copperColor,
                                        thermalIsolate, polyLineW * 2.0
                                );
                                for (Shape ts : therms) {
                                    ts.setLayoutX(cref.getElementO().getX());
                                    ts.setLayoutY(-cref.getElementO().getY());
                                    ts.getTransforms().add(new Rotate(-cref.getElementO().getRot()));
                                    thdIsoShape = Shape.subtract(thdIsoShape, ts);
                                }
                                thdIsoShape = Shape.subtract(thdIsoShape, thdShape);
                                sigCopper.addAll(therms);
                                //rank.get(0).addAll(therms);  // Debug.
                                restricts.add(thdIsoShape); // Absolute isolation
                                //isolation.add(thdIsoShape);
                                thdIsoShape.setFill(silkScreenColor);
                                //silkNodes.add(thdIsoShape);  // Debug
                            } else {
                                isolation.add(thdIsoShape);
                            }
                            sigCopper.add(thdShape);
                        }

                    } else if (pad instanceof PadSMD p) {
                        if ((!elm.getRotation().isMirror() && p.getLayerNum() == 1) || (elm.getRotation().isMirror() && p.getLayerNum() == 16)) {
                            //LOGGER.log(Level.SEVERE, "Add Pad: sig: {0}  pad: {1}.{2}", new Object[]{sig.getName(), cref.getElement(), cref.getPad()});
                            Shape smdShape = LibraryElementNode.createSmdPad(p, copperColor);
                            smdShape.setLayoutX(cref.getElementO().getX());
                            smdShape.setLayoutY(-cref.getElementO().getY());
                            smdShape.getTransforms().add(new Rotate(-cref.getElementO().getRot()));
                            sigCopper.add(smdShape);

                            // Isolate Pad
                            Shape smdIsoShape = LibraryElementNode.createSmdPad(p, solderMaskColor);
                            smdIsoShape.setStrokeWidth(wireIsolate);
                            smdIsoShape.setStrokeType(StrokeType.OUTSIDE);
                            smdIsoShape.setStroke(solderMaskColor);
                            smdIsoShape.setLayoutX(cref.getElementO().getX());
                            smdIsoShape.setLayoutY(-cref.getElementO().getY());
                            smdIsoShape.getTransforms().add(new Rotate(-cref.getElementO().getRot()));

                            if (p.isThermals()) {
                                boolean doThermals = false;
                                for (Shape s : copper) {
                                    if ((s instanceof Path) || (s instanceof Polygon)) {
                                        Shape intersect = Shape.intersect(s, smdShape);
                                        if (intersect.getBoundsInLocal().getWidth() != -1) {
                                            doThermals = true;
                                            break;
                                        }
                                    }
                                }
                                if (doThermals) {
                                    ArrayList<Shape> therms = LibraryElementNode.createSmdThermal(p, copperColor, thermalIsolate, wireMin);
                                    for (Shape ts : therms) {
                                        ts.setLayoutX(cref.getElementO().getX());
                                        ts.setLayoutY(-cref.getElementO().getY());
                                        ts.getTransforms().add(new Rotate(-cref.getElementO().getRot()));
                                        smdIsoShape = Shape.subtract(smdIsoShape, ts);
                                    }
                                    smdIsoShape = Shape.subtract(smdIsoShape, smdShape);
                                    sigCopper.addAll(therms);
                                    restrict.add(smdIsoShape);
                                } else {
                                    isolation.add(smdIsoShape);
                                }
                            }
                        }
                    } else {
                        LOGGER.log(Level.SEVERE, "pad is: {0} not handled.", new Object[]{pad.getElementName()});
                    }
                } else if (el instanceof Via v) {
                    Circle viaC2 = new Circle(v.getDerivedDiameter(dr, Via.Layer.TOP) / 2.0, copperColor);
                    viaC2.setLayoutX(v.getX());
                    viaC2.setLayoutY(-v.getY());
                    sigCopper.add(viaC2);

                    Circle isoC2 = new Circle(v.getDerivedDiameter(dr, Via.Layer.TOP) / 2.0 + wireIsolate, substrateColor);
                    isoC2.setLayoutX(v.getX());
                    isoC2.setLayoutY(-v.getY());
                    isolation.add(isoC2);

                    if (v.getDrill() > viaStopLimit) {
                        Circle maskC2 = new Circle(v.getMaskDiameter(dr, Via.Layer.TOP) / 2.0, substrateColor);
                        maskC2.setLayoutX(v.getX());
                        maskC2.setLayoutY(-v.getY());
                        maskNodes.add(maskC2);
                    }

                    Circle drlC = new Circle(v.getDrill() / 2.0, backgroundColor);
                    drlC.setLayoutX(v.getX());
                    drlC.setLayoutY(-v.getY());
                    holeNodes.add(drlC);
                } else if (el instanceof SignalPolygon) {
                    // Already processed in previous code section.
                } else {
                    LOGGER.log(Level.SEVERE, "    Signal Element not handled: {0}", el.getElementName());
                }
            }
            // Merge sigCopper into copper
            copper.addAll(sigCopper);
        }

        ArrayList<Shape> isolationShapes = new ArrayList<>();
        ArrayList<Shape> signalShapes = new ArrayList<>();

        for (String key : copperSignals2.keySet()) {
            {   // Shapes on each isolation group are combined.
                ArrayList<Shape> cs = isolationSignals2.get(key);
                Shape sss = new Rectangle(0, 0, Color.WHITE);
                sss.setLayoutY(-sss.getBoundsInLocal().getHeight());
                for (Shape sh : cs) {
                    sss = Shape.union(sss, sh);
                }
                sss.setFill(substrateColor);
                isolationShapes.add(sss);
            }
            {
                ArrayList<Shape> rs = restricts2.get(key);
                Shape sss = new Rectangle(0, 0, Color.WHITE);
                sss.setLayoutY(-sss.getBoundsInLocal().getHeight());
                for (Shape sh : rs) {
                    sss = Shape.union(sss, sh);
                }
                sss.setFill(substrateColor);
                restrict.add(sss);
            }

            { // Shapes on each copper group are combined.
                ArrayList<Shape> cs = copperSignals2.get(key);
                Shape sss = new Rectangle(0, 0, Color.WHITE);
                sss.setLayoutY(-sss.getBoundsInLocal().getHeight());
                for (Shape sh : cs) {
                    sss = Shape.union(sss, sh);
                }
                sss.setFill(copperColor);
                signalShapes.add(sss);
            }
        }

        for (int i = 0; i < signalShapes.size(); i++) {
            Shape csp = signalShapes.get(i);
            //LOGGER.log(Level.SEVERE, "Process signal: " + i);
            for (int j = 0; j < isolationShapes.size(); j++) {
                if (i != j) {
                    csp = Shape.subtract(csp, isolationShapes.get(j));
                }
            }

            // Subtract restrict shapes
            // debug
            for (int k = 0; k < restrict.size(); k++) {
                csp = Shape.subtract(csp, restrict.get(k));
            }

            //signalShapes.set(i, csp);
            csp.setFill(copperColor);
            chld.add(csp);
        }

        rank.get(0).forEach(node -> {
            chld.add(node);
        });

        // Add our drawabales in the correct order over the default layer.
        if (!substrateWires.isEmpty()) {
            substrate = LibraryElementNode.createPath(substrateWires, substrateColor, false);

            // A thin line around the PCB. Probably not needed.
            dimOutline = LibraryElementNode.createPath(substrateWires, null, false);
            dimOutline.setStrokeWidth(0.01);
            dimOutline.setStrokeType(StrokeType.OUTSIDE);
            dimOutline.setStroke(substrateColor.brighter());

            dimMask = LibraryElementNode.createPath(substrateWires, null, false);
            dimMask.setFill(solderMaskColor);

            pcbClip = LibraryElementNode.createPath(substrateWires, null, false);
            pcbClip.setFill(substrateColor);
            pcbClip.setStrokeWidth(1); // Margin
            pcbClip.setStrokeType(StrokeType.OUTSIDE);
            pcbClip.setStroke(Color.GREEN);
        }

        if (substrate != null) {
            chld.add(substrate);
            substrate.toBack();
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

        dimMask.setFill(solderMaskColor); // Subtract obliterates original fill color.
        chld.add(dimMask);

        // TODO: Subtract mask from silk
        // Silk
        for (Node n : silkNodes) {
            chld.add(n);
        }

        // Holes
        for (Node n : holeNodes) {
            chld.add(n);
        }

        // Dimension outline
        if (dimOutline != null) {
            chld.add(dimOutline);
        }

        // Clip outside dimension shape.
        if (pcbClip != null) {
//            setClip(pcbClip);
        }
    }
}
