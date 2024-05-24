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
package com.maehem.mangocad.view.widgets.toolmode;

import com.maehem.mangocad.view.ViewUtils;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RotationWidget extends HBox {

    private final ResourceBundle MSG; // Must be set in constructor or after.
    private static final String ICON_PATH = "/icons/rotate.png";

    private final ObservableList<String> options
            = FXCollections.observableArrayList(
                    "0",
                    "90",
                    "180",
                    "270"
            );
    private final ComboBox comboBox = new ComboBox(options);

    public RotationWidget() {

        MSG = ResourceBundle.getBundle("i18n/Editor");

        Image img = ViewUtils.getImage(ICON_PATH);
        ImageView icon = ViewUtils.createIcon(img, 20);
        Label iconLabel = new Label("", icon);
        iconLabel.setPadding(new Insets(4));
        iconLabel.setAlignment(Pos.BASELINE_CENTER);
        iconLabel.setTooltip(new Tooltip(MSG.getString("TOOL_ICON_ROTATE")));

        comboBox.getSelectionModel().selectFirst();
        getChildren().addAll(iconLabel, comboBox);

    }

}
