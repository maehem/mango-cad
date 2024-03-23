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
import com.maehem.mangocad.view.ElementType;
import com.maehem.mangocad.view.ViewUtils;
import java.util.ArrayList;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryEditorDialogs {

    private static final String DEV_NAME_MESSAGE = "Enter name for new Device.";

    public static final ChoiceDialog getDeviceChooserDialog(Library library, ArrayList<String> items) {
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

        if (message != null) {
            inputDialog.setHeaderText(DEV_NAME_MESSAGE + "\n " + message);
        } else {
            inputDialog.setHeaderText(DEV_NAME_MESSAGE);
        }

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
