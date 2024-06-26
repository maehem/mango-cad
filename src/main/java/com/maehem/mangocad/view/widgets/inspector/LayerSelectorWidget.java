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
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.IntValue;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.widgets.LayerChooser;
import java.util.MissingResourceException;
import java.util.logging.Level;
import javafx.scene.control.Tooltip;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LayerSelectorWidget extends InspectorWidget implements ElementValueListener {

    //private final double PREF_WIDTH = 130;
    //private final ObservableList<Integer> options;
    private final LayerChooser comboBox;
    private final IntValue layerNumber;
    //private final ElementField field;
    private final Layers layers;

    public LayerSelectorWidget(Layers layers, IntValue value, ElementField f,
            String msgKeyBase) {
        super(msgKeyBase);
        this.layers = layers;
        this.layerNumber = value;
        //this.field = f;
        //this.options = options;
        comboBox = new LayerChooser(layers);

        updateComboState(layerNumber.get());
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

        layerNumber.addListener(this);

        //comboBox.setButtonCell(new TextFieldListCell<>());
        //comboBox.setEditable(false);
        //comboBox.getSelectionModel().selectFirst();
        getChildren().add(comboBox);

        // Set the element value to the selected comboBox item.
        comboBox.setOnAction((event) -> {

            LayerElement selectedItem = comboBox.getSelectionModel().getSelectedItem();
            layerNumber.set(selectedItem.getNumber());
//            if (selectedItem instanceof Integer) {  // It came from the list.
//                layerNumber.set((int) selectedItem);
//            } else { // User typed a new value (string), might be a non-number string.
//                try {
//                    int parseInteger = Integer.parseInt((String) selectedItem);
//                    if (!options.contains(parseInteger)) { // Check if it's already in list.
//                        if (layerNumber.isInRange(parseInteger)) {
//                            // If not, add it to the list, sort the list
//                            options.add(parseInteger);
//                            Collections.sort(options);
//                        } else {
//                            doRangeErrorDialog(parseInteger);
//                        }
//                    }
//                    // Select the value.
//                    comboBox.getSelectionModel().select(parseInteger); // Select it.
//                    layerNumber.set(parseInteger);
//                } catch (NumberFormatException ex) {
//                    // If not a number, show error dialog.
//                    doRangeErrorDialog(selectedItem);
//                }
//            }
        });

    }

    private void updateComboState(int pl) {
        for (LayerElement t : layers.getElements()) {
            if (t.getNumber() == pl) {
                comboBox.getSelectionModel().select(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        layerNumber.removeListener(this);
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
        if (newVal.equals(layerNumber)) {
            updateComboState(layerNumber.get());
        }
    }

}
