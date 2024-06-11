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
import com.maehem.mangocad.model.ElementValue;
import com.maehem.mangocad.model.ElementValueListener;
import com.maehem.mangocad.model.RealValue;
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.widgets.inspector.RealValueListWidget2;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class EditableDoubleListWidgetTester extends Application implements ElementValueListener {

    private final RealValue autoValue = new RealValue(123.456);

    private final ObservableList<Double> options
            = FXCollections.observableArrayList(
                    -1.0,
                    0.0,
                    0.01,
                    0.0125,
                    0.025,
                    0.03937008,
                    0.05,
                    0.10,
                    0.5,
                    1.0,
                    2.0,
                    5.0,
                    10.0
            );

    public static final int WIDGET_WIDTH = 300;
    private final Text sampleText = new Text("Hello");
    //private RealValue realValue = new RealValue(123.456);
    private final Dimension dimElement = new Dimension();
    private final RealValueListWidget2 listWidget = new RealValueListWidget2(
            dimElement.getTextSizeProperty(),
            Dimension.Field.TEXTSIZE,
            "TEXT_SIZE", "mm",
            true, autoValue, 1.0,
            options
    );
    private final VBox hBox = new VBox(listWidget, sampleText);

    @Override
    public void start(Stage stage) throws Exception {
        configureLogging();
        LOGGER.log(Level.SEVERE, "MangoCAD Double List Tester...");

        // Set the title of the Stage
        stage.setTitle("MangoCAD Double List Tester");

        // Add icon for the app
        Image appIcon = new Image(getClass().getResourceAsStream("/icons/app-icon.png"));
        stage.getIcons().add(appIcon);

        BorderPane root = new BorderPane(hBox);
        root.setPrefSize(300, 200);

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
        dimElement.getTextSizeProperty().addListener(this);

//        textField.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                try {
//                    new PinCommand(symbol, textField.getText());
//                } catch (CommandException ex) {
//                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
//                    ex.printStackTrace();
//                }
//            }
//        });
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

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(dimElement.getTextSizeProperty())) {
            sampleText.setText(String.valueOf(dimElement.getTextsize()));
        }
    }
}
