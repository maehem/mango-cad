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
package com.maehem.mangocad.view.library.symbol;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.drawing.Drawing;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.misc.LayerElement;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.EditorOption;
import com.maehem.mangocad.view.EditorOptionsBar;
import com.maehem.mangocad.view.EditorTool;
import com.maehem.mangocad.view.EditorToolbar;
import com.maehem.mangocad.view.library.LibraryEditor;
import com.maehem.mangocad.view.library.LibrarySubEditor;
import com.maehem.mangocad.view.settings.GridSettingsDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import static javafx.scene.input.KeyCode.ESCAPE;
import javafx.scene.layout.HBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibrarySymbolSubEditor extends LibrarySubEditor {

    //private final LibraryEditor parent;
    private final ArrayList<EditorOption> options = new ArrayList<>(Arrays.asList(
            EditorOption.LAYER_SETTINGS,
            EditorOption.GRID,
            EditorOption.SEPARATOR,
            EditorOption.LAYER_CHOOSER,
            EditorOption.SEPARATOR,
            EditorOption.GRID_MOUSE_INFO,
            EditorOption.SEPARATOR,
            //EditorOption.COMMAND_LINE, // Move to parent toolbar
            //EditorOption.SEPARATOR,
            EditorOption.TOOL_MODE_SETTINGS
    //EditorOption.SEPARATOR,
    //EditorOption.CONTEXT_MESSAGE // Move to parent toolbar.
    ));

    private final ArrayList<EditorTool> tools = new ArrayList<>(Arrays.asList(
            EditorTool.INFO, EditorTool.LOOK,
            EditorTool.SELECT,
            EditorTool.SEPARATOR,
            EditorTool.MOVE, EditorTool.MIRROR,
            EditorTool.ROTATE,
            EditorTool.SEPARATOR,
            EditorTool.COPY,
            EditorTool.PASTE,
            EditorTool.TRASH,
            EditorTool.CHANGE,
            EditorTool.SEPARATOR,
            EditorTool.PIN,
            EditorTool.SEPARATOR,
            EditorTool.LINE,
            EditorTool.TEXT,
            EditorTool.SEPARATOR,
            EditorTool.SPLIT, EditorTool.MITER,
            EditorTool.PAINT, EditorTool.ARRAY,
            EditorTool.OPTIMIZE, EditorTool.NAME,
            EditorTool.SEPARATOR,
            EditorTool.ARC, EditorTool.POLYGON,
            EditorTool.CIRCLE, EditorTool.RECTANGLE,
            EditorTool.SEPARATOR,
            EditorTool.MARK, EditorTool.DIMENSION
    ));

    private final EditorOptionsBar optionsToolbar;
    private final EditorToolbar editorToolbar;
    private final HBox bottomArea = new HBox();
    private final Symbol symbol;
    private final SymbolEditorInteractiveArea symbolEditorInteractiveArea;
    private final SymbolEditorPropertiesTabPane propertiesTabPane = new SymbolEditorPropertiesTabPane();

    public LibrarySymbolSubEditor(LibraryEditor parent, Symbol symbol) {
        super(parent);
        this.symbol = symbol;

        // top:  option toolbar row
        optionsToolbar = new EditorOptionsBar(parent.getLibrary().getParentDrawing(), options, this);
        // TODO: i18n bundle
        optionsToolbar.setMessage("Editing Symbol:   " + symbol.getName());

        // left: tool bar
        editorToolbar = new EditorToolbar(tools, this);
        editorToolbar.setOrientation(Orientation.VERTICAL);
        editorToolbar.setPrefWidth(48);
        editorToolbar.addListener(optionsToolbar);

        bottomArea.setPrefHeight(24);
        bottomArea.setFillHeight(true);

        symbolEditorInteractiveArea = new SymbolEditorInteractiveArea(this);

        SplitPane workArea = new SplitPane(symbolEditorInteractiveArea, propertiesTabPane);
        workArea.setDividerPosition(0, 0.8);

        setTop(optionsToolbar);
        setLeft(editorToolbar);
        setCenter(workArea); // center: work area
        setBottom(bottomArea); // bottom: message area

        // Plumb the mouse XY tracker to the mouse location in work area.
        symbolEditorInteractiveArea.setMouseMoveListener(optionsToolbar.getMouseListener());

        setOnKeyPressed((ke) -> {
            if (ke.getCode() == ESCAPE) {
                //LOGGER.log(Level.SEVERE, "Escape Pressed in editor.");
                symbolEditorInteractiveArea.abandonOperation(true);
                //leftToolBar.setCurrentTool(EditorTool.SELECT);
                ke.consume();
            }
        });
    }

    protected Drawing getDrawing() {
        return getParentEditor().getLibrary().getParentDrawing();
    }

    protected Symbol getSymbol() {
        return symbol;
    }

    public void setToolMode(EditorTool tool) {
        editorToolbar.setCurrentTool(tool);
        optionsToolbar.editorToolBarToolChanged(tool, tool);
    }

    public void setElementFocus(Element e) {
        propertiesTabPane.setPropertiesItem(e);
    }

    @Override
    public void editorToolBarToolChanged(EditorTool oldValue, EditorTool newValue) {
        if (!oldValue.equals(newValue)) {
            LOGGER.log(Level.SEVERE, "User changed tool: {0} ==> {1}", new Object[]{
                oldValue == null ? "null" : oldValue.name(),
                newValue == null ? "null" : newValue.name()
            });
            symbolEditorInteractiveArea.setEditorTool(newValue);
        } else {
            LOGGER.log(Level.SEVERE, "SubEditor leftToolbar nothing changed.");
        }
    }

    @Override
    public void editorOptionBarToggleButtonChanged(EditorOption oldValue, EditorOption newValue) {
        LOGGER.log(Level.SEVERE, "User changed option toggle: {0} ==> {1}", new Object[]{
            oldValue == null ? "null" : oldValue.name(),
            newValue == null ? "null" : newValue.name()
        });
        switch (newValue) {
            case GRID -> {
                GridSettingsDialog dialog = new GridSettingsDialog(getDrawing().getGrid());
            }
        }
    }

    @Override
    public void editorOptionBarWidgetAction(EditorOption option, Event event) {
        LOGGER.log(Level.SEVERE, "User changed option widget: {0} ==> src: {1}  tgt: {2}  evtType: {3}",
                new Object[]{
                    option.name(),
                    event.getSource(),
                    event.getTarget(), // ComboBox
                    event.getEventType().getName() // ACTION
                });
        Object source = event.getSource();
        switch (option) {
            case LAYER_CHOOSER -> {
                if (source instanceof ComboBox cb) {
                    Object selectedItem = cb.getSelectionModel().getSelectedItem();
                    if (selectedItem instanceof LayerElement le) {
                        LOGGER.log(Level.SEVERE, "User Selected: {0}", le.getName());
                        //        parent.setCurrentLayerElement( le );
                    }
                }
            }
        }
    }

}
