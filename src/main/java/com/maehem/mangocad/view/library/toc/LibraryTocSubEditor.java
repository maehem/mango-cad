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
package com.maehem.mangocad.view.library.toc;

import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.LibraryEditor;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryTocSubEditor extends BorderPane {

    private static final Image EDIT_IMAGE = new Image(
            LibraryTocSubEditor.class.getResourceAsStream("/icons/pencil.png")
    );

    private final ToolBar leftToolBar = new ToolBar();
    private final HBox bottomArea = new HBox();
    private final ToolBar mainToolbar = new ToolBar();
    private final VBox topArea = new VBox(mainToolbar);

    private final LibraryTableOfContentsPane tocPane;
    private final LibraryEditor parent;

    public LibraryTocSubEditor(LibraryEditor parent) {
        this.parent = parent;

        // left: tool bar
        setLeft(leftToolBar);

        tocPane = new LibraryTableOfContentsPane(parent.getFile());
        setCenter(tocPane);

        // bottom: message area
        setBottom(bottomArea);

        topArea.setFillWidth(true);
        mainToolbar.setPrefHeight(24);
        //topToolbar2.setPrefHeight(32);
        bottomArea.setPrefHeight(32);
        bottomArea.setFillHeight(true);

        initLeftToolbar();
        initMainToolbar();
    }

    /**
     * ToC has no items in main toolbar.
     */
    private void initMainToolbar() {
//        ObservableList<Node> items = mainToolbar.getItems();
//        //// File section
//        // Open  --  file.png
//        items.add(createIconButton("Open", FILE_IMAGE));
//        // Save -- floppy-disk.png
//        items.add(createIconButton("Save", SAVE_IMAGE));
//        // Print
//        items.add(createIconButton("Print", PRINT_IMAGE));
//
//        items.add(new Separator());
    }

    private void initLeftToolbar() {
        ObservableList<Node> items = leftToolBar.getItems();
        //// File section
        // Open  --  file.png
        items.add(ViewUtils.createIconButton("Edit", EDIT_IMAGE));

        //items.add(new Separator());
    }

}
