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
import com.maehem.mangocad.model.element.enums.PinVisible;
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
public class PinVisibilityToggleWidget extends ToolModeWidget {

    private static final String ICON_PIN_NONE_PATH = "/icons/pin-vis-none.png";
    private static final String ICON_PIN_NAME_PATH = "/icons/pin-vis-name.png";
    private static final String ICON_PIN_NUM_PATH = "/icons/pin-vis-num.png";
    private static final String ICON_PIN_BOTH_PATH = "/icons/pin-vis-both.png";

    private final ToggleGroup group = new ToggleGroup();
    private Toggle currentToggle;

    private final Pin pin;

    public PinVisibilityToggleWidget(Element e) {
        if (e instanceof Pin p) {
            this.pin = p;
            this.pin.addListener(this);
        } else {
            this.pin = null;
            LOGGER.log(Level.SEVERE, "PinVisibleToggleWidget: element is not of type Pin!");
        }

        Image imgNone = ViewUtils.getImage(ICON_PIN_NONE_PATH);
        Image imgName = ViewUtils.getImage(ICON_PIN_NAME_PATH);
        Image imgNumber = ViewUtils.getImage(ICON_PIN_NUM_PATH);
        Image imgBoth = ViewUtils.getImage(ICON_PIN_BOTH_PATH);
        ImageView pinNoneImg = ViewUtils.createIcon(imgNone, ICON_SIZE);
        ImageView pinNameImg = ViewUtils.createIcon(imgName, ICON_SIZE);
        ImageView pinNumImg = ViewUtils.createIcon(imgNumber, ICON_SIZE);
        ImageView pinBothImg = ViewUtils.createIcon(imgBoth, ICON_SIZE);

        ToggleButton visNoneButton = new ToggleButton("", pinNoneImg); // TODO MSG Tooltip
        ToggleButton visNameButton = new ToggleButton("", pinNameImg);
        ToggleButton visNumButton = new ToggleButton("", pinNumImg);
        ToggleButton visBothButton = new ToggleButton("", pinBothImg);

        getChildren().addAll(visNoneButton, visNameButton, visNumButton, visBothButton);

        visNoneButton.setToggleGroup(group);
        visNoneButton.setUserData(PinVisible.OFF);
        visNoneButton.setSelected(true);

        visNameButton.setToggleGroup(group);
        visNameButton.setUserData(PinVisible.PIN);
        visNameButton.setSelected(false);

        visNumButton.setToggleGroup(group);
        visNumButton.setUserData(PinVisible.PAD);
        visNumButton.setSelected(false);

        visBothButton.setToggleGroup(group);
        visBothButton.setUserData(PinVisible.BOTH);
        visBothButton.setSelected(false);

        updateToggleState(PinVisible.BOTH);
        currentToggle = visNumButton;
        //group.selectToggle(lenMidButton);

        group.selectedToggleProperty().addListener((ov, toggle, newToggle) -> {
            if (newToggle == null) { // If newToggle is null, reselect it.
                currentToggle.setSelected(true); // user action might have un-toggled it.
            } else {
                LOGGER.log(Level.SEVERE, "Change PinVisible toggle to:{0}", newToggle.getUserData().toString());
                currentToggle = newToggle;
                pin.setVisible((PinVisible) newToggle.getUserData());
            }

        });

    }

    private void updateToggleState(PinVisible pv) {
        for (Toggle t : group.getToggles()) {
            if (t.getUserData().equals(pv)) {
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
        if (!field.equals(PinField.VISIBLE)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "PinVisiblehWidget: Pin vis: ==> {0}", newVal.toString());

        if (newVal instanceof PinVisible pl) {
            updateToggleState(pl);
        }
    }

}
