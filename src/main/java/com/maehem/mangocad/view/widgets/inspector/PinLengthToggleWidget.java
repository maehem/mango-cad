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
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.enums.PinField;
import com.maehem.mangocad.model.element.enums.PinLength;
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
public class PinLengthToggleWidget extends InspectorWidget {

    private static final String ICON_PIN_POINT_PATH = "/icons/pin-point.png";
    private static final String ICON_PIN_SHORT_PATH = "/icons/pin-short.png";
    private static final String ICON_PIN_MID_PATH = "/icons/pin-mid.png";
    private static final String ICON_PIN_LONG_PATH = "/icons/pin-long.png";

    private final ToggleGroup group = new ToggleGroup();
    private Toggle currentToggle;

    private final Pin pin;

    public PinLengthToggleWidget(Element e) {
        super("PIN_LENGTH");
        if (e instanceof Pin p) {
            this.pin = p;
            this.pin.addListener(this);
        } else {
            this.pin = null;
            LOGGER.log(Level.SEVERE, "PinLengthToggleWidget: pin element is not of type Pin!");
        }

        Image imgPoint = ViewUtils.getImage(ICON_PIN_POINT_PATH);
        Image imgShort = ViewUtils.getImage(ICON_PIN_SHORT_PATH);
        Image imgMid = ViewUtils.getImage(ICON_PIN_MID_PATH);
        Image imgLong = ViewUtils.getImage(ICON_PIN_LONG_PATH);
        ImageView pinPointImg = ViewUtils.createIcon(imgPoint, ICON_SIZE);
        ImageView pinShortImg = ViewUtils.createIcon(imgShort, ICON_SIZE);
        ImageView pinMidImg = ViewUtils.createIcon(imgMid, ICON_SIZE);
        ImageView pinLongImg = ViewUtils.createIcon(imgLong, ICON_SIZE);

        ToggleButton lenPointButton = new ToggleButton("", pinPointImg); // TODO MSG Tooltip
        ToggleButton lenShortButton = new ToggleButton("", pinShortImg);
        ToggleButton lenMidButton = new ToggleButton("", pinMidImg);
        ToggleButton lenLongButton = new ToggleButton("", pinLongImg);

        getChildren().addAll(lenPointButton, lenShortButton, lenMidButton, lenLongButton);

        lenPointButton.setToggleGroup(group);
        lenPointButton.setUserData(PinLength.POINT);
        lenPointButton.setSelected(true);

        lenShortButton.setToggleGroup(group);
        lenShortButton.setUserData(PinLength.SHORT);
        lenShortButton.setSelected(false);

        lenMidButton.setToggleGroup(group);
        lenMidButton.setUserData(PinLength.MIDDLE);
        lenMidButton.setSelected(false);

        lenLongButton.setToggleGroup(group);
        lenLongButton.setUserData(PinLength.LONG);
        lenLongButton.setSelected(false);

        updateToggleState(PinLength.MIDDLE);
        currentToggle = lenMidButton;
        //group.selectToggle(lenMidButton);

        group.selectedToggleProperty().addListener((ov, toggle, newToggle) -> {
            if (newToggle == null) { // If newToggle is null, reselect it.
                currentToggle.setSelected(true); // user action might have un-toggled it.
            } else {
                LOGGER.log(Level.SEVERE, "Change PinLen toggle to:{0}", newToggle.getUserData().toString());
                currentToggle = newToggle;
                pin.setLength((PinLength) newToggle.getUserData());
            }

        });

    }

    private void updateToggleState(PinLength pl) {
        for (Toggle t : group.getToggles()) {
            if (t.getUserData().equals(pl)) {
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
        if (!field.equals(PinField.LENGTH)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "PinLengthWidget: Pin angle: ==> {0}", newVal.toString());

        if (newVal instanceof PinLength pl) {
            updateToggleState(pl);
        }
    }

}
