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
import com.maehem.mangocad.model.SchematicCache;
import com.maehem.mangocad.model.element.drawing.Schematic;
import com.maehem.mangocad.model.element.highlevel.Sheet;
import com.maehem.mangocad.view.controlpanel.ControlPanelUtils;
import com.maehem.mangocad.view.library.GroupContainer;
import com.maehem.mangocad.view.LibraryEditor;
import com.maehem.mangocad.view.schematic.SchematicPreview;
import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
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
public class SchematicFileItem extends ControlPanelListItem {

    private static final Logger LOGGER = Logger.getLogger(SchematicFileItem.class.getSimpleName());

    private static final Image iconImage = new Image(
            SchematicFileItem.class.getResourceAsStream("/icons/integrated-circuit.png")
    );

    private Stage stage = null;

    public SchematicFileItem(String name, String description, File file) {
        super(name, description, file);
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.FINER, "getContextMenu(): Schematic Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Edit");
        MenuItem menuItem2 = new MenuItem("Rename");
        MenuItem menuItem3 = new MenuItem("Delete");
        MenuItem menuItem4 = new MenuItem("Open in Default App (Eagle)");
        MenuItem menuItem5 = new MenuItem("Show in Finder");
        MenuItem menuItem6 = new MenuItem("Print...");

        menuItem2.setDisable(true);
        menuItem3.setDisable(true);


        menuItem1.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem1.getText()});

            if (stage == null) {
                stage = new Stage();
                LibraryEditor root = new LibraryEditor(getFile());
                stage.setTitle("Schematic Editor: " + getName());
                stage.setScene(new Scene(root, 1280, 960));
                stage.centerOnScreen();
                stage.setOnCloseRequest((t) -> {
                    // TODO: Popup if file edited and not saved.

                    stage.close();
                    stage = null;
                });
            }
            stage.toFront();
            stage.show();
        });

        menuItem4.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem4.getText()});

            //   AppProperties.getInstance().getHostServices().showDocument() 
            //   from the Application class. So, the URI of home directory on 
            //   Windows is typically: file:///C:/Users/$USER/. The link to 
            //   documentation. 
            //   If you have a Path object, you can do .toUri().toString() for example.
            AppProperties.getInstance().getHostServices().showDocument(getFile().toURI().toString());

        });

        menuItem5.setOnAction((event) -> {
            //LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem5.getText()});

            //   AppProperties.getInstance().getHostServices().showDocument() 
            //   from the Application class. So, the URI of home directory on 
            //   Windows is typically: file:///C:/Users/$USER/. The link to 
            //   documentation. 
            //   If you have a Path object, you can do .toUri().toString() for example.
            AppProperties.getInstance().getHostServices().showDocument(getFile().getParentFile().toURI().toString());

        });

        menuItem6.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem6.getText()});

            Schematic sch = SchematicCache.getInstance().getSchematic(getFile());
            if (sch == null) {
                LOGGER.log(Level.SEVERE, "OOPS! Schematic File didn't load!");
            }

            for (int i = 0; i < sch.getSheets().size(); i++) {
                Sheet sheet = sch.getSheets().get(i);
                //GroupContainer schematicPreviewNode = schematicPreviewNode(sch, i);
                print(schematicPreviewNode(sch, i));
            }

        });

        contextMenu.getItems().addAll(
                menuItem1,
                menuItem2,
                menuItem3,
                new SeparatorMenuItem(),
                menuItem4,
                menuItem5,
                menuItem6
        );

        return contextMenu;
    }

    @Override
    public Node getPreviewTabNode() {
        Text itemName = new Text("Schematic: " + getName());
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

        Schematic sch = SchematicCache.getInstance().getSchematic(getFile());
        if (sch == null) {
            LOGGER.log(Level.SEVERE, "OOPS! Schematic File didn't load!");
        }

        TabPane tabPane = new TabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        for (int i = 0; i < sch.getSheets().size(); i++) {
            Sheet sheet = sch.getSheets().get(i);
            GroupContainer schematicPreviewNode = schematicPreviewNode(sch, i);
            VBox.setVgrow(schematicPreviewNode, Priority.SOMETIMES);

            Node pageDesc = ControlPanelUtils.markdownNode(
                    1.0,
                    sheet.getDescription().getValue()
            );
            VBox.setVgrow(pageDesc, Priority.SOMETIMES);

            MessageFormat mf = new MessageFormat("Size: {0}W x {1}H ({2}x{3}cm)");
            Double MM2INCH = 0.0393701;
            Text pageSizeText = new Text(mf.format(new Object[]{
                schematicPreviewNode.getNativeWidth() * MM2INCH,
                schematicPreviewNode.getNativeHeight() * MM2INCH,
                schematicPreviewNode.getNativeWidth() / 10.0,
                schematicPreviewNode.getNativeHeight() / 10.0
            }));
            pageSizeText.setId("preview-document-dimensions-text");

            HBox pageInfo = new HBox(pageSizeText);
            pageInfo.setAlignment(Pos.CENTER);

            VBox pageDetails = new VBox(pageInfo, pageDesc);

            SplitPane spPane = new SplitPane(schematicPreviewNode, pageDetails);
            spPane.setOrientation(Orientation.VERTICAL);
            spPane.setDividerPosition(0, 0.8);

            Tab tab = new Tab("Sheet " + (i + 1), spPane);

            tab.setClosable(false);
            tab.setTooltip(new Tooltip(sheet.getDescription().getValue()));
            tabPane.getTabs().add(tab);

        }

        VBox contentArea = new VBox(
                heading,
                ControlPanelUtils.markdownNode(
                        1.5,
                        sch.getDescription().getValue()
                ),
                tabPane
        );
        contentArea.setFillWidth(true);

        BorderPane pane = new BorderPane(contentArea);
        return pane;
    }

