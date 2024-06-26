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
package com.maehem.mangocad.view.library.footprint;

import com.maehem.mangocad.view.EditorOption;
import com.maehem.mangocad.view.EditorOptionsBar;
import com.maehem.mangocad.view.EditorTool;
import com.maehem.mangocad.view.EditorToolbar;
import com.maehem.mangocad.view.library.LibraryEditor;
import com.maehem.mangocad.view.library.LibrarySubEditor;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryFootprintSubEditor extends LibrarySubEditor {

    //private final LibraryEditor parent;

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
            EditorTool.MOVE, EditorTool.MIRROR,
            EditorTool.ROTATE,
            EditorTool.SEPARATOR,
            EditorTool.COPY,
            EditorTool.PASTE,
            EditorTool.TRASH,
            EditorTool.CHANGE,
            EditorTool.SEPARATOR,
            EditorTool.PAD_THD,
            EditorTool.PAD_SMD,
            EditorTool.SEPARATOR,
            EditorTool.LINE,
            EditorTool.TEXT,
            EditorTool.HOLE,
            EditorTool.SEPARATOR,
            EditorTool.SPLIT, EditorTool.MITER,
            EditorTool.PAINT, EditorTool.ARRAY,
            EditorTool.LOCK,
            EditorTool.OPTIMIZE, EditorTool.NAME,
            EditorTool.SEPARATOR,
            EditorTool.ARC, EditorTool.POLYGON,
            EditorTool.CIRCLE, EditorTool.RECTANGLE,
            EditorTool.SEPARATOR,
            EditorTool.MARK, EditorTool.DIMENSION
    ));

    private final ToolBar topToolbar;
//    private final VBox topArea = new VBox(topToolbar);
    private final ToolBar leftToolBar;
    private final HBox bottomArea = new HBox();

    public LibraryFootprintSubEditor(LibraryEditor parent, String item) {
        super(parent);
        //this.parent = parent;

        // top:  two tool bar rows
        topToolbar = new EditorOptionsBar(parent.getLibrary().getParentDrawing(), null, options, this);
        setTop(topToolbar);

        // left: tool bar
        leftToolBar = new EditorToolbar(tools, this);
        setLeft(leftToolBar);

        SplitPane workArea = new SplitPane(new FootprintEditorInteractiveArea(), new Pane());
        workArea.setDividerPosition(0, 0.8);
        // center: work area
        setCenter(workArea);

        // bottom: message area
        setBottom(bottomArea);
        bottomArea.getChildren().add(new Text("Editing: " + item));

        // right: nothing.
//        topArea.setPrefHeight(24);
//        topArea.setFillWidth(true);
        topToolbar.setPrefHeight(24);
        bottomArea.setPrefHeight(24);
        bottomArea.setFillHeight(true);

        leftToolBar.setOrientation(Orientation.VERTICAL);
        leftToolBar.setPrefWidth(48);
    }

    @Override
    public void editorOptionBarToggleButtonChanged(EditorOption oldValue, EditorOption newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void editorOptionBarWidgetAction(EditorOption tool, Event event) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void editorToolBarToolChanged(EditorTool oldValue, EditorTool newValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
