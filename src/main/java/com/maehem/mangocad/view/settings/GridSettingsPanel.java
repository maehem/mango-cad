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

import com.maehem.mangocad.model.element.enums.GridStyle;
import com.maehem.mangocad.model.element.enums.GridUnit;
import com.maehem.mangocad.model.element.misc.Grid;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class GridSettingsPanel extends VBox {

    Grid grid;
    private final TextField sizeTextField;
    private final TextField multipleTextField;
    private final TextField altTextField;
    ComboBox<String> sizeUnitsComboBox;
    ComboBox<String> altUnitsComboBox;
    private final ToggleGroup displayGroup;
    private final ToggleGroup styleGroup;
    private final RadioButton onButton;
    private final RadioButton offButton;
    private final RadioButton dotsButton;
    private final RadioButton linesButton;

    public GridSettingsPanel(Grid gridSettings, boolean useDefaults) {

        this.grid = gridSettings;

        Label displayLabel = new Label("Display");
        Label styleLabel = new Label("Style");
        Label sizeLabel = new Label("Size:");
        Label multipleLabel = new Label("Multiple:");
        Label hintLabel = new Label("Hint:");
        Label altLabel = new Label("Alt:");
        Text hintInfoText = new Text(
                """
                It is highly recommended to use the
                default grid (0.10 inch) in schematics.""");
        hintInfoText.setId("grid-settings");
        onButton = new RadioButton("On");
        offButton = new RadioButton("Off");
        dotsButton = new RadioButton("Dots");
        linesButton = new RadioButton("Lines");

        HBox displayButtonsArea = new HBox(onButton, offButton);
        displayButtonsArea.setSpacing(16);
        displayButtonsArea.setPadding(new Insets(10, 22, 10, 22));
        displayButtonsArea.setBorder(new Border(new BorderStroke(
                Color.GREY, BorderStrokeStyle.SOLID,
                new CornerRadii(4), new BorderWidths(1)
        )));
        VBox displayArea = new VBox(displayLabel, displayButtonsArea);
        displayArea.setFillWidth(true);

        HBox styleButtonsArea = new HBox(dotsButton, linesButton);
        styleButtonsArea.setSpacing(16);
        styleButtonsArea.setPadding(new Insets(10, 22, 10, 22));
        styleButtonsArea.setBorder(new Border(new BorderStroke(
                Color.GREY, BorderStrokeStyle.SOLID,
                new CornerRadii(4), new BorderWidths(1)
        )));
        VBox styleArea = new VBox(styleLabel, styleButtonsArea);
        styleArea.setFillWidth(true);

        Pane spacer = new Pane();
        spacer.setMaxWidth(Double.MAX_VALUE);
        spacer.setMinSize(10, 10);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox radioButtonsBox = new HBox(displayArea, spacer, styleArea);
        HBox.getHgrow(radioButtonsBox);

        displayGroup = new ToggleGroup();
        onButton.setToggleGroup(displayGroup);
        offButton.setToggleGroup(displayGroup);

        styleGroup = new ToggleGroup();
        dotsButton.setToggleGroup(styleGroup);
        dotsButton.setUserData(GridStyle.DOTS);
        linesButton.setToggleGroup(styleGroup);
        linesButton.setUserData(GridStyle.LINES);

        // TODO: Live checking on values typed here.
        sizeTextField = new TextField();
        multipleTextField = new TextField();
        altTextField = new TextField();

        sizeUnitsComboBox = new ComboBox<>(FXCollections.observableArrayList(GridUnit.asCodeList()));
        altUnitsComboBox = new ComboBox<>(FXCollections.observableArrayList(GridUnit.asCodeList()));

        Button sizeFinestButton = new Button("Finest");
        Button altFinestButton = new Button("Finest");

        GridPane gpArea = new GridPane();
        gpArea.setHgap(6);
        gpArea.setVgap(6);
        //gpArea.setGridLinesVisible(true);

        gpArea.add(sizeLabel, 0, 3, 3, 1);
        gpArea.add(sizeTextField, 3, 3, 2, 1);
        gpArea.add(sizeUnitsComboBox, 7, 3, 2, 1);
        gpArea.add(sizeFinestButton, 9, 3, 2, 1);
        gpArea.add(multipleLabel, 0, 4, 3, 1);
        gpArea.add(multipleTextField, 3, 4, 2, 2);
        gpArea.add(hintLabel, 0, 6, 3, 5);
        gpArea.add(hintInfoText, 3, 6, 6, 5);
        gpArea.add(altLabel, 0, 11, 3, 1);
        gpArea.add(altTextField, 3, 11, 2, 1);
        gpArea.add(altUnitsComboBox, 7, 11, 2, 1);
        gpArea.add(altFinestButton, 9, 11, 2, 1);

        GridPane.setHalignment(sizeLabel, HPos.RIGHT);
        GridPane.setHalignment(multipleLabel, HPos.RIGHT);
        GridPane.setHalignment(hintLabel, HPos.RIGHT);
        GridPane.setHalignment(altLabel, HPos.RIGHT);

        GridPane.setValignment(sizeLabel, VPos.CENTER);
        GridPane.setValignment(multipleLabel, VPos.CENTER);
        GridPane.setValignment(hintLabel, VPos.CENTER);
        GridPane.setValignment(altLabel, VPos.CENTER);

        radioButtonsBox.setPadding(new Insets(4, 10, 0, 10));
        gpArea.setPadding(new Insets(0, 10, 4, 10));
        getChildren().addAll(radioButtonsBox, gpArea);

        // Set data state of UI items
        if (!useDefaults) { // Use Drawing's Grid Settings
            displayGroup.selectToggle(grid.isDisplay() ? onButton : offButton);
            //onButton.setSelected(gridSettings.isDisplay());
            //offButton.setSelected(!gridSettings.isDisplay());
            styleGroup.selectToggle(grid.isDots() ? dotsButton : linesButton);
            //dotsButton.setSelected(gridSettings.isDots()); // Should toggle state of other radio button.
            //linesButton.setSelected(!gridSettings.isDots()); // Should toggle state of other radio button.
            sizeTextField.setText(String.valueOf(gridSettings.getSize())); // convert to current units
            sizeUnitsComboBox.getSelectionModel().select(gridSettings.getSizeUnit().code());

            multipleTextField.setText(String.valueOf(gridSettings.getMultiple()));

            altTextField.setText(String.valueOf(gridSettings.getAltSize())); // convert to current units
            altUnitsComboBox.getSelectionModel().select(gridSettings.getAltUnit().code());
        } else {
            Grid gridDefaults = new Grid();

            displayGroup.selectToggle(gridDefaults.isDisplay() ? onButton : offButton);
            //onButton.setSelected(gridDefaults.isDisplay());
            //offButton.setSelected(!gridDefaults.isDisplay());
            styleGroup.selectToggle(gridDefaults.isDots() ? dotsButton : linesButton);
            //dotsButton.setSelected(gridDefaults.isDots()); // Should toggle state of other radio button.
            //linesButton.setSelected(!gridDefaults.isDots()); // Should toggle state of other radio button.
            sizeTextField.setText(String.valueOf(gridDefaults.getSize())); // convert to current units
            sizeUnitsComboBox.getSelectionModel().select(gridDefaults.getSizeUnit().code());

            multipleTextField.setText(String.valueOf(gridDefaults.getMultiple()));

            altTextField.setText(String.valueOf(gridDefaults.getAltSize())); // convert to current units
            altUnitsComboBox.getSelectionModel().select(gridDefaults.getAltUnit().code());
        }
    }

    protected void doSave() {
        LOGGER.log(Level.SEVERE, "Grid Panel: Do save called.");
        grid.setAltSize(Double.parseDouble(altTextField.getText()));
        grid.setSize(Double.parseDouble(sizeTextField.getText()));
        grid.setDisplay(displayGroup.getSelectedToggle().equals(onButton));
        grid.setStyle((GridStyle) styleGroup.getSelectedToggle().getUserData());
        grid.setSizeUnit(GridUnit.valueOf(sizeUnitsComboBox.getSelectionModel().getSelectedItem()));
        grid.setAltStoredUnit(GridUnit.valueOf(altUnitsComboBox.getSelectionModel().getSelectedItem()));
        grid.setMultiple(Integer.parseInt(multipleTextField.getText()));
    }

}
