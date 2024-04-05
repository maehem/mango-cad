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

import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.EditorTool;
import com.maehem.mangocad.view.EditorToolbar;
import com.maehem.mangocad.view.EditorToolbarListener;
import com.maehem.mangocad.view.library.LibraryEditor;
import com.maehem.mangocad.view.library.SymbolEditorPropertiesTabPane;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorPane extends BorderPane implements EditorToolbarListener {

    private final LibraryEditor parent;

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

    private final ToolBar topToolbar1 = new ToolBar();
    private final VBox topArea = new VBox(topToolbar1);
    private final ToolBar leftToolBar;
    private final HBox bottomArea = new HBox();

    public SymbolEditorPane(LibraryEditor parent, String item) {
        this.parent = parent;

        // top:  two tool bar rows
        setTop(topToolbar1);

        // left: tool bar
        leftToolBar = new EditorToolbar(tools, this);
        setLeft(leftToolBar);

        SplitPane workArea = new SplitPane(new SymbolEditorInteractiveArea(), new SymbolEditorPropertiesTabPane());
        workArea.setDividerPosition(0, 0.8);
        // center: work area
        setCenter(workArea);

        // bottom: message area
        setBottom(bottomArea);
        bottomArea.getChildren().add(new Text("Editing: " + item));

        // right: nothing.
        topArea.setPrefHeight(24);
        topArea.setFillWidth(true);
        topToolbar1.setPrefHeight(24);
        bottomArea.setPrefHeight(24);
        bottomArea.setFillHeight(true);

        leftToolBar.setOrientation(Orientation.VERTICAL);
        leftToolBar.setPrefWidth(48);
    }

    @Override
    public void editorToolBarButtonChanged(EditorTool oldValue, EditorTool newValue) {
        LOGGER.log(Level.SEVERE, "User changed tool: {0} ==> {1}", new Object[]{oldValue.name(), newValue.name()});
    }

}
