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
public class ArcClockwiseToggleWidget extends ToolModeWidget {

    private static final String ICON_PATH = "/icons/rotate.png";

    private final ToggleGroup group = new ToggleGroup();

    public ArcClockwiseToggleWidget(Element e) {

        setSpacing(0.0);

        Image img = ViewUtils.getImage(ICON_PATH);
        ImageView mirNormal = ViewUtils.createIcon(img, ICON_SIZE);
        ImageView mirFlipped = ViewUtils.createIcon(img, ICON_SIZE);
        mirFlipped.setScaleX(-1.0);

        ToggleButton cwToggle = new ToggleButton("", mirNormal); // TODO MSG Tooltip
        ToggleButton ccwToggle = new ToggleButton("", mirFlipped);

        cwToggle.setTooltip(new Tooltip(MSG.getString("ARC_CW_TOGGLE")));
        ccwToggle.setTooltip(new Tooltip(MSG.getString("ARC_CCW_TOGGLE")));

        getChildren().addAll(ccwToggle, cwToggle);

        ccwToggle.setToggleGroup(group);
        cwToggle.setToggleGroup(group);

        group.selectToggle(cwToggle);

        group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) -> {
            if (newToggle == null) {
                LOGGER.log(Level.SEVERE, "Nothing selected. Set target to not-mirrored.");
                group.selectToggle(cwToggle);
            } else {
                LOGGER.log(Level.SEVERE, "Change toggle to:{0}", newToggle.getUserData().toString());
                group.selectToggle(newToggle);
            }
        });

    }

    @Override
    public void stopListening() {

    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {

    }

}
