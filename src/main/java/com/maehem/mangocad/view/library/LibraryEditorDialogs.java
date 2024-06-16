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

import com.maehem.mangocad.model.LibraryElement;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ElementType;
import static com.maehem.mangocad.view.ElementType.FOOTPRINT;
import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.symbol.LibrarySymbolSubEditor;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryEditorDialogs {

    private static final String DEV_NAME_MESSAGE = "Enter name for new %s.";

    public static final ChoiceDialog getDeviceChooserDialogOld(Library library, ArrayList<String> items) {
        ChoiceDialog choiceDialog = new ChoiceDialog<>(items.get(0), items);
        ViewUtils.applyAppStylesheet(choiceDialog.getDialogPane().getStylesheets());
        //choiceDialog.getDialogPane().getStylesheets().add(this.getClass().getResource("/style/dark.css").toExternalForm());
        choiceDialog.setHeaderText("Select a device or create a new one in " + library.getFile().getName());
        choiceDialog.setContentText("Select: ");
        choiceDialog.setTitle("Edit/Create Device");
        ImageView graphic = ViewUtils.createIcon(LibraryEditor.DEVICE_IMAGE, ViewUtils.DIALOG_GRAPHIC_SIZE);
        choiceDialog.setGraphic(graphic);

        return choiceDialog;
    }

    public static final Dialog getSymbolChooserDialog(Library library, ArrayList<Symbol> items, ArrayList<LibrarySymbolSubEditor> openEditors) {
        Dialog<Symbol> dialog = new Dialog<>();
        ViewUtils.applyAppStylesheet(dialog.getDialogPane().getStylesheets());

        dialog.setTitle("Edit/Create Symbol");
        dialog.setHeaderText("Select a symbol or create a new one in " + library.getFile().getName());
        ImageView graphic = ViewUtils.createIcon(LibraryEditor.SYMBOL_IMAGE, ViewUtils.DIALOG_GRAPHIC_SIZE);
        dialog.setGraphic(graphic);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.CANCEL, ButtonType.OK
        );
        // TEST ME!
        ListView<Symbol> availableItemsListView = new ListView<>();
        for (Symbol item : items) {
            availableItemsListView.getItems().add(item);
        }
        availableItemsListView.setCellFactory((p) -> {
            return new ListCell<>() {
                @Override
                public void updateItem(Symbol symbol, boolean empty) {
                    super.updateItem(symbol, empty);
                    if (empty || symbol == null) {
                        setText(null);
                    } else {
                        setText(symbol.getName());
                    }
                }
            };
        });
        availableItemsListView.setOnMouseClicked((me) -> {
            if (me.getClickCount() == 2) {
                Symbol selectedItem = availableItemsListView.getSelectionModel().getSelectedItem();
                LOGGER.log(Level.SEVERE, "User double-clicked item: {0}", selectedItem.getName());
                dialog.setResult(selectedItem);
                dialog.close();
            }
        });

        dialog.setResultConverter(param
                -> ButtonType.OK.equals(param)
                ? availableItemsListView.getSelectionModel().getSelectedItem()
                : null
        );

        VBox content = new VBox(availableItemsListView);
        dialog.getDialogPane().setContent(content);

        return dialog;
    }

    public static final String presentNewLibElementNameDialog(Library library, ElementType type, String message) {
        TextInputDialog inputDialog = new TextInputDialog();
        ViewUtils.applyAppStylesheet(inputDialog.getDialogPane().getStylesheets());
        inputDialog.getEditor().addEventFilter(KeyEvent.KEY_TYPED, (event) -> {

            // Validate the name only letters, digits and some characters (_*?).
            String c = event.getCharacter().toUpperCase();
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ_?*0123456789".contains(c)) {
                TextInputControl source = (TextInputControl) event.getSource();
                source.appendText(c);
            } //else  Nothing appended if no match.
            event.consume();
        });

        String devMessage = String.format(DEV_NAME_MESSAGE, type.text());
        if (message != null) {
            devMessage += "\n" + message;
        }
        inputDialog.setTitle("New " + type.text());
        inputDialog.setHeaderText(devMessage);

        Image img;
        switch (type) {
            case DEVICE -> {
                img = LibraryEditor.DEVICE_IMAGE;
            }
            case FOOTPRINT -> {
                img = LibraryEditor.DEVICE_IMAGE;
            }
            case PACKAGE3D -> {
                img = LibraryEditor.PACKAGE_3D_IMAGE;
            }
            case SYMBOL -> {
                img = LibraryEditor.SYMBOL_IMAGE;
            }
            default -> {
                img = LibraryEditor.SYMBOL_IMAGE;
            }
        }
        ImageView graphic = ViewUtils.createIcon(img, ViewUtils.DIALOG_GRAPHIC_SIZE);
        inputDialog.setGraphic(graphic);
        // TODO Graphic for type.


        inputDialog.showAndWait();
        String nameResult = inputDialog.getResult();
        if (nameResult == null) {
            return null;
        } else {
            //LOGGER.log(Level.SEVERE, "User Typed: " + nameResult);

            if (library.hasElement(type, nameResult)) { // Check if typed value is already in Library.
                // If so, pop-up deny dialog and recursively re-present input dialog.
                return presentNewLibElementNameDialog(library, type, "ERROR: " + type.text() + " name is already in library!");
            } else if (!DeviceSet.isValidName(nameResult)) {
                return presentNewLibElementNameDialog(library, type, "ERROR: " + type.text() + " name invalid. Try a different name.");
            } else { // create new device in library, set item, create DevicePane().
                LibraryElement newElement = library.createNewElement(type, nameResult);
                if (newElement != null) {
                    return newElement.getName();
                }
            }
        }
        return null;
    }
}
