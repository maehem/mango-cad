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

import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.EditorOption;
import com.maehem.mangocad.view.EditorOptionsBar;
import com.maehem.mangocad.view.EditorTool;
import com.maehem.mangocad.view.EditorToolbar;
import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.LibraryEditor;
import com.maehem.mangocad.view.library.LibrarySubEditor;
import com.maehem.mangocad.view.library.symbol.SymbolEditorPropertiesTabPane;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryDeviceSubEditor extends LibrarySubEditor {

    private final ArrayList<EditorOption> options = new ArrayList<>(Arrays.asList(
            EditorOption.LAYER_SETTINGS,
            EditorOption.GRID,
            EditorOption.SEPARATOR,
            EditorOption.LAYER_CHOOSER,
            EditorOption.SEPARATOR,
            EditorOption.GRID_MOUSE_INFO,
            EditorOption.SEPARATOR,
            EditorOption.COMMAND_LINE
    ));

    private final ArrayList<EditorTool> tools = new ArrayList<>(Arrays.asList(
            EditorTool.INFO, EditorTool.LOOK,
            EditorTool.SELECT,
            EditorTool.SEPARATOR,
            EditorTool.MOVE,
            EditorTool.SEPARATOR,
            EditorTool.COPY,
            EditorTool.TRASH,
            EditorTool.SEPARATOR,
            EditorTool.ADD,
            EditorTool.SEPARATOR,
            EditorTool.TAG, EditorTool.NAME,
            EditorTool.PAINT, EditorTool.ARRAY,
            EditorTool.SEPARATOR,
            EditorTool.LINK, EditorTool.UNLINK,
            EditorTool.SEPARATOR,
            EditorTool.MARK
    ));

    private static final int TOOLBAR_ICON_SIZE = 20;
//    private static final Image INFO_IMAGE = ViewUtils.getImage("/icons/information.png");
//    private static final Image LOOK_IMAGE = ViewUtils.getImage("/icons/eye.png");
//    private static final Image SELECT_IMAGE = ViewUtils.getImage("/icons/selection.png");
//    private static final Image MOVE_IMAGE = ViewUtils.getImage("/icons/move.png");
//    private static final Image COPY_IMAGE = ViewUtils.getImage("/icons/copy.png");
//    private static final Image TRASH_IMAGE = ViewUtils.getImage("/icons/trash-can.png");
//    private static final Image WRENCH_IMAGE = ViewUtils.getImage("/icons/wrench.png");
//    private static final Image TAG_IMAGE = ViewUtils.getImage("/icons/tag.png");
//    private static final Image PAINT_IMAGE = ViewUtils.getImage("/icons/paint-roller.png");
//    private static final Image LINK_IMAGE = ViewUtils.getImage("/icons/link.png");
//    private static final Image UNLINK_IMAGE = ViewUtils.getImage("/icons/unlink.png");
//    private static final Image MARK_IMAGE = ViewUtils.getImage("/icons/compass-tool.png");
//    private static final Image ADD_IMAGE = ViewUtils.getImage("/icons/add-symbol.png");
//    private static final Image GRID_IMAGE = ViewUtils.getImage("/icons/grid.png");
//    private static final Image NAME_IMAGE = ViewUtils.getImage("/icons/name.png");
//    private static final Image LAYERS_IMAGE = ViewUtils.getImage("/icons/layers.png");
//    private static final Image ARRAY_IMAGE = ViewUtils.getImage("/icons/array.png");

    //private final LibraryEditor parent;

    private final ToolBar topToolBar;
    //private final VBox topArea = new VBox();
    private final ToolBar leftToolBar;
    private final HBox bottomArea = new HBox();

    public LibraryDeviceSubEditor(LibraryEditor parentEditor, String item) {
        super(parentEditor);
        //this.parent = parent;

        // top:  option toolbar row
        topToolBar = new EditorOptionsBar(parentEditor.getLibrary().getParentDrawing(), options, this);
        setTop(topToolBar);
        //topArea.getChildren().add(topToolBar);

        // left: tool bar
        leftToolBar = new EditorToolbar(tools, this);
        setLeft(leftToolBar);

        SplitPane workArea = new SplitPane(new DeviceEditorInteractiveArea(), new SymbolEditorPropertiesTabPane());
        workArea.setDividerPosition(0, 0.8);
        // center: work area
        setCenter(workArea);

        // bottom: message area
        setBottom(bottomArea);
        bottomArea.getChildren().add(new Text("Editing: " + item));

        // right: nothing.
        //topArea.setPrefHeight(24);
        //topArea.setFillWidth(true);
        bottomArea.setPrefHeight(24);
        bottomArea.setFillHeight(true);

        initTopToolbar();
    }

    private void initTopToolbar() {
//        Drawing drawing = parent.getLibrary().getParentDrawing();
//        topToolBar.setPrefHeight(24);
//        Button layersButton = createToolbarButton("Layers", LAYERS_IMAGE);
//        layersButton.setOnAction((t) -> {
//            LayersVisibilityDialog layersDialog = new LayersVisibilityDialog(drawing.getPalette(), drawing.getLayers());
//        });
//        Button gridButton = createToolbarButton("Grid", GRID_IMAGE);
//        gridButton.setOnAction((t) -> {
//            GridSettingsDialog gridSettings = new GridSettingsDialog(drawing.getGrid());
//        });

//        FillStyleChooser fillStyleChooser = new FillStyleChooser(1, 16);

//        ColorPalette palette = drawing.getPalette();
//        LayerChooser layerChooser = new LayerChooser(drawing.getPalette(), drawing.getLayers()); // Does nothing for this editor.
//        CommandFieldWidget commandField = new CommandFieldWidget();
//        Rectangle spacer = new Rectangle(16, 16, Color.TRANSPARENT);

        //topToolBar.getItems().addAll(layersButton, gridButton, new Separator(), layerChooser, fillStyleChooser, spacer, commandField);
    }

//    private void initLeftToolbar() {
//        leftToolBar.setOrientation(Orientation.VERTICAL);
//        leftToolBar.setPrefWidth(48);
//
//        ToggleGroup toggleGroup = new ToggleGroup();
//
//        // Note: Paintroller example --  https://www.facebook.com/watch/?v=497104337543477
//        // GridPane for each section
//        // Inspect region
//        ToggleButton infoButton = createToolbarToggleButton("Information", INFO_IMAGE);
//        ToggleButton lookButton = createToolbarToggleButton("Show", LOOK_IMAGE);
//        ToggleButton selectButton = createToolbarToggleButton("Group", SELECT_IMAGE);
//        GridPane inspPane = createToolbarGroup();
//        inspPane.add(infoButton, 0, 0);
//        inspPane.add(lookButton, 1, 0);
//        inspPane.add(selectButton, 0, 1);
//
//        // Move region
//        ToggleButton moveButton = createToolbarToggleButton("Move", MOVE_IMAGE);
//        GridPane movePane = createToolbarGroup();
//        movePane.add(moveButton, 0, 0);
//
//        // Copy/Paste region
//        ToggleButton copyButton = createToolbarToggleButton("Copy", COPY_IMAGE);
//        ToggleButton trashButton = createToolbarToggleButton("Delete", TRASH_IMAGE);
//        ToggleButton wrenchButton = createToolbarToggleButton("Copy", WRENCH_IMAGE);
//        GridPane copyPastePane = createToolbarGroup();
//        copyPastePane.add(copyButton, 0, 0);
//        copyPastePane.add(trashButton, 1, 0);
//        copyPastePane.add(wrenchButton, 0, 1);
//
//        // Add region
//        ToggleButton addButton = createToolbarToggleButton("Add Symbol", ADD_IMAGE);
//        GridPane addPane = createToolbarGroup();
//        addPane.add(addButton, 0, 0);
//
//        // Tag region
//        ToggleButton tagButton = createToolbarToggleButton("Tag", TAG_IMAGE);
//        ToggleButton nameButton = createToolbarToggleButton("Name", NAME_IMAGE);
//        ToggleButton paintButton = createToolbarToggleButton("Paint", PAINT_IMAGE);
//        ToggleButton arrayButton = createToolbarToggleButton("Array", ARRAY_IMAGE);
//        GridPane tagPane = createToolbarGroup();
//        tagPane.add(tagButton, 0, 0);
//        tagPane.add(nameButton, 1, 0);
//        tagPane.add(paintButton, 0, 1);
//        tagPane.add(arrayButton, 1, 1);
//
//        // Link region
//        ToggleButton linkButton = createToolbarToggleButton("Link", LINK_IMAGE);
//        ToggleButton unlinkButton = createToolbarToggleButton("Unlink", UNLINK_IMAGE);
//        GridPane linkPane = createToolbarGroup();
//        linkPane.add(linkButton, 0, 0);
//        linkPane.add(unlinkButton, 1, 0);
//
//        // Link region
//        ToggleButton markButton = createToolbarToggleButton("Mark", MARK_IMAGE);
//        GridPane markPane = createToolbarGroup();
//        markPane.add(markButton, 0, 0);
//        Region r = new Region();
//        r.setMinSize(16, 16);
//
//        leftToolBar.getItems().addAll(
//                r,
//                inspPane, new Separator(),
//                movePane, new Separator(),
//                copyPastePane, new Separator(),
//                addPane, new Separator(),
//                tagPane, new Separator(),
//                linkPane, new Separator(),
//                markPane, new Separator()
//        );
//
//        toggleGroup.getToggles().addAll(
//                infoButton, lookButton, selectButton,
//                moveButton,
//                copyButton, trashButton, wrenchButton,
//                addButton,
//                tagButton, nameButton, paintButton, arrayButton,
//                linkButton, unlinkButton
//        );
//    }
//    private static final GridPane createToolbarGroup() {
//        GridPane gp = new GridPane();
//        gp.setId("toolbar-gridpane");
//
//        return gp;
//    }
//
//    private static final ToggleButton createToolbarToggleButton(String name, Image img) {
//        ToggleButton b = (ToggleButton) ViewUtils.createIconButton(name, img, TOOLBAR_ICON_SIZE, true);
//        b.setTooltip(new Tooltip(name));
//        b.getGraphic().setId("toolbar-button-icon");
//        b.setId("toolbar-button");
//
//        return b;
//    }
    private static final Button createToolbarButton(String name, Image img) {
        Button b = (Button) ViewUtils.createIconButton(name, img, TOOLBAR_ICON_SIZE, false);
        b.setTooltip(new Tooltip(name));
        b.getGraphic().setId("toolbar-button-icon");
        b.setId("toolbar-button");

        return b;
    }

    @Override
    public void editorOptionBarToggleButtonChanged(EditorOption oldValue, EditorOption newValue) {
        LOGGER.log(Level.SEVERE, "User changed option toggle: {0} ==> {1}", new Object[]{
            oldValue == null ? "null" : oldValue.name(),
            newValue == null ? "null" : newValue.name()
        });
    }

    @Override
    public void editorOptionBarWidgetAction(EditorOption tool, Event event) {
        LOGGER.log(Level.SEVERE, "User changed option widget: {0} ==> src: {1}  tgt: {2}  evtType: {3}",
                new Object[]{
                    tool.name(),
                    event.getSource(),
                    event.getTarget(), // ComboBox
                    event.getEventType().getName() // ACTION
                });
    }

    @Override
    public void editorToolBarButtonChanged(EditorTool oldValue, EditorTool newValue) {
        LOGGER.log(Level.SEVERE, "User changed tool: {0} ==> {1}", new Object[]{
            oldValue == null ? "null" : oldValue.name(),
            newValue == null ? "null" : newValue.name()
        });
    }

}
