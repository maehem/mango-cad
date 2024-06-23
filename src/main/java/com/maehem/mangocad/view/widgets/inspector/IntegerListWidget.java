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
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.property.IntValue;
import com.maehem.mangocad.model.element.ElementField;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.utils.Dialogs;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.MissingResourceException;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class IntegerListWidget extends InspectorWidget implements ElementValueListener {

    //private final double PREF_WIDTH = 130;
    private final ObservableList<Integer> options;
    private final ComboBox<Integer> comboBox;
    private final IntValue intValue;
    private final ElementField field;
    private final String unitDisplay;
    //private final boolean allowEdit;

    public IntegerListWidget(IntValue rv, ElementField f,
            String msgKeyBase, String unit,
            ObservableList<Integer> options) {
        this(rv, f, msgKeyBase, unit, false, options);
    }

    public IntegerListWidget(IntValue rv, ElementField f,
            String msgKeyBase, String unit, boolean allowEdit,
            ObservableList<Integer> options) {
        super(msgKeyBase);
        this.intValue = rv;
        this.field = f;
        this.options = options;
        comboBox = new ComboBox<>(options);
        this.unitDisplay = unit;
        //this.allowEdit = allowEdit;

        //setPrefWidth(PREF_WIDTH);
        //setSpacing(4);
        //setPadding(new Insets(0, 0, 0, 4));
        // TODO: Icon as Label
        Tooltip tt = new Tooltip();
        if (msgKeyBase != null) {
            // Set the tooltip
            try {
                String string = MSG.getString(msgKeyBase + "_TOOLTIP");
                tt.setText(string);
            } catch (MissingResourceException ex) {
                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_TOOLTIP", msgKeyBase);
                // tt can remain blank.
            }
        }

        intValue.addListener(this);
        comboBox.setButtonCell(new TextFieldListCell<>());
        comboBox.setEditable(allowEdit);
        comboBox.getSelectionModel().selectFirst();
        getChildren().add(comboBox);

        if (unitDisplay != null && !unitDisplay.isEmpty()) {
            Label unitLabel;
            unitLabel = new Label(unitDisplay);
            unitLabel.setId("widget-label");
            getChildren().add(unitLabel);
        }

        // Set the element value to the selected comboBox item.
        comboBox.setOnAction((event) -> {

            Object selectedItem = comboBox.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof Integer) {  // It came from the list.
                intValue.set((int) selectedItem);
                if (allowEdit) {
                    comboBox.setEditable(false);
                }
            } else { // User typed a new value (string), might be a non-number string.
                try {
                    int parseInteger = Integer.parseInt((String) selectedItem);
                    if (!options.contains(parseInteger)) { // Check if it's already in list.
                        if (intValue.isInRange(parseInteger)) {
                            // If not, add it to the list, sort the list
                            options.add(parseInteger);
                            Collections.sort(options);
                        } else {
                            doRangeErrorDialog(parseInteger);
                        }
                    }
                    // Select the value.
                    comboBox.getSelectionModel().select(parseInteger); // Select it.
                    intValue.set(parseInteger);
                    if (allowEdit) {
                        comboBox.setEditable(false);
                    }
                } catch (NumberFormatException ex) {
                    // If not a number, show error dialog.
                    doRangeErrorDialog(selectedItem);
                }
            }
        });
        comboBox.setOnMouseClicked((t) -> {
            if (allowEdit) {
                comboBox.setEditable(true);
            }
        });

    }

    private void doRangeErrorDialog(Object selectedItem) {
        String errorHeader = MSG.getString("INT_VALUE_ERROR_HEADER");
        String errorMsg = MessageFormat.format(
                MSG.getString("INT_VALUE_ERROR_RANGE"),
                field.fName(), selectedItem,
                intValue.getMin(), intValue.getMax());
        Dialogs.errorDialog(errorHeader, errorMsg).show();
    }

    private void updateComboState(double pl) {
        for (Integer t : options) {
            if (t == pl) {
                comboBox.getSelectionModel().select(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        intValue.removeListener(this);
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
        if (newVal.equals(intValue)) {
            updateComboState(intValue.get());
        }
    }

}
