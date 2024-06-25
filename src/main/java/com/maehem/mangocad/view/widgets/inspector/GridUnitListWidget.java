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
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.UnitProperty;
import com.maehem.mangocad.model.element.property.UnitValue;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.MissingResourceException;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class GridUnitListWidget extends InspectorWidget implements ElementValueListener {

    private final ObservableList<UnitProperty.Unit> options
            = FXCollections.observableArrayList(
                    UnitProperty.Unit.values()
            );

    private final ComboBox<UnitProperty.Unit> comboBox = new ComboBox<>(options);
    private final UnitValue unit;
    private final ElementField field;

    public GridUnitListWidget(UnitValue e, String msgKeyBase, ElementField f) {
        super(msgKeyBase);

//        if (e instanceof UnitProperty w) {
        this.unit = e;
            this.unit.addListener(this); // platform.runLater()???
//        }// else {
//            this.unit = null;
//            LOGGER.log(Level.SEVERE, "GridUnitListWidget: element is not of type Unit!");
//        }
        this.field = f;

        Tooltip tt = new Tooltip();
        comboBox.setTooltip(tt);

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

        updateComboState(unit.get());

        getChildren().addAll(comboBox);

        comboBox.setOnAction((t) -> {
            unit.set(comboBox.getSelectionModel().getSelectedItem());
            t.consume();
        });
    }

    private void updateComboState(UnitProperty.Unit unitToSet) {
        for (UnitProperty.Unit unitOption : options) {
            if (unitOption.equals(unitToSet)) {
                comboBox.getSelectionModel().select(unitOption);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        if (unit != null) {
            unit.removeListener(this);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(this.field)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "GridUnitListWidget: Unit: ==> {0}", newVal.toString());

        if (newVal instanceof UnitValue pd) {
            updateComboState(pd.get());
        }
    }

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal instanceof UnitValue uv) {
            updateComboState(uv.get());
        }
    }

}
