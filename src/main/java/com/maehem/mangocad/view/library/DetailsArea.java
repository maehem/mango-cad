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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

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
                        VBox box = new VBox(DetailNodes.descriptionNode(s.getDescription()));
                        box.getChildren().add(DetailNodes.scaleGauge());
                        getItems().add(box);
                        return;
                    }
                }
            }
            case FOOTPRINT -> {
                for (Footprint footprint : lib.getPackages()) {
                    if (footprint.getName().equals(newValue)) {
                        Node footprintPreview = DetailNodes.footprintPreview(footprint, lib);
                        Node scaleGauge = DetailNodes.scaleGauge();
                        scaleGauge.setScaleX(footprintPreview.getScaleX());
                        scaleGauge.setScaleY(footprintPreview.getScaleY());

                        StackPane footprintPane = new StackPane(footprintPreview);
                        StackPane gaugePane = new StackPane( scaleGauge );
                        BorderPane pane = new BorderPane(footprintPane);
                        pane.setBottom(gaugePane);


                        getItems().add(pane);
                        getItems().add(DetailNodes.descriptionNode(footprint.getDescription()));

                        gaugePane.scaleYProperty().bind(getDividers().get(0).positionProperty());
                        gaugePane.scaleXProperty().bind(getDividers().get(0).positionProperty());
                        footprintPane.scaleYProperty().bind(getDividers().get(0).positionProperty());
                        footprintPane.scaleXProperty().bind(getDividers().get(0).positionProperty());
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
                        Node symbolPreview = DetailNodes.symbolPreview(s, lib);
                        Node scaleGauge = DetailNodes.scaleGauge();
                        scaleGauge.setScaleX(symbolPreview.getScaleX());
                        scaleGauge.setScaleY(symbolPreview.getScaleY());
                                                
                        StackPane symbolPane = new StackPane(symbolPreview);
                        StackPane gaugePane = new StackPane( scaleGauge );
                        BorderPane pane = new BorderPane(symbolPane);
                        
                        pane.setBottom(gaugePane);
                        
                        getItems().add(pane);
                        getItems().add(DetailNodes.descriptionNode(s.getDescription()));
                                                
                        gaugePane.scaleYProperty().bind(getDividers().get(0).positionProperty());
                        gaugePane.scaleXProperty().bind(getDividers().get(0).positionProperty());
                        symbolPane.scaleYProperty().bind(getDividers().get(0).positionProperty());
                        symbolPane.scaleXProperty().bind(getDividers().get(0).positionProperty());
                        return;
                    }
                }
            }
        }
    }

}
