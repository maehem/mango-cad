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
import com.maehem.mangocad.RepoPathListListener;
import com.maehem.mangocad.RepoPathManager;
import com.maehem.mangocad.model.BoardCache;
import com.maehem.mangocad.model.LibraryCache;
import com.maehem.mangocad.model.SchematicCache;
import com.maehem.mangocad.model.element.drawing.Board;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.drawing.Schematic;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Package3d;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.controlpanel.listitem.BoardFileItem;
import com.maehem.mangocad.view.controlpanel.listitem.ControlPanelListItem;
import com.maehem.mangocad.view.controlpanel.listitem.GitHubFolderItem;
import com.maehem.mangocad.view.controlpanel.listitem.LibraryDeviceFootprintItem;
import com.maehem.mangocad.view.controlpanel.listitem.LibraryDevicePackage3dItem;
import com.maehem.mangocad.view.controlpanel.listitem.LibraryDeviceSetItem;
import com.maehem.mangocad.view.controlpanel.listitem.LibraryDeviceSymbolItem;
import com.maehem.mangocad.view.controlpanel.listitem.LibraryFolderItem;
import com.maehem.mangocad.view.controlpanel.listitem.LibraryItem;
import com.maehem.mangocad.view.controlpanel.listitem.LibraryModuleItem;
import com.maehem.mangocad.view.controlpanel.listitem.LibrarySubItem;
import com.maehem.mangocad.view.controlpanel.listitem.ModuleItem;
import com.maehem.mangocad.view.controlpanel.listitem.ProjectFolderItem;
import com.maehem.mangocad.view.controlpanel.listitem.ProjectModuleItem;
import com.maehem.mangocad.view.controlpanel.listitem.ProjectSubFolderItem;
import com.maehem.mangocad.view.controlpanel.listitem.RepositoryFolderItem;
import com.maehem.mangocad.view.controlpanel.listitem.RepositoryModuleItem;
import com.maehem.mangocad.view.controlpanel.listitem.RepositorySubFolderItem;
import com.maehem.mangocad.view.controlpanel.listitem.SchematicFileItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;

