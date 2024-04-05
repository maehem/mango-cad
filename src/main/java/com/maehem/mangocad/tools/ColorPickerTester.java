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
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.settings.ColorChooserDialog;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ColorPickerTester extends Application {

    public static final int WIDGET_WIDTH = 300;
    //private final Menu fileMenu = new Menu("File");
    //private final ElementText et = new ElementText();
    //private final Group contentArea = new Group();
    //private final PreviewContent previewContentPane = new PreviewContent();
    //private final TextPropertiesList propertiesList = new TextPropertiesList(this, et);

    //private final String textValue = "HridH\nXXXXX\nHHHHH";
    private final String textValue = "!HridH";

    @Override
    public void start(Stage stage) throws Exception {
        configureLogging();
        LOGGER.log(Level.SEVERE, "MangoCAD Color Picker Dialog Tester...");

        // Set the title of the Stage
        stage.setTitle("MangoCAD Color Picker Dialog Tester");

        // Add icon for the app
        Image appIcon = new Image(getClass().getResourceAsStream("/icons/app-icon.png"));
        stage.getIcons().add(appIcon);

        ColorPalette colorPalette = new ColorPalette(ColorPalette.Style.DARK);
        Button button = new Button("Show Dialog");
        button.setOnAction((t) -> {

            new ColorChooserDialog(colorPalette, 2);
        });

        BorderPane root = new BorderPane(button);
        root.setPrefSize(100, 50);

        //splitPane.setDividerPosition(0, 800);

        stage.setX(400);
        stage.setY(100);

        //ViewUtils.applyAppStylesheet(root.getStylesheets());

        root.setStyle("-fx-fill: #333;"
                + "-fx-padding: 10;"
                + //    "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
        //               + "-fx-border-color: blue;"
        );

        //ViewUtils.applyAppStylesheet(root.getStylesheets());

        //root.setTop(menuBar);
        Scene scene = new Scene(root);  // Create the Scene
        //scene.getStylesheets().add(this.getClass().getResource("/style/dark.css").toExternalForm());
        ViewUtils.applyAppStylesheet(scene.getStylesheets());

        stage.setScene(scene); // Add the scene to the Stage
        stage.show(); // Display the Stage

        stage.setOnCloseRequest((t) -> {
            Platform.exit();
        });

//        et.setX(0);
//        et.setY(0);
//        et.setValue(textValue);
//        et.setSize(1.0);
//        et.setRatio(8);
//        et.setLayer(21);
//        et.setAlign(TextAlign.TOP_LEFT);
//        et.setDistance(50);
//        et.getRotation().setMirror(false);
//        et.getRotation().setSpin(false);
//
//        updateContent();

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

    public static void main(String[] args) {
        launch(args);
    }
}
