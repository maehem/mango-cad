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
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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

                        dsPane.scaleYProperty().bind(getDividers().get(0).positionProperty());
                        dsPane.scaleXProperty().bind(getDividers().get(0).positionProperty());

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
                        StackPane gaugePane = new StackPane(scaleGauge);
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
                        Pane symbolPreview = DetailNodes.symbolPreview(s, lib);
                        Pane scaleGauge = DetailNodes.scaleGauge();
                        double symbH = symbolPreview.getBoundsInLocal().getHeight();
                        double symbW = symbolPreview.getBoundsInLocal().getWidth();
                        double scaleH = scaleGauge.getBoundsInLocal().getHeight();
                        double scaleW = scaleGauge.getBoundsInLocal().getWidth();
                        
                        Group nodeGroup = new Group(symbolPreview, scaleGauge);

                        //StackPane nodeGroupPane = new StackPane(nodeGroup);
                        
                        scaleGauge.setTranslateY(symbH + scaleH/2.0);
                        scaleGauge.setTranslateX(symbW/2.0 - scaleW/2.0); // Place gauge bottom center.
                        

//                        scaleGauge.setBorder(new Border(new BorderStroke(
//                                new Color(1.0,0.7,0,0.5), BorderStrokeStyle.SOLID, 
//                                CornerRadii.EMPTY, new BorderWidths(0.1)
//                        )));
                        
                        StackPane nodePane = new StackPane(nodeGroup);
//                        nodePane.setBorder(new Border(new BorderStroke(
//                                Color.GREEN, BorderStrokeStyle.SOLID, 
//                                CornerRadii.EMPTY, new BorderWidths(1),
//                                new Insets(30)
//                        )));
                        double nodeH = nodePane.getBoundsInLocal().getHeight();
                        double nodeW = nodePane.getBoundsInLocal().getWidth();
                        
                        //StackPane bp = new StackPane(nodePane);
                        
                        getItems().add(nodePane);
                        getItems().add( DetailNodes.descriptionNode(s.getDescription()));
                        getDividers().get(0).setPosition(0.5);
                        
                        Bounds winBounds = getBoundsInLocal();

                        Platform.runLater(() -> {
                            // TODO:  Base initial/max scale at slider 0.5
                            //        Fit max w/h at 50%.
                            //        Clamp scale >0.6
                            //        Update scale in callback.
                            
                            double scale = 2*winBounds.getWidth()/nodeW;
//                            scaleGauge.setBorder(new Border(new BorderStroke(
//                                    new Color(1.0,0.7,0,0.5), BorderStrokeStyle.SOLID, 
//                                    CornerRadii.EMPTY, new BorderWidths(0.4)
//                            )));
//                            LOGGER.log(Level.SEVERE, "RunLater: win:{0}x{1}  nodeH:{2}  scale: {3}",
//                                    new Object[]{winBounds.getWidth(), winBounds.getHeight(), nodeH, scale}
//                            );
                            double nodeWMax = nodeW*scale;
                            double nodeHMax = nodeH*scale;
                            
//                            LOGGER.log(Level.SEVERE, "Symbol Max width: " + nodeHMax );
                            
                            if ( nodeHMax> winBounds.getHeight() ) {
                                scale = winBounds.getHeight()/nodeH;
//                                LOGGER.log(Level.SEVERE, 
//                                        "Node Hmax: {0}   nodeH: {1}  winH: {2}", 
//                                        new Object[]{nodeHMax, nodeH, winBounds.getHeight()}
//                                );
//                                LOGGER.log(Level.SEVERE, "Scale based on H.  scale:{0}", scale);
//                                scaleGauge.setBorder(new Border(new BorderStroke(
//                                        new Color(0.0,0.5,1.00,0.5), BorderStrokeStyle.SOLID, 
//                                        CornerRadii.EMPTY, new BorderWidths(0.1)
//                                )));
                            }
                            nodeGroup.setScaleX(scale);
                            nodeGroup.setScaleY(scale);
                        });
                        
                        getDividers().get(0).positionProperty().addListener(((ov, t, t1) -> {
                            double position = getDividers().get(0).getPosition();
                            if ( position <= 0.5 ) {
                                nodePane.setScaleX(position);
                                nodePane.setScaleY(position);
                            } else {
                                nodePane.setScaleX(0.5);
                                nodePane.setScaleY(0.5);
                            }
//                            double wMax = getBoundsInLocal().getWidth();
//                            double hMax = getBoundsInLocal().getHeight();
//                            
//                            LOGGER.log(Level.SEVERE, "WindowArea: w:" + wMax  +  "   h:" + hMax );
                        }));
                        
                        //nodePane.scaleXProperty().bind(getDividers().get(0).positionProperty());
                        //nodePane.scaleYProperty().bind(getDividers().get(0).positionProperty());

                        return;
                    }
                }
            }
        }
    }

}
