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
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.ArrayList;
import java.util.logging.Level;
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

    public static final String presentNewDevNameDialog(Library library, ArrayList<String> deviceSets, String message) {
        // Name New Device Dialog.
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.getEditor().addEventFilter(KeyEvent.KEY_TYPED, (event) -> {

            String c = event.getCharacter().toUpperCase();
            if (!"ABCDEFGHIJKLMNOPQRSTUVWXYZ_?*0123456789".contains(c)) {
                event.consume();
            } else {
                TextInputControl source = (TextInputControl) event.getSource();
                //source.deletePreviousChar();
                source.appendText(c);
                event.consume();
            }
        });

        if (message != null) {
            inputDialog.setHeaderText(DEV_NAME_MESSAGE + "\n " + message);
        } else {
            inputDialog.setHeaderText(DEV_NAME_MESSAGE);
        }

        inputDialog.showAndWait();
        Object nameResult = inputDialog.getResult();
        if (nameResult == null) {
            return null;
        } else {
            LOGGER.log(Level.SEVERE, "User Typed: " + nameResult);
            if (deviceSets.contains(nameResult)) { // Check if typed value is already in Library.
                // If so, pop-up deny dialog and recursively re-present input dialog.
                return presentNewDevNameDialog(library, deviceSets, "ERROR: Device name is already in library!");
            } else if (!DeviceSet.isValidName(nameResult)) {
                return presentNewDevNameDialog(library, deviceSets, "ERROR: Device name invalid. Try a different name.");
            } else { // create new device in library, set item, create DevicePane().

                DeviceSet dsNew = new DeviceSet();
                dsNew.setName((String) nameResult);
                library.getDeviceSets().add(dsNew);
                return dsNew.getName();
            }
        }
    }
}
