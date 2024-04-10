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
package com.maehem.mangocad.view.schematic;

import com.maehem.mangocad.view.library.symbol.SymbolEditorPropertiesTabPane;
import java.io.File;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SchematicEditor extends BorderPane {

    private final File file;

    private final ToolBar topToolbar1 = new ToolBar();
    private final VBox topArea = new VBox(topToolbar1);
    private final ToolBar leftToolBar = new ToolBar();
    private final HBox bottomArea = new HBox();

    public SchematicEditor(File file) {
        this.file = file;

        // top: tool bar
        setTop(topArea);

        // left: tool bar
        setLeft(leftToolBar);

        SplitPane workArea = new SplitPane(new SchematicEditorInteractiveArea(), new SymbolEditorPropertiesTabPane());
        workArea.setDividerPosition(0, 0.8);
        setCenter(workArea);

        // bottom: message area
        setBottom(bottomArea);

        // right: nothing.
        topArea.setPrefHeight(24);
        topArea.setFillWidth(true);
        topToolbar1.setPrefHeight(24);
        bottomArea.setPrefHeight(24);
        bottomArea.setFillHeight(true);
    }

}
