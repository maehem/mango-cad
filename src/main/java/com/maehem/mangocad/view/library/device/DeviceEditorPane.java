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
package com.maehem.mangocad.view.library.device;

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.element.drawing.Drawing;
import com.maehem.mangocad.view.LayerChooser;
import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.LibraryEditor;
import com.maehem.mangocad.view.library.SymbolEditorPropertiesTabPane;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DeviceEditorPane extends BorderPane {

    private static final int TOOLBAR_ICON_SIZE = 20;
    private static final Image INFO_IMAGE = ViewUtils.getImage("/icons/information.png");
    private static final Image LOOK_IMAGE = ViewUtils.getImage("/icons/eye.png");
    private static final Image SELECT_IMAGE = ViewUtils.getImage("/icons/selection.png");
    private static final Image MOVE_IMAGE = ViewUtils.getImage("/icons/move.png");
    private static final Image COPY_IMAGE = ViewUtils.getImage("/icons/copy.png");
    private static final Image TRASH_IMAGE = ViewUtils.getImage("/icons/trash-can.png");
    private static final Image WRENCH_IMAGE = ViewUtils.getImage("/icons/wrench.png");
    private static final Image TAG_IMAGE = ViewUtils.getImage("/icons/tag.png");
    private static final Image PAINT_IMAGE = ViewUtils.getImage("/icons/paint-roller.png");
    private static final Image LINK_IMAGE = ViewUtils.getImage("/icons/link.png");
    private static final Image UNLINK_IMAGE = ViewUtils.getImage("/icons/unlink.png");
    private static final Image MARK_IMAGE = ViewUtils.getImage("/icons/compass-tool.png");
    private static final Image ADD_IMAGE = ViewUtils.getImage("/icons/add-symbol.png");
    private static final Image GRID_IMAGE = ViewUtils.getImage("/icons/grid.png");
    private static final Image NAME_IMAGE = ViewUtils.getImage("/icons/name.png");
    private static final Image LAYERS_IMAGE = ViewUtils.getImage("/icons/layers.png");
    private static final Image ARRAY_IMAGE = ViewUtils.getImage("/icons/array.png");


    private final LibraryEditor parent;

    private final ToolBar topToolBar = new ToolBar();
    private final VBox topArea = new VBox(topToolBar);
    private final ToolBar leftToolBar = new ToolBar();
    private final HBox bottomArea = new HBox();

    public DeviceEditorPane(LibraryEditor parent, String item) {
        this.parent = parent;

        // top:  two tool bar rows
        setTop(topToolBar);

        // left: tool bar
        setLeft(leftToolBar);

        SplitPane workArea = new SplitPane(new DeviceEditorInteractiveArea(), new SymbolEditorPropertiesTabPane());
        workArea.setDividerPosition(0, 0.8);
        // center: work area
        setCenter(workArea);

        // bottom: message area
        setBottom(bottomArea);
        bottomArea.getChildren().add(new Text("Editing: " + item));

        // right: nothing.
        topArea.setPrefHeight(24);
        topArea.setFillWidth(true);
        bottomArea.setPrefHeight(24);
        bottomArea.setFillHeight(true);

        initTopToolbar();
        initLeftToolbar();
    }
    private void initTopToolbar() {
        topToolBar.setPrefHeight(24);
        Button infoButton = createToolbarButton("Layers", LAYERS_IMAGE);
        Button gridButton = createToolbarButton("Grid", GRID_IMAGE);

        Drawing drawing = parent.getLibrary().getParentDrawing();
        ColorPalette palette = drawing.getPalette();
        LayerChooser layerChooser = new LayerChooser(drawing.getPalette(), drawing.getLayers()); // Does nothing for this editor.

        topToolBar.getItems().addAll(infoButton, gridButton, new Separator(), layerChooser);
    }

    private void initLeftToolbar() {
        leftToolBar.setOrientation(Orientation.VERTICAL);
        leftToolBar.setPrefWidth(48);

        ToggleGroup toggleGroup = new ToggleGroup();

        // Note: Paintroller example --  https://www.facebook.com/watch/?v=497104337543477
        // GridPane for each section
        // Inspect region
        ToggleButton infoButton = createToolbarToggleButton("Information", INFO_IMAGE);
        ToggleButton lookButton = createToolbarToggleButton("Show", LOOK_IMAGE);
        ToggleButton selectButton = createToolbarToggleButton("Group", SELECT_IMAGE);
        GridPane inspPane = createToolbarGroup();
        inspPane.add(infoButton, 0, 0);
        inspPane.add(lookButton, 1, 0);
        inspPane.add(selectButton, 0, 1);

        // Move region
        ToggleButton moveButton = createToolbarToggleButton("Move", MOVE_IMAGE);
        GridPane movePane = createToolbarGroup();
        movePane.add(moveButton, 0, 0);

        // Copy/Paste region
        ToggleButton copyButton = createToolbarToggleButton("Copy", COPY_IMAGE);
        ToggleButton trashButton = createToolbarToggleButton("Delete", TRASH_IMAGE);
        ToggleButton wrenchButton = createToolbarToggleButton("Copy", WRENCH_IMAGE);
        GridPane copyPastePane = createToolbarGroup();
        copyPastePane.add(copyButton, 0, 0);
        copyPastePane.add(trashButton, 1, 0);
        copyPastePane.add(wrenchButton, 0, 1);

        // Add region
        ToggleButton addButton = createToolbarToggleButton("Add Symbol", ADD_IMAGE);
        GridPane addPane = createToolbarGroup();
        addPane.add(addButton, 0, 0);

        // Tag region
        ToggleButton tagButton = createToolbarToggleButton("Tag", TAG_IMAGE);
        ToggleButton nameButton = createToolbarToggleButton("Name", NAME_IMAGE);
        ToggleButton paintButton = createToolbarToggleButton("Paint", PAINT_IMAGE);
        ToggleButton arrayButton = createToolbarToggleButton("Array", ARRAY_IMAGE);
        GridPane tagPane = createToolbarGroup();
        tagPane.add(tagButton, 0, 0);
        tagPane.add(nameButton, 1, 0);
        tagPane.add(paintButton, 0, 1);
        tagPane.add(arrayButton, 1, 1);

        // Link region
        ToggleButton linkButton = createToolbarToggleButton("Link", LINK_IMAGE);
        ToggleButton unlinkButton = createToolbarToggleButton("Unlink", UNLINK_IMAGE);
        GridPane linkPane = createToolbarGroup();
        linkPane.add(linkButton, 0, 0);
        linkPane.add(unlinkButton, 1, 0);

        // Link region
        ToggleButton markButton = createToolbarToggleButton("Mark", MARK_IMAGE);
        GridPane markPane = createToolbarGroup();
        markPane.add(markButton, 0, 0);
        Region r = new Region();
        r.setMinSize(16, 16);

        leftToolBar.getItems().addAll(
                r,
                inspPane, new Separator(),
                movePane, new Separator(),
                copyPastePane, new Separator(),
                addPane, new Separator(),
                tagPane, new Separator(),
                linkPane, new Separator(),
                markPane, new Separator()
        );

        toggleGroup.getToggles().addAll(
                infoButton, lookButton, selectButton,
                moveButton,
                copyButton, trashButton, wrenchButton,
                addButton,
                tagButton, nameButton, paintButton, arrayButton,
                linkButton, unlinkButton
        );
    }

    private static final GridPane createToolbarGroup() {
        GridPane gp = new GridPane();
        gp.setId("toolbar-gridpane");

        return gp;
    }

    private static final ToggleButton createToolbarToggleButton(String name, Image img) {
        ToggleButton b = (ToggleButton) ViewUtils.createIconButton(name, img, TOOLBAR_ICON_SIZE, true);
        b.setTooltip(new Tooltip(name));
        b.getGraphic().setId("toolbar-button-icon");
        b.setId("toolbar-button");

        return b;
    }

    private static final Button createToolbarButton(String name, Image img) {
        Button b = (Button) ViewUtils.createIconButton(name, img, TOOLBAR_ICON_SIZE, false);
        b.setTooltip(new Tooltip(name));
        b.getGraphic().setId("toolbar-button-icon");
        b.setId("toolbar-button");

        return b;
    }
}
