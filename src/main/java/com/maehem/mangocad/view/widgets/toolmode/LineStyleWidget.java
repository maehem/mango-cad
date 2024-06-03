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
import com.maehem.mangocad.model.element.enums.WireStyle;
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
public class LineStyleWidget extends ToolModeWidget {

    private final ObservableList<WireStyle> options = FXCollections.observableArrayList(
            WireStyle.values()
    );

    @SuppressWarnings("unchecked")
    private final ComboBox comboBox = new ComboBox(options);
    private final Wire wire;

    @SuppressWarnings("unchecked")
    public LineStyleWidget(Element e) {
        if (e instanceof Wire w) {
            this.wire = w;
            this.wire.addListener(this);
        } else {
            this.wire = null;
            LOGGER.log(Level.SEVERE, "WireStyleWidget: element is not of type Wire!");
        }

        Label iconLabel = new Label(MSG.getString("LINE_STYLE") + ":");
        iconLabel.setPadding(new Insets(4));
        iconLabel.setAlignment(Pos.BASELINE_CENTER);

        updateComboState(wire.getStyle());

        getChildren().addAll(iconLabel, comboBox);

        comboBox.setOnAction((t) -> {
            wire.setStyle((WireStyle) comboBox.getSelectionModel().getSelectedItem());
            t.consume();
        });
    }

    @SuppressWarnings({"unchecked", "unchecked"})
    private void updateComboState(WireStyle pl) {
        for (WireStyle t : options) {
            if (t.equals(pl)) {
                comboBox.getSelectionModel().select(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        wire.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(Wire.Field.STYLE)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "WireStyleWidget: Wire style: ==> {0}", newVal.toString());

        if (newVal instanceof WireStyle pd) {
            updateComboState(pd);
        }
    }

}
