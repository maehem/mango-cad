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
package com.maehem.mangocad.view.controlpanel;

import com.maehem.mangocad.view.ControlPanel;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibrarySubItem extends ControlPanelListItem {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/folder.png")
    );

    //private Stage stage = null;

    public LibrarySubItem(String name, String description, File file) {
        super(name, description, file);

//        if (file != null) {
//            // TODO: Maybe get date format from AppSettings? Let user define format in settings panel.
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
//            lastModifiedProperty().set(sdf.format(file.lastModified()));
//        }
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.SEVERE, "getContextMenu(): Library Sub-Item");
        ContextMenu contextMenu = new ContextMenu();
//        MenuItem menuItem1 = new MenuItem("Open");
//        MenuItem menuItem2 = new MenuItem("Rename");
//        MenuItem menuItem3 = new MenuItem("Copy");
//        MenuItem menuItem4 = new MenuItem("[x] In Use");
//        MenuItem menuItem5 = new MenuItem("Show in Finder");
//
//        menuItem1.setOnAction((event) -> {
//            logger.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem1.getText()});
//
//            if (stage == null) {
//                stage = new Stage();
//                LibraryEditor root = new LibraryEditor( getFile() );
//                stage.setTitle("Library Editor: " + getName());
//                stage.setScene(new Scene(root, 1280, 960));
//                stage.centerOnScreen();
//                stage.setOnCloseRequest((t) -> {
//                    // TODO: Popup if file edited and not saved.
//
//                    stage.close();
//                    stage = null;
//                });
//            }
//            stage.toFront();
//            stage.show();
//
//        });
//        menuItem2.setOnAction((event) -> {
//            logger.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});
//        });
//        menuItem3.setOnAction((event) -> {
//            logger.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem3.getText()});
//        });
//        menuItem4.setOnAction((event) -> {
//            logger.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem4.getText()});
//        });
//        menuItem5.setOnAction((event) -> {
//            logger.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem5.getText()});
//        });
//
//        contextMenu.getItems().addAll(
//                menuItem1,
//                menuItem2,
//                menuItem3,
//                menuItem4,
//                new SeparatorMenuItem(),
//                menuItem5);

        return contextMenu;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

}
