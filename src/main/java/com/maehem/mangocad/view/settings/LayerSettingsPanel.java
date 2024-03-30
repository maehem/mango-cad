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

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.element.drawing.Layers;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.LayerListView;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LayerSettingsPanel extends VBox {

    private static final ResourceBundle MSG = ResourceBundle.getBundle("i18n/Editor");

    public LayerSettingsPanel(ColorPalette palette, Layers layers) {
        Node filtBox = createFilterArea();
        // Layers List Panel
        LayerListView layerListView = new LayerListView(palette, layers);
        VBox.setVgrow(layerListView, Priority.ALWAYS);

        // New, Show, Hide buttons
        Node showHideButtons = createNewShowHideButtons();

        Region spacerPane1 = new Region();
        spacerPane1.setMinSize(2, 2);
        Region spacerPane2 = new Region();
        spacerPane2.setMinSize(2, 2);

        // Layer Sets Combo box,  New Set, Remove Set buttons.
        Node setButtons = createSetsButtons();

        getChildren().addAll(filtBox, layerListView, showHideButtons, spacerPane1, setButtons, spacerPane2);

        setSpacing(8);
    }

    void doSave() {
        LOGGER.log(Level.SEVERE, "Layer Settings: Do save.");
    }

    private Node createFilterArea() {
        Label filterComboLabel = new Label(MSG.getString("LAYER_LIST_FILTER_LABEL"));
        ComboBox<String> filterCombo = new ComboBox<>(
                FXCollections.observableArrayList(
                        MSG.getString("LAYER_LIST_FILTER_ALL"),
                        MSG.getString("LAYER_LIST_FILTER_USED"),
                        MSG.getString("LAYER_LIST_FILTER_UNUSED")
                ));
        HBox filterBox = new HBox(filterComboLabel, filterCombo);

        return filterBox;
    }

    private Node createNewShowHideButtons() {
        Button newLayerButton = new Button(MSG.getString("LAYER_LIST_NEW_BUTTON"));
        Button showLayersButton = new Button(MSG.getString("LAYER_LIST_SHOW_BUTTON"));
        Button hideLayersButton = new Button(MSG.getString("LAYER_LIST_HIDE_BUTTON"));
        Region spacer = new Region();
        spacer.setMinSize(16, 16);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox box = new HBox(newLayerButton, spacer, showLayersButton, hideLayersButton);
        box.setSpacing(8);

        return box;
    }

    private Node createSetsButtons() {
        Button newSetButton = new Button(MSG.getString("LAYER_LIST_NEW_SET_BUTTON"));
        Button removeSetButton = new Button(MSG.getString("LAYER_LIST_REMOVE_SET_BUTTON"));
        Region spacer = new Region();
        spacer.setMinSize(16, 16);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ComboBox<String> layerSetsChooser = new ComboBox<>(
                FXCollections.observableArrayList(
                        MSG.getString("LAYER_SETS_CHOOSER_LABEL"),
                        "Foo",
                        "Bar"
                ));

        HBox box = new HBox(layerSetsChooser, spacer, newSetButton, removeSetButton);
        box.setSpacing(8);

        return box;
    }
}
