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
import com.maehem.mangocad.model.LibraryCache;
import com.maehem.mangocad.model.SchematicCache;
import com.maehem.mangocad.model.element.drawing.Schematic;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.view.controlpanel.ControlPanelUtils;
import com.maehem.mangocad.view.library.DetailNodes;
import com.maehem.mangocad.view.library.GroupContainer;
import com.maehem.mangocad.view.LibraryEditor;
import com.maehem.mangocad.view.schematic.SchematicPreview;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
        LOGGER.log(Level.FINER, "getContextMenu(): Library Device Footprint Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Open");
        MenuItem menuItem2 = new MenuItem("Show in Finder");
        MenuItem menuItem3 = new MenuItem("Rename");
        MenuItem menuItem4 = new MenuItem("Delete");
        MenuItem menuItem5 = new MenuItem("Print...");

        menuItem1.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});
        });
        menuItem2.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});

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
        menuItem5.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem3.getText()});
        });

        contextMenu.getItems().addAll(
                menuItem1,
                menuItem2,
                menuItem3,
                menuItem4,
                new SeparatorMenuItem(),
                menuItem5
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

        Node schematicPreviewNode = schematicPreviewNode();
        VBox.setVgrow(schematicPreviewNode, Priority.ALWAYS);
        VBox contentArea = new VBox(
                heading,
                ControlPanelUtils.markdownNode(
                        1.5,
                        ControlPanelUtils.getItemDescriptionFull(this)
                ),
                schematicPreviewNode
        );

        BorderPane pane = new BorderPane(contentArea);
        return pane;
    }

    private Node schematicPreviewNode() {
        Schematic sch = SchematicCache.getInstance().getSchematic(getFile());
        if (sch == null) {
            LOGGER.log(Level.SEVERE, "OOPS! Schematic File didn't load!");
        }

        Group schematicPreview = new SchematicPreview(sch);
        StackPane sp = new StackPane(schematicPreview);
        sp.setBackground(new Background(new BackgroundFill(new Color(0.1,0.1,0.1,1.0), CornerRadii.EMPTY, Insets.EMPTY)));
        Group schemPreviewGroup = new Group(sp);
        GroupContainer container = new GroupContainer(schemPreviewGroup);
        container.setBorder(new Border(new BorderStroke(Color.AQUAMARINE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        return container;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

}
