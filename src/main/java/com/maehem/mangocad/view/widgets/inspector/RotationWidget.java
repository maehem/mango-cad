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
import com.maehem.mangocad.model.element.property.Rotation;
import com.maehem.mangocad.model.element.property.RotationProperty;
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
public class RotationWidget extends InspectorWidget {

    private static final String ICON_PATH = "/icons/rotate.png";

    private final ObservableList<Double> options
            = FXCollections.observableArrayList(
                    0.0,
                    90.0,
                    180.0,
                    270.0
            );
    @SuppressWarnings("unchecked")
    private final ComboBox<Double> comboBox = new ComboBox(options);
    private final Element element;
    private final RotationProperty rotation;

    public RotationWidget(Element e, String msgKeyBase) {
        super(msgKeyBase);
        if (e instanceof RotationProperty p) {
            this.element = e;
            this.rotation = p;
            this.element.addListener(this);
        } else {
            this.element = null;
            this.rotation = null;
            LOGGER.log(Level.SEVERE, "RotationWidget: element is not of type RotationProperty!");
        }

        //Image img = ViewUtils.getImage(ICON_PATH);
        //ImageView icon = ViewUtils.createIcon(img, 20);
//        Label iconLabel = new Label("", icon);
//        iconLabel.setPadding(new Insets(4));
//        iconLabel.setAlignment(Pos.BASELINE_CENTER);
//        iconLabel.setTooltip(new Tooltip(MSG.getString("TOOL_ICON_ROTATE")));

        updateRotation(rotation.getRot());

        getChildren().addAll(comboBox);

        comboBox.setOnAction((t) -> {
            rotation.setRot((double) comboBox.getSelectionModel().getSelectedItem());
            t.consume();
        });
    }

    @SuppressWarnings("unchecked")
    private void updateRotation(double rot) {

        for (double option : options) {
            if (option == rot) {
                comboBox.getSelectionModel().select(option);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        if (element != null) {
            element.removeListener(this);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Qualitfy what we can rotate.
        // TODO: support rotate for groups of things and higher level things
        // like devices and footprints.
        if ( //!field.equals(ElementText.Field.ROTATION) &&
                !field.equals(Rotation.Field.VALUE)
                && !field.equals(Rotation.Field.MIRROR)) {
            //LOGGER.log(Level.SEVERE, "The Rotation Field is not an expected type: " + field.name());
            return;
        }
        if (newVal == null) {
            LOGGER.log(Level.SEVERE, "NewVal is null! Don't do anything.");
            return;
        }
        LOGGER.log(Level.SEVERE, "RotationWidget: Element rot: ==> {0}", newVal.toString());

        if (newVal instanceof Double pl) {
            updateRotation(pl);
        }

    }

}
