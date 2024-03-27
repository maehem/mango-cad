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
package com.maehem.mangocad.view.settings;

import com.maehem.mangocad.model.element.misc.Grid;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class GridSettingsDialog extends Dialog<ButtonType> {

    private final ButtonType okButtonType = ButtonType.OK;
    private final ButtonType cancelButtonType = ButtonType.CANCEL;
    private final ButtonType defaultsButtonType = new ButtonType("Defaults", ButtonData.LEFT);

    private final GridSettingsPanel gridSettingsPanel;

    public GridSettingsDialog(Grid gridSettings) {
        this(gridSettings, false);
    }

    public GridSettingsDialog(Grid gridSettings, boolean useDefaults) {
        setTitle("Grid Settings");

        getDialogPane().getButtonTypes().addAll(
                defaultsButtonType, okButtonType, cancelButtonType
        );

        getDialogPane().setPrefSize(400, 300);

        boolean disabled = false; // computed based on content of text fields, for example
        getDialogPane().lookupButton(okButtonType).setDisable(disabled);

        gridSettingsPanel = new GridSettingsPanel(gridSettings, useDefaults);
        getDialogPane().setContent(gridSettingsPanel);
        ViewUtils.applyAppStylesheet(getDialogPane().getStylesheets());
        showAndWait().ifPresent(response -> {
            if (response == okButtonType) {
                // Tell the pane to save the changes.
                gridSettingsPanel.doSave();
                LOGGER.log(Level.SEVERE, "OK pressed.");
            } else if (response == defaultsButtonType) {
                LOGGER.log(Level.SEVERE, "Grid Defaults selected.");
                new GridSettingsDialog(gridSettings, true);
            } else {
                LOGGER.log(Level.SEVERE, response.toString());
            }
        });

    }
}
