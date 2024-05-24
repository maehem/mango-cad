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
import com.maehem.mangocad.model.element.enums.PinField;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinSwapLevelWidget extends ToolModeWidget {

    @SuppressWarnings("unchecked")
    private final Spinner spinner = new Spinner(0, 255, 0);
    private final Pin pin;

    @SuppressWarnings({"unchecked", "unchecked"})
    public PinSwapLevelWidget(Element e) {
        if (e instanceof Pin p) {
            this.pin = p;
            this.pin.addListener(this);
        } else {
            this.pin = null;
            LOGGER.log(Level.SEVERE, "PinSwapLevelWidget: element is not of type Pin!");
        }

        spinner.setPrefWidth(70);
        Label iconLabel = new Label(MSG.getString("PIN_SWAP_LEVEL"));
        iconLabel.setPadding(new Insets(4));
        iconLabel.setAlignment(Pos.BASELINE_CENTER);

        updateSpinnerState(0);

        getChildren().addAll(iconLabel, spinner);

        spinner.valueProperty().addListener((obsVal, oldValue, newValue) -> {
            pin.setSwapLevel((int) newValue);
        });

    }

    @SuppressWarnings("unchecked")
    private void updateSpinnerState(int pl) {
        spinner.getValueFactory().setValue(pl);
    }

    @Override
    public void stopListening() {
        pin.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(PinField.SWAPLEVEL)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "PinSwapLevelWidget: Pin swap: ==> {0}", newVal.toString());

        if (newVal instanceof Integer pd) {
            updateSpinnerState(pd);
        }
    }

}
