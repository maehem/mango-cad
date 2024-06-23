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
package com.maehem.mangocad.view.widgets.inspector;

import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementText;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TextSizeWidget extends InspectorWidget {

//    private final ObservableList<Double> options
//            = FXCollections.observableArrayList(
//                    0.01,
//                    0.012,
//                    0.016,
//                    0.024,
//                    0.032,
//                    0.04,
//                    0.05,
//                    0.056,
//                    0.066,
//                    0.07,
//                    0.08,
//                    0.09,
//                    1.0
//            );
    private final ObservableList<Double> options = FXCollections.observableArrayList(
            ElementText.SIZE_DEFAULT_OPTIONS);
    private final ComboBox<Double> comboBox = new ComboBox<>(options);
    private final ElementText text;
    private final Element element;
    private final Dimension dimension;

    public TextSizeWidget(Element e, String msgKeyBase) {
        super(msgKeyBase);
        comboBox.setButtonCell(new EditableItemCell<>());
        comboBox.setEditable(true);
        if (e instanceof ElementText p) {
            this.text = p;
            this.element = e;
            this.dimension = null;
            this.text.addListener(this);
            updateComboState(text.getSize());
        } else if (e instanceof Dimension d) {
            this.element = e;
            this.dimension = d;
            this.text = null;
            updateComboState(dimension.getTextsize());
        } else {
            this.element = null;
            this.text = null;
            this.dimension = null;
            LOGGER.log(Level.SEVERE, "TextSizeWidget: element is not of type ElementText!");
        }

//        setPrefWidth(170);
//        Label iconLabel = new Label(MSG.getString("TEXT_SIZE") + ":");
//        iconLabel.setPadding(new Insets(4));
//        iconLabel.setAlignment(Pos.BASELINE_CENTER);
//        double labelWidth = 55;
//        iconLabel.setMinWidth(labelWidth);
//        iconLabel.setPrefWidth(labelWidth);


        getChildren().addAll(comboBox);

        comboBox.setOnAction((t) -> {
            if (text != null) {
                //text.setSize(Double.parseDouble((String) comboBox.getSelectionModel().getSelectedItem()));
                Object selectedItem = comboBox.getSelectionModel().getSelectedItem();
                if (selectedItem instanceof String string) {
                    text.setSize(Double.parseDouble(string));
                } else {
                    text.setSize((Double) selectedItem);
                }
            } else if (dimension != null) {
                //dimension.setTextsize(Double.parseDouble((String) comboBox.getSelectionModel().getSelectedItem()));
                //dimension.setTextsize(comboBox.getSelectionModel().getSelectedItem());
                Object selectedItem = comboBox.getSelectionModel().getSelectedItem();
                if (selectedItem instanceof String string) {
                    dimension.setTextsize(Double.parseDouble(string));
                } else {
                    dimension.setTextsize((Double) selectedItem);

                }
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
        // value was not in list. Add and select.
        options.add(pl);

        comboBox.getSelectionModel().select(pl);
    }

    @Override
    public void stopListening() {
        element.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        switch (field) {
            case ElementText.Field.SIZE, Dimension.Field.TEXTSIZE -> {
                if (newVal != null) {
                    LOGGER.log(Level.SEVERE, "TextSizeWidget: Text size: ==> {0}", newVal.toString());

                    if (newVal instanceof Double pd) {
                        updateComboState(pd);
                    }
                }
            }
            default -> {
            }
        }
    }

    public class EditableItemCell<Double> extends ListCell<Double> {

        private final TextField textField = new TextField();

        public EditableItemCell() {
            textField.setPrefWidth(100);
            textField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
            textField.setOnAction(e -> {
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
