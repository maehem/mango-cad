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
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ElementType;
import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.device.LibraryDeviceSubEditor;
import com.maehem.mangocad.view.library.footprint.LibraryFootprintSubEditor;
import com.maehem.mangocad.view.library.symbol.LibrarySymbolSubEditor;
import com.maehem.mangocad.view.library.toc.LibraryTocSubEditor;
import com.maehem.mangocad.view.widgets.CommandFieldWidget;
import com.maehem.mangocad.view.widgets.ContextMessageWidget;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryEditor extends VBox {

    private final BorderPane mainPane = new BorderPane();

    private static final ResourceBundle MSG = ResourceBundle.getBundle("i18n/Editor");
    // TODO: Get from i18n bundle.
    private static final String TOC_STR = "Table of Contents";
    private static final String DEV_STR = "Device";
    private static final String FPT_STR = "Footprint";
    private static final String SYM_STR = "Symbol";

    public static final Image FILE_IMAGE = ViewUtils.getImage("/icons/file.png");//new Image(
    public static final Image SAVE_IMAGE = ViewUtils.getImage("/icons/floppy-disk.png");
    public static final Image PRINT_IMAGE = ViewUtils.getImage("/icons/printer.png");
    public static final Image TOC_IMAGE = ViewUtils.getImage("/icons/book.png");
    public static final Image DEVICE_IMAGE = ViewUtils.getImage("/icons/photo-album.png");
    public static final Image FOOTPRINT_IMAGE = ViewUtils.getImage("/icons/integrated-circuit.png");
    public static final Image SYMBOL_IMAGE = ViewUtils.getImage("/icons/logic-gate.png");
    public static final Image PACKAGE_3D_IMAGE = ViewUtils.getImage("/icons/cube-isometric.png");

    private final File file;
    private final Library library;

    private final Menu fileMenu = new Menu("File");
    private final Menu viewMenu = new Menu("View");
    private final Menu optionsMenu = new Menu("Options");
    private final Menu windowMenu = new Menu("Window");
    private final Menu helpMenu = new Menu("Help");

    private final ToolBar mainToolbar = new ToolBar();
    private final Button openButton = ViewUtils.createIconButton("Open", FILE_IMAGE);
    private final Button saveButton = ViewUtils.createIconButton("Save", SAVE_IMAGE);
    private final Button printButton = ViewUtils.createIconButton("Print", PRINT_IMAGE);

    private final ToggleButton tocButton = ViewUtils.createIconToggleButton(TOC_STR, TOC_IMAGE);
    private final ToggleButton deviceButton = ViewUtils.createIconToggleButton(DEV_STR, DEVICE_IMAGE);
    private final ToggleButton footprintButton = ViewUtils.createIconToggleButton(FPT_STR, FOOTPRINT_IMAGE);
    private final ToggleButton symbolButton = ViewUtils.createIconToggleButton(SYM_STR, SYMBOL_IMAGE);
    private final CommandFieldWidget commandField = new CommandFieldWidget("OPTION_ICON_COMMAND", "/icons/command.png");
    private final ContextMessageWidget editMessage = new ContextMessageWidget("Foo", null);

    final ToggleGroup modeToggle = new ToggleGroup();
    private Toggle currentToggle = tocButton;

    private final VBox topArea = new VBox(mainToolbar);//, topToolbar2);
    //private final ToolBar leftToolBar = new ToolBar();
    private final HBox bottomArea = new HBox();

    // The editors.  Only one showing in the center at a time.
    private LibrarySubEditor currentEditor;
    private LibrarySubEditor tocPane = null;
    private LibrarySubEditor devicePane = null;
    private LibrarySymbolSubEditor symbolPane = null;
    private LibrarySubEditor footprintPane = null;

    private final ArrayList<LibrarySymbolSubEditor> symbolEditors = new ArrayList<>();
    private final ArrayList<LibraryDeviceSubEditor> deviceEditors = new ArrayList<>();
    private final ArrayList<LibraryFootprintSubEditor> footprintEditors = new ArrayList<>();

    public LibraryEditor(File file, Library library) {
        this.file = file;
        this.library = library;

        this.tocPane = new LibraryTocSubEditor(this);
        this.currentEditor = tocPane;

        VBox.setVgrow(mainPane, Priority.ALWAYS);

        mainPane.setTop(topArea);
        mainPane.setCenter(tocPane);
        mainPane.setBottom(bottomArea); // bottom: message area

        topArea.setFillWidth(true);
        bottomArea.setPrefHeight(16);
        bottomArea.setFillHeight(true);
        bottomArea.getChildren().add(new Text(library.getFile().getAbsolutePath()));

        initToolbar();  // Top Ribbon
        initMenuBar();  // File, View, Options, Window, Help, etc.
        getChildren().add(mainPane);
    }

    private void initMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(
                fileMenu, viewMenu, optionsMenu, windowMenu, helpMenu
        );

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            Platform.runLater(() -> menuBar.setUseSystemMenuBar(true));
        }
        getChildren().add(menuBar);
    }

    private void initToolbar() {
        mainToolbar.setPrefHeight(24);

        tocButton.setToggleGroup(modeToggle);
        deviceButton.setToggleGroup(modeToggle);
        footprintButton.setToggleGroup(modeToggle);
        symbolButton.setToggleGroup(modeToggle);

        modeToggle.selectToggle(tocButton);

        editMessage.setText("Table of Contents");

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
        items.add(new Separator());
        items.add(new Region());
        items.add(commandField);
        items.add(editMessage);

        /*
         *  Button callbacks
         */
        modeToggle.selectedToggleProperty().addListener((ov, toggle, newToggle) -> {
            if (newToggle == null) { // If newToggle is null, reselect it.
                currentToggle.setSelected(true); // user action might have un-toggled it.
            } else {
                currentToggle = newToggle;
                LOGGER.log(Level.SEVERE, "modeToggle: set new toggle.  ==> initiateSwitchEditor");
                initiateSwitchEditorAction(false);
            }
        });
        symbolButton.setOnAction((eh) -> {
            // TODO:
            //
            // Update dialog to include a list of open symbols and list
            // them at the top.
            // Clicking the symbol button will always open the dialog.
            // Maybe the buttons are not toggles. Simply update the icon colors
            // to match the current mode.
            // Enhance the button with a number of open documents. Or a red dot.
            // Maybe a number in a red circle for unsaved edits.
            // Number in a green circle for normal viewed items.

            if (modeToggle.getSelectedToggle().equals(symbolButton)) {
                LOGGER.log(Level.FINER, "symbol button: clicked. ===> initiateSwitchEditor");
                initiateSwitchEditorAction(true); // Pop up item selector.
            }
        });
    }

    public File getFile() {
        return file;
    }

    private void initiateSwitchEditorAction(boolean choose) {
        // TOC, always there just switch back
        String togName = (String) currentToggle.getUserData();
        switch (togName) {
            case TOC_STR -> {
                setSubEditor(null, null);
            }
            case DEV_STR -> {
                setSubEditor(ElementType.DEVICE, null);
            }
            case FPT_STR -> {
                setSubEditor(ElementType.FOOTPRINT, null);
            }
            case SYM_STR -> {
                String item = null;
                if (!choose && symbolPane != null) {
                    item = symbolPane.getSymbol().getName();
                }
                setSubEditor(ElementType.SYMBOL, item);
            }
        }

    }

    public void setSubEditor(ElementType type, String item) {
        if (type == null) {
            currentEditor = tocPane;
            tocButton.setSelected(true);
            editMessage.setText("");
        } else {
            final String CREATE_NEW_MSG = "Create New " + type.text() + "...";
            switch (type) {
                case DEVICE -> {
                    if (devicePane == null) {
                        if (item == null) {
                            ArrayList<String> list = new ArrayList<>();
                            for (DeviceSet element : getLibrary().getDeviceSets()) {
                                list.add(element.getName());
                            }
                            Collections.sort(list);
                            list.add(0, CREATE_NEW_MSG);

                            ChoiceDialog dialog = LibraryEditorDialogs.getDeviceChooserDialogOld(getLibrary(), list);
                            dialog.showAndWait(); // Present chooser

                            Object result = dialog.getResult();
                            if (result == null) { // User Canceled
                                return;
                            } else {
                                item = (String) result;
                            }
                            if (item.equals(CREATE_NEW_MSG)) {
                                //deviceSets.remove(CREATE_NEW_MSG);
                                String newName = LibraryEditorDialogs.presentNewLibElementNameDialog(getLibrary(), type, null);
                                if (newName == null) { // A valid new device was added, go edit it.
                                    return;
                                } else {
                                    item = newName;
                                }
                            }
                        }
                        devicePane = new LibraryDeviceSubEditor(this, item);
                    }
                    currentEditor = devicePane;
                    editMessage.setText("Editing Device: " + item);
                    //editMessage.setLabel("Editing Device:");
                    deviceButton.setSelected(true);
                }
                case FOOTPRINT -> {
                    if (footprintPane == null) {
                        if (item != null) {

                        } else {
                            ArrayList<String> list = new ArrayList<>();
                            for (Footprint element : getLibrary().getPackages()) {
                                list.add(element.getName());
                            }
                            Collections.sort(list);
                            list.add(0, CREATE_NEW_MSG);

                            ChoiceDialog dialog = LibraryEditorDialogs.getDeviceChooserDialogOld(getLibrary(), list);
                            dialog.showAndWait(); // Present chooser

                            Object result = dialog.getResult();
                            if (result == null) { // User Canceled
                                return;
                            } else {
                                item = (String) result;
                            }
                            if (item.equals(CREATE_NEW_MSG)) {
                                //deviceSets.remove(CREATE_NEW_MSG);
                                String newName = LibraryEditorDialogs.presentNewLibElementNameDialog(getLibrary(), type, null);
                                if (newName == null) { // A valid new device was added, go edit it.
                                    return;
                                } else {
                                    item = newName;
                                }
                            }
                        }
                        footprintPane = new LibraryFootprintSubEditor(this, item);
                    }
                    currentEditor = footprintPane;
                    editMessage.setText("Editing Footprint: " + item);
                    //editMessage.setLabel("Editing Footprint:");
                    footprintButton.setSelected(true);
                }
                case PACKAGE3D -> {

                }
                case SYMBOL -> {
                    Symbol symbol;
                    if (item != null) {
                        symbol = getLibrary().getSymbol(item);
                    } else {

                        //  TODO: Supply list as Symbols.
                        // Write a comparitor to compare symbols by name.
//                        ArrayList<String> list = new ArrayList<>();
//                        for (Symbol element : getLibrary().getSymbols()) {
//                            list.add(element.getName());
//                        }
                        ArrayList<Symbol> symbols = new ArrayList<>(getLibrary().getSymbols());

                        //Collections.sort(list);
                        Collections.sort(symbols, (Symbol r1, Symbol r2)
                                -> r1.getName().compareTo(r2.getName()));
                        Symbol createNew = new Symbol();
                        createNew.setName(CREATE_NEW_MSG);
                        symbols.add(0, createNew);

                        Dialog dialog = LibraryEditorDialogs.getSymbolChooserDialog(getLibrary(), symbols, symbolEditors);
                        dialog.showAndWait(); // Present chooser

                        Object result = dialog.getResult();
                        if (result == null) { // User Canceled
                            return;
                        } else {
                            //item = (String) result;
                            //symbol = getLibrary().getSymbol(item);
                            // Returns null for "Create New"
                            symbol = (Symbol) result;
                            //if (item.equals(CREATE_NEW_MSG)) {
                            if (symbol.getName().equals(CREATE_NEW_MSG)) {
                                //deviceSets.remove(CREATE_NEW_MSG);
                                String newName = LibraryEditorDialogs.presentNewLibElementNameDialog(getLibrary(), type, null);
                                if (newName == null) { // A valid new device was added, go edit it.
                                    return;
                                } else {
                                    //item = newName;
                                    symbol = new Symbol();
                                    symbol.setName(newName);
                                }
                            }
                        }
                    }
                    LOGGER.log(Level.FINER, "Symbol is: {0}  Look for an existing editor...", symbol.getName());
                    // Find editor for symbol in list. It might be null.
                    symbolPane = null;
                    for (LibrarySymbolSubEditor editor : symbolEditors) {
                        if (editor.getSymbol().equals(symbol)) {
                            LOGGER.log(Level.FINER, "Found matching editor for " + symbol.getName());
                            symbolPane = editor;
                            break;
                        }
                    }
                    if (symbolPane == null) {
                        LOGGER.log(Level.CONFIG, "Create new editor for: " + symbol.getName());
                        symbolPane = new LibrarySymbolSubEditor(this, symbol);
                        symbolEditors.add(symbolPane);
                        editMessage.setText("Editing Symbol:  " + symbol.getName());
                    }
                    currentEditor = symbolPane;
                    symbolButton.setSelected(true);
                }
            }
        }
        mainPane.setCenter(currentEditor);
    }

    public static final Stage invokeWindow(Library library, String name, File file) {
        final Stage stage = new Stage();
        LibraryEditor root = new LibraryEditor(file, library);
        stage.setTitle("Library Editor: " + name);
        //scene.getStylesheets().add(this.getClass().getResource("/style/dark.css").toExternalForm());
        Scene scene = new Scene(root, 1280, 960);
        ViewUtils.applyAppStylesheet(scene.getStylesheets());
        //scene.getStylesheets().add(LibraryEditor.class.getResource("/style/dark.css").toExternalForm());
        stage.setScene(scene);
        //stage.setScene(new Scene(root, 1280, 960));
        stage.centerOnScreen();
        stage.setOnCloseRequest((t) -> {
            // TODO: Popup if file edited and not saved.

            stage.close();
            //stage = null;
        });

        return stage;
    }

    /**
     * @return the library
     */
    public Library getLibrary() {
        return library;
    }
}
