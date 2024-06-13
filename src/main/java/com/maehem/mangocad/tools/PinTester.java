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
package com.maehem.mangocad.tools;

import com.maehem.mangocad.logging.LoggingFormatter;
import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.ElementListener;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.element.property.LocationXYProperty;
import com.maehem.mangocad.view.PickListener;
import com.maehem.mangocad.view.controlpanel.tab.PreviewContent;
import com.maehem.mangocad.view.node.PinNode;
import com.maehem.mangocad.view.node.ViewNode;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinTester extends Application implements ElementListener, PickListener {

    private static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    private static final double BG_W = 60;
    private static final double BG_H = 48;
    public static final int WIDGET_WIDTH = 300;
    private final Menu fileMenu = new Menu("File");
    private final Pin pinElement = new Pin();
    private final Group contentArea = new Group();
    private final PreviewContent previewContentPane = new PreviewContent();
    private final PinPropertiesList propertiesList = new PinPropertiesList(this, pinElement);

    private final ColorPalette palette = new ColorPalette(ColorPalette.Style.DARK);
    private final Layers layers = new Layers();

    //private final String textValue = "HridH\nXXXXX\nHHHHH";
    private final String pinName = "PIN";
    private Line hLine;
    private Line vLine;

    @Override
    public void start(Stage stage) throws Exception {
        configureLogging();
        LOGGER.log(Level.SEVERE, "MangoCAD Pin Tester...");

        // Set the title of the Stage
        stage.setTitle("MangoCAD Pin Tester");

        // Add icon for the app
        Image appIcon = new Image(getClass().getResourceAsStream("/icons/app-icon.png"));
        stage.getIcons().add(appIcon);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(
                fileMenu
        );

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            Platform.runLater(() -> menuBar.setUseSystemMenuBar(true));
        }

        SplitPane splitPane = new SplitPane(previewContentPane, propertiesList);

        contentArea.setScaleX(40);
        contentArea.setScaleY(40);
        previewContentPane.setItem(contentArea);

        BorderPane root = new BorderPane(splitPane);
        root.setPrefSize(1400, 1000);

        splitPane.setDividerPosition(0, 800);

        stage.setX(200);
        stage.setY(100);

        root.setStyle("-fx-padding: 10;"
                + //    "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
        //               + "-fx-border-color: blue;"
        );

        root.setTop(menuBar);
        Scene scene = new Scene(root);  // Create the Scene
        scene.getStylesheets().add(this.getClass().getResource("/style/dark.css").toExternalForm());
        stage.setScene(scene); // Add the scene to the Stage
        stage.show(); // Display the Stage

        stage.setOnCloseRequest((t) -> {
            Platform.exit();
        });

        pinElement.setX(0);
        pinElement.setY(0);
        pinElement.setName(pinName);
        pinElement.getRotationProperty().setMirror(false);
        pinElement.getRotationProperty().setSpin(false);

        pinElement.addListener(this);

        LayerElement layer = new LayerElement();
        layer.setNumber(96);
        layer.setName("tNames");
        layer.setColorIndex(5);
        layer.setFill(1);
        layer.setVisible(true);
        layer.setActive(true);

        layers.getElements().add(layer);

        PinNode pinNode = new PinNode(pinElement,
                Color.MAROON, Color.DARKGREEN, Color.DARKGRAY,
                null, true, this
        );

        initBackground();
        contentArea.getChildren().addAll(pinNode);

        initMarker();
    }

    private void configureLogging() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LoggingFormatter());

        // Get the top most logger and add our handler.
        LOGGER.setUseParentHandlers(false);  // Prevent INFO and HIGHER from going to stderr.
        LOGGER.addHandler(handler);

        // Log everything and use the DebugTab to filter later.
        LOGGER.setLevel(Level.FINEST);

        //ConsoleHandler handler = new ConsoleHandler();
        // Add console handler as handler of logs
        //Logger.getLogger("com.maehem.abyss").addHandler(handler);
        //Logger.getLogger("com.maehem.abyss").setUseParentHandlers(false);
    }

    private void initBackground() {
        //contentArea.getChildren().clear();

        Rectangle r = new Rectangle(-BG_W / 2, -BG_H / 2, BG_W, BG_H);
        r.setFill(Color.BLACK.brighter());
        contentArea.getChildren().add(r);

        // GRID
        for (int i = 0; i < BG_H / 2 / 2.54; i++) {
            Line ghLine = new Line(-BG_W / 2, -i * 2.54, BG_W / 2, -i * 2.54);
            ghLine.setStroke(new Color(1.0, 1.0, 1.0, 0.2));
            ghLine.setStrokeWidth(0.01);
            ghLine.setStrokeType(StrokeType.CENTERED);
            contentArea.getChildren().add(ghLine);

            ghLine = new Line(-BG_W / 2, i * 2.54, BG_W / 2, i * 2.54);
            ghLine.setStroke(new Color(1.0, 1.0, 1.0, 0.2));
            ghLine.setStrokeWidth(0.01);
            ghLine.setStrokeType(StrokeType.CENTERED);
            contentArea.getChildren().add(ghLine);
        }
        for (int i = 0; i < BG_W / 2 / 2.54; i++) {
            Line gvLine = new Line(-i * 2.54, -BG_H / 2, -i * 2.54, BG_H / 2);
            gvLine.setStroke(new Color(1.0, 1.0, 1.0, 0.2));
            gvLine.setStrokeWidth(0.01);
            gvLine.setStrokeType(StrokeType.CENTERED);
            contentArea.getChildren().add(gvLine);

            gvLine = new Line(i * 2.54, -BG_H / 2, i * 2.54, BG_H / 2);
            gvLine.setStroke(new Color(1.0, 1.0, 1.0, 0.2));
            gvLine.setStrokeWidth(0.01);
            gvLine.setStrokeType(StrokeType.CENTERED);
            contentArea.getChildren().add(gvLine);
        }
    }

    private void initMarker() {

        hLine = new Line(-BG_W / 2, -pinElement.getY(), BG_W / 2, -pinElement.getY());
        hLine.setStroke(new Color(0.2, 0.2, 1.0, 0.6));
        hLine.setStrokeWidth(0.02);
        hLine.setStrokeType(StrokeType.CENTERED);

        vLine = new Line(pinElement.getX(), -BG_H / 2, pinElement.getX(), BG_H / 2);
        vLine.setStroke(new Color(0.2, 0.2, 1.0, 0.6));
        vLine.setStrokeWidth(0.02);
        vLine.setStrokeType(StrokeType.CENTERED);

        contentArea.getChildren().addAll(hLine, vLine);

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        switch (field) {
            case LocationXYProperty.Field.Y -> {
                hLine.setStartY(-pinElement.getY());
                hLine.setEndY(-pinElement.getY());
            }
            case LocationXYProperty.Field.X -> {
                vLine.setStartX(pinElement.getX());
                vLine.setEndX(pinElement.getX());
            }
            default -> {
            }
        }
    }

    @Override
    public void nodePicked(ViewNode node, MouseEvent me) {
        LOGGER.log(Level.SEVERE, "Node picked: " + node.toString());
    }
}
