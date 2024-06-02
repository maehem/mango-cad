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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.ElementPolygon;
import com.maehem.mangocad.model.element.enums.ElementPolygonField;
import static com.maehem.mangocad.model.element.enums.ElementPolygonField.SPACING;
import com.maehem.mangocad.model.element.enums.PolygonPour;
import com.maehem.mangocad.model.element.misc.WireWidthDefaults;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PolygonFillWidget extends ToolModeWidget {

    private final double WIDGET_WIDTH = 210;
    private final double LABEL_WIDTH = 60;

    private static final String FILL_HATCH_PATH = "/icons/fill-hatch.png";
    private static final String FILL_SOLID_PATH = "/icons/fill-solid.png";

    private final ObservableList<Double> options
            = FXCollections.observableArrayList(
                    WireWidthDefaults.values()
            );

    private final ToggleGroup group = new ToggleGroup();

    private final Label spacingLabel = new Label(MSG.getString("POLYGON_SPACING") + ":");
    private final ComboBox<Double> spacingComboBox = new ComboBox<>(options);
    private final Element element;
    private final ElementPolygon polygonElement;

    public PolygonFillWidget(Element e) {
        if (e instanceof ElementPolygon p) {
            this.element = e;
            this.polygonElement = p;
            this.element.addListener(this);
        } else {
            this.element = null;
            this.polygonElement = null;
            LOGGER.log(Level.SEVERE, "PolygonFillToggleWidget: element is not of type ElementPolygon!");
        }

        setSpacing(0.0);
        setPrefWidth(WIDGET_WIDTH);

        Image fillHatchImg = ViewUtils.getImage(FILL_HATCH_PATH);
        Image fillSolidImg = ViewUtils.getImage(FILL_SOLID_PATH);
        ImageView fillHatchView = ViewUtils.createIcon(fillHatchImg, ICON_SIZE);
        ImageView fillSolidView = ViewUtils.createIcon(fillSolidImg, ICON_SIZE);

        ToggleButton fillHatchToggle = new ToggleButton("", fillHatchView); // TODO MSG Tooltip
        ToggleButton fillSolidToggle = new ToggleButton("", fillSolidView);

        getChildren().addAll(fillSolidToggle, fillHatchToggle);

        fillHatchToggle.setToggleGroup(group);
        fillHatchToggle.setUserData(PolygonPour.HATCH);

        fillSolidToggle.setToggleGroup(group);
        fillSolidToggle.setUserData(PolygonPour.SOLID);

        spacingLabel.setPadding(new Insets(4));
        spacingLabel.setAlignment(Pos.BASELINE_CENTER);
        spacingLabel.setMinWidth(LABEL_WIDTH);
        spacingLabel.setPrefWidth(LABEL_WIDTH);

        spacingComboBox.setButtonCell(new PolygonFillWidget.EditableItemCell());
        spacingComboBox.setEditable(true);
        updateComboState(polygonElement.getSpacing());

        getChildren().addAll(spacingLabel, spacingComboBox);

        updateToggleState();

        group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) -> {
            if (newToggle == null) {
                LOGGER.log(Level.SEVERE, "Nothing selected. Set target to not-mirrored.");
                group.selectToggle(fillHatchToggle);
            } else {
                LOGGER.log(Level.SEVERE, "Change toggle to:{0}", newToggle.getUserData().toString());
                polygonElement.setPour((PolygonPour) newToggle.getUserData());
            }
        });

        spacingComboBox.setOnAction((t) -> {
            Object selectedItem = spacingComboBox.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof Double d) {
                polygonElement.setSpacing(d);
            } else if (selectedItem instanceof String s) {
                polygonElement.setSpacing(Double.parseDouble(s));
            }
            t.consume();
        });

    }

    private void updateToggleState() {

        if (polygonElement != null) {
            for (Toggle option : group.getToggles()) {
                if ((PolygonPour) option.getUserData() == polygonElement.getPour()) {
                    group.selectToggle(option);
                    if (option.getUserData().equals(PolygonPour.SOLID)) {
                        // Grey out spacing.
                        spacingComboBox.setDisable(true);
                        spacingLabel.setDisable(true);
                    } else {
                        // Enable spacing.
                        spacingComboBox.setDisable(false);
                        spacingLabel.setDisable(false);
                    }
                    break;
                }
            }
        } else {
            group.getToggles().getFirst();
        }
    }

    private void updateComboState(double pl) {
        for (Double t : options) {
            if (t == pl) {
                spacingComboBox.getSelectionModel().select(t);
                return;
            }
        }
        // Value not in the exsisting list, add it.
        options.add(pl);
        spacingComboBox.getSelectionModel().select(pl);
    }

    @Override
    public void stopListening() {
        if (element != null) {
            element.removeListener(this);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        if (!field.equals(ElementPolygonField.POUR)
                && !field.equals(ElementPolygonField.SPACING)) {
            return;
        }
        if (newVal == null) {
            LOGGER.log(Level.SEVERE, "NewVal is null! Don't do anything.");
            return;
        }
        LOGGER.log(Level.SEVERE, "PolygonFillWidget: Element fill: ==> {0}", newVal.toString());

        if (newVal instanceof PolygonPour) {
            updateToggleState();
        } else if (field.equals(SPACING)) {
            updateComboState((double) newVal);
        } else {
            LOGGER.log(Level.SEVERE, "Provded newVal was not a PolygonFill or Spacing!");
        }
    }

    public class EditableItemCell extends ListCell<Double> {

        private final TextField textField = new TextField();
        private double previousValue;

        public EditableItemCell() {
            textField.setPrefWidth(100);
            textField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                } else if (e.getCode() == KeyCode.ENTER) {
                    LOGGER.log(Level.SEVERE, "SpacingWidget: Enter key in combobox.");
                    String typedValue = textField.getText();
                    Double dValue = Double.valueOf(typedValue);
                    if (!options.contains(dValue)) {
                        options.add(dValue);
                    }
                    spacingComboBox.getSelectionModel().select(dValue);
                    polygonElement.setSpacing(dValue);
                }
            });
            textField.setOnAction(e -> {
                LOGGER.log(Level.SEVERE, "SpacingWidget: TextField action.");
                //getItem().setName(textField.getText());
                String typedValue = textField.getText();
                Double dValue;
                try {
                    dValue = Double.valueOf(typedValue);
                } catch (NumberFormatException ex) {
                    dValue = previousValue;
                }
                polygonElement.setSpacing(dValue);
                setText(String.valueOf(dValue));
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            });
            setGraphic(textField);
        }

        @Override
        protected void updateItem(Double client, boolean empty) {
            super.updateItem(client, empty);
            if (isEditing()) {
                textField.setText(String.valueOf(client));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(client));
                }
            }
        }

    }
}
