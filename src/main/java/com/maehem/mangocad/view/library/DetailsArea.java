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
import javafx.scene.control.SplitPane;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DetailsArea extends SplitPane {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final double PANE_H = 0.25;

    public DetailsArea() {
        setOrientation(Orientation.VERTICAL);
        setDividerPosition(0, 1 * PANE_H);
        setDividerPosition(1, 2 * PANE_H);
        setDividerPosition(2, 3 * PANE_H);
    }

    void setItem(Library lib, ElementType src, String newValue) {

        getItems().clear();

        switch (src) {
            case DEVICE -> {
                // Preview
                // Description
                for (DeviceSet s : lib.getDeviceSets()) {
                    if (s.getName().equals(newValue)) {
                        getItems().add(DetailNodes.descriptionNode(s.getDescription()));
                        return;
                    }
                }
                // Variants
            }
            case FOOTPRINT -> {
                // Preview
                // Description
                for (Footprint footprint : lib.getPackages()) {
                    if (footprint.getName().equals(newValue)) {
                        getItems().add(DetailNodes.footprintPreview(footprint, lib));
                        getItems().add(DetailNodes.descriptionNode(footprint.getDescription()));
                        return;
                    }
                }
            }
            case PACKAGE3D -> {
                // Preview
                // Description
                for (Package3d s : lib.getPackages3d()) {
                    if (s.getName().equals(newValue)) {
                        getItems().add(DetailNodes.descriptionNode(s.getDescription()));
                        return;
                    }
                }
            }
            case SYMBOL -> {
                // Preview
                // Description
                for (Symbol s : lib.getSymbols()) {
                    if (s.getName().equals(newValue)) {
                        
                        getItems().add(DetailNodes.symbolPreview(s, lib ));
                        getItems().add(DetailNodes.descriptionNode(s.getDescription()));
                        return;
                    }
                }
            }
        }
    }

}
