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

import com.maehem.mangocad.model.element.basic.CircleElement;
import com.maehem.mangocad.model.element.basic.PolygonElement;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.view.EditorTool;
import static com.maehem.mangocad.view.EditorTool.ARC;
import com.maehem.mangocad.view.widgets.inspector.LineWidthWidget;
import com.maehem.mangocad.view.widgets.inspector.MirrorToggleWidget;
import com.maehem.mangocad.view.widgets.inspector.RotationWidget;
import com.maehem.mangocad.view.widgets.toolmode.ArcClockwiseToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.LineBendStyleWidget;
import com.maehem.mangocad.view.widgets.toolmode.LineCapWidget;
import com.maehem.mangocad.view.widgets.toolmode.MiterRadiusWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinArrayWidget;
import com.maehem.mangocad.view.widgets.toolmode.PolygonFillWidget;
import com.maehem.mangocad.view.widgets.toolmode.ToolModeWidget;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * Tool specific settings Widget near top of each Editor
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ToolModeWidgetBox extends HBox {

    //private EditorTool mode;
    public ToolModeWidgetBox() {

        setHeight(24);
        setSpacing(2);
    }

    public void setMode(EditorTool mode) {
        for (Node n : getChildren()) {
            if (n instanceof ToolModeWidget w) {
                w.stopListening();
            }
        }
        //this.mode = mode;
        getChildren().clear();
        switch (mode) {
            case MOVE -> {
                // alignment options
                RotationWidget rW = new RotationWidget(mode.getToolElement(), "ROTATION");
                MirrorToggleWidget mW = new MirrorToggleWidget(mode.getToolElement(), "MIRROR");
                getChildren().addAll(rW, new Region(), mW);
            }
            case ROTATE -> {
                // alignment options
                RotationWidget rW = new RotationWidget(mode.getToolElement(), "ROTATION");
                MirrorToggleWidget mW = new MirrorToggleWidget(mode.getToolElement(), "MIRROR");
                getChildren().addAll(rW, new Region(), mW);
            }
            case MIRROR -> {
                // alignment options
                MirrorToggleWidget mW = new MirrorToggleWidget(mode.getToolElement(), "MIRROR");
                getChildren().addAll(mW);
            }
            case PIN -> {
                // TODO: Pin Array Dialog.
                PinArrayWidget paW = new PinArrayWidget(mode.getToolElement());
                getChildren().addAll(paW);
            }
            case LINE -> { // Line options
                LineBendStyleWidget lbsW = new LineBendStyleWidget(mode.getToolElement());
                //LineWidthWidget lwW = new LineWidthWidget(mode.getToolElement());
                //LineStyleWidget lsW = new LineStyleWidget(mode.getToolElement());
                MiterRadiusWidget mrW = new MiterRadiusWidget(mode.getToolElement());
                getChildren().addAll(lbsW, mrW);
            }
            case ARC -> { // Arc options
                // Tool Element is null until user clicks to start new arc/line.
                if (mode.getToolElement() != null) {
                    if (mode.getToolElement() instanceof Wire arc) {
                        ArcClockwiseToggleWidget acW = new ArcClockwiseToggleWidget(mode.getToolElement());
                        LineWidthWidget lwW = new LineWidthWidget(arc, arc.widthProperty);
                        LineCapWidget lcW = new LineCapWidget(mode.getToolElement());
                        getChildren().addAll(acW, lwW, lcW);
                    }
                }
            }
            case TEXT -> {  // Text options
            }
            case SPLIT -> { // Split options
                LineBendStyleWidget lbsW = new LineBendStyleWidget(mode.getToolElement());
                MiterRadiusWidget mrW = new MiterRadiusWidget(mode.getToolElement());
                getChildren().addAll(lbsW, mrW);
            }
            case MITER -> { // Miter options
                MiterRadiusWidget mrW = new MiterRadiusWidget(mode.getToolElement());
                getChildren().addAll(mrW);
            }
            case POLYGON -> { // Polygon options
                if (mode.getToolElement() instanceof PolygonElement poly) {
                    LineBendStyleWidget lbsW = new LineBendStyleWidget(mode.getToolElement());
                    LineWidthWidget lwW = new LineWidthWidget(poly, poly.widthProperty);
                    // Hatch Fill style with spacing
                    PolygonFillWidget pfW = new PolygonFillWidget(mode.getToolElement());
                    MiterRadiusWidget mrW = new MiterRadiusWidget(mode.getToolElement());
                    getChildren().addAll(lbsW, lwW, pfW, mrW);
                }
            }
            case CIRCLE -> { // Circle options
                if (mode.getToolElement() instanceof CircleElement ec) {
                    LineWidthWidget lwW = new LineWidthWidget(ec, ec.widthProperty);
                    getChildren().addAll(lwW);
                }
            }
            case DIMENSION -> {
            }
            default -> {
                // Blank area.
            }
        }
    }

}