/**
 * In the main control panel, list all the tool modules the user can access.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ModuleList extends TreeTableView<ControlPanelListItem> implements RepoPathListListener {

    private static final Logger LOGGER = ControlPanel.LOGGER;

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
    private final TreeItem repositoriesItem;

    private final TabArea tabArea;

    private final TreeTableColumn<ControlPanelListItem, String> nameColumn = new TreeTableColumn<>("Name");
    private final TreeTableColumn<ControlPanelListItem, String> descColumn = new TreeTableColumn<>("Description");
    private final TreeTableColumn<ControlPanelListItem, String> modifiedColumn = new TreeTableColumn<>("Last Modified");
    private final TreeTableColumn<ControlPanelListItem, String> useColumn = new TreeTableColumn<>("Use");

    @SuppressWarnings("unchecked")
    private final TreeItem githubSubFolderItem = new TreeItem(new RepositorySubFolderItem("Git Hub", "Repos at GitHub.com"));
    @SuppressWarnings("unchecked")
    private final TreeItem otherSubFolderItem = new TreeItem(new RepositorySubFolderItem("Other URLs", "Misc. Repo URLs"));

    @SuppressWarnings("unchecked")
    public ModuleList(TabArea tabArea) {
        this.tabArea = tabArea;

        initColumns();

        modules = new TreeItem(new ModuleItem("Modules", "..."));
        librariesItem = new TreeItem(new LibraryModuleItem("Libraries", "..."));
        projectsItem = new TreeItem(new ProjectModuleItem("Projects", "..."));
        repositoriesItem = new TreeItem(new RepositoryModuleItem("Repositories", "..."));

        modules.getChildren().add(librariesItem);
        modules.getChildren().add(projectsItem);
        modules.getChildren().add(repositoriesItem);

        repositoriesItem.getChildren().addAll(githubSubFolderItem, otherSubFolderItem);

        // Add the Items
        populateLibraries();
        populateProjects();
        populateRepositories();

        setShowRoot(false);
        setRoot(modules);

        // Update the context menu every time it is displayed.
        getSelectionModel().selectedItemProperty().addListener((o) -> {
            setContextMenu(getSelectionModel().getSelectedItem().getValue().getContextMenu());
            LOGGER.log(Level.SEVERE, "Selected: {0}", getSelectionModel().getSelectedItem().getValue().getName());
            tabArea.setPreviewItem(getSelectionModel().getSelectedItem().getValue());
        });
        RepoPathManager.getInstance().addListener(this);

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
                protected void updateItem(String text, boolean empty) {
                    super.updateItem(text, empty);

                    if (!empty && text != null) {
                        //LOGGER.log(Level.SEVERE, "updateItem(" + item + ")");

                        if (imageView == null) {
                            imageView = new ImageView();
                            imageView.setFitHeight(16);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                        }

                        ControlPanelListItem item = getTableRow().getItem();
                        imageView.setImage(item.getImage());
                        setText(text);
                        setGraphic(imageView);
                        setTooltip(item.getTooltip());
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

    @SuppressWarnings("unchecked")
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
                // If the path string begins with $HOME, replace it with our users home dir path.
                path = path.replaceFirst("^\\$HOME", System.getProperty("user.home"));

                File dirFile = new File(path + File.separator + "library");
                if (!dirFile.exists() || !dirFile.isDirectory()) {
                    continue; // Skip if no dirFile file here.
                }

                // TODO: Maybe use TreeCell to enhance what is displayed (tooltips) as well as maybe adding ways to edit in place?
                @SuppressWarnings("unchecked")
                TreeItem item = new TreeItem(new LibraryFolderItem(
                        dirFile.getParentFile().getName(), ControlPanelUtils.getFolderDescriptionShort(dirFile), dirFile)
                );
                librariesItem.getChildren().add(item);
                populateLibrary(dirFile, item);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void populateLibrary(File dir, TreeItem parentItem) {
        File[] libs = dir.listFiles((file) -> {    // lambda expression
            return (file.isFile() && file.getName().endsWith(".lbr"));
        });
        for (File lbrFile : libs) {
            // TODO: If the eagle.dtd is missing from the library dir, loading will fail.
            //       See: https://xerces.apache.org/xml-commons/components/resolver/resolver-article.html
            //       for a possible solution.
            // Library importLBR = EagleCADUtils.importLBR(lbr);
            Library library = LibraryCache.getInstance().getLibrary(lbrFile);
            if (library != null) {
                TreeItem item;
                if (!library.getDescriptions().isEmpty()) {
                    item = new TreeItem(new LibraryItem(
                            lbrFile.getName(),
                            Library.getDescriptionShort(library.getDescription()),
                            lbrFile,
                            library
                    ));
                    parentItem.getChildren().add(item);
                } else {
                    item = new TreeItem(new LibraryItem(lbrFile.getName(), "", lbrFile, library));
                    parentItem.getChildren().add(item);
                }
                populateLibraryDetailItems(library, lbrFile, item);
            } else {
                TreeItem item = new TreeItem(new LibraryItem("ERROR", "Library Error", null, null));
                parentItem.getChildren().add(item);
            }
        }
    }

    /**
     * Looks for subdirectories and project files (*.sch, *.brd) Also add icons
     * for popular files: PDF, TXT, MD, ODF
     *
     * @param dir
     * @param parentItem
     */
    @SuppressWarnings("unchecked")
    private void populateProjectFolder(File dir, TreeItem parentItem) {
        // List any sub directories.
        File[] sdirs = dir.listFiles((sdir) -> {
            return sdir.isDirectory();
        });
        for (File sdir : sdirs) {
            TreeItem item = new TreeItem(new ProjectSubFolderItem(
                    sdir.getName(),
                    ControlPanelUtils.getFolderDescriptionShort(sdir),
                    sdir
            ));
            parentItem.getChildren().add(item);
            populateProjectFolder(sdir, item);
        }

        File[] schs = dir.listFiles((file) -> {    // lambda expression
            return (file.isFile() && file.getName().endsWith(".sch"));
        });
        for (File schFile : schs) {
            // TODO: If the eagle.dtd is missing from the library dir, loading will fail.
            //       See: https://xerces.apache.org/xml-commons/components/resolver/resolver-article.html
            //       for a possible solution.
            // Library importLBR = EagleCADUtils.importLBR(lbr);
            //Library library = LibraryCache.getInstance().getLibrary(schFile);
            Schematic schem = SchematicCache.getInstance().getSchematic(schFile);
            if (schem != null) {
                TreeItem item;
                item = new TreeItem(new SchematicFileItem(schFile.getName(), schem.getDescription().getValue(), schFile));
                parentItem.getChildren().add(item);
                //populateSchematicDetailItems(schem, schFile, item);
            } else {
                TreeItem item = new TreeItem(new LibraryItem("ERROR", "Library Error", null, null));
                parentItem.getChildren().add(item);
            }
        }

        // TODO: Combine Library, Schematic and Board into one "Design" Cache.
        File[] brds = dir.listFiles((file) -> {    // lambda expression
            return (file.isFile() && file.getName().endsWith("." + Board.FILE_EXTENSION));
        });
        for (File brdFile : brds) {
            // TODO: If the eagle.dtd is missing from the library dir, loading will fail.
            //       See: https://xerces.apache.org/xml-commons/components/resolver/resolver-article.html
            //       for a possible solution.
            // Library importLBR = EagleCADUtils.importLBR(lbr);
            //Library library = LibraryCache.getInstance().getLibrary(schFile);
            Board board = BoardCache.getInstance().getBoard(brdFile);
            if (board != null) {
                TreeItem item;
                item = new TreeItem(new BoardFileItem(brdFile.getName(), board.getDescription().getValue(), brdFile));
                parentItem.getChildren().add(item);
            } else {
                TreeItem item = new TreeItem(new LibraryItem("ERROR", "Board Error", null, null));
                parentItem.getChildren().add(item);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void populateLibraryDetailItems(Library library, File file, TreeItem parentItem) {
        // List each deviceset as item (leaf)
        for (DeviceSet ds : library.getDeviceSets()) {
            TreeItem item = new TreeItem(new LibraryDeviceSetItem(ds.getName(), ds.getDescription(), file, library));
            parentItem.getChildren().add(item);
        }

        TreeItem item;
        // List each footprint(package)  (Folder)
        item = new TreeItem(new LibrarySubItem("Footprints", "", file));

        parentItem.getChildren().add(item);
        for (Footprint f : library.getPackages()) {
            TreeItem footprintItem = new TreeItem(new LibraryDeviceFootprintItem(
                    f.getName(), f.getDescription().getValue(), file, library
            ));
            item.getChildren().add(footprintItem);
        }

        // List each 3D package (Folder)
        item = new TreeItem(new LibrarySubItem("3D Packages", "", file));
        parentItem.getChildren().add(item);
        for (Package3d f : library.getPackages3d()) {
            TreeItem package3dItem = new TreeItem(new LibraryDevicePackage3dItem(f.getName(), f.getDescription(), file, library));
            item.getChildren().add(package3dItem);
        }

        // List each symbol (Folder)
        item = new TreeItem(new LibrarySubItem("Symbols", "", file));
        parentItem.getChildren().add(item);
        for (Symbol f : library.getSymbols()) {
            TreeItem symbolItem = new TreeItem(new LibraryDeviceSymbolItem(
                    f.getName(), f.getDescription().getValue(), file, library
            ));
            item.getChildren().add(symbolItem);
        }
    }

    @SuppressWarnings("unchecked")
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

    private void populateRepositories() {
        //repositoriesItem.getChildren().clear();
        githubSubFolderItem.getChildren().clear();
        otherSubFolderItem.getChildren().clear();

        //AppProperties appProperties = AppProperties.getInstance();
        //String reposPath = appProperties.getProperty(DirectoriesConfigPanel.REPOSITORY_PATHS_KEY);
        RepoPathManager repoManager = RepoPathManager.getInstance();
        repoManager.forEach((rPath) -> {
            populateRepoItem(rPath);
        });

        repositoriesItem.setExpanded(true);
    }

    @SuppressWarnings("unchecked")
    private void populateRepoItem(RepoPath rPath) {
        // TODO: Maybe use TreeCell to enhance what is displayed (tooltips) as well as maybe adding ways to edit in place?
        if (rPath.getUrl().startsWith(RepoPath.GITHUB_PREFIX)) {
            TreeItem item = new TreeItem(new GitHubFolderItem(rPath));
            githubSubFolderItem.getChildren().add(item);
        } else {
            TreeItem item = new TreeItem(new RepositoryFolderItem(rPath));
            otherSubFolderItem.getChildren().add(item);
        }
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

    @Override
    public void itemAdded(RepoPath item) {
        populateRepositories();
    }

    @Override
    public void itemRemoved(RepoPath item) {
        populateRepositories();
    }

}
