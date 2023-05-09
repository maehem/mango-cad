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
import javafx.scene.image.Image;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ModuleItem extends ControlPanelListItem {

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/folder.png")
    );

    public ModuleItem(String name, String description) {
        super(name, description);
    }

    @Override
    public ContextMenu getContextMenu() {
        Logger.getLogger("ModuleItem").log(Level.SEVERE, "getContextMenu(): ModuleItem");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Module thing 1");
        MenuItem menuItem2 = new MenuItem("Module thing 2");
        MenuItem menuItem3 = new MenuItem("Module thing 3");

        menuItem3.setOnAction((event) -> {
            if (event.getSource().getClass() == MenuItem.class) {
                MenuItem item = (MenuItem) event.getSource();

                Logger.getLogger("ModuleItem").log(Level.SEVERE, item.getText());
            }
            Logger.getLogger("ModuleItem").log(Level.SEVERE, "{0}: Choice 3 clicked!", getName());
        });

        contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);

        return contextMenu;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

}
