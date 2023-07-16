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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RepositorySubFolderItem extends ControlPanelListItem {

    private static final Logger LOGGER = Logger.getLogger(RepositorySubFolderItem.class.getSimpleName());

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/folder.png")
    );

    private Stage stage = null;

    public RepositorySubFolderItem(String name, String description) {
        super(name, description);
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.SEVERE, "getContextMenu(): Project Sub Folder Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Open");
        MenuItem menuItem2 = new MenuItem("Rename");
        MenuItem menuItem3 = new MenuItem("Copy");
        MenuItem menuItem4 = new MenuItem("[x] In Use");
        MenuItem menuItem5 = new MenuItem("Show in Finder");

        menuItem1.setOnAction((event) -> {
//            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem1.getText()});
//
//            if (stage == null) {
//                stage = new Stage();
//                LibraryEditor root = new LibraryEditor( getFile() ); // TODO Project Editor
//                stage.setTitle("Project Editor: " + getName());
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

        });
        menuItem2.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});
        });
        menuItem3.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem3.getText()});
        });
        menuItem4.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem4.getText()});
        });
        menuItem5.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem5.getText()});
        });

        contextMenu.getItems().addAll(
                menuItem1,
                menuItem2,
                menuItem3,
                menuItem4,
                new SeparatorMenuItem(),
                menuItem5);

        return contextMenu;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

//    @Override
//    public Tooltip getTooltip() {
//        String descString = ControlPanelUtils.getFolderDescriptionFull(getFile());
//        if ( !descString.isEmpty() ) {
//                Tooltip tt = new Tooltip();
//                tt.setGraphic(ControlPanelUtils.markdownNode(descString));
//                return tt;
//            
//        } else {
//            return null;
//        }
////        if (getFile().isDirectory()) {
////            File descFile = new File(getFile(), "DESCRIPTION.md");
////            if ( !descFile.exists() || descFile.isDirectory() || !descFile.canRead() ) return null;
////            try {
////                String readString = Files.readString(descFile.toPath());
////                if (readString.isEmpty()) {
////                    return null;
////                }
////                Tooltip tt = new Tooltip();
////                tt.setGraphic(ControlPanelUtils.markdownNode(readString));
////                return tt; // TODO Markdown processor.
////            } catch (IOException ex) {
////                Logger.getLogger(ProjectSubFolderItem.class.getName()).log(Level.SEVERE, null, ex);
////            }
////        }
////        
////        return null;
//    }

}