//    private SplitPane getSplitPane(Schematic sch, Node sheetList) {
//        GroupContainer schematicPreviewNode = schematicPreviewNode(sch, 0);
//        VBox.setVgrow(schematicPreviewNode, Priority.ALWAYS);
//
//        SplitPane spPane = new SplitPane(schematicPreviewNode, sheetList );
//        spPane.setOrientation(Orientation.VERTICAL);
//        spPane.setDividerPosition(0, 0.8);
//        VBox.setVgrow(spPane, Priority.ALWAYS);
//        
//        return spPane;
//    }
    private GroupContainer schematicPreviewNode(Schematic sch, int index) {

        SchematicPreview schematicPreview = new SchematicPreview(sch, index);
        StackPane sp = new StackPane(schematicPreview);
        sp.setBackground(new Background(new BackgroundFill(new Color(0.1, 0.1, 0.1, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));
        Group schemPreviewGroup = new Group(sp);
        GroupContainer container = new GroupContainer(schemPreviewGroup);
        //container.setBorder(new Border(new BorderStroke(Color.AQUAMARINE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        return container;
    }

//    private TableView sheetList(Schematic sch) {
//
//        TableView tableView = new TableView();
//        tableView.setPlaceholder( new Label("No rows to display"));
//        
//        TableColumn<Map, String> sheetname = new TableColumn<>("Sheet");
//        sheetname.setCellValueFactory(new MapValueFactory<>("sheet"));
//
//        // Size can't be computed without loading all the sheets.
//        // It is also somewhat unimportant here. So maybe we'll add it
//        // someday but not right now.
////        TableColumn<Map, String> size = new TableColumn<>("Size");
////        size.setCellValueFactory(new MapValueFactory<>("size"));
//        
//        TableColumn<Map, String> desc = new TableColumn<>("Description");
//        desc.setCellValueFactory(new MapValueFactory<>("description"));
//
//        tableView.getColumns().add(sheetname);
//        //tableView.getColumns().add(size);
//        tableView.getColumns().add(desc);
//
//        ObservableList<Map<String, Object>> items
//                = FXCollections.<Map<String, Object>>observableArrayList();
//
//        int sheetIndex = 1;
//        for ( Sheet sheet : sch.getSheets() ) {
//            Map<String, Object> item = new HashMap<>();
//
//            item.put("sheet", "Sheet " + sheetIndex);
//            item.put("index", sheetIndex-1);
//            //item.put("size", "???");
//            item.put("description", sheet.getDescription().getValue());
//            items.add(item);
//            
//            sheetIndex++;
//        }
//
//        tableView.getItems().addAll(items);
//            
//        return tableView;
//    }
    @Override
    public Image getImage() {
        return iconImage;
    }

    private void print(Node node) {
        LOGGER.log(Level.SEVERE, "Creating a printer job...");

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            System.out.println(job.jobStatusProperty().asString());

            boolean printed = job.printPage(node);
            if (printed) {
                job.endJob();
            } else {
                LOGGER.log(Level.SEVERE, "Printing failed.");
            }
        } else {
            LOGGER.log(Level.SEVERE, "Could not create a printer job.");
        }
    }
}
