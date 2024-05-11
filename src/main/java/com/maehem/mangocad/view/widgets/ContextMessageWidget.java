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

import com.maehem.mangocad.view.ViewUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Command Entry Widget near top of each Editor
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ContextMessageWidget extends HBox {

    //private final Label label = new Label("Command:");
    private final Label messageLabel = new Label();

    public ContextMessageWidget(String labelText, String iconPath) {

        if (iconPath != null) {
            Image img = ViewUtils.getImage(iconPath);
            ImageView icon = ViewUtils.createIcon(img, 16);
            Label iconLabel = new Label("", icon);
            iconLabel.setTooltip(new Tooltip(labelText));
            getChildren().add(iconLabel);
        }

        messageLabel.setId("editor-options-message-text");

        getChildren().add(messageLabel);
        setSpacing(4);
        setAlignment(Pos.CENTER);

        messageLabel.setPrefWidth(300);
    }

    public void setText(String messageText) {
        messageLabel.setText(messageText);
    }

}
