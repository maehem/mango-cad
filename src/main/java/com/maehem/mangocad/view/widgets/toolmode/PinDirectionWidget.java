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
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.enums.PinDirection;
import com.maehem.mangocad.model.element.enums.PinField;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinDirectionWidget extends ToolModeWidget {

    private final ObservableList<PinDirection> options = FXCollections.observableArrayList(
            PinDirection.values()
    );

    @SuppressWarnings("unchecked")
    private final ComboBox comboBox = new ComboBox(options);
    private final Pin pin;

    @SuppressWarnings("unchecked")
    public PinDirectionWidget(Element e) {
        if (e instanceof Pin p) {
            this.pin = p;
            this.pin.addListener(this);
        } else {
            this.pin = null;
            LOGGER.log(Level.SEVERE, "PinDirectionWidget: element is not of type Pin!");
        }

        Label iconLabel = new Label(MSG.getString("PIN_DIRECTION"));
        iconLabel.setPadding(new Insets(4));
        iconLabel.setAlignment(Pos.BASELINE_CENTER);

        updateComboState(PinDirection.IO);

        getChildren().addAll(iconLabel, comboBox);

        comboBox.setOnAction((t) -> {
            pin.setDirection(PinDirection.fromCode((String) comboBox.getSelectionModel().getSelectedItem()));
            t.consume();
        });
    }

    @SuppressWarnings("unchecked")
    private void updateComboState(PinDirection pl) {
        for (PinDirection t : options) {
            if (t.equals(pl)) {
                comboBox.getSelectionModel().select(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        pin.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(PinField.DIRECTION)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "PinDirectionWidget: Pin dir: ==> {0}", newVal.toString());

        if (newVal instanceof PinDirection pd) {
            updateComboState(pd);
        }
    }

}
