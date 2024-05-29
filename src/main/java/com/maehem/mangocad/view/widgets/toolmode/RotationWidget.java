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
import com.maehem.mangocad.model.ElementRotation;
import com.maehem.mangocad.model.element.enums.ElementTextField;
import com.maehem.mangocad.model.element.enums.PinField;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RotationWidget extends ToolModeWidget {

    private static final String ICON_PATH = "/icons/rotate.png";

    private final ObservableList<String> options
            = FXCollections.observableArrayList(
                    "0",
                    "90",
                    "180",
                    "270"
            );
    @SuppressWarnings("unchecked")
    private final ComboBox comboBox = new ComboBox(options);
    private final Element element;
    private final ElementRotation rotation;

    public RotationWidget(Element e) {
        if (e instanceof ElementRotation p) {
            this.element = e;
            this.rotation = p;
            this.element.addListener(this);
        } else {
            this.element = null;
            this.rotation = null;
            LOGGER.log(Level.SEVERE, "RotationWidget: element is not of type Pin!");
        }

        Image img = ViewUtils.getImage(ICON_PATH);
        ImageView icon = ViewUtils.createIcon(img, 20);
        Label iconLabel = new Label("", icon);
        iconLabel.setPadding(new Insets(4));
        iconLabel.setAlignment(Pos.BASELINE_CENTER);
        iconLabel.setTooltip(new Tooltip(MSG.getString("TOOL_ICON_ROTATE")));

        updateRotation(rotation.getRot());

        getChildren().addAll(iconLabel, comboBox);

    }

    @SuppressWarnings("unchecked")
    private void updateRotation(double rot) {

        for (String s : options) {
            if (Double.parseDouble(s) == rot) {
                comboBox.getSelectionModel().select(s);
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
        if (!field.equals(ElementTextField.ROTATION)
                && !field.equals(PinField.ROTATION)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "RotationWidget: Element rot: ==> {0}", newVal.toString());

        if (newVal instanceof Double pl) {
            updateRotation(pl);
        }

    }

}
