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
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.GroupContainer;
import com.maehem.mangocad.view.MarkdownUtils;
import com.maehem.mangocad.view.controlpanel.ControlPanelUtils;
import com.maehem.mangocad.view.library.DetailNodes;
import com.maehem.mangocad.view.library.LibraryEditor;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryDeviceSymbolItem extends ControlPanelListItem {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/transistor.png")
    );

    private Stage stage = null;
    private final Library library;

    public LibraryDeviceSymbolItem(String name, String description, File file, Library library) {
        super(name, description, file);
        this.library = library;
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.FINER, "getContextMenu(): Library Device Symbol Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Open Library");
        MenuItem menuItem2 = new MenuItem("Copy to Library");

        menuItem1.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem1.getText()});
        });
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

        contextMenu.getItems().addAll(
                menuItem1,
                menuItem2
        );

        return contextMenu;
    }

    @Override
    public Node getPreviewTabNode() {
        Text itemName = new Text("Symbol: " + getName());
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

        Node symbolPreviewNode = symbolPreviewNode();
        VBox.setVgrow(symbolPreviewNode, Priority.ALWAYS);
        VBox contentArea = new VBox(
                heading,
                MarkdownUtils.markdownNode(
                        1.0,
                        ControlPanelUtils.getItemDescriptionFull(this),
                        null
                ),
                symbolPreviewNode
        );

        BorderPane pane = new BorderPane(contentArea);
        return pane;
    }

    private Node symbolPreviewNode() {
        Library lib = LibraryCache.getInstance().getLibrary(getFile());
        if (lib == null) {
            LOGGER.log(Level.SEVERE, "OOPS! Library File didn't load!");
        }

        Symbol symbol = lib.getSymbol(getName());
        Group symbolPreview = DetailNodes.symbolPreview(symbol, lib, true);
        GroupContainer symbolContainer = new GroupContainer(symbolPreview, 0.1);

        return symbolContainer;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }


}
