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
package com.maehem.mangocad.view.controlpanel.listitem;

import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.controlpanel.ControlPanelUtils;
import com.maehem.mangocad.view.library.LibraryEditor;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryDeviceSetItem extends ControlPanelListItem {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/electronics-component-kit.png")
    );

    private Stage stage = null;

    public LibraryDeviceSetItem(String name, String description, File file) {
        super(name, description, file);

//        if (file != null) {
//            // TODO: Maybe get date format from AppSettings? Let user define format in settings panel.
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
//            lastModifiedProperty().set(sdf.format(file.lastModified()));
//        }
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.SEVERE, "getContextMenu(): Library DeviceSet Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Add to Schematic");
        MenuItem menuItem2 = new MenuItem("Open Library");
        MenuItem menuItem3 = new MenuItem("Copy to Library");

        menuItem2.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});

            if (stage == null) {
                stage = new Stage();
                LibraryEditor root = new LibraryEditor( getFile() );
                stage.setTitle("Library Editor: " + getName());
                stage.setScene(new Scene(root, 1280, 960));
                stage.centerOnScreen();
                stage.setOnCloseRequest((t) -> {
                    // TODO: Popup if file edited and not saved.

                    stage.close();
                    stage = null;
                });
            }
            stage.toFront();
            stage.show();

        });
        menuItem2.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});
        });
        menuItem3.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem3.getText()});
        });

        contextMenu.getItems().addAll(
                menuItem1,
                new SeparatorMenuItem(),
                menuItem2,
                menuItem3
        );

        return contextMenu;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

    @Override
    public Node getPreviewTabNode() {
        
        Text itemName = new Text(getName());
        itemName.setId("control-panel-preview-area-heading");
        
        Text fileName = new Text(getFile().getName());
        fileName.setId("control-panel-preview-area-heading-filename");
        
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        spacer.setMaxWidth(Double.MAX_VALUE);
        spacer.setMinSize(10, 10);
        HBox.getHgrow(spacer);

        HBox headingBox = new HBox(itemName, spacer, fileName);
        headingBox.setAlignment(Pos.CENTER);
        Separator sep = new Separator();
        
        VBox top = new VBox(headingBox, sep);
        top.setFillWidth(true);
        VBox.setMargin(headingBox, new Insets(5,10,5,10));
                
        BorderPane pane = new BorderPane(
                ControlPanelUtils.markdownNode(
                        1.5, 
                        ControlPanelUtils.getItemDescriptionFull(this)
                )
        );
        pane.setTop(top);
        
        
        return pane;
    }
    
}
