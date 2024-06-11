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

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TextEditPanel extends VBox {

    protected final TextArea textArea;

    public TextEditPanel(String text) {
        textArea = new TextArea(text);
        Label label = new Label("Shift+Enter to add a new line.");

        textArea.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.isShiftDown() && keyEvent.getCode().equals(KeyCode.ENTER)) {
                textArea.appendText("\n");
            }
        });
        getChildren().addAll(textArea, label);
    }

    public String getValue() {
        return textArea.getText();
    }
}
