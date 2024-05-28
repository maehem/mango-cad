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
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.enums.WireField;
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
                    0.01,
                    0.02,
                    0.04,
                    0.08,
                    1.0
            );
    @SuppressWarnings("unchecked")
    private final ComboBox comboBox = new ComboBox(options);
    private final Wire wire;

    @SuppressWarnings({"unchecked"})
    public LineWidthWidget(Element e) {
        if (e instanceof Wire p) {
            this.wire = p;
            this.wire.addListener(this);
        } else {
            this.wire = null;
            LOGGER.log(Level.SEVERE, "LineWidthWidget: element is not of type Wire! type: {0}", e.getElementName());
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
        comboBox.getSelectionModel().selectFirst();

        getChildren().addAll(iconLabel, comboBox);

        comboBox.setOnAction((t) -> {
            wire.setWidth((double) comboBox.getSelectionModel().getSelectedItem());
            t.consume();
        });
    }

    @SuppressWarnings("unchecked")
    private void updateComboState(double pl) {
        for (Double t : options) {
            if (t == pl) {
                comboBox.getSelectionModel().select(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        if (wire != null) {
            wire.removeListener(this);
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

        public EditableItemCell() {
            textField.setPrefWidth(100);
            textField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
            textField.setOnAction(e -> {
                //getItem().setName(textField.getText());
                setText(textField.getText());
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
