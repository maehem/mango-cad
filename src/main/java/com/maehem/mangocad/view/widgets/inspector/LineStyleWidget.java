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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.enums.WireStyle;
import com.maehem.mangocad.view.widgets.toolmode.ToolModeWidget;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LineStyleWidget extends ToolModeWidget {

    private final ObservableList<WireStyle> options = FXCollections.observableArrayList(
            WireStyle.values()
    );

    private final ComboBox<WireStyle> comboBox = new ComboBox<>(options);
    private final Wire wire;

    public LineStyleWidget(Element e) {
        super("LINE_STYLE");
        if (e instanceof Wire w) {
            this.wire = w;
            this.wire.addListener(this);
        } else {
            this.wire = null;
            LOGGER.log(Level.SEVERE, "WireStyleWidget: element is not of type Wire!");
        }

        updateComboState(wire.getStyle());

        getChildren().addAll(comboBox);

        comboBox.setOnAction((t) -> {
            wire.setStyle((WireStyle) comboBox.getSelectionModel().getSelectedItem());
            t.consume();
        });
    }

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
