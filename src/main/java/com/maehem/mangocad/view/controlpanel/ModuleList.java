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

import com.maehem.mangocad.AppProperties;
import com.maehem.mangocad.model.library.Library;
import com.maehem.mangocad.model.library.LibraryCache;
import com.maehem.mangocad.model.library.element.DeviceSet;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.Package3d;
import com.maehem.mangocad.model.library.element.Symbol;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * In the main control panel, list all the tool modules the user can access.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ModuleList extends TreeTableView<ControlPanelListItem> {

    private static final Logger LOGGER = Logger.getLogger(ModuleList.class.getSimpleName());

    public static final String NAME_COL_WIDTH_PROP_KEY = "ModuleList.Name.W";
    public static final String DESC_COL_WIDTH_PROP_KEY = "ModuleList.Description.W";
    public static final String MODIFIED_COL_WIDTH_PROP_KEY = "ModuleList.LastModified.W";

    private static final Double NAME_COL_WIDTH = 200.0;
    private static final Double DESC_COL_WIDTH = 200.0;
    private static final Double MODIFIED_COL_WIDTH = 70.0;
    private static final Double USE_COL_WIDTH = 30.0; // Fixed width

    private final TreeItem modules;
    private final TreeItem librariesItem;
    private final TreeItem projectsItem;

    private final TreeTableColumn<ControlPanelListItem, String> nameColumn = new TreeTableColumn<>("Name");
    private final TreeTableColumn<ControlPanelListItem, String> descColumn = new TreeTableColumn<>("Description");
    private final TreeTableColumn<ControlPanelListItem, String> modifiedColumn = new TreeTableColumn<>("Last Modified");
    private final TreeTableColumn<ControlPanelListItem, String> useColumn = new TreeTableColumn<>("Use");

    public ModuleList() {
        initColumns();

        modules = new TreeItem(new ModuleItem("Modules", "..."));
        librariesItem = new TreeItem(new LibraryModuleItem("Libraries", "..."));
        projectsItem = new TreeItem(new ProjectModuleItem("Projects", "..."));

        modules.getChildren().add(librariesItem);
        modules.getChildren().add(projectsItem);

        // Add the Items
        populateLibraries();
        populateProjects();

        setShowRoot(false);
        setRoot(modules);

        // Update the context menu every time it is displayed.
        getSelectionModel().selectedItemProperty().addListener((o) -> {
            setContextMenu(getSelectionModel().getSelectedItem().getValue().getContextMenu());
        });
    }

    private void initColumns() {

        // TODO: Save current column width in AppProperties.
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameColumn.setMinWidth(100);
        nameColumn.setPrefWidth(NAME_COL_WIDTH);
        nameColumn.setCellFactory((p) -> {

            return new TreeTableCell<>() {
                ImageView imageView;

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty && item != null) {
                        //LOGGER.log(Level.SEVERE, "updateItem(" + item + ")");

                        if (imageView == null) {
                            imageView = new ImageView();
                            imageView.setFitHeight(16);
                            imageView.setPreserveRatio(true);
                        }

                        imageView.setImage(getTableRow().getItem().getImage());
                        setText(item);
                        setGraphic(imageView);
                    } else {
                        setText(null);
                        setGraphic(null);
                    }

                }

            };
        });

        descColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        descColumn.setMinWidth(100);
        descColumn.setPrefWidth(DESC_COL_WIDTH);
        modifiedColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastModified"));
        modifiedColumn.setMinWidth(70);
        modifiedColumn.setPrefWidth(MODIFIED_COL_WIDTH);
        useColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("inUse"));
        useColumn.setMinWidth(USE_COL_WIDTH);
        useColumn.setMaxWidth(USE_COL_WIDTH);

        getColumns().add(nameColumn);
        getColumns().add(descColumn);
        getColumns().add(modifiedColumn);
        getColumns().add(useColumn);
    }

    private void populateLibraries() {
        librariesItem.getChildren().clear();
        librariesItem.setExpanded(true);
        AppProperties appProperties = AppProperties.getInstance();
        String libUtilsPath = appProperties.getProperty(DirectoriesConfigPanel.LIB_UTILS_PATHS_KEY);

        if (libUtilsPath != null) {
            String[] paths = libUtilsPath.split(":");
            for (String path : paths) {
                if (path.endsWith("/")) { // Remove trailing path slash.
                    path = path.substring(0, path.length() - 1);
                }
                String description = "";
                // If the path string begins with $HOME, replace it with our users home dir path.
                path = path.replaceFirst("^\\$HOME", System.getProperty("user.home"));

                File dirFile = new File(path + File.separator + "library");
                if (!dirFile.exists() || !dirFile.isDirectory()) {
                    continue; // Skip if no dirFile file here.
                }
                File descFile = new File(dirFile, "DESCRIPTION.md");

                // Is it really the description file?
                if (descFile.exists() && descFile.isFile() && descFile.canRead()) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(descFile));
                        description = br.readLine(); // We just need the first line here.
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    Logger.getLogger("ModuleList").log(Level.SEVERE, "Description not found.");
                }

                // TODO: Maybe use TreeCell to enhance what is displayed (tooltips) as well as maybe adding ways to edit in place?
                TreeItem item = new TreeItem(new LibraryFolderItem(
                        dirFile.getParentFile().getName(), description, dirFile)
                );
                librariesItem.getChildren().add(item);
                populateLibrary(dirFile, item);
            }
        }

    }

    private void populateLibrary(File dir, TreeItem parentItem) {
        File[] libs = dir.listFiles((file) -> {    // lambda expression
            return (file.isFile() && file.getName().endsWith(".lbr"));
        });
        for (File lbrFile : libs) {
//            try {
            // TODO: If the eagle.dtd is missing from the library dir, loading will fail.
            //       See: https://xerces.apache.org/xml-commons/components/resolver/resolver-article.html
            //       for a possible solution.
            // Library importLBR = EagleCADUtils.importLBR(lbr);
            Library library = LibraryCache.getInstance().getLibrary(lbrFile);
            if (library != null) {
                TreeItem item;
                if (!library.getDescriptions().isEmpty()) {
                    item = new TreeItem(new LibraryItem(lbrFile.getName(), library.getDescriptions().get(0).getValue(), lbrFile));
                    parentItem.getChildren().add(item);
                } else {
                    item = new TreeItem(new LibraryItem(lbrFile.getName(), "", lbrFile));
                    parentItem.getChildren().add(item);
                }
                populateLibraryDetailItems(library, lbrFile, item);
            } else {
                TreeItem item = new TreeItem(new LibraryItem("ERROR", "Library Error", null));
                parentItem.getChildren().add(item);
            }
//            } catch (IOException ex) {
//                TreeItem item = new TreeItem(new LibraryItem(lbr.getName(), "File IO Exception", null));
//                lib.getChildren().add(item);
//
//                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (EagleCADLibraryFileException ex) {
//                TreeItem item = new TreeItem(new LibraryItem(lbr.getName(), "File XML Error", null));
//                lib.getChildren().add(item);
//
//                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
//            }

        }
        //    }
    }

    private void populateProjectFolder(File dir, TreeItem parentItem) {
        // List any sub directories.
        File[] sdirs = dir.listFiles((sdir) -> {
            return sdir.isDirectory();
        });
        for (File sdir : sdirs) {
            TreeItem item = new TreeItem(new ProjectSubFolderItem(sdir.getName(), "to do", sdir));
            parentItem.getChildren().add(item);

        }

//        File[] libs = dir.listFiles((file) -> {    // lambda expression
//            return (file.isFile() && file.getName().endsWith(".sch"));
//        });
//        for (File lbrFile : libs) {
////            try {
//            // TODO: If the eagle.dtd is missing from the library dir, loading will fail.
//            //       See: https://xerces.apache.org/xml-commons/components/resolver/resolver-article.html
//            //       for a possible solution.
//            // Library importLBR = EagleCADUtils.importLBR(lbr);
//            Library library = LibraryCache.getInstance().getLibrary(lbrFile);
//            if (library != null) {
//                TreeItem item;
//                if (!library.getDescriptions().isEmpty()) {
//                    item = new TreeItem(new LibraryItem(lbrFile.getName(), library.getDescriptions().get(0).getValue(), lbrFile));
//                    parentItem.getChildren().add(item);
//                } else {
//                    item = new TreeItem(new LibraryItem(lbrFile.getName(), "", lbrFile));
//                    parentItem.getChildren().add(item);
//                }
//                populateLibraryDetailItems(library, lbrFile, item);
//            } else {
//                TreeItem item = new TreeItem(new LibraryItem("ERROR", "Library Error", null));
//                parentItem.getChildren().add(item);
//            }
////            } catch (IOException ex) {
////                TreeItem item = new TreeItem(new LibraryItem(lbr.getName(), "File IO Exception", null));
////                lib.getChildren().add(item);
////
////                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (EagleCADLibraryFileException ex) {
////                TreeItem item = new TreeItem(new LibraryItem(lbr.getName(), "File XML Error", null));
////                lib.getChildren().add(item);
////
////                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
////            }
//
//        }
//        //    }
    }

    private void populateLibraryDetailItems(Library library, File file, TreeItem parentItem) {
        // List each deviceset as item (leaf)
        for (DeviceSet ds : library.getDeviceSets()) {
            TreeItem item = new TreeItem(new LibraryDeviceSetItem(ds.getName(), ds.getDescription(), file));
            parentItem.getChildren().add(item);
        }

        TreeItem item;
        // List each footprint(package)  (Folder)
        item = new TreeItem(new LibrarySubItem("Footprints", "", file));

        parentItem.getChildren().add(item);
        for (Footprint f : library.getPackages()) {
            TreeItem footprintItem = new TreeItem(new LibraryDeviceFootprintItem(f.getName(), f.getDescription(), file));
            item.getChildren().add(footprintItem);
        }

        // List each 3D package (Folder)
        item = new TreeItem(new LibrarySubItem("3D Packages", "", file));
        parentItem.getChildren().add(item);
        for (Package3d f : library.getPackages3d()) {
            TreeItem package3dItem = new TreeItem(new LibraryDevicePackage3dItem(f.getName(), f.getDescription(), file));
            item.getChildren().add(package3dItem);
        }

        // List each symbol (Folder)
        item = new TreeItem(new LibrarySubItem("Symbols", "", file));
        parentItem.getChildren().add(item);
        for (Symbol f : library.getSymbols()) {
            TreeItem symbolItem = new TreeItem(new LibraryDeviceSymbolItem(f.getName(), f.getDescription(), file));
            item.getChildren().add(symbolItem);
        }
    }

    private void populateProjects() {
        projectsItem.getChildren().clear();

        AppProperties appProperties = AppProperties.getInstance();
        String projectsPath = appProperties.getProperty(DirectoriesConfigPanel.PROJECT_PATHS_KEY);

        if (projectsPath != null) {
            String[] paths = projectsPath.split(":");
            for (String path : paths) {
                if (path.endsWith("/")) { // Remove trailing path slash.
                    path = path.substring(0, path.length() - 1);
                }
                String description = "";
                // If the path string begins with $HOME, replace it with our users home dir path.
                path = path.replaceFirst("^\\$HOME", System.getProperty("user.home"));

                File dirFile = new File(path);
                if (!dirFile.exists() || !dirFile.isDirectory()) {
                    continue; // Skip if no dirFile file here.
                }
                File descFile = new File(dirFile, "DESCRIPTION.md");

                // Is it really the description file?
                if (descFile.exists() && descFile.isFile() && descFile.canRead()) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(descFile));
                        description = br.readLine(); // We just need the first line here.
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    LOGGER.log(Level.FINE, "Description not found.");
                }

                // TODO: Maybe use TreeCell to enhance what is displayed (tooltips) as well as maybe adding ways to edit in place?
                TreeItem item = new TreeItem(new ProjectFolderItem(
                        dirFile.getName(), description, dirFile)
                );
                projectsItem.getChildren().add(item);
                populateProjectFolder(dirFile, item);
            }
        }

        projectsItem.setExpanded(true);
    }

    public void pushProperties(Properties p) {
        p.setProperty(NAME_COL_WIDTH_PROP_KEY, String.valueOf((int) nameColumn.getWidth()));
        p.setProperty(DESC_COL_WIDTH_PROP_KEY, String.valueOf((int) descColumn.getWidth()));
        p.setProperty(MODIFIED_COL_WIDTH_PROP_KEY, String.valueOf((int) modifiedColumn.getWidth()));
    }

    public void pullProperties(Properties p) {
        nameColumn.setPrefWidth(Double.parseDouble(p.getProperty(NAME_COL_WIDTH_PROP_KEY, String.valueOf(NAME_COL_WIDTH))));
        descColumn.setPrefWidth(Double.parseDouble(p.getProperty(DESC_COL_WIDTH_PROP_KEY, String.valueOf(DESC_COL_WIDTH))));
        modifiedColumn.setPrefWidth(Double.parseDouble(p.getProperty(MODIFIED_COL_WIDTH_PROP_KEY, String.valueOf(MODIFIED_COL_WIDTH))));
    }

}
