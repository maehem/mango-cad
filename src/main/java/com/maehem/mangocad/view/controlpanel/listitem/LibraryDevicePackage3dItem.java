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

import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.view.library.LibraryEditor;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryDevicePackage3dItem extends ControlPanelListItem {

    private static final Logger LOGGER = Logger.getLogger(LibraryDevicePackage3dItem.class.getSimpleName());

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/cube-isometric.png")
    );

    private Stage stage = null;
    private final Library library;

    public LibraryDevicePackage3dItem(String name, String description, File file, Library library) {
        super(name, description, file);
        this.library = library;
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.FINER, "getContextMenu(): Library Device Footprint Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem2 = new MenuItem("Copy to Library");

        menuItem2.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});

            if (stage == null) {
                stage = LibraryEditor.invokeWindow(library, getName(), getFile());
//                stage = new Stage();
//                LibraryEditor root = new LibraryEditor(getFile(), library);
//                stage.setTitle("Library Editor: " + getName());
//                stage.setScene(new Scene(root, 1280, 960));
//                stage.centerOnScreen();
//                stage.setOnCloseRequest((t) -> {
//                    // TODO: Popup if file edited and not saved.
//
//                    stage.close();
//                    stage = null;
//                });
            }
            stage.toFront();
            stage.show();

        });

        contextMenu.getItems().addAll(
                menuItem2
        );

        return contextMenu;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

}
