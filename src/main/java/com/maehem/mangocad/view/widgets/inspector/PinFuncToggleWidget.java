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
import com.maehem.mangocad.model.element.enums.PinFunction;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.application.Platform;
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
public class PinFuncToggleWidget extends InspectorWidget {

    private static final String ICON_PIN_NONE_PATH = "/icons/pin-long.png";
    private static final String ICON_PIN_DOT_PATH = "/icons/pin-dot.png";
    private static final String ICON_PIN_CLK_PATH = "/icons/pin-clk.png";
    private static final String ICON_PIN_DOT_CLK_PATH = "/icons/pin-dot-clk.png";

    private final ToggleGroup group = new ToggleGroup();
    private Toggle currentToggle;

    private final Pin pin;

    public PinFuncToggleWidget(Element e) {
        super("PIN_FUNCTION");
        if (e instanceof Pin p) {
            this.pin = p;
            Platform.runLater(() -> {
                this.pin.addListener(this);
            });
        } else {
            this.pin = null;
            LOGGER.log(Level.SEVERE, "PinFunctionWidget: pin element is not of type Pin!");
        }

        Image imgNone = ViewUtils.getImage(ICON_PIN_NONE_PATH);
        Image imgDot = ViewUtils.getImage(ICON_PIN_DOT_PATH);
        Image imgClk = ViewUtils.getImage(ICON_PIN_CLK_PATH);
        Image imgDotClk = ViewUtils.getImage(ICON_PIN_DOT_CLK_PATH);
        ImageView pinPointImg = ViewUtils.createIcon(imgNone, ICON_SIZE);
        ImageView pinShortImg = ViewUtils.createIcon(imgDot, ICON_SIZE);
        ImageView pinMidImg = ViewUtils.createIcon(imgClk, ICON_SIZE);
        ImageView pinLongImg = ViewUtils.createIcon(imgDotClk, ICON_SIZE);

        ToggleButton funcNoneButton = new ToggleButton("", pinPointImg); // TODO MSG Tooltip
        ToggleButton funcDotButton = new ToggleButton("", pinShortImg);
        ToggleButton funcClkButton = new ToggleButton("", pinMidImg);
        ToggleButton funcDotClkButton = new ToggleButton("", pinLongImg);

        getChildren().addAll(funcNoneButton, funcDotButton, funcClkButton, funcDotClkButton);

        funcNoneButton.setToggleGroup(group);
        funcNoneButton.setUserData(PinFunction.NONE);
        funcNoneButton.setSelected(true);

        funcDotButton.setToggleGroup(group);
        funcDotButton.setUserData(PinFunction.DOT);
        funcDotButton.setSelected(false);

        funcClkButton.setToggleGroup(group);
        funcClkButton.setUserData(PinFunction.CLK);
        funcClkButton.setSelected(false);

        funcDotClkButton.setToggleGroup(group);
        funcDotClkButton.setUserData(PinFunction.DOTCLK);
        funcDotClkButton.setSelected(false);

        updateToggleState(PinFunction.NONE);
        currentToggle = funcClkButton;
        //group.selectToggle(currentToggle);

        group.selectedToggleProperty().addListener((ov, oldToggle, newToggle) -> {
            if (newToggle == null) {
                LOGGER.log(Level.SEVERE, "Nothing selected. Set target to non-func.");
                currentToggle.setSelected(true);
            } else {
                LOGGER.log(Level.SEVERE, "Change PinFunc toggle to:{0}", newToggle.getUserData().toString());
                currentToggle = newToggle;
                pin.setFunction((PinFunction) currentToggle.getUserData());
            }
        });

    }

    private void updateToggleState(PinFunction pf) {
        for (Toggle t : group.getToggles()) {
            if (t.getUserData().equals(pf)) {
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
        if (newVal == null) {
            return;
        }
        if (!(newVal instanceof PinFunction)) {
            return;
        }

        LOGGER.log(Level.SEVERE, "PinFuncWidget: Pin change. ==> {0}", newVal.toString());

        if (newVal instanceof PinFunction pf) {
            LOGGER.log(Level.SEVERE, "New Value: {0}", pf.code());

            for (Toggle t : group.getToggles()) {
                if (t.getUserData().equals(pf)) {
                    group.selectToggle(t);
                    break;
                }
            }
        }
    }

}
