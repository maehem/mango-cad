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
import com.maehem.mangocad.model.element.basic.ElementText;
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
public class TextRatioWidget extends InspectorWidget {

    // TODO:  Add Cell Renderer to add % sign.
    private final ObservableList<Integer> options
            = FXCollections.observableArrayList(
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                    30, 31
            );
    private final ComboBox<Integer> comboBox = new ComboBox<>(options);
    private final ElementText text;

    public TextRatioWidget(Element e, String msgKeyBase) {
        super(msgKeyBase);
        if (e instanceof ElementText p) {
            this.text = p;
            this.text.addListener(this);
        } else {
            this.text = null;
            LOGGER.log(Level.SEVERE, "TextRatioWidget: element is not of type Text!");
        }

//        Label iconLabel = new Label(MSG.getString("TEXT_RATIO"));
//        iconLabel.setPadding(new Insets(4));
//        iconLabel.setAlignment(Pos.BASELINE_CENTER);

        updateComboState(text.getRatio());

        getChildren().addAll(comboBox);

        comboBox.setOnAction((t) -> {
            text.setRatio((int) comboBox.getSelectionModel().getSelectedItem());
            t.consume();
        });
    }

    private void updateComboState(int pl) {
        for (Integer t : options) {
            if (t == pl) {
                comboBox.getSelectionModel().select(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        if (text != null) {
            text.removeListener(this);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(ElementText.Field.RATIO)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "TextRatioWidget: Text ratio: ==> {0}", newVal.toString());

        if (newVal instanceof Integer pd) {
            updateComboState(pd);
        }
    }

}
