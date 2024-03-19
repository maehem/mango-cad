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
package com.maehem.mangocad.view.library;

import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.symbol.SymbolEditorPane;
import com.maehem.mangocad.view.library.toc.LibraryTocSubEditor;
import java.io.File;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryEditor extends BorderPane {

    private static final Image FILE_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/file.png")
    );
    private static final Image SAVE_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/floppy-disk.png")
    );
    private static final Image PRINT_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/printer.png")
    );
    private static final Image TOC_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/book.png")
    );
    private static final Image DEVICE_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/photo-album.png")
    );
    private static final Image FOOTPRINT_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/integrated-circuit.png")
    );
    private static final Image SYMBOL_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/logic-gate.png")
    );

    private final File file;

    private final ToolBar mainToolbar = new ToolBar();
    //private final ToolBar topToolbar2 = new ToolBar();
    private final VBox topArea = new VBox(mainToolbar);//, topToolbar2);
    //private final ToolBar leftToolBar = new ToolBar();
    private final HBox bottomArea = new HBox();

    private LibraryTocSubEditor tocPane = null;
    private SymbolEditorPane symbolPane = null;
    //private DeviceEditorPane devicePane = null;
    //private FootprintEditorPane footprintPane = null;
    //private Package3DEditorPane packagePane = null;

    public LibraryEditor( File file) {
        this.file = file;

        // top:  two tool bar rows
        setTop(topArea);

        // left: tool bar
        //setLeft(leftToolBar);

        // center: work area
        this.tocPane = new LibraryTocSubEditor(this);
        setCenter(tocPane);



        // bottom: message area
        setBottom(bottomArea);

        // right: nothing.

        //topArea.setPrefHeight(48);
        topArea.setFillWidth(true);
        mainToolbar.setPrefHeight(24);
        //topToolbar2.setPrefHeight(32);
        bottomArea.setPrefHeight(32);
        bottomArea.setFillHeight(true);

        initMainToolbar();

    }

    private void initMainToolbar() {
        ObservableList<Node> items = mainToolbar.getItems();
        //// File section
        // Open  --  file.png
        items.add(ViewUtils.createIconButton("Open", FILE_IMAGE));
        // Save -- floppy-disk.png
        items.add(ViewUtils.createIconButton("Save", SAVE_IMAGE));
        // Print
        items.add(ViewUtils.createIconButton("Print", PRINT_IMAGE));

        items.add(new Separator());
        //// Sub-editor Section
        // ToC icon  --  book.png
        items.add(ViewUtils.createIconButton("ToC", TOC_IMAGE));
        // Device icon  -- Chip+Gate+DownArrow
        items.add(ViewUtils.createIconButton("Device", DEVICE_IMAGE));
        // Footprint icon -- Chip
        items.add(ViewUtils.createIconButton("Footprint", FOOTPRINT_IMAGE));
        // Symbol icon -- Gate
        items.add(ViewUtils.createIconButton("Symbol", SYMBOL_IMAGE));
    }

    public File getFile() {
        return file;
    }
}
