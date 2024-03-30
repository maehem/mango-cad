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

import com.maehem.mangocad.model.LibraryCache;
import com.maehem.mangocad.model.element.basic.Attribute;
import com.maehem.mangocad.model.element.basic.Technology;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.Device;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.GroupContainer;
import com.maehem.mangocad.view.MarkdownUtils;
import com.maehem.mangocad.view.controlpanel.ControlPanelUtils;
import com.maehem.mangocad.view.library.DetailNodes;
import com.maehem.mangocad.view.library.LibraryEditor;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryDeviceSetItem extends ControlPanelListItem {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/electronics-component-kit.png")
    );

    private Stage stage = null;
    private final Library library;

    public LibraryDeviceSetItem(String name, String description, File file, Library library) {
        super(name, description, file);
        this.library = library;

//        if (file != null) {
//            // TODO: Maybe get date format from AppSettings? Let user define format in settings panel.
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
//            lastModifiedProperty().set(sdf.format(file.lastModified()));
//        }
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.SEVERE, "getContextMenu(): Library DeviceSet Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Add to Schematic");
        MenuItem menuItem2 = new MenuItem("Open Library");
        MenuItem menuItem3 = new MenuItem("Copy to Library");

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
        menuItem2.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});
        });
        menuItem3.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem3.getText()});
        });

        contextMenu.getItems().addAll(
                menuItem1,
                new SeparatorMenuItem(),
                menuItem2,
                menuItem3
        );

        return contextMenu;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node getPreviewTabNode() {

        Text itemName = new Text(getName());
        itemName.setId("control-panel-preview-area-heading");

        Pane spacer = new Pane();
        spacer.setMaxWidth(Double.MAX_VALUE);
        spacer.setMinSize(10, 10);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text fileName = new Text(getFile().getName());
        fileName.setId("control-panel-preview-area-heading-filename");

        HBox headingBox = new HBox(itemName, spacer, fileName);
        headingBox.setAlignment(Pos.CENTER);
        Separator sep = new Separator();

        VBox heading = new VBox(headingBox, sep);
        heading.setFillWidth(true);

        VBox.setMargin(headingBox, new Insets(5, 10, 5, 10));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        TableView deviceSetList = deviceSetList(0);
        SplitPane spPane = new SplitPane(scrollPane, deviceSetList);
        spPane.setOrientation(Orientation.VERTICAL);
        spPane.setDividerPosition(0, 0.72);
        VBox.setVgrow(spPane, Priority.ALWAYS);

        scrollPane.setContent(devicePreviewNode(
                (Map<String, Object>) deviceSetList.getSelectionModel().getSelectedItem()
        ));

        deviceSetList.setOnMouseClicked((t) -> {
            LOGGER.log(Level.FINEST, "User Clicked: {0}", deviceSetList.getSelectionModel().getSelectedIndex());
            TableView.TableViewSelectionModel model = deviceSetList.getSelectionModel();
            Map<String, Object> item = (Map<String, Object>) model.getSelectedItem();
            scrollPane.setContent(devicePreviewNode(item));
        });

        VBox contentArea = new VBox(
                heading,
                MarkdownUtils.markdownNode(
                        1.5,
                        ControlPanelUtils.getItemDescriptionFull(this),
                        null
                ),
                spPane
        );

        return new BorderPane(contentArea);
    }

    private Node devicePreviewNode(Map<String, Object> item) {
        Library lib = LibraryCache.getInstance().getLibrary(getFile());
        if (lib == null) {
            LOGGER.log(Level.SEVERE, "OOPS! Library File didn't load!");
        }

        DeviceSet deviceSet = lib.getDeviceSet(getName());

        Group gateSetPreview = DetailNodes.gateSetPreview(deviceSet.getGates(), lib);

        GroupContainer gatePreviewPane = new GroupContainer(gateSetPreview, 0.1);
        gatePreviewPane.setBorder(new Border(new BorderStroke(
                new Color(0.1, 0.1, 0.1, 0.5),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(2, 4, 4, 2),
                new Insets(6)
        )));

        String pkgName = (String) item.get("footprint");
        LOGGER.log(Level.FINEST, "Selected Footprint:{0}", pkgName);

        Group footprintPreview = DetailNodes.footprintPreview((Footprint) item.get("f"), lib, true);
        GroupContainer footprintContainer = new GroupContainer(footprintPreview, 0.1);

        // Add a border
        footprintContainer.setBorder(new Border(new BorderStroke(
                new Color(0.1, 0.1, 0.1, 0.5),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(2, 4, 4, 2),
                new Insets(6)
        )));

        Node deviceTechnologyAttrList = deviceTechnologyAttrList((Technology) item.get("t"));

        VBox.setMargin(deviceTechnologyAttrList, new Insets(8));
        VBox footAttrVertArea = new VBox(footprintContainer, deviceTechnologyAttrList);
        VBox.setVgrow(footprintContainer, Priority.ALWAYS);
        VBox.setVgrow(deviceTechnologyAttrList, Priority.SOMETIMES);

        HBox gateFootAttrArea = new HBox(gatePreviewPane, footAttrVertArea);
        VBox.setVgrow(gateFootAttrArea, Priority.SOMETIMES); // Makes it stretch downward.

        HBox.setHgrow(gatePreviewPane, Priority.SOMETIMES); // Makes them fit width.
        HBox.setHgrow(footAttrVertArea, Priority.SOMETIMES);

        ImageView package3DPreview = DetailNodes.package3DPreview(
                ((Device) item.get("d")).getPackage3dInstances().get(0),
                lib
        );
        package3DPreview.setFitHeight(200);
        VBox contentArea = new VBox(gateFootAttrArea, new StackPane(package3DPreview));
        contentArea.setFillWidth(true);

        return contentArea;
    }

    @SuppressWarnings("unchecked")
    private TableView deviceSetList(int index) {
        TableView tableView = new TableView();
        tableView.setPlaceholder(new Label("No rows to display"));
        TableColumn<Map, String> deviceName = new TableColumn<>("Device");
        deviceName.setCellValueFactory(new MapValueFactory<>("device"));

        TableColumn<Map, String> footprint = new TableColumn<>("Footprint");
        footprint.setCellValueFactory(new MapValueFactory<>("footprint"));

        TableColumn<Map, String> has3DCol = new TableColumn<>("3D");
        has3DCol.setCellValueFactory(new MapValueFactory<>("has3D"));

        TableColumn<Map, String> desc = new TableColumn<>("Description");
        desc.setCellValueFactory(new MapValueFactory<>("description"));

        ObservableList<TableColumn> columns = tableView.getColumns();
        columns.add(deviceName);
        columns.add(footprint);
        columns.add(has3DCol);
        columns.add(desc);

        Library lib = LibraryCache.getInstance().getLibrary(getFile());
        if (lib == null) {
            LOGGER.log(Level.SEVERE, "OOPS! Library File didn't load!");
            tableView.setPlaceholder(new Label("OOPS! Library File didn't load!"));
            return tableView;
        }

        DeviceSet deviceSet = lib.getDeviceSet(getName());

        ObservableList<Map<String, Object>> items
                = FXCollections.<Map<String, Object>>observableArrayList();

        for (Device d : deviceSet.getDevices()) {
            Footprint pkg = lib.getPackage(d.getFootprint());
            String haz3d = "";
            // There is always a default(blank) 3D package ending in "/1" initially created by CAD software.
            if (!d.getPackage3dInstances().isEmpty()
                    && !d.getPackage3dInstances().get(0).getPackage3dUrn().endsWith("/1")) {
                haz3d = "Y";
            }

            List<Technology> technologies = d.getTechnologies();
            if (technologies.isEmpty()) { // tecnologies should nver be empty, but...
                Map<String, Object> item = new HashMap<>();

                item.put("device", d.getName());
                item.put("d", d); // Device Object
                item.put("t", null); // Technology Object
                item.put("f", lib.getPackage(d.getFootprint()));
                item.put("footprint", d.getFootprint());
                item.put("has3D", haz3d);
                item.put("description", pkg.getDescription());
                items.add(item);
            } else {
                for (Technology t : technologies) {
                    Map<String, Object> item = new HashMap<>();

                    // Replace '*' in deviceset.name with technology.name
                    // Replace '?' in deviceset.name with d.name
                    String name = deviceSet.getName();
                    name = name.replace("*", t.getName());
                    if (name.contains("?")) {
                        name = name.replace("?", d.getName());
                    } else {
                        name += d.getName();
                    }
                    item.put("device", name);
                    item.put("d", d); // Device Object
                    item.put("t", t); // Technology Object
                    item.put("f", lib.getPackage(d.getFootprint()));
                    item.put("footprint", d.getFootprint());
                    item.put("has3D", haz3d);
                    item.put("description", pkg.getDescription());
                    items.add(item);
                }
            }

        }

        tableView.getItems().addAll(items);

        if (tableView.getItems().size() > index) {
            tableView.getSelectionModel().select(index);
        }

        return tableView;
    }

    @SuppressWarnings("unchecked")
    private Node deviceTechnologyAttrList(Technology tech) {

        TableView tableView = new TableView();
        tableView.setId("technology-attributes-table"); // Makes the font smaller
        tableView.setPlaceholder(new Label("No attrributes to display"));

        TableColumn<Map, String> attributeColumn = new TableColumn<>("Attribute");
        attributeColumn.setCellValueFactory(new MapValueFactory<>("attribute"));

        TableColumn<Map, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new MapValueFactory<>("value"));

        tableView.getColumns().add(attributeColumn);
        tableView.getColumns().add(valueColumn);

        ObservableList<Map<String, Object>> items
                = FXCollections.<Map<String, Object>>observableArrayList();

        if (tech != null) {
            for (Attribute d : tech.getAttributes()) {
                Map<String, Object> item = new HashMap<>();
                item.put("attribute", d.getName());
                item.put("value", d.getValue());

                items.add(item);
            }
        }

        tableView.getItems().addAll(items);
        tableView.setPrefHeight(120);
        tableView.setMinHeight(120);

        return tableView;
    }

}
