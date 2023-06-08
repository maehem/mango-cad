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
package com.maehem.mangocad.view.library;

import com.maehem.mangocad.model.library.Library;
import com.maehem.mangocad.model.library.element.DeviceSet;
import com.maehem.mangocad.model.library.element.ElementType;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.Package3d;
import com.maehem.mangocad.model.library.element.Symbol;
import com.maehem.mangocad.view.ControlPanel;
import java.util.logging.Logger;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DetailsArea extends SplitPane {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final double PANE_H = 0.10;

    public DetailsArea() {
        setOrientation(Orientation.VERTICAL);
        setDividerPosition(0, 5 * PANE_H);
        setDividerPosition(1, 2 * PANE_H);
        setDividerPosition(2, 2 * PANE_H);
    }

    void setItem(Library lib, ElementType src, String newValue) {

        getItems().clear();

        switch (src) {
            case DEVICE -> {
                for (DeviceSet s : lib.getDeviceSets()) {
                    if (s.getName().equals(newValue)) {
                        Node dsPreview = DetailNodes.devicePreview(s, lib);
                        StackPane dsPane = new StackPane(dsPreview);

                        getItems().add(dsPreview);
                        getItems().add(DetailNodes.descriptionNode(s.getDescription()));
                        getItems().add(DetailNodes.devicePackageListNode(s));

                        dsPane.scaleYProperty().bind(getDividers().get(0).positionProperty());
                        dsPane.scaleXProperty().bind(getDividers().get(0).positionProperty());

                        return;
                    }
                }
            }
            case FOOTPRINT -> {
                for (Footprint footprint : lib.getPackages()) {
                    if (footprint.getName().equals(newValue)) {
                        Group footprintPreview = DetailNodes.footprintPreview(footprint, lib, true);
                        GroupContainer nodeContainer = new GroupContainer(footprintPreview);

                        getItems().add(nodeContainer);
                        getItems().add(DetailNodes.descriptionNode(footprint.getDescription()));

                        getDividers().get(0).setPosition(0.5);

                        return;
                    }
                }
            }
            case PACKAGE3D -> {
                for (Package3d s : lib.getPackages3d()) {
                    if (s.getName().equals(newValue)) {
                        getItems().add(DetailNodes.descriptionNode(s.getDescription()));
                        return;
                    }
                }
            }
            case SYMBOL -> {
                for (Symbol s : lib.getSymbols()) {
                    if (s.getName().equals(newValue)) {
                        Group symbolPreview = DetailNodes.symbolPreview(s, lib, true);
                        GroupContainer nodeContainer = new GroupContainer(symbolPreview);

                        getItems().add(nodeContainer);
                        getItems().add(DetailNodes.descriptionNode(s.getDescription()));
                        getDividers().get(0).setPosition(0.5);

                        return;
                    }
                }
            }
        }
    }

}
