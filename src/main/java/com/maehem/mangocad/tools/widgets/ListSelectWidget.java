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
package com.maehem.mangocad.tools.widgets;

import com.maehem.mangocad.model.element.enums.TextAlign;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ListSelectWidget extends Widget {

    private final WidgetListener listener;

    ComboBox<String> alignSelector;

    public ListSelectWidget(String title, WidgetListener listener) {
        super(title);

        this.listener = listener;

        alignSelector = new ComboBox<String>(FXCollections.observableArrayList(TextAlign.asStringList()));
        alignSelector.getSelectionModel().selectFirst();

        VBox.setMargin(alignSelector, new Insets(1, 4, 1, 40));

        getChildren().add(alignSelector);

        alignSelector.getSelectionModel().selectedItemProperty().addListener((o) -> {
            listener.widgetChanged(this);
        });
    }

    public void setValue(TextAlign align) {

    }

    public String getValue() {
        return alignSelector.getSelectionModel().getSelectedItem();
    }

    @Override
    public void updateValue(double val) {
    }

    @Override
    public void updateValue(String val) {
    }

}
