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
import com.maehem.mangocad.model.library.eaglecad.EagleCADLibraryFileException;
import com.maehem.mangocad.model.library.eaglecad.EagleCADUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * In the main control panel, list all the tool modules the user can access.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ModuleList extends TreeTableView<ControlPanelListItem> {

    public static final String NAME_COL_WIDTH_PROP_KEY = "ModuleList.Name.W";
    public static final String DESC_COL_WIDTH_PROP_KEY = "ModuleList.Description.W";
    public static final String MODIFIED_COL_WIDTH_PROP_KEY = "ModuleList.LastModified.W";

    private static final Double NAME_COL_WIDTH = 200.0;
    private static final Double DESC_COL_WIDTH = 200.0;
    private static final Double MODIFIED_COL_WIDTH = 70.0;
    private static final Double USE_COL_WIDTH = 30.0; // Fixed width

    private final TreeItem modules;// = new TreeItem(new ModuleItem("Modules", "..."), null);
    private final TreeItem librariesItem;// = new TreeItem(new ModuleItem("Libraries", "..."));
    private final TreeItem projectsItem;// = new TreeItem(new ModuleItem("Projects", "..."));

    private final TreeTableColumn<ControlPanelListItem, String> nameColumn = new TreeTableColumn<>("Name");
    private final TreeTableColumn<ControlPanelListItem, String> descColumn = new TreeTableColumn<>("Description");
    private final TreeTableColumn<ControlPanelListItem, String> modifiedColumn = new TreeTableColumn<>("Last Modified");
    private final TreeTableColumn<ControlPanelListItem, String> useColumn = new TreeTableColumn<>("Use");

    private final Image folderIconImage;
    private final Image libraryIconImage;

    public ModuleList() {
        initColumns();

        folderIconImage = new Image(getClass().getResourceAsStream("/icons/folder.png"));
        libraryIconImage = new Image(getClass().getResourceAsStream("/icons/photo-album.png"));

        modules = new TreeItem(new ModuleItem("Modules", "..."), libraryIcon());
        librariesItem = new TreeItem(new LibraryModuleItem("Libraries", "..."), libraryIcon());
        projectsItem = new TreeItem(new ProjectModuleItem("Projects", "..."), libraryIcon());

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
                TreeItem item = new TreeItem(new LibraryFolderItem(dirFile.getParentFile().getName(), description, dirFile), folderIcon());
                librariesItem.getChildren().add(item);
                populateLibrary(dirFile, item);
            }
        }

    }

    private void populateLibrary(File dir, TreeItem lib) {
        File[] libs = dir.listFiles((file) -> {    // lambda expression
            return (file.isFile() && file.getName().endsWith(".lbr"));
        });
        for (File lbr : libs) {
//            try {
                // TODO: If the eagle.dtd is missing from the library dir, loading will fail.
                //       See: https://xerces.apache.org/xml-commons/components/resolver/resolver-article.html
                //       for a possible solution.
                // Library importLBR = EagleCADUtils.importLBR(lbr);
                Library importLBR = LibraryCache.getInstance().getLibrary(lbr);
                if (importLBR != null) {
                    if (!importLBR.getDescriptions().isEmpty()) {
                        TreeItem item = new TreeItem(new LibraryItem(lbr.getName(), importLBR.getDescriptions().get(0).getValue(), lbr), libraryIcon());
                        lib.getChildren().add(item);
                    } else {
                        TreeItem item = new TreeItem(new LibraryItem(lbr.getName(), "", lbr), libraryIcon());
                        lib.getChildren().add(item);
                    }
                } else {
                    TreeItem item = new TreeItem(new LibraryItem("ERROR", "Library Error", null));
                    lib.getChildren().add(item);
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
    

    private ImageView folderIcon() {
        ImageView iconNode = new ImageView(folderIconImage);
        iconNode.setFitHeight(16);
        iconNode.setPreserveRatio(true);

        return iconNode;
    }

    private ImageView libraryIcon() {
        ImageView iconNode = new ImageView(libraryIconImage);
        iconNode.setFitHeight(16);
        iconNode.setPreserveRatio(true);

        return iconNode;
    }

    private void populateProjects() {
        projectsItem.getChildren().clear();

        TreeItem project1 = new TreeItem(new ModuleItem("projects", "User Projects"), folderIcon());
        TreeItem project2 = new TreeItem(new ModuleItem("boards", "My Boards"), folderIcon());
        TreeItem project3 = new TreeItem(new ModuleItem("examples", "Eample Projects"), folderIcon());

        projectsItem.setExpanded(true);
        projectsItem.getChildren().add(project1);
        projectsItem.getChildren().add(project2);
        projectsItem.getChildren().add(project3);
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
