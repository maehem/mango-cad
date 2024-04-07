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
package com.maehem.mangocad.view.widgets;

import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Command Entry Widget near top of each Editor
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class CommandFieldWidget extends HBox {

    private final Label label = new Label("Command:");
    private final TextField textField = new TextField();

    public CommandFieldWidget() {
        getChildren().addAll(label, textField);

        setSpacing(4);
        setAlignment(Pos.CENTER);

        textField.setPrefWidth(300);

        textField.setOnAction((t) -> {
            LOGGER.log(Level.SEVERE, "Command Typed: " + textField.getText());

            // TODO: Submit command to control level processor.
            // Errors should post to a log that can be opened via an
            // icon at bottom of window.
            textField.clear();
        });
    }

}
