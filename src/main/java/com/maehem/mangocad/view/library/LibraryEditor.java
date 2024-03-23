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

import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.view.ElementType;
import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.device.DeviceEditorPane;
import com.maehem.mangocad.view.library.footprint.FootprintEditorPane;
import com.maehem.mangocad.view.library.symbol.SymbolEditorPane;
import com.maehem.mangocad.view.library.toc.LibraryTocSubEditor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Separator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryEditor extends BorderPane {

    // TODO: Get from i18n bundle.
    private static final String TOC_STR = "Table of Contents";
    private static final String DEV_STR = "Device";
    private static final String FPT_STR = "Footprint";
    private static final String SYM_STR = "Symbol";

    public static final Image FILE_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/file.png")
    );
    public static final Image SAVE_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/floppy-disk.png")
    );
    public static final Image PRINT_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/printer.png")
    );
    public static final Image TOC_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/book.png")
    );
    public static final Image DEVICE_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/photo-album.png")
    );
    public static final Image FOOTPRINT_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/integrated-circuit.png")
    );
    public static final Image SYMBOL_IMAGE = new Image(
            LibraryEditor.class.getResourceAsStream("/icons/logic-gate.png")
    );

    private final File file;
    private final Library library;

    private final ToolBar mainToolbar = new ToolBar();
    private final Button openButton = ViewUtils.createIconButton("Open", FILE_IMAGE);
    private final Button saveButton = ViewUtils.createIconButton("Save", SAVE_IMAGE);
    private final Button printButton = ViewUtils.createIconButton("Print", PRINT_IMAGE);

    private final ToggleButton tocButton = ViewUtils.createIconToggleButton(TOC_STR, TOC_IMAGE);
    private final ToggleButton deviceButton = ViewUtils.createIconToggleButton(DEV_STR, DEVICE_IMAGE);
    private final ToggleButton footprintButton = ViewUtils.createIconToggleButton(FPT_STR, FOOTPRINT_IMAGE);
    private final ToggleButton symbolButton = ViewUtils.createIconToggleButton(SYM_STR, SYMBOL_IMAGE);

    final ToggleGroup modeToggle = new ToggleGroup();
    private Toggle currentToggle = tocButton;

    private final VBox topArea = new VBox(mainToolbar);//, topToolbar2);
    //private final ToolBar leftToolBar = new ToolBar();
    private final HBox bottomArea = new HBox();

    // The editors.  Only one showing in the center at a time.
    private Node currentEditor = null;
    private LibraryTocSubEditor tocPane = null;
    private DeviceEditorPane devicePane = null;
    private SymbolEditorPane symbolPane = null;
    private FootprintEditorPane footprintPane = null;

    //private DeviceEditorPane devicePane = null;
    //private FootprintEditorPane footprintPane = null;
    //private Package3DEditorPane packagePane = null;
    public LibraryEditor(File file, Library library) {
        this.file = file;
        this.library = library;

        // top:  two tool bar rows
        setTop(topArea);

        // left: tool bar
        //setLeft(leftToolBar);
        // center: work area
        this.tocPane = new LibraryTocSubEditor(this);
        this.currentEditor = tocPane;
        setCenter(tocPane);

        // bottom: message area
        setBottom(bottomArea);

        topArea.setFillWidth(true);
        bottomArea.setPrefHeight(16);
        bottomArea.setFillHeight(true);
        bottomArea.getChildren().add(new Text(library.getFile().getAbsolutePath()));

        initToolbar();
    }

    private void initToolbar() {
        mainToolbar.setPrefHeight(24);

        tocButton.setToggleGroup(modeToggle);

        deviceButton.setToggleGroup(modeToggle);
        footprintButton.setToggleGroup(modeToggle);
        symbolButton.setToggleGroup(modeToggle);

        tocButton.setSelected(true);
        deviceButton.setSelected(false);
        footprintButton.setSelected(false);
        symbolButton.setSelected(false);

        /* Add toolbar buttons */
        ObservableList<Node> items = mainToolbar.getItems();
        items.add(openButton);
        items.add(saveButton);
        items.add(printButton);
        items.add(new Region());
        items.add(new Separator());
        items.add(new Region());
        items.add(tocButton);
        items.add(deviceButton);
        items.add(footprintButton);
        items.add(symbolButton);

        /*
         *  Button callbacks
         */
        modeToggle.selectedToggleProperty().addListener((ov, toggle, newToggle) -> {
            if (newToggle == null) { // If newToggle is null, reselect it.
                currentToggle.setSelected(true); // user action might have un-toggled it.
            } else {
                currentToggle = newToggle;
                initiateSwitchEditorAction();
            }

        });
    }

    public File getFile() {
        return file;
    }

    private void initiateSwitchEditorAction() {
        // TOC, always there just witch back
        String togName = (String) currentToggle.getUserData();
        switch (togName) {
            case TOC_STR -> {
                setEditor(null, null);
            }
            case DEV_STR -> {
                // Present Device chooser
                setEditor(ElementType.DEVICE, null);
            }
            case FPT_STR -> {
                // Present Footprint Chooser
                setEditor(ElementType.FOOTPRINT, null);
            }
            case SYM_STR -> {
                // Present Symbol
                setEditor(ElementType.SYMBOL, null);
            }
        }

    }

    public void setEditor(ElementType type, String item) {
        if (type == null) {
            currentEditor = tocPane;
            tocButton.setSelected(true);
        } else {
            switch (type) {
                case DEVICE -> {
                    if (devicePane == null) {
                        final String NEW_DEVICE = "Create New Device...";
                        if (item == null) {
                            ArrayList<String> deviceSets = new ArrayList<>();
                            for (DeviceSet ds : library.getDeviceSets()) {
                                deviceSets.add(ds.getName());
                            }
                            Collections.sort(deviceSets);
                            deviceSets.add(0, NEW_DEVICE);

                            ChoiceDialog dialog = LibraryEditorDialogs.getDeviceChooserDialog(library, deviceSets);
                            dialog.showAndWait(); // Present chooser

                            Object result = dialog.getResult();
                            if (result == null) { // User Canceled
                                return;
                            } else {
                                item = (String) result;
                            }
                            if (item.equals(NEW_DEVICE)) {
                                //deviceSets.remove(NEW_DEVICE);
                                String newName = LibraryEditorDialogs.presentNewLibElementNameDialog(library, type, null);
                                if (newName == null) { // A valid new device was added, go edit it.
                                    return;
                                } else {
                                    item = newName;
                                }
                            }
                        }
                        devicePane = new DeviceEditorPane(this, item);

                    }
                    currentEditor = devicePane;
                    deviceButton.setSelected(true);
                }
                case FOOTPRINT -> {
                    if (footprintPane == null) {
                        if (item == null) {
                            // Present chooser
                        }
                        footprintPane = new FootprintEditorPane(this, item);
                    }
                    currentEditor = footprintPane;
                    footprintButton.setSelected(true);
                }
                case PACKAGE3D -> {

                }
                case SYMBOL -> {
                    if (symbolPane == null) {
                        symbolPane = new SymbolEditorPane(this, item);
                    }
                    currentEditor = symbolPane;
                    symbolButton.setSelected(true);
                }
            }
        }
        setCenter(currentEditor);
    }

}
