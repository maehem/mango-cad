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
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.StringValue;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.MissingResourceException;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Settings for real(Double) type element properties.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class StringValueWidget extends InspectorWidget implements ElementValueListener {

    private final StringValue value;

    private final TextField textField = new TextField();

    public StringValueWidget(
            StringValue sValue,
            String msgKeyBase
    ) {
        super(msgKeyBase);
        this.value = sValue;

        textField.setText(sValue.get());

        Tooltip tt = new Tooltip();
        textField.setTooltip(tt);

        if (msgKeyBase != null) {
            // Set the tooltips
            try {
                String string = MSG.getString(msgKeyBase + "_TOOLTIP");
                tt.setText(string);
            } catch (MissingResourceException ex) { // tt can remain blank.
                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_TOOLTIP", msgKeyBase);
            }
        }

        getChildren().addAll(textField);

        textField.setOnAction((t) -> {
            LOGGER.log(Level.SEVERE, "Textfield action.");
            value.set(textField.getText());
        });

        Platform.runLater(() -> {
            value.addListener(this);
        });

    }

//    private void doRangeErrorDialog(Object item, double multiplier) {
//        String errorHeader = MSG.getString("REAL_VALUE_ERROR_HEADER");
//        String errorMsg = MessageFormat.format(
//                MSG.getString("REAL_VALUE_ERROR_RANGE"),
//                field.fName(), item,
//                yValue.getMin() * multiplier, yValue.getMax() * multiplier);
//        Dialogs.errorDialog(errorHeader, errorMsg).show();
//    }
    @Override
    public void stopListening() {
        value.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        LOGGER.log(Level.SEVERE, "Element changed: " + e.toString());
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
        LOGGER.log(Level.SEVERE, "ElementValue changed: " + newVal.toString());
        if (newVal.equals(value)) {
            //updateComboState(yValue.get());
        }
    }

//    private class TextFieldCell<Double> extends TextFieldListCell<Double> {
//
//        private final double multiplier;
//        private final boolean hasAuto;
//
//        public TextFieldCell(double multiplier, boolean hasAuto) {
//            this.multiplier = multiplier;
//            this.hasAuto = hasAuto;
//        }
//
//        @Override
//        public void updateItem(Double value, boolean empty) {
//            super.updateItem(value, empty);
//            //LOGGER.log(Level.SEVERE, "TextField updateItem():{0} {1}", new Object[]{value, (empty ? "empty" : "...")});
//            if (empty || value == null) {
//                setText(null);
//            } else if (hasAuto && value.equals(-1.0)) {
//                setText("Auto");
//            } else {
//                if (value.equals(-1.0)) {
//                    setText(null);
//                } else {
//                    double v = (double) value;
//                    setText(String.valueOf(v * multiplier));
//                }
//            }
//        }
//    }
//
//    private class ListCellFactory implements Callback<ListView<Double>, ListCell<Double>> {
//
//        private final boolean hasAuto;
//        private final double multiplier;
//
//        public ListCellFactory(double multiplier, boolean hasAuto) {
//            super();
//            this.multiplier = multiplier;
//            this.hasAuto = hasAuto;
//        }
//
//        @Override
//        public ListCell<Double> call(ListView<Double> param) {
//            return new ListCell<>() {
//                @Override
//                public void updateItem(Double value, boolean empty) {
//                    super.updateItem(value, empty);
//                    if (empty || value == null) {
//                        setText(null);
//                    } else if (hasAuto && value == -1.0) {
//                        setText("Auto");
//                    } else {
//                        if (value.equals(-1.0)) {
//                            setText(null);
//                        } else {
//                            setText(String.valueOf(value * multiplier));
//                        }
//                    }
//                }
//            };
//        }
//    }
}
