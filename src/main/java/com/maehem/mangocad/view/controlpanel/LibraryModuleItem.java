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

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryModuleItem extends ControlPanelListItem {
    private static final Logger LOGGER = Logger.getLogger("LibraryModuleItem");

    public LibraryModuleItem(String name, String description) {
        super(name, description);
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.SEVERE, "getContextMenu(): LibraryModuleItem");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Edit Paths...");
        MenuItem menuItem2 = new MenuItem("Open Library Manager...");

        menuItem1.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem1.getText()});
        });
        menuItem2.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});
        });

        contextMenu.getItems().addAll(menuItem1, menuItem2);

        return contextMenu;
    }
}
