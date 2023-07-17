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
package com.maehem.mangocad.view;

import com.maehem.mangocad.AppProperties;
import com.maehem.mangocad.logging.LoggingFormatter;
import com.maehem.mangocad.view.controlpanel.DirectoriesConfigDialog;
import com.maehem.mangocad.view.controlpanel.ModuleList;
import com.maehem.mangocad.view.controlpanel.TabArea;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class ControlPanel extends Application {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static final String SPLITTER_X_PROP_KEY = "Splitter.X";
    public static final String WINDOW_POS_X_PROP_KEY = "Window.X";
    public static final String WINDOW_POS_Y_PROP_KEY = "Window.Y";
    public static final String WINDOW_SIZE_W_PROP_KEY = "Window.W";
    public static final String WINDOW_SIZE_H_PROP_KEY = "Window.H";

    private final Menu fileMenu = new Menu("File");
    private final Menu viewMenu = new Menu("View");
    private final Menu optionsMenu = new Menu("Options");
    private final Menu windowMenu = new Menu("Window");
    private final Menu helpMenu = new Menu("Help");

    private AppProperties appProperties;// = AppProperties.getInstance();

    private ModuleList moduleList;

    @Override
    public void start(Stage stage) throws Exception {
        configureLogging();
        LOGGER.log(Level.SEVERE, "MangoCAD Start...");
        appProperties = AppProperties.getInstance();
        // Host Services allows opening of browser links inside app.
        appProperties.setHostServices(getHostServices());

        // Set the title of the Stage
        stage.setTitle("MangoCAD");

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(
                fileMenu, viewMenu, optionsMenu, windowMenu, helpMenu
        );

        initOptionsMenu();

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            Platform.runLater(() -> menuBar.setUseSystemMenuBar(true));
        }

        TabArea tabArea = new TabArea();
        moduleList = new ModuleList(tabArea);

        SplitPane splitPane = new SplitPane(moduleList, tabArea);

        BorderPane root = new BorderPane(splitPane);

        String splitX = appProperties.getProperty(getClass().getSimpleName() + "." + SPLITTER_X_PROP_KEY);
        splitPane.setDividerPosition(0, Double.parseDouble(splitX));

        String posX = appProperties.getProperty(getClass().getSimpleName() + "." + WINDOW_POS_X_PROP_KEY, "50");
        String posY = appProperties.getProperty(getClass().getSimpleName() + "." + WINDOW_POS_Y_PROP_KEY, "50");
        stage.setX(Double.parseDouble(posX));
        stage.setY(Double.parseDouble(posY));

        String sizeW = appProperties.getProperty(getClass().getSimpleName() + "." + WINDOW_SIZE_W_PROP_KEY, "1024");
        String sizeH = appProperties.getProperty(getClass().getSimpleName() + "." + WINDOW_SIZE_H_PROP_KEY, "600");
        root.setPrefSize(Double.parseDouble(sizeW), Double.parseDouble(sizeH));

        moduleList.pullProperties(appProperties);

        // TODO: Move this to style.css with setId()
        // Set the Style-properties of the BorderPane
        root.setStyle("-fx-padding: 10;"
                + //    "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
        //               + "-fx-border-color: blue;"
        );

        root.setTop(menuBar);
        Scene scene = new Scene(root);  // Create the Scene
        //scene.getStylesheets().add("/style/dark.css");
        scene.getStylesheets().add(this.getClass().getResource("/style/dark.css").toExternalForm());
        stage.setScene(scene); // Add the scene to the Stage
        stage.show(); // Display the Stage

        stage.setOnCloseRequest((t) -> {   // Remember window parameters
            String prefix = getClass().getSimpleName() + ".";
            appProperties.setProperty(prefix + SPLITTER_X_PROP_KEY, String.valueOf(splitPane.getDividerPositions()[0]).substring(0, 5));
            appProperties.setProperty(prefix + WINDOW_POS_X_PROP_KEY, String.valueOf(stage.getX()));
            appProperties.setProperty(prefix + WINDOW_POS_Y_PROP_KEY, String.valueOf(stage.getY()));
            appProperties.setProperty(prefix + WINDOW_SIZE_W_PROP_KEY, String.valueOf(root.getWidth()));
            appProperties.setProperty(prefix + WINDOW_SIZE_H_PROP_KEY, String.valueOf(root.getHeight()));
            moduleList.pushProperties(appProperties);
            appProperties.save();

            Platform.exit();
        });
    }

    private void initOptionsMenu() {
        MenuItem dirs = new MenuItem("Directories...");
        dirs.setOnAction((t) -> new DirectoriesConfigDialog()); // Loving those Lamdas!

        MenuItem backups = new MenuItem("Backups/Locking...");
        MenuItem userIface = new MenuItem("User Interface...");
        optionsMenu.getItems().addAll(dirs, backups, userIface);
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
