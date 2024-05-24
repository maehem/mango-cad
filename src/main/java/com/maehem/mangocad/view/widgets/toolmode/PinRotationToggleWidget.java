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
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinRotationToggleWidget extends ToolModeWidget {

    private static final String ICON_PIN_PATH = "/icons/pin-long.png";

    private final ToggleGroup group = new ToggleGroup();
    private Toggle currentToggle;

    private final Pin pin;

    public PinRotationToggleWidget(Element e) {
        if (e instanceof Pin p) {
            this.pin = p;
            this.pin.addListener(this);
        } else {
            this.pin = null;
            LOGGER.log(Level.SEVERE, "PinFunToggleWidget: pin element is not of type Pin!");
        }

        Image imgPin = ViewUtils.getImage(ICON_PIN_PATH);
        ImageView pinRot0 = ViewUtils.createIcon(imgPin, ICON_SIZE);
        ImageView pinRot90 = ViewUtils.createIcon(imgPin, ICON_SIZE);
        ImageView pinRot180 = ViewUtils.createIcon(imgPin, ICON_SIZE);
        ImageView pinRot270 = ViewUtils.createIcon(imgPin, ICON_SIZE);

        pinRot90.setRotate(90);
        pinRot180.setRotate(180);
        pinRot270.setRotate(270);

        ToggleButton rot0Button = new ToggleButton("", pinRot0); // TODO MSG Tooltip
        ToggleButton rot90Button = new ToggleButton("", pinRot90);
        ToggleButton rot180Button = new ToggleButton("", pinRot180);
        ToggleButton rot270Button = new ToggleButton("", pinRot270);

        getChildren().addAll(rot0Button, rot90Button, rot180Button, rot270Button);

        rot0Button.setToggleGroup(group);
        rot0Button.setUserData(0.0);
        rot0Button.setSelected(true);

        rot90Button.setToggleGroup(group);
        rot90Button.setUserData(90.0);
        rot90Button.setSelected(false);

        rot180Button.setToggleGroup(group);
        rot180Button.setUserData(180.0);
        rot180Button.setSelected(false);

        rot270Button.setToggleGroup(group);
        rot270Button.setUserData(270.0);
        rot270Button.setSelected(false);

        updateToggleState(pin.getRot());
        currentToggle = group.getSelectedToggle();
        //group.selectToggle(currentToggle);

        group.selectedToggleProperty().addListener((ov, oldToggle, newToggle) -> {
            if (newToggle == null) {
                LOGGER.log(Level.SEVERE, "Nothing selected. Set target to non-func.");
                currentToggle.setSelected(true);
            } else if (oldToggle == null || !oldToggle.equals(newToggle)) {
                LOGGER.log(Level.SEVERE, "Change PinRotation toggle to:{0}", newToggle.getUserData().toString());
                currentToggle = newToggle;
                pin.setRot((double) newToggle.getUserData());
            }
        });

    }

    private void updateToggleState(double rot) {
        for (Toggle t : group.getToggles()) {
            if (t.getUserData().equals(rot)) {
                group.selectToggle(t);
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
        if (!field.equals(PinField.ROTATION)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "PinRotationWidget: Pin angle: ==> {0}", newVal.toString());

        if (newVal instanceof Double dd) {
            updateToggleState(dd);
        }
//        if ((double) oldVal != 0 && (double) newVal == 0)
//        throw new Error("Stack Trace Time!");
    }

}
