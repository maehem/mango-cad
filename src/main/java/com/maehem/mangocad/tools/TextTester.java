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
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.enums.TextAlign;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.controlpanel.tab.PreviewContent;
import com.maehem.mangocad.view.library.LibraryElementNode;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
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
public class TextTester extends Application {

    public static final int WIDGET_WIDTH = 300;
    private final Menu fileMenu = new Menu("File");
    private final ElementText et = new ElementText();
    private final Group contentArea = new Group();
    private final PreviewContent previewContentPane = new PreviewContent();
    private final TextPropertiesList propertiesList = new TextPropertiesList(this, et);

    //private final String textValue = "HridH\nXXXXX\nHHHHH";
    private final String textValue = "!HridH";

    @Override
    public void start(Stage stage) throws Exception {
        configureLogging();
        LOGGER.log(Level.SEVERE, "MangoCAD Text Tester...");

        // Set the title of the Stage
        stage.setTitle("MangoCAD Text Tester");

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

        contentArea.setScaleX(100);
        contentArea.setScaleY(100);
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

        et.setX(0);
        et.setY(0);
        et.setValue(textValue);
        et.setSize(1.0);
        et.setRatio(8);
        et.setLayerNum(21);
        et.setAlign(TextAlign.TOP_LEFT);
        et.setDistance(50);
        et.rotation.setMirror(false);
        et.rotation.setSpin(false);

        updateContent();

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

    public void updateContent() {
        contentArea.getChildren().clear();

        Rectangle r = new Rectangle(-7, -6, 14, 12);
        r.setFill(Color.BLACK.brighter());
        contentArea.getChildren().add(r);

        // GRID
        for (int i = 0; i < 11; i++) {
            Line hLine = new Line(-7, i - 5, 7, i - 5);
            hLine.setStroke(new Color(1.0, 1.0, 1.0, 0.2));
            hLine.setStrokeWidth(0.01);
            hLine.setStrokeType(StrokeType.CENTERED);
            contentArea.getChildren().add(hLine);
        }
        for (int i = 0; i < 11; i++) {
            Line vLine = new Line(i - 5, -5, i - 5, 5);
            vLine.setStroke(new Color(1.0, 1.0, 1.0, 0.2));
            vLine.setStrokeWidth(0.01);
            vLine.setStrokeType(StrokeType.CENTERED);
            contentArea.getChildren().add(vLine);
        }

        Line hLine = new Line(-7, -et.getY(), 7, -et.getY());
        hLine.setStroke(new Color(0.2, 0.2, 1.0, 0.6));
        hLine.setStrokeWidth(0.02);
        hLine.setStrokeType(StrokeType.CENTERED);

        Line vLine = new Line(et.getX(), -5, et.getX(), 5);
        vLine.setStroke(new Color(0.2, 0.2, 1.0, 0.6));
        vLine.setStrokeWidth(0.02);
        vLine.setStrokeType(StrokeType.CENTERED);

        contentArea.getChildren().addAll(hLine, vLine);

        // TEXT
        contentArea.getChildren().addAll(
                LibraryElementNode.createText2(et, Color.CORAL)
        );

    }

    public static void main(String[] args) {
        launch(args);
    }
}
