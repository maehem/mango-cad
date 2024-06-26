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
import com.maehem.mangocad.model.element.basic.CircleElement;
import com.maehem.mangocad.model.element.basic.Gate;
import com.maehem.mangocad.model.element.basic.Package3dInstance;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.PolygonElement;
import com.maehem.mangocad.model.element.basic.RectangleElement;
import com.maehem.mangocad.model.element.basic.TextElement;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.Device;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.GroupContainer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    public static Node devicePreview(DeviceSet devSet, Library lib) {
        SplitPane pane = new SplitPane();
        pane.setOrientation(Orientation.HORIZONTAL);
        SplitPane pkgPane = new SplitPane();
        pkgPane.setOrientation(Orientation.VERTICAL);
        List<Device> devices = devSet.getDevices();
        Group footprintPreview;
        if (!devices.isEmpty()) {
            footprintPreview = footprintPreview(
                    lib.getPackage(devSet.getDevices().get(0).getFootprint()),
                    lib,
                    true
            );
        } else {
            // There are no footprints in device yet.
            footprintPreview = new Group();
        }
        GroupContainer footprintPane = new GroupContainer(footprintPreview, 0.1);

        pkgPane.getItems().add(footprintPane);

        ImageView package3DPreview = package3DPreview(null, lib);
        pkgPane.getItems().add(new BorderPane(package3DPreview));

        Group gsPreview = gateSetPreview(devSet.getGates(), lib);

        GroupContainer gsPane = new GroupContainer(gsPreview);

        pane.getItems().add(gsPane);
        pane.getItems().add(pkgPane);

        return pane;
    }

    /**
     * Render a preview of the symbols.
     *
     * NOTE: Eagle Y coordinates are negative. Up is positive, Y origin at
     * bottom.
     *
     * @param symbol
     * @param lib
     * @param showGauge
     * @return
     */
    public static Group symbolPreview(Symbol symbol, Library lib, boolean showGauge) {
        //LayerElement[] layers = lib.getParentDrawing().getLayers();
        Layers layers = lib.getParentDrawing().getLayers();

        ColorPalette palette = lib.getParentDrawing().getLayers().getPalette();

        Node g = LibraryElementNode.createSymbolNode(null, symbol, null, null, null, layers, palette);

        StackPane stackPane = new StackPane(g); // Centers the symbol
        Group nodeGroup = new Group(stackPane);

        // Add optional size gauge
        if (showGauge) {
            Pane scaleGauge = DetailNodes.scaleGauge();
            double scaleH = scaleGauge.getBoundsInLocal().getHeight();
            double scaleW = scaleGauge.getBoundsInLocal().getWidth();

            scaleGauge.setTranslateX(
                    stackPane.getBoundsInLocal().getWidth() / 2.0 - scaleW / 2.0
            );
            scaleGauge.setTranslateY(
                    stackPane.getBoundsInLocal().getHeight() + scaleH / 4.0
            );
            nodeGroup.getChildren().add(scaleGauge);
        }

        return nodeGroup;
    }

    /**
     * Render a preview of the symbols.NOTE: Eagle Y coordinates are reversed.
     *
     * Up is positive, Y origin at bottom.
     *
     * @param gate
     * @param lib
     * @return
     */
    public static Node deviceGatePreview(Gate gate, Library lib) {
        //LayerElement[] layers = lib.getParentDrawing().getLayers();
        Layers layers2 = lib.getParentDrawing().getLayers();
        ColorPalette palette = lib.getParentDrawing().getLayers().getPalette();

        Group g = new Group();
        //StackPane pane = new StackPane(g);

        Symbol symbol = lib.getSymbol(gate.getSymbol());
        symbol.getElements().forEach((e) -> {
            Color c;
            if (e instanceof LayerNumberProperty lp) {
                //LOGGER.log(Level.SEVERE, "Process Symbol Element: " + e.getElementName() );
                int layerNum = lp.getLayerNum();
                //LayerElement le = layers[layerNum];
                LayerElement le = layers2.get(layerNum);
                if (le == null) {
                    LOGGER.log(Level.SEVERE, "No Layer for: {0}", layerNum);
                    c = Color.RED;
                } else {
                    c = ColorUtils.getColor(palette.getHex(le.getColorIndex()));
                }
            } else {
                c = Color.GREY;
            }

            if (e instanceof Wire wire) {
                g.getChildren().add(LibraryElementNode.createWireNode(wire, c, false));
            } else if (e instanceof RectangleElement elementRectangle) {
                g.getChildren().add(LibraryElementNode.createRectangle(elementRectangle, c, false));
            } else if (e instanceof TextElement elementText) {
                String gateName = null;
                if (elementText.getValue().equals(">NAME")) {
                    gateName = gate.getName();
                }
                //LOGGER.log(Level.SEVERE, "Gate: {0}  text: {1}", new Object[]{name, elementText.getValue()});
                g.getChildren().addAll(LibraryElementNode.createText2(elementText, gateName, c, null, true));
                //g.getChildren().add(LibraryElementNode.crosshairs(elementText.getX(), -elementText.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if (e instanceof PolygonElement elementPolygon) {
                g.getChildren().add(LibraryElementNode.createPolygonCurved(elementPolygon, c, false));
            } else if (e instanceof Pin pin) {
                g.getChildren().add(LibraryElementNode.createPinNode(pin, c, null, false));
            } else if (e instanceof CircleElement elementCircle) {
                g.getChildren().add(LibraryElementNode.createCircleNode(elementCircle, c, false));
            }
        });
        g.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.05, Color.RED
        ));

        return g;
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
    public static Group footprintPreview(Footprint footprint, Library lib, boolean showGauge) {
        //LayerElement[] layers = lib.getParentDrawing().getLayers();
        Layers layers = lib.getParentDrawing().getLayers();
        ColorPalette palette = lib.getParentDrawing().getLayers().getPalette();

        //Group g = new Group();
        Node g = LibraryElementNode.createPackageNode(footprint, layers, palette);

        StackPane stackPane = new StackPane(g); // Centers the symbol
        Group nodeGroup = new Group(stackPane);

        // Add optional size gauge
        if (showGauge) {
            Pane scaleGauge = DetailNodes.scaleGauge();
            double scaleH = scaleGauge.getBoundsInLocal().getHeight();
            double scaleW = scaleGauge.getBoundsInLocal().getWidth();

            scaleGauge.setTranslateX(
                    stackPane.getBoundsInLocal().getWidth() / 2.0 - scaleW / 2.0
            );
            scaleGauge.setTranslateY(
                    stackPane.getBoundsInLocal().getHeight() + scaleH / 4.0
            );
            nodeGroup.getChildren().add(scaleGauge);
        }

        return nodeGroup;
    }

    public static ImageView package3DPreview(Package3dInstance pkg3d, Library lib) {
        // pkg3d might be null

        Image img = new Image(DetailNodes.class.getResourceAsStream("/icons/cube-isometric.png"));
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.setFitHeight(100);
        iv.setOpacity(0.2);
        return iv;
    }

    public static Pane scaleGauge() {
        final Color COLOR = new Color(0.5, 0.7, 1.0, 0.5);
        final double FONT_SIZE = 2.0;
        double mmNum = 10.0;
        double inNum = 10 * 1.27;
        Group g = new Group();

        Line left = new Line(0, FONT_SIZE * 1.4, 0, -FONT_SIZE * 1.4);
        Line center = new Line(0, 0, inNum, 0);
        Line mm = new Line(mmNum, 0, mmNum, -FONT_SIZE);
        Line in = new Line(inNum, 0, inNum, FONT_SIZE);

        g.getChildren().addAll(left, center, mm, in);

        Text mmText = new Text("10mm");
        mmText.setFont(Font.font(FONT_SIZE));
        mmText.setFill(COLOR.brighter().desaturate());
        mmText.setLayoutX(1.0);
        mmText.setLayoutY(-FONT_SIZE * 0.4);
        Text inText = new Text("0.50in");
        inText.setFont(Font.font(FONT_SIZE));
        inText.setFill(COLOR.brighter().desaturate());
        inText.setLayoutX(1.0);
        inText.setLayoutY(FONT_SIZE * 1);

        g.getChildren().addAll(mmText, inText);

        for (Node n : g.getChildren()) {
            if (n instanceof Line l) {
                l.setStroke(COLOR);
                l.setStrokeWidth(FONT_SIZE * 0.1);
            }
        }
        left.setStrokeWidth(FONT_SIZE * 0.16);

        StackPane sp = new StackPane(g);
        Bounds bounds = sp.getBoundsInLocal();
        sp.setPrefSize(bounds.getWidth(), bounds.getHeight());
        sp.setMaxSize(bounds.getWidth(), bounds.getHeight());
        return sp;
    }

    public static Group gateSetPreview(List<Gate> gates, Library lib) {
        Group g = new Group();

        gates.forEach((gate) -> {
            Node n = deviceGatePreview(gate, lib);
            n.setLayoutX(gate.getX());
            n.setLayoutY(-gate.getY());

            g.getChildren().add(n);
        });

        return g;
    }

    public static Node devicePackageListNode(DeviceSet deviceSet) {
        return new DevicePackageList(deviceSet);
    }
}
