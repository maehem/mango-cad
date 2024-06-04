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
import com.maehem.mangocad.model.ElementValue;
import com.maehem.mangocad.model.ElementValueListener;
import com.maehem.mangocad.model.RealValue;
import com.maehem.mangocad.model.element.ElementField;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.utils.Dialogs;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.MissingResourceException;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.text.Text;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class EditableDoubleListWidget extends ToolModeWidget implements ElementValueListener {

    private final double MIN_WIDTH = 40;
    private final ObservableList<Double> options;
    private final ComboBox<Double> comboBox;
    private final RealValue realValue;
    private final ElementField field;
    private final String unitDisplay;

    public EditableDoubleListWidget(RealValue rv, ElementField f, String msgKeyBase, String unit, ObservableList<Double> options) {
        this.realValue = rv;
        this.field = f;
        this.options = options;
        comboBox = new ComboBox<>(options);
        this.unitDisplay = unit;

        setMinWidth(MIN_WIDTH);
        setPrefWidth(MIN_WIDTH);
        setSpacing(4);
        // TODO: Icon as Label
        String labelStr = "";
        Tooltip tt = new Tooltip();
        if (msgKeyBase != null) {
            try {
                labelStr = MSG.getString(msgKeyBase + "_LABEL");
            } catch (MissingResourceException ex) {
                labelStr = "???";
                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_LABEL", msgKeyBase);
            }

            // Set the tooltip
            try {
                String string = MSG.getString(msgKeyBase + "_TOOLTIP");
                tt.setText(string);
            } catch (MissingResourceException ex) {
                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_TOOLTIP", msgKeyBase);
                // tt can remain blank.
            }
        }
        if (unitDisplay != null && !unitDisplay.isEmpty()) {
            labelStr += " (" + unitDisplay + ")";
        }
        if (!labelStr.isEmpty()) {
            labelStr += ":";
        }

        Text iconLabel;
        iconLabel = new Text(labelStr);
        iconLabel.setId("widget-label");
        getChildren().add(iconLabel);

        realValue.addListener(this);
        comboBox.setButtonCell(new TextFieldListCell<>());
        comboBox.setEditable(true);
        comboBox.getSelectionModel().selectFirst();
        getChildren().add(comboBox);

        // Set the element value to the selected comboBox item.
        comboBox.setOnAction((event) -> {

            Object selectedItem = comboBox.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof Double) {  // It came from the list.
                realValue.set((double) selectedItem);
                comboBox.setEditable(false);
            } else { // User typed a new value (string), might be a non-number string.
                try {
                    double parseDouble = Double.parseDouble((String) selectedItem);
                    if (!options.contains(parseDouble)) { // Check if it's already in list.
                        // If not, add it to the list, sort the list
                        options.add(parseDouble);
                        Collections.sort(options);
                    }
                    // Select the value.
                    comboBox.getSelectionModel().select(parseDouble); // Select it.
                    realValue.set(parseDouble);
                    comboBox.setEditable(false);
                } catch (NumberFormatException ex) {
                    // If not a number, show error dialog.
                    String errorHeader = MSG.getString("REAL_VALUE_ERROR_HEADER");
                    String errorMsg = MessageFormat.format(
                            MSG.getString("REAL_VALUE_ERROR_RANGE"),
                            field.fName(), selectedItem,
                            realValue.getMin(), realValue.getMax());
                    Dialogs.errorDialog(errorHeader, errorMsg).show();
                }
            }
        });
        comboBox.setOnMouseClicked((t) -> {
            comboBox.setEditable(true);
        });

    }

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
        realValue.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
//        switch (field) {
//            case ElementText.Field.SIZE, Dimension.Field.TEXTSIZE -> {
//                if (newVal != null) {
//                    LOGGER.log(Level.SEVERE, "TextSizeWidget: Text size: ==> {0}", newVal.toString());
//
//                    if (newVal instanceof Double pd) {
//                        updateComboState(pd);
//                    }
//                }
//            }
//            default -> {
//            }
//        }
    }

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(realValue)) {
            updateComboState(realValue.get());
        }
    }

}
