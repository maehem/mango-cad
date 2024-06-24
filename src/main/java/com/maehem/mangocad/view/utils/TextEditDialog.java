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
package com.maehem.mangocad.view.utils;

import com.maehem.mangocad.model.element.basic.TextElement;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TextEditDialog extends Dialog<ButtonType> {

    private final ButtonType okButtonType = ButtonType.OK;
    private final ButtonType cancelButtonType = ButtonType.CANCEL;

    private final TextEditPanel textEditPanel;
    private final TextElement textElement;

    public TextEditDialog(TextElement text) {
        this.textElement = text;

        setTitle("Enter Text");

        getDialogPane().getButtonTypes().addAll(
                okButtonType, cancelButtonType
        );

        getDialogPane().setPrefSize(400, 300);

        boolean disabled = false; // computed based on content of text fields, for example
        getDialogPane().lookupButton(okButtonType).setDisable(disabled);

        textEditPanel = new TextEditPanel(text.getValue());
        getDialogPane().setContent(textEditPanel);
        ViewUtils.applyAppStylesheet(getDialogPane().getStylesheets());

        textEditPanel.textArea.addEventFilter(KeyEvent.KEY_PRESSED, (keyEvent) -> {
            if (!keyEvent.isShiftDown() && keyEvent.getCode().equals(KeyCode.ENTER)) {
                //LOGGER.log(Level.SEVERE, "Enter Pressed.");
                setResult(okButtonType);
                close();
            }
        });
        showAndWait().ifPresent(response -> {
            if (response == okButtonType) {
                textElement.setValue(textEditPanel.getValue());
                LOGGER.log(Level.SEVERE, "OK pressed.");
            } else {
                LOGGER.log(Level.SEVERE, "Cancel. Nothing changes.");
            }
        });

    }
}
