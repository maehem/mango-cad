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
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.enums.WireCap;
import com.maehem.mangocad.model.element.enums.WireField;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LineCapWidget extends ToolModeWidget {

    private static final String LINECAP_ROUND_PATH = "/icons/line-cap-round.png";
    private static final String LINECAP_FLAT_PATH = "/icons/line-cap-flat.png";

    private final ToggleGroup group = new ToggleGroup();

    Wire wire;

    public LineCapWidget(Element e) {

        if (e instanceof Wire w) {
            this.wire = w;
        } else {
            LOGGER.log(Level.SEVERE, "No element for WireCap Widget.");
        }
        setSpacing(0.0);

        Image roundCapImg = ViewUtils.getImage(LINECAP_ROUND_PATH);
        Image flatCapImg = ViewUtils.getImage(LINECAP_FLAT_PATH);
        ImageView roundCapImgView = ViewUtils.createIcon(roundCapImg, ICON_SIZE);
        ImageView flatCapImgView = ViewUtils.createIcon(flatCapImg, ICON_SIZE);

        ToggleButton roundCapToggle = new ToggleButton("", roundCapImgView); // TODO MSG Tooltip
        ToggleButton flatCapToggle = new ToggleButton("", flatCapImgView);

        roundCapToggle.setTooltip(new Tooltip(MSG.getString("LINE_CAP_ROUND_TOOLTIP")));
        flatCapToggle.setTooltip(new Tooltip(MSG.getString("LINE_CAP_FLAT_TOOLTIP")));

        getChildren().addAll(roundCapToggle, flatCapToggle);

        roundCapToggle.setToggleGroup(group);
        roundCapToggle.setUserData(WireCap.ROUND);

        flatCapToggle.setToggleGroup(group);
        flatCapToggle.setUserData(WireCap.FLAT);

        updateToggleState(wire.getCap());

        group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) -> {
            if (newToggle == null) {
                LOGGER.log(Level.SEVERE, "Nothing selected. Set target to round.");
                group.selectToggle(roundCapToggle);
            } else {
                LOGGER.log(Level.SEVERE, "Change toggle to:{0}", newToggle.getUserData().toString());
                group.selectToggle(newToggle);
            }
        });

    }

    private void updateToggleState(WireCap cap) {
        for (Toggle t : group.getToggles()) {
            if (t.getUserData().equals(cap)) {
                group.selectToggle(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        if (wire != null) {
            wire.removeListener(this);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(WireField.CAP)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "LineCapWidget: Wire cap: ==> {0}", newVal.toString());

        if (newVal instanceof WireCap cap) {
            updateToggleState(cap);
        }
    }

}
