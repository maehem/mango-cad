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

import com.maehem.mangocad.controller.command.PinCommand;
import com.maehem.mangocad.controller.command.exception.CommandException;
import com.maehem.mangocad.logging.LoggingFormatter;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class CommandTester extends Application {

    public static final int WIDGET_WIDTH = 300;
    private final TextField textField = new TextField();

    @Override
    public void start(Stage stage) throws Exception {
        configureLogging();
        LOGGER.log(Level.SEVERE, "MangoCAD Command Tester...");

        // Set the title of the Stage
        stage.setTitle("MangoCAD Command Tester");

        // Add icon for the app
        Image appIcon = new Image(getClass().getResourceAsStream("/icons/app-icon.png"));
        stage.getIcons().add(appIcon);

        BorderPane root = new BorderPane(textField);
        root.setPrefSize(600, 100);

        stage.setX(200);
        stage.setY(100);

        root.setStyle("-fx-padding: 10;"
                + //    "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
        //               + "-fx-border-color: blue;"
        );

        Scene scene = new Scene(root);  // Create the Scene
        scene.getStylesheets().add(this.getClass().getResource("/style/dark.css").toExternalForm());
        stage.setScene(scene); // Add the scene to the Stage
        stage.show(); // Display the Stage

        stage.setOnCloseRequest((t) -> {
            Platform.exit();
        });

        Symbol symbol = new Symbol();

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    new PinCommand(symbol, textField.getText());
                } catch (CommandException ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            }
        });
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
