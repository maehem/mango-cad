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
import com.maehem.mangocad.model.FieldWidth;
import com.maehem.mangocad.model.element.enums.WireField;
import com.maehem.mangocad.model.element.misc.WireWidthDefaults;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LineWidthWidget extends ToolModeWidget {

    private final ObservableList<Double> options
            = FXCollections.observableArrayList(
                    WireWidthDefaults.values()
            );
    @SuppressWarnings("unchecked")
    private final ComboBox<Double> comboBox = new ComboBox(options);
    private final Element element;
    private final FieldWidth widthElement;

    @SuppressWarnings({"unchecked"})
    public LineWidthWidget(Element e) {
        if (e instanceof FieldWidth fw) {
            this.element = e;
            this.widthElement = fw;
            this.element.addListener(this);
        } else {
            this.element = null;
            this.widthElement = null;
            if (e != null) {
                LOGGER.log(Level.SEVERE, "LineWidthWidget: element is not of type Wire or Circle! type: {0}", e.getElementName());
            } else {
                LOGGER.log(Level.SEVERE, "LineWidthWidget: Element is null.");
            }
        }

        setPrefWidth(170);
        Label iconLabel = new Label(MSG.getString("LINE_WIDTH") + ":");
        iconLabel.setPadding(new Insets(4));
        iconLabel.setAlignment(Pos.BASELINE_CENTER);
        double labelWidth = 55;
        iconLabel.setMinWidth(labelWidth);
        iconLabel.setPrefWidth(labelWidth);

        comboBox.setButtonCell(new EditableItemCell());
        comboBox.setEditable(true);
        updateComboState(widthElement.getWidth());

        getChildren().addAll(iconLabel, comboBox);

        comboBox.setOnAction((t) -> {
            Object selectedItem = comboBox.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof Double d) {
                widthElement.setWidth(d);
            } else if (selectedItem instanceof String s) {
                widthElement.setWidth(Double.parseDouble(s));
            }
            t.consume();
        });
    }

    private void updateComboState(double pl) {
        for (Double t : options) {
            if (t == pl) {
                comboBox.getSelectionModel().select(t);
                return;
            }
        }
        // Value not in the exsisting list, add it.
        options.add(pl);
        comboBox.getSelectionModel().select(pl);
    }

    @Override
    public void stopListening() {
        if (element != null) {
            element.removeListener(this);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(WireField.WIDTH)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "LineWidthWidget: Wire width: ==> {0}", newVal.toString());

        if (newVal instanceof Double pd) {
            updateComboState(pd);
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
                    LOGGER.log(Level.SEVERE, "LineWidthWidget: Enter key in combobox.");
                    String typedValue = textField.getText();
                    Double dValue = Double.valueOf(typedValue);
                    if (!options.contains(dValue)) {
                        options.add(dValue);
                    }
                    comboBox.getSelectionModel().select(dValue);
                    widthElement.setWidth(dValue);
                }
            });
            textField.setOnAction(e -> {
                LOGGER.log(Level.SEVERE, "LineWidthWidget: TextField action.");
                //getItem().setName(textField.getText());
                String typedValue = textField.getText();
                Double dValue;
                try {
                    dValue = Double.valueOf(typedValue);
                } catch (NumberFormatException ex) {
                    dValue = previousValue;
                }
                widthElement.setWidth(dValue);
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
