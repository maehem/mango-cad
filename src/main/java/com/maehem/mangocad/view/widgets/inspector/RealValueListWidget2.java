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
import com.maehem.mangocad.model.element.property.RealValue;
import com.maehem.mangocad.model.element.ElementField;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.utils.Dialogs;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.MissingResourceException;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;

/**
 * Settings for real(Double) type element properties.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RealValueListWidget2 extends InspectorWidget implements ElementValueListener {

    //private final double PREF_WIDTH = 160;
    private final ObservableList<Double> options;
    private final ComboBox<Double> comboBox;
    private final RealValue realValue;
    private final ElementField field;
    private final String unitDisplay;

    public RealValueListWidget2(RealValue rv, ElementField f,
            String msgKeyBase, String unit,
            ObservableList<Double> options) {
        this(rv, f, msgKeyBase, unit, false, null, 1.0, options);
    }

    public RealValueListWidget2(RealValue rv, ElementField f,
            String msgKeyBase, String unit,
            boolean allowEdit,
            RealValue autoValue, double multiplier,
            ObservableList<Double> options) {
        super(msgKeyBase);
        this.realValue = rv;
        this.field = f;
        this.options = options;
        comboBox = new ComboBox<>(options);
        this.unitDisplay = unit;

        //setPrefWidth(PREF_WIDTH);
        //setSpacing(4);
        // TODO: Icon as Label
        //String labelStr = "";
        Tooltip tt = new Tooltip();
        comboBox.setTooltip(tt);

        if (msgKeyBase != null) {
//            try {
//                labelStr = MSG.getString(msgKeyBase + "_LABEL");
//            } catch (MissingResourceException ex) {
//                labelStr = "???";
//                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_LABEL", msgKeyBase);
//            }
//
            // Set the tooltip
            try {
                String string = MSG.getString(msgKeyBase + "_TOOLTIP");
                tt.setText(string);
            } catch (MissingResourceException ex) {
                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_TOOLTIP", msgKeyBase);
                // tt can remain blank.
            }
        }
//        if (!labelStr.isEmpty()) {
//            labelStr += ":";
//        }

//        Label iconLabel; // TODO: try as Label again and set tooltip.
//        iconLabel = new Label(labelStr);
//        iconLabel.setId("widget-label");
//        getChildren().add(iconLabel);

        Platform.runLater(() -> {
            realValue.addListener(this);
        });

        comboBox.setButtonCell(new TextFieldCell<>(multiplier, autoValue != null));
        comboBox.setCellFactory(new ListCellFactory(multiplier, autoValue != null));
        comboBox.setEditable(false);
        getChildren().add(comboBox);

        if (unitDisplay != null && !unitDisplay.isEmpty()) {
            Label unitLabel = new Label(unitDisplay); // TODO: try as Label again and set tooltip.
            getChildren().add(unitLabel);
        }

        // Set the element value to the selected comboBox item.
        comboBox.setOnAction((event) -> {

            Object selectedItem = comboBox.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof Double) {  // It came from the list.
                LOGGER.log(Level.SEVERE, "selectedItem: Double: {0}", selectedItem);
                if (autoValue != null && selectedItem == options.get(0)) {
                    realValue.set(autoValue.get());
                    //LOGGER.log(Level.SEVERE, "Set autoValue: " + autoValue.get());
                } else {
                    if (selectedItem == options.get(0)) {
                        comboBox.getSelectionModel().select(1);
                    } else {
                        realValue.set((double) selectedItem);
                        //LOGGER.log(Level.SEVERE, "Set ListValue: " + selectedItem);
                    }
                }
                if (allowEdit) {
                    comboBox.setEditable(false);
                }
            } else { // User typed a new value (string), might be a non-number string.
                LOGGER.log(Level.SEVERE, "selectedItem: String: {0}", selectedItem);
                try {
                    double parseDouble = Double.parseDouble((String) selectedItem);
                    if (!options.contains(parseDouble)) { // Check if it's already in list.
                        if (realValue.isInRange(parseDouble)) {
                            // If not, add it to the list, sort the list
                            options.add(parseDouble);
                            Collections.sort(options);
                        } else {
                            doRangeErrorDialog(parseDouble, multiplier);
                        }
                    }
                    // Select the value.
                    comboBox.getSelectionModel().select(parseDouble); // Select it.
                    realValue.set(parseDouble);
                    if (allowEdit) {
                        comboBox.setEditable(false);
                    }
                } catch (NumberFormatException ex) {
                    // If not a number, show error dialog.
                    doRangeErrorDialog(selectedItem, multiplier);
                }
            }
        });
        comboBox.setOnMouseClicked((t) -> {
            if (allowEdit) {
                comboBox.setEditable(true);
            }
        });

        comboBox.getSelectionModel().clearAndSelect(0);
    }

    private void doRangeErrorDialog(Object item, double multiplier) {
        String errorHeader = MSG.getString("REAL_VALUE_ERROR_HEADER");
        String errorMsg = MessageFormat.format(
                MSG.getString("REAL_VALUE_ERROR_RANGE"),
                field.fName(), item,
                realValue.getMin() * multiplier, realValue.getMax() * multiplier);
        Dialogs.errorDialog(errorHeader, errorMsg).show();
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

    private class TextFieldCell<Double> extends TextFieldListCell<Double> {

        private final double multiplier;
        private final boolean hasAuto;

        public TextFieldCell(double multiplier, boolean hasAuto) {
            this.multiplier = multiplier;
            this.hasAuto = hasAuto;
        }

        @Override
        public void updateItem(Double value, boolean empty) {
            super.updateItem(value, empty);
            //LOGGER.log(Level.SEVERE, "TextField updateItem():{0} {1}", new Object[]{value, (empty ? "empty" : "...")});
            if (empty || value == null) {
                setText(null);
            } else if (hasAuto && value.equals(-1.0)) {
                setText("Auto");
            } else {
                if (value.equals(-1.0)) {
                    setText(null);
                } else {
                    double v = (double) value;
                    setText(String.valueOf(v * multiplier));
                }
            }
        }
    }

    private class ListCellFactory implements Callback<ListView<Double>, ListCell<Double>> {

        private final boolean hasAuto;
        private final double multiplier;

        public ListCellFactory(double multiplier, boolean hasAuto) {
            super();
            this.multiplier = multiplier;
            this.hasAuto = hasAuto;
        }

        @Override
        public ListCell<Double> call(ListView<Double> param) {
            return new ListCell<>() {
                @Override
                public void updateItem(Double value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty || value == null) {
                        setText(null);
                    } else if (hasAuto && value == -1.0) {
                        setText("Auto");
                    } else {
                        if (value.equals(-1.0)) {
                            setText(null);
                        } else {
                            setText(String.valueOf(value * multiplier));
                        }
                    }
                }
            };
        }
    }
}
