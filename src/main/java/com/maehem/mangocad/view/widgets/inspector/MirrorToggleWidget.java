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
import com.maehem.mangocad.model.element.property.RotationProperty;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
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
public class MirrorToggleWidget extends InspectorWidget {

    private static final String ICON_PATH = "/icons/flip-horizontal.png";

    private final ToggleGroup group = new ToggleGroup();
    private final Element element;
    private final RotationProperty rotation;

    public MirrorToggleWidget(Element e, String msgKeyBase) {
        super(msgKeyBase);
        if (e instanceof RotationProperty p) {
            this.element = e;
            this.rotation = p;
            this.element.addListener(this);
        } else {
            this.element = null;
            this.rotation = null;
            LOGGER.log(Level.SEVERE, "MirrorToggleWidget: element is not of type RotationProperty!");
        }

        setSpacing(0.0);

        Image img = ViewUtils.getImage(ICON_PATH);
        ImageView mirNormal = ViewUtils.createIcon(img, ICON_SIZE);
        ImageView mirFlipped = ViewUtils.createIcon(img, ICON_SIZE);
        mirFlipped.setScaleX(-1.0);

        ToggleButton mirNormalToggle = new ToggleButton("", mirNormal); // TODO MSG Tooltip
        ToggleButton mirFlippedToggle = new ToggleButton("", mirFlipped);

        getChildren().addAll(mirNormalToggle, mirFlippedToggle);

        mirNormalToggle.setToggleGroup(group);
        mirNormalToggle.setUserData(Boolean.FALSE);
        //mirNormalToggle.setSelected(true);

        mirFlippedToggle.setToggleGroup(group);
        mirFlippedToggle.setUserData(Boolean.TRUE);
        //mirFlippedToggle.setSelected(false);

        updateToggleState(rotation.isMirrored());

        group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) -> {
            if (newToggle == null) {
                LOGGER.log(Level.SEVERE, "Nothing selected. Set target to not-mirrored.");
                group.selectToggle(mirNormalToggle);
            } else {
                LOGGER.log(Level.SEVERE, "Change toggle to:{0}", newToggle.getUserData().toString());
                rotation.setMirror((boolean) newToggle.getUserData());
                //updateToggleState(rotation.isMirrored());
            }
        });

    }

    private void updateToggleState(boolean mir) {

        for (Toggle option : group.getToggles()) {
            if ((Boolean) option.getUserData() == mir) {
                group.selectToggle(option);
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
        // Qualify what we can mirror.
        // TODO: support rotate for groups of things and higher level things
        // like devices and footprints.
        if (!field.equals(RotationProperty.Field.MIRROR)) {
            //LOGGER.log(Level.SEVERE, "The Rotation/Mirror Field is not an expected type: " + field.toString() + field.name());
            return;
        }
        if (newVal == null) {
            LOGGER.log(Level.SEVERE, "NewVal is null! Don't do anything.");
            return;
        }
        LOGGER.log(Level.SEVERE, "RotationWidget: Element mir: ==> {0}", newVal.toString());

        if (newVal instanceof Boolean mirrored) {
            updateToggleState(mirrored);
        } else {
            LOGGER.log(Level.SEVERE, "Provded newVal was not a Boolean!");
        }
    }

}
