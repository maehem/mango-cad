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
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.enums.DimensionType;
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
public class DimensionTypeToggleWidget extends ToolModeWidget {

    private static final String ICON_PARALLEL = "/icons/dim-parallel.png";
    private static final String ICON_HORIZ = "/icons/dim-horiz.png";
    private static final String ICON_VERT = "/icons/dim-vert.png";
    private static final String ICON_RADIUS = "/icons/dim-radius.png";
    private static final String ICON_DIAMETER = "/icons/dim-diameter.png";
    private static final String ICON_ANGLE = "/icons/dim-angle.png";
    private static final String ICON_LEADER = "/icons/dim-leader.png";

    private final ToggleGroup group = new ToggleGroup();
    private Toggle currentToggle;

    private final Dimension dimension;

    public DimensionTypeToggleWidget(Element e) {
        if (e instanceof Dimension p) {
            this.dimension = p;
            this.dimension.addListener(this);
        } else {
            this.dimension = null;
            LOGGER.log(Level.SEVERE, "DimensionTypeToggleWidget: element is not of type Dimension!");
        }

        Image imgParalell = ViewUtils.getImage(ICON_PARALLEL);
        Image imgHoriz = ViewUtils.getImage(ICON_HORIZ);
        Image imgVert = ViewUtils.getImage(ICON_VERT);
        Image imgRadius = ViewUtils.getImage(ICON_RADIUS);
        Image imgDiameter = ViewUtils.getImage(ICON_DIAMETER);
        Image imgAngle = ViewUtils.getImage(ICON_ANGLE);
        Image imgLeader = ViewUtils.getImage(ICON_LEADER);
        ImageView parallelImg = ViewUtils.createIcon(imgParalell, ICON_SIZE);
        ImageView horizImg = ViewUtils.createIcon(imgHoriz, ICON_SIZE);
        ImageView vertImg = ViewUtils.createIcon(imgVert, ICON_SIZE);
        ImageView radiusImg = ViewUtils.createIcon(imgRadius, ICON_SIZE);
        ImageView diameterImg = ViewUtils.createIcon(imgDiameter, ICON_SIZE);
        ImageView angleImg = ViewUtils.createIcon(imgAngle, ICON_SIZE);
        ImageView leaderImg = ViewUtils.createIcon(imgLeader, ICON_SIZE);

        ToggleButton paralellButton = new ToggleButton("", parallelImg); // TODO MSG Tooltip
        ToggleButton horizButton = new ToggleButton("", horizImg);
        ToggleButton vertButton = new ToggleButton("", vertImg);
        ToggleButton radiusButton = new ToggleButton("", radiusImg);
        ToggleButton diameterButton = new ToggleButton("", diameterImg);
        ToggleButton angleButton = new ToggleButton("", angleImg);
        ToggleButton leaderButton = new ToggleButton("", leaderImg);

        getChildren().addAll(
                paralellButton,
                horizButton,
                vertButton,
                radiusButton,
                diameterButton,
                angleButton,
                leaderButton
        );

        paralellButton.setToggleGroup(group);
        paralellButton.setUserData(DimensionType.PARALLEL);

        horizButton.setToggleGroup(group);
        horizButton.setUserData(DimensionType.HORIZONTAL);

        vertButton.setToggleGroup(group);
        vertButton.setUserData(DimensionType.VERTICAL);

        radiusButton.setToggleGroup(group);
        radiusButton.setUserData(DimensionType.RADIUS);

        diameterButton.setToggleGroup(group);
        diameterButton.setUserData(DimensionType.DIAMETER);

        angleButton.setToggleGroup(group);
        angleButton.setUserData(DimensionType.ANGLE);

        leaderButton.setToggleGroup(group);
        leaderButton.setUserData(DimensionType.LEADER);

        updateToggleState(DimensionType.PARALLEL);
        currentToggle = vertButton;

        group.selectedToggleProperty().addListener((ov, toggle, newToggle) -> {
            if (newToggle == null) { // If newToggle is null, reselect it.
                currentToggle.setSelected(true); // user action might have un-toggled it.
            } else {
                LOGGER.log(Level.SEVERE, "Change DimType toggle to:{0}", newToggle.getUserData().toString());
                currentToggle = newToggle;
                dimension.setDtype((DimensionType) newToggle.getUserData());
            }

        });

    }

    private void updateToggleState(DimensionType type) {
        for (Toggle t : group.getToggles()) {
            if (t.getUserData().equals(type)) {
                group.selectToggle(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        dimension.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(Dimension.Field.D_TYPE)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "DimensionTypeWidget: type: ==> {0}", newVal.toString());

        if (newVal instanceof DimensionType type) {
            updateToggleState(type);
        }
    }

}
