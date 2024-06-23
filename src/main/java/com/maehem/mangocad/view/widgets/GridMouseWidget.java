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
package com.maehem.mangocad.view.widgets;

import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.ElementListener;
import com.maehem.mangocad.model.element.enums.GridUnit;
import com.maehem.mangocad.model.element.misc.Grid;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.library.MouseMovementListener;
import java.util.logging.Level;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Command Entry Widget near top of each Editor
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class GridMouseWidget extends HBox implements MouseMovementListener, ElementListener {

    //private final Label label = new Label("Command:");
    private final Label gridSettingLabel = new Label("nn unit");
    private final Label mouseXYLabel = new Label("(99.9 99.9)");

    private double snap = 1; // Always in MM
    private GridUnit currentUnits = GridUnit.MM;

    public GridMouseWidget() {

        setId("grid-mouse-widget");
        gridSettingLabel.setId("grid-mouse-widget-units");
        mouseXYLabel.setId("grid-mouse-widget-location");

        mouseXYLabel.setPrefWidth(100);

        getChildren().addAll(gridSettingLabel, mouseXYLabel);
        setSpacing(8);
        setMinWidth(160);
        setAlignment(Pos.CENTER);

    }

    public void setGridUnitText(String messageText) {
        gridSettingLabel.setText(messageText);
    }

    public void updateUnitDisplay(Grid g) {
        currentUnits = g.getSizeUnit();
        snap = g.getSizeMM();
        gridSettingLabel.setText(g.getSize() + " " + currentUnits.code());
    }

    @Override
    public void workAreaMouseMoved(double x, double y) {
        String fX = String.format("%8.2f", GridUnit.convertUnit((int) (x / snap) * snap, GridUnit.MM, currentUnits));
        String fY = String.format("%8.2f", GridUnit.convertUnit((int) (y / snap) * snap, GridUnit.MM, currentUnits));
        mouseXYLabel.setText("(" + fX + "," + fY + ")");
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        if (e instanceof Grid g) {
            LOGGER.log(Level.SEVERE, "GridMouseWidget: Grid has changed.");
            updateUnitDisplay(g);
        }
    }

}
