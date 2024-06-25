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
import com.maehem.mangocad.model.element.property.CoordinateValue;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.RealValue;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.MissingResourceException;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

/**
 * Settings for real(Double) type element properties.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class CoordinateWidget extends InspectorWidget implements ElementValueListener {

    private final RealValue xValue;
    private final RealValue yValue;

    private final TextField xField = new TextField();
    private final TextField yField = new TextField();

    public CoordinateWidget(
            CoordinateValue coordinate,
            String msgKeyBase
    ) {
        super(msgKeyBase);
        this.xValue = coordinate.x;
        this.yValue = coordinate.y;

        Label xLabel = new Label("x:");
        xLabel.setId("location-xy-label");
        Label yLabel = new Label("y:");
        yLabel.setId("location-xy-label");
        Pane spacer = new Pane();
        spacer.setPrefSize(8, 8);

        //Font TF_FONT = Font.font(10);
        xField.setText(String.valueOf(xValue.get()));
        xField.setPrefColumnCount(10);
        xField.setMaxWidth(70);
        xField.setId("location-xy-field"); // make the number field a little smeller
        yField.setText(String.valueOf(yValue.get()));
        yField.setPrefColumnCount(11);
        yField.setMaxWidth(70);
        yField.setId("location-xy-field");

        Tooltip ttX = new Tooltip();
        Tooltip ttY = new Tooltip();
        xField.setTooltip(ttX);
        yField.setTooltip(ttY);

        if (msgKeyBase != null) {
            // Set the tooltips
            try {
                String string = MSG.getString(msgKeyBase + "_X_TOOLTIP");
                ttX.setText(string);
            } catch (MissingResourceException ex) { // tt can remain blank.
                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_X_TOOLTIP", msgKeyBase);
            }
            try {
                String string = MSG.getString(msgKeyBase + "_Y_TOOLTIP");
                ttY.setText(string);
            } catch (MissingResourceException ex) { // tt can remain blank.
                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_Y_TOOLTIP", msgKeyBase);
            }
        }

        getChildren().addAll(xLabel, xField, spacer, yLabel, yField);

        Platform.runLater(() -> {
            xValue.addListener(this);
            yValue.addListener(this);
        });

        xField.setOnAction((t) -> {
            LOGGER.log(Level.SEVERE, "X Textfield action.");
            try {
                Double value = Double.valueOf(xField.getText());
                // TODO Range checking.
                xValue.set(value);
            } catch ( NumberFormatException ex ) {
                // Make the textfield bg red.
            }
        });
        yField.setOnAction((t) -> {
            LOGGER.log(Level.SEVERE, "Y Textfield action.");
            try {
                Double value = Double.valueOf(yField.getText());
                // TODO: Range Checking
                yValue.set(value);
            } catch (NumberFormatException ex) {
                // Make the textfield bg red.
            }
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
        xValue.removeListener(this);
        yValue.removeListener(this);
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
        if (newVal.equals(xValue)) {
            xField.setText(String.valueOf(xValue.get()));
        } else if (newVal.equals(yValue)) {
            yField.setText(String.valueOf(yValue.get()));
        }
    }

}
