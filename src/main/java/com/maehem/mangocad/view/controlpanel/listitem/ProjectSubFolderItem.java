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

import com.maehem.mangocad.AppProperties;
import com.maehem.mangocad.view.controlpanel.DirectoriesConfigDialog;
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
public class ProjectSubFolderItem extends ControlPanelListItem {

    private static final Logger LOGGER = Logger.getLogger(ProjectSubFolderItem.class.getSimpleName());

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/folder.png")
    );

    private Stage stage = null;

    public ProjectSubFolderItem(String name, String description, File file) {
        super(name, description, file);

//        if (file != null) {
//            // TODO: Maybe get date format from AppSettings? Let user define format in settings panel.
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
//            lastModifiedProperty().set(sdf.format(file.lastModified()));
//        }
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.SEVERE, "getContextMenu(): Project Sub Folder Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Edit Paths...");
        MenuItem menuItem2 = new MenuItem("Edit Description...");
        MenuItem menuItem3 = new MenuItem("Show in Finder");

        menuItem1.setOnAction((t) -> new DirectoriesConfigDialog());

        menuItem3.setOnAction((event) -> {

            //   AppProperties.getInstance().getHostServices().showDocument() 
            //   from the Application class. So, the URI of home directory on 
            //   Windows is typically: file:///C:/Users/$USER/. The link to 
            //   documentation. 
            //   If you have a Path object, you can do .toUri().toString() for example.
            AppProperties.getInstance().getHostServices().showDocument(getFile().toURI().toString());

        });

        contextMenu.getItems().addAll(
                menuItem1,
                menuItem2,
                menuItem3);

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
