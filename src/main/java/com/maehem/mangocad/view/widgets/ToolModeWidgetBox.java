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

import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.property.GridUnitProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import com.maehem.mangocad.view.EditorTool;
import static com.maehem.mangocad.view.EditorTool.ARC;
import com.maehem.mangocad.view.widgets.toolmode.ArcClockwiseToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.DimensionTypeToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.GridUnitListWidget;
import com.maehem.mangocad.view.widgets.toolmode.IntegerListWidget;
import com.maehem.mangocad.view.widgets.toolmode.LineBendStyleWidget;
import com.maehem.mangocad.view.widgets.toolmode.LineCapWidget;
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
import com.maehem.mangocad.view.widgets.toolmode.PolygonFillWidget;
import com.maehem.mangocad.view.widgets.toolmode.RealValueListWidget2;
import com.maehem.mangocad.view.widgets.toolmode.RotationWidget;
import com.maehem.mangocad.view.widgets.toolmode.TextAlignWidget;
import com.maehem.mangocad.view.widgets.toolmode.TextDistanceWidget;
import com.maehem.mangocad.view.widgets.toolmode.TextFontWidget;
import com.maehem.mangocad.view.widgets.toolmode.TextRatioWidget;
import com.maehem.mangocad.view.widgets.toolmode.TextSizeWidget;
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
                RotationWidget rW = new RotationWidget(mode.getToolElement());
                MirrorToggleWidget mW = new MirrorToggleWidget(mode.getToolElement());
                getChildren().addAll(rW, new Region(), mW);
            }
            case ROTATE -> {
                // alignment options
                RotationWidget rW = new RotationWidget(mode.getToolElement());
                MirrorToggleWidget mW = new MirrorToggleWidget(mode.getToolElement());
                getChildren().addAll(rW, new Region(), mW);
            }
            case MIRROR -> {
                // alignment options
                MirrorToggleWidget mW = new MirrorToggleWidget(mode.getToolElement());
                getChildren().addAll(mW);
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
            case LINE -> { // Line options
                LineBendStyleWidget lbsW = new LineBendStyleWidget(mode.getToolElement());
                LineWidthWidget lwW = new LineWidthWidget(mode.getToolElement());
                LineStyleWidget lsW = new LineStyleWidget(mode.getToolElement());
                MiterRadiusWidget mrW = new MiterRadiusWidget(mode.getToolElement());
                getChildren().addAll(lbsW, lwW, lsW, mrW);
            }
            case ARC -> { // Arc options
                // Tool Element is null until user clicks to start new arc/line.
                if (mode.getToolElement() != null) {
                    ArcClockwiseToggleWidget acW = new ArcClockwiseToggleWidget(mode.getToolElement());
                    LineWidthWidget lwW = new LineWidthWidget(mode.getToolElement());
                    LineCapWidget lcW = new LineCapWidget(mode.getToolElement());
                    getChildren().addAll(acW, lwW, lcW);
                }
            }
            case TEXT -> {  // Text options
                RotationWidget rW = new RotationWidget(mode.getToolElement());
                MirrorToggleWidget mW = new MirrorToggleWidget(mode.getToolElement());
                TextSizeWidget tsW = new TextSizeWidget(mode.getToolElement()); // Upgrade to EditableDoubleListWidget
                TextRatioWidget trW = new TextRatioWidget(mode.getToolElement());
                TextFontWidget tfW = new TextFontWidget(mode.getToolElement());
                TextAlignWidget taW = new TextAlignWidget(mode.getToolElement());
                TextDistanceWidget tdW = new TextDistanceWidget(mode.getToolElement());
                getChildren().addAll(rW, new Region(), mW, tsW, trW, tfW, taW, tdW);
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
                LineBendStyleWidget lbsW = new LineBendStyleWidget(mode.getToolElement());
                LineWidthWidget lwW = new LineWidthWidget(mode.getToolElement());
                // Hatch Fill style with spacing
                PolygonFillWidget pfW = new PolygonFillWidget(mode.getToolElement());
                MiterRadiusWidget mrW = new MiterRadiusWidget(mode.getToolElement());
                getChildren().addAll(lbsW, lwW, pfW, mrW);
            }
            case CIRCLE -> { // Circle options
                LineWidthWidget lwW = new LineWidthWidget(mode.getToolElement());
                getChildren().addAll(lwW);
            }
            case DIMENSION -> {
                if (mode.getToolElement() instanceof Dimension d) {
                    // Dimension options

                    // Dim Style
                    DimensionTypeToggleWidget dimTypeWidget = new DimensionTypeToggleWidget(d);
                    TextSizeWidget textSizeWidget = new TextSizeWidget(d);
                    IntegerListWidget textRatioWidget = new IntegerListWidget(
                            d.getTextRatioProperty(), ElementText.Field.RATIO,
                            "TEXT_RATIO", "%",
                            ElementText.TEXT_RATIO_DEFAULT_OPTIONS);
                    // Unit
                    GridUnitListWidget unitWidget = new GridUnitListWidget(
                            d.getUnitProperty(), "UNIT", GridUnitProperty.Unit.MM);
                    // Precision
                    IntegerListWidget precisionWidget = new IntegerListWidget(
                            d.getPrecisionProperty(), Dimension.Field.PRECISION,
                            "PRECISION", null,
                            Dimension.PRECISION_OPTIONS);
                    // Show
                    RealValueListWidget2 lineWidthWidget = new RealValueListWidget2(
                            d.getWidthProperty(), WidthProperty.Field.WIDTH,
                            "LINE_WIDTH", null, ToolModeWidget.EDITABLE,
                            null, 1.0,
                            Wire.WIDTH_DEFAULT_OPTIONS
                    );
                    // Ext. Width (with auto)
                    RealValueListWidget2 extWidthWidget = new RealValueListWidget2(
                            d.getExtWidthProperty(), WidthProperty.Field.WIDTH,
                            "EXT_LINE_WIDTH", null, ToolModeWidget.EDITABLE,
                            d.getWidthProperty(), 1.0,
                            Wire.WIDTH_DEFAULT_OPTIONS
                    );
                    // Ext Length (with auto)  Auto = 10x width
                    RealValueListWidget2 extLengthWidget = new RealValueListWidget2(
                            d.getExtLengthProperty(), WidthProperty.Field.WIDTH,
                            "EXT_LINE_WIDTH", null, ToolModeWidget.EDITABLE,
                            d.getWidthProperty(), 10.0,
                            Wire.WIDTH_DEFAULT_OPTIONS
                    );
                    // Ext Offset (with auto)  Auto = 10x width
                    RealValueListWidget2 extOffsetWidget = new RealValueListWidget2(
                            d.getExtOffsetProperty(), WidthProperty.Field.WIDTH,
                            "EXT_LINE_WIDTH", null, ToolModeWidget.EDITABLE,
                            d.getWidthProperty(), 10.0,
                            Wire.WIDTH_DEFAULT_OPTIONS
                    );
                    getChildren().addAll(
                            dimTypeWidget,
                            textSizeWidget,
                            textRatioWidget,
                            unitWidget,
                            precisionWidget,
                            lineWidthWidget,
                            extWidthWidget,
                            extLengthWidget,
                            extOffsetWidget
                    );
                }
            }
            default -> {
                // Blank area.
            }
        }
    }

}
