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

import com.maehem.mangocad.view.EditorTool;
import com.maehem.mangocad.view.widgets.toolmode.LineBendStyleWidget;
import com.maehem.mangocad.view.widgets.toolmode.LineStyleWidget;
import com.maehem.mangocad.view.widgets.toolmode.LineWidthWidget;
import com.maehem.mangocad.view.widgets.toolmode.MirrorToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.MiterRadiusWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinArrayWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinDirectionWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinFuncToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinLengthToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinRotationToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinSwapLevelWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinVisibilityToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.RotationWidget;
import com.maehem.mangocad.view.widgets.toolmode.ToolModeWidget;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * Tool specific settings Widget near top of each Editor
 *
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ToolModeWidgetBox extends HBox {

    private EditorTool mode;

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
        this.mode = mode;
        getChildren().clear();
        switch (mode) {
            case MOVE -> {
                // alignment options
                RotationWidget rW = new RotationWidget();
                MirrorToggleWidget mW = new MirrorToggleWidget();
                getChildren().addAll(rW, new Region(), mW);
            }
            case ROTATE -> {
                // alignment options
                RotationWidget rW = new RotationWidget();
                MirrorToggleWidget mW = new MirrorToggleWidget();
                getChildren().addAll(rW, new Region(), mW);
            }
            case PIN -> {
                // Available pin styles.
                // TODO: Pin Array Dialog.
                PinArrayWidget paW = new PinArrayWidget(mode.getToolElement());
                PinRotationToggleWidget prW = new PinRotationToggleWidget(mode.getToolElement());
                PinFuncToggleWidget pfW = new PinFuncToggleWidget(mode.getToolElement());
                PinLengthToggleWidget plW = new PinLengthToggleWidget(mode.getToolElement());
                PinVisibilityToggleWidget pvW = new PinVisibilityToggleWidget(mode.getToolElement());
                PinDirectionWidget pdW = new PinDirectionWidget(mode.getToolElement());
                PinSwapLevelWidget pswW = new PinSwapLevelWidget(mode.getToolElement());
                getChildren().addAll(
                        paW,
                        plW,
                        pfW,
                        prW,
                        pvW,
                        pdW,
                        pswW
                );
            }
            case LINE -> {
                // Line options
                LineBendStyleWidget lbsW = new LineBendStyleWidget(mode.getToolElement());
                LineWidthWidget lwW = new LineWidthWidget(mode.getToolElement());
                LineStyleWidget lsW = new LineStyleWidget(mode.getToolElement());
                MiterRadiusWidget mrW = new MiterRadiusWidget(mode.getToolElement());
                getChildren().addAll(lbsW, lwW, lsW, mrW);
            }
            case TEXT -> {
                // Text options
                Text t = new Text("Text");
                getChildren().add(t);
            }
            case SPLIT -> {
                // Split options
                Text t = new Text("Split");
                getChildren().add(t);
            }
            case MITER -> {
                // Miter options
                Text t = new Text("Miter");
                getChildren().add(t);
            }
            case ARC -> {
                // Arc options
                Text t = new Text("Arc");
                getChildren().add(t);
            }
            case POLYGON -> {
                // Polygon options
                Text t = new Text("Polygon");
                getChildren().add(t);
            }
            case CIRCLE -> {
                // Circle options
                Text t = new Text("Circle");
                getChildren().add(t);
            }
            case DIMENSION -> {
                // Dimension options
                Text t = new Text("Dimension");
                getChildren().add(t);
            }
            default -> {
                // Blank area.
            }
        }
    }

}
