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
package com.maehem.mangocad.view.library.toc;

import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Package3d;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ElementType;
import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.LibraryEditorDialogs;
import java.util.Collections;
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Priority.ALWAYS;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TocElementListView extends VBox {

    private final String SAVE_EMOJI = "💾";  // Floppy Emoji --  U+1F4BE

    private LibraryTableOfContentsPane listener;
    private final Library library;
    private final ElementType type;
    private ListView<String> listView;

    private boolean sortAscend = true;

    @SuppressWarnings("unchecked")
    public TocElementListView(LibraryTableOfContentsPane listener, Library lib, ElementType type) {
        this.listener = listener;
        this.library = lib;
        this.type = type;

        VBox.setVgrow(this, Priority.ALWAYS);
        ObservableList<String> list = FXCollections.observableArrayList();
        if (lib != null) {
            switch (type) {
                case DEVICE -> {
                    for (DeviceSet ds : lib.getDeviceSets()) {
                        list.add(ds.getName());
                    }
                }
                case FOOTPRINT -> { // Footprint
                    for (Footprint ds : lib.getPackages()) {
                        list.add(ds.getName());
                    }
                }
                case PACKAGE3D -> {
                    for (Package3d ds : lib.getPackages3d()) {
                        list.add(ds.getName());
                    }
                }
                case SYMBOL -> {
                    for (Symbol ds : lib.getSymbols()) {
                        list.add(ds.getName());
                    }
                }
                default ->
                    throw new AssertionError();
            }
        }

        Collections.sort(list);
        listView = new ListView(list);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Your action here
                LOGGER.log(Level.FINER, "Selected item: {0}", newValue);
                listener.selectionChanged(type, newValue);
            }
        });
        listView.setOnMouseClicked((me) -> {
            if (me.getClickCount() == 2) {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                listener.editItem(type, selectedItem);
            }
        });

        initContextMenus();

        // TODO: List in ScrollPane?
        ScrollPane sp = new ScrollPane(listView);
        sp.setMaxHeight(Double.MAX_VALUE);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getChildren().addAll(getHeader(), sp);
        VBox.setVgrow(sp, ALWAYS); // Make the ScrollPane fill height.

    }

    @SuppressWarnings("unchecked")
    public void select(String item) {
        LOGGER.log(Level.FINER, "{0} select: {1}", new Object[]{type, item});
        listView.getSelectionModel().select(item);
        listView.getFocusModel().focus(-1);

    }

    private Node getHeader() {
        Label label = new Label(type.text());  // Type
        label.setPadding(new Insets(3, 0, 0, 6));
        Pane gapper = new Pane();
        HBox.setHgrow(gapper, Priority.ALWAYS);
        Pane sortPane = new Pane();
        //Button sortButton = new Button("^");
        Button sortAscButton = ViewUtils.createHeaderButton("^",
                new Image(getClass().getResourceAsStream("/icons/chevron-up.png")), // icon of current mode
                "Sort Descending" // new mode if user clicks it.
        );
        Button sortDesButton = ViewUtils.createHeaderButton("⌄",
                new Image(getClass().getResourceAsStream("/icons/chevron-down.png")),
                "Sort Ascending"
        );
        sortPane.getChildren().add(sortDesButton);

        // These two toggle up -down
        sortAscButton.setOnAction((t) -> {
            sortPane.getChildren().clear();
            sortPane.getChildren().add(sortDesButton);
            sortAscend = false;
            doSort();
            // Sort to match post-action, sort descending
//            listView.getItems().sort((o1, o2) -> {
//                return o2.compareTo(o1);
//            });
        });
        sortDesButton.setOnAction((t) -> {
            sortPane.getChildren().clear();
            sortPane.getChildren().add(sortAscButton);
            sortAscend = true;
            doSort();
            // Sort to match post-action, sort ascending
//            listView.getItems().sort((o1, o2) -> {
//                return o1.compareTo(o2);
//            });
        });

        //Button addButton = new Button("+");
        Button addButton = ViewUtils.createHeaderButton("+",
                new Image(getClass().getResourceAsStream("/icons/plus-circle.png")),
                "Add " + type.text() + "..."
        );
        addButton.setOnAction((t) -> {
            // Dialog for add new *type*
            String newName = LibraryEditorDialogs.presentNewLibElementNameDialog(library, type, null);
            if (newName != null) { // A valid new device was added, go edit it.
                listView.getItems().add(newName);
                doSort();
                listener.getParentEditor().setSubEditor(type, newName);
            }
        });
        HBox header = new HBox(label, gapper, sortPane, addButton);
        header.setSpacing(2);
        header.setMaxHeight(24);
        return header;

    }

    void clearSelections() {
        listView.getSelectionModel().clearSelection();
    }

    private void initContextMenus() {
        listView.setCellFactory(lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem();
            //editItem.textProperty().bind(Bindings.format("Edit"));
            editItem.setText("Edit");
            editItem.setOnAction(event -> {
                //ObjectProperty<String> itemProperty = cell.itemProperty();
                String item = cell.getItem();
                listener.editItem(type, item);
            });

            MenuItem updateItem = new MenuItem("Update to latest version");
            //updateItem.textProperty().bind(Bindings.format("Update \"%s\"", cell.itemProperty()));
            updateItem.setOnAction(event -> {
                //listView.getItems().remove(cell.getItem());
                LOGGER.log(Level.WARNING, "Library ToC List: {0}: Menu Item, Update to lastest version. Not implemented yet.", type.text());
            });
            updateItem.setDisable(true);

            contextMenu.getItems().addAll(
                    editItem,
                    updateItem
            );

            if (type == ElementType.FOOTPRINT) {
                MenuItem updateFootprints = new MenuItem("Update Footprints from Library");
                updateFootprints.setDisable(true);
                //updateFootprints.setOnAction(event -> {});  // TODO

                contextMenu.getItems().add(updateFootprints);
            }

            if (type != ElementType.PACKAGE3D) {
                // Footprint :  Update Footprints from Library
                MenuItem duplicateItem = new MenuItem("Duplicate");
                duplicateItem.setDisable(true);
                //duplicateItem.setOnAction(event -> { });  // TODO

                MenuItem renameItem = new MenuItem("Rename"); // NOT 3D
                renameItem.setDisable(true);
                //renameItem.setOnAction(event -> {}); // TODO

                contextMenu.getItems().addAll(duplicateItem, renameItem);
            }

            MenuItem removeItem = new MenuItem("Remove"); // Not 3D
            //removeItem.setOnAction(event -> {}); // TODO
            removeItem.setDisable(true);

            contextMenu.getItems().addAll(removeItem);

            MenuItem editDescriptionItem = new MenuItem("Edit Description");
            //editDescriptionItem.setOnAction(event -> {}); // TODO
            editDescriptionItem.setDisable(true);

            MenuItem copyUrnItem = new MenuItem("Copy Urn");
            //copyUrnItem.setOnAction(event -> {});// TODO
            copyUrnItem.setDisable(true);

            contextMenu.getItems().addAll(
                    editDescriptionItem,
                    new SeparatorMenuItem(),
                    copyUrnItem
            );

            if (type == ElementType.PACKAGE3D) {
                MenuItem viewOnWeb = new MenuItem("View On Web");
                //viewOnWeb.setOnAction(event -> {}); // TODO
                viewOnWeb.setDisable(true);

                Menu usedFootprints = new Menu("Used Footprints");
                //usedFootprints.setOnAction(event -> {}); // TODO

                // Using device sets sample
                MenuItem devSet1 = new MenuItem("DIP14");
                devSet1.setDisable(true);
                MenuItem devSet2 = new MenuItem("SOI16");
                devSet2.setDisable(true);
                MenuItem devSet3 = new MenuItem("DERP33");
                devSet3.setDisable(true);
                MenuItem devSet4 = new MenuItem("BLAH12");
                devSet4.setDisable(true);
                MenuItem devSet5 = new MenuItem("NEER33");
                devSet5.setDisable(true);
                usedFootprints.getItems().addAll(devSet1, devSet2, devSet3, devSet4, devSet5);

                contextMenu.getItems().addAll(viewOnWeb, new SeparatorMenuItem(), usedFootprints);
            }

            if (type == ElementType.DEVICE) {
                Menu usedFootprints = new Menu("Used Footprints");
                //usedFootprints.setOnAction(event -> { });

                // Using device sets sample
                MenuItem devSet1 = new MenuItem("DIP14");
                devSet1.setDisable(true);
                MenuItem devSet2 = new MenuItem("SOI16");
                devSet2.setDisable(true);
                MenuItem devSet3 = new MenuItem("DERP33");
                devSet3.setDisable(true);
                MenuItem devSet4 = new MenuItem("BLAH12");
                devSet4.setDisable(true);
                MenuItem devSet5 = new MenuItem("NEER33");
                devSet5.setDisable(true);
                usedFootprints.getItems().addAll(devSet1, devSet2, devSet3, devSet4, devSet5);

                Menu used3DPackages = new Menu("Used 3D Packages");
                // Using device sets sample
                MenuItem u3d1 = new MenuItem("DIP143D");
                u3d1.setDisable(true);
                MenuItem u3d2 = new MenuItem("SOI163D");
                u3d2.setDisable(true);
                MenuItem u3d3 = new MenuItem("DERP333D");
                u3d3.setDisable(true);
                MenuItem u3d4 = new MenuItem("BLAH123D");
                u3d4.setDisable(true);
                MenuItem u3d5 = new MenuItem("NEER333D");
                u3d5.setDisable(true);
                used3DPackages.getItems().addAll(u3d1, u3d2, u3d3, u3d4, u3d5);

                Menu usedSymbols = new Menu("Used Symbols");
                // Using device sets sample
                MenuItem us1 = new MenuItem("DIP14");
                us1.setDisable(true);
                MenuItem us2 = new MenuItem("SOI16");
                us2.setDisable(true);
                MenuItem us3 = new MenuItem("DERP33");
                us3.setDisable(true);
                MenuItem us4 = new MenuItem("BLAH12");
                us4.setDisable(true);
                MenuItem us5 = new MenuItem("NEER33");
                us5.setDisable(true);
                usedSymbols.getItems().addAll(us1, us2, us3, us4, us5);

                contextMenu.getItems().addAll(
                        new SeparatorMenuItem(),
                        usedFootprints, used3DPackages, usedSymbols
                );
            }

            if (type == ElementType.SYMBOL) {
                Menu usingDeviceSets = new Menu("Using Device Sets"); // Symbol only
                // Using device sets sample
                MenuItem devSet1 = new MenuItem("0402");
                devSet1.setDisable(true);
                MenuItem devSet2 = new MenuItem("0603");
                devSet2.setDisable(true);
                MenuItem devSet3 = new MenuItem("0805");
                devSet3.setDisable(true);
                MenuItem devSet4 = new MenuItem("1220");
                devSet4.setDisable(true);
                MenuItem devSet5 = new MenuItem("1460");
                devSet5.setDisable(true);
                usingDeviceSets.getItems().addAll(devSet1, devSet2, devSet3, devSet4, devSet5);

                contextMenu.getItems().addAll(new SeparatorMenuItem(), usingDeviceSets);
            }

            if (type == ElementType.FOOTPRINT) {
                Menu using3DPackages = new Menu("Using 3D Packages"); // Symbol only
                using3DPackages.setOnAction(event -> {
                });

                // Using device sets sample
                MenuItem devSet1 = new MenuItem("0402");
                devSet1.setDisable(true);
                MenuItem devSet2 = new MenuItem("0603");
                devSet2.setDisable(true);
                MenuItem devSet3 = new MenuItem("0805");
                devSet3.setDisable(true);
                MenuItem devSet4 = new MenuItem("1220");
                devSet4.setDisable(true);
                MenuItem devSet5 = new MenuItem("1460");
                devSet5.setDisable(true);
                using3DPackages.getItems().addAll(devSet1, devSet2, devSet3, devSet4, devSet5);

                contextMenu.getItems().addAll(new SeparatorMenuItem(), using3DPackages);
            }

            if (type == ElementType.DEVICE) {
                Menu addToSchematic = new Menu("Add to Schematic"); // Device only
                addToSchematic.setDisable(true);
                contextMenu.getItems().addAll(new SeparatorMenuItem(), addToSchematic);
            }

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });

    }

    private void doSort() {
        if (sortAscend) {
            listView.getItems().sort((o1, o2) -> {
                return o1.compareTo(o2);
            });
        } else {
            listView.getItems().sort((o1, o2) -> {
                return o2.compareTo(o1);
            });
        }
    }
}
