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
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.enums.TextAlign;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TextAlignWidget extends InspectorWidget {

    private final ObservableList<TextAlign> options = FXCollections.observableArrayList(
            TextAlign.values()
    );

    private final ComboBox<TextAlign> comboBox = new ComboBox<>(options);
    private final ElementText text;

    public TextAlignWidget(Element e, String msgKeyBase) {
        super(msgKeyBase);
        if (e instanceof ElementText w) {
            this.text = w;
            Platform.runLater(() -> {
                this.text.addListener(this);
            });
        } else {
            this.text = null;
            LOGGER.log(Level.SEVERE, "TextAlignWidget: element is not of type ElementText!");
        }

//        Label iconLabel = new Label(MSG.getString("TEXT_ALIGN") + ":");
//        iconLabel.setPadding(new Insets(4));
//        iconLabel.setAlignment(Pos.BASELINE_CENTER);
        updateComboState(text.getAlign());

        getChildren().addAll(comboBox);

        comboBox.setOnAction((t) -> {
            text.setAlign((TextAlign) comboBox.getSelectionModel().getSelectedItem());
            t.consume();
        });
    }

    @SuppressWarnings({"unchecked", "unchecked"})
    private void updateComboState(TextAlign pl) {
        for (TextAlign t : options) {
            if (t.equals(pl)) {
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
        if (!field.equals(ElementText.Field.ALIGN)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "TextAlignWidget: Text align: ==> {0}", newVal.toString());

        if (newVal instanceof TextAlign pd) {
            updateComboState(pd);
        }
    }

}
