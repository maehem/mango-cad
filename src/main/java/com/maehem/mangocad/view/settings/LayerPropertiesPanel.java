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
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.FillStyle;
import java.util.logging.Level;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LayerPropertiesPanel extends VBox {

    private static final double SIZE = 16;

    public LayerPropertiesPanel(LayerElement layerElement, ColorPalette palette) {

        Label numberValueLabel = new Label(String.valueOf(layerElement.getNumber()));

        Label numberLabel = new Label("Number");
        Label nameLabel = new Label("Name");
        Label colorLabel = new Label("Color");
        Label highlightLabel = new Label("Highlight");
        Label fillstyleLabel = new Label("Fillstyle");

        Button colorValueButton = new Button("", FillStyle.getSwatch(
                1,
                ColorUtils.getColor(palette.getHex(
                        layerElement.getColorIndex()
                )), SIZE
        ));
        Button highlightValueButton = new Button("", FillStyle.getSwatch(
                1,
                ColorUtils.getColor(palette.getHex(
                        layerElement.getColorIndex()
                )).brighter(), SIZE // TODO: Proper highlight color data.
        ));
        Button fillstyleValueButton = new Button("", FillStyle.getSwatch(
                layerElement.getFill(),
                Color.LIGHTGRAY, // TODO: Get from CSS style sheet.
                SIZE
        ));
        FillStyleChooser fillStyleChooser = new FillStyleChooser(layerElement.getFill(), 16);

        TextField nameTextField = new TextField(layerElement.getName());
        // TODO Set non-editable if layer
        nameTextField.setEditable(layerElement.isAllowDelete());

        GridPane gpArea = new GridPane();
        gpArea.setHgap(12);
        gpArea.setVgap(12);
        //gpArea.setGridLinesVisible(true);
        gpArea.add(numberLabel, 0, 0);
        gpArea.add(nameLabel, 0, 1);
        gpArea.add(colorLabel, 0, 2);
        gpArea.add(highlightLabel, 0, 3);
        gpArea.add(fillstyleLabel, 0, 4);

        gpArea.add(numberValueLabel, 1, 0);
        gpArea.add(nameTextField, 1, 1);
        gpArea.add(colorValueButton, 1, 2);
        gpArea.add(highlightValueButton, 1, 3);
        gpArea.add(fillStyleChooser, 1, 4);

        GridPane.setHalignment(numberLabel, HPos.RIGHT);
        GridPane.setHalignment(nameLabel, HPos.RIGHT);
        GridPane.setHalignment(colorLabel, HPos.RIGHT);
        GridPane.setHalignment(highlightLabel, HPos.RIGHT);
        GridPane.setHalignment(fillstyleLabel, HPos.RIGHT);

        gpArea.setPadding(new Insets(0, 10, 4, 10));
        getChildren().add(gpArea);

    }

    protected void doSave() {
        LOGGER.log(Level.SEVERE, "Layer Properties Panel: Do save called.");
    }

}
