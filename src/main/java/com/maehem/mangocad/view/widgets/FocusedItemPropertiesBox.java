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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.property.GridUnitProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import com.maehem.mangocad.view.widgets.inspector.DimensionTypeToggleWidget;
import com.maehem.mangocad.view.widgets.inspector.GridUnitListWidget;
import com.maehem.mangocad.view.widgets.inspector.InspectorWidget;
import com.maehem.mangocad.view.widgets.inspector.IntegerListWidget;
import com.maehem.mangocad.view.widgets.inspector.LineStyleWidget;
import com.maehem.mangocad.view.widgets.inspector.LineWidthWidget;
import com.maehem.mangocad.view.widgets.inspector.LocationXYWidget;
import com.maehem.mangocad.view.widgets.inspector.MirrorToggleWidget;
import com.maehem.mangocad.view.widgets.inspector.PinDirectionWidget;
import com.maehem.mangocad.view.widgets.inspector.PinFuncToggleWidget;
import com.maehem.mangocad.view.widgets.inspector.PinLengthToggleWidget;
import com.maehem.mangocad.view.widgets.inspector.PinRotationToggleWidget;
import com.maehem.mangocad.view.widgets.inspector.PinSwapLevelWidget;
import com.maehem.mangocad.view.widgets.inspector.PinVisibilityToggleWidget;
import com.maehem.mangocad.view.widgets.inspector.RealValueListWidget2;
import com.maehem.mangocad.view.widgets.inspector.RotationWidget;
import com.maehem.mangocad.view.widgets.inspector.StringValueWidget;
import com.maehem.mangocad.view.widgets.inspector.TextAlignWidget;
import com.maehem.mangocad.view.widgets.inspector.TextDistanceWidget;
import com.maehem.mangocad.view.widgets.inspector.TextFontWidget;
import com.maehem.mangocad.view.widgets.inspector.TextRatioWidget;
import com.maehem.mangocad.view.widgets.inspector.TextSizeWidget;
import com.maehem.mangocad.view.widgets.toolmode.ToolModeWidget;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class FocusedItemPropertiesBox extends VBox {

    public FocusedItemPropertiesBox() {
        updateContent(null);
    }

    public final void updateContent(Element item) {
        for (Node n : getChildren()) {
            if (n instanceof InspectorWidget w) {
                w.stopListening();
            }
        }

        getChildren().clear();
        if (item != null) {
            Label label = new Label(item.getElementName());
            label.setId("properties-list-heading");
            label.setPadding(new Insets(10));
            getChildren().add(label);
            generatePropertyNodes(item);
        } else {
            Label label = new Label("Nothing Selected");
            label.setId("properties-list-heading-nothing");
            label.setPadding(new Insets(10));
            getChildren().add(label);
        }

    }

    private void generatePropertyNodes(Element element) {
        switch (element) {
            case Pin p -> {
                // XY location
                StringValueWidget nw = new StringValueWidget(p.getNameProperty(), "PIN_NAME");
                LocationXYWidget lxy = new LocationXYWidget(p.getXProperty(), p.getYProperty(), "PIN_LOCATION");
                PinRotationToggleWidget prw = new PinRotationToggleWidget(p);
                PinDirectionWidget pdw = new PinDirectionWidget(p);
                PinSwapLevelWidget psw = new PinSwapLevelWidget(p);
                PinLengthToggleWidget plw = new PinLengthToggleWidget(p);
                PinFuncToggleWidget pfw = new PinFuncToggleWidget(p);
                PinVisibilityToggleWidget pvw = new PinVisibilityToggleWidget(p);

                getChildren().addAll(nw, lxy, prw, pdw, psw, plw, pfw, pvw);
            }
            case Wire w -> {
                LocationXYWidget lxy1 = new LocationXYWidget(w.x1Property, w.y1Property, "LINE_LOCATION_1");
                LocationXYWidget lxy2 = new LocationXYWidget(w.x2Property, w.y2Property, "LINE_LOCATION_2");
                //LineBendStyleWidget lbsW = new LineBendStyleWidget(w);
                LineWidthWidget lwW = new LineWidthWidget(w);
                LineStyleWidget lsW = new LineStyleWidget(w);
                //MiterRadiusWidget mrW = new MiterRadiusWidget(w);

                getChildren().addAll(lxy1, lxy2, lwW, lsW);
            }
            case ElementText t -> {
                StringValueWidget valw = new StringValueWidget(t.valueProperty, "TEXT_VALUE");
                LocationXYWidget lxy = new LocationXYWidget(t.xProperty, t.yProperty, "TEXT_LOCATION");
                RotationWidget rW = new RotationWidget(t, "ROTATION");
                MirrorToggleWidget mW = new MirrorToggleWidget(t, "MIRROR");
                TextSizeWidget tsW = new TextSizeWidget(t, "TEXT_SIZE"); // Upgrade to EditableDoubleListWidget
                TextRatioWidget trW = new TextRatioWidget(t, "TEXT_RATIO");
                TextFontWidget tfW = new TextFontWidget(t, "TEXT_FONT");
                TextAlignWidget taW = new TextAlignWidget(t, "TEXT_ALIGN");
                TextDistanceWidget tdW = new TextDistanceWidget(t, "TEXT_DISTANCE");

                getChildren().addAll(valw, lxy, rW, new Region(), mW, tsW, trW, tfW, taW, tdW);
            }
            case Dimension d -> {
                DimensionTypeToggleWidget dimTypeWidget = new DimensionTypeToggleWidget(d, "DIM_TYPE");
                TextSizeWidget textSizeWidget = new TextSizeWidget(d, "TEXT_SIZE");
                IntegerListWidget textRatioWidget = new IntegerListWidget(
                        d.getTextRatioProperty(), ElementText.Field.RATIO,
                        "TEXT_RATIO", "%",
                        ElementText.RATIO_DEFAULT_OPTIONS);
                GridUnitListWidget unitWidget = new GridUnitListWidget(
                        d.getUnitProperty(), "UNIT", GridUnitProperty.Unit.MM);
                IntegerListWidget precisionWidget = new IntegerListWidget(
                        d.getPrecisionProperty(), Dimension.Field.PRECISION,
                        "DIM_PRECISION", null,
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
                        "DIM_EXT_LINE_WIDTH", null, ToolModeWidget.EDITABLE,
                        d.getWidthProperty(), 1.0,
                        Wire.WIDTH_DEFAULT_OPTIONS
                );
                // Ext Length (with auto)  Auto = 10x width
                RealValueListWidget2 extLengthWidget = new RealValueListWidget2(
                        d.getExtLengthProperty(), WidthProperty.Field.WIDTH,
                        "DIM_EXT_LINE_LENGTH", null, ToolModeWidget.EDITABLE,
                        d.getWidthProperty(), 10.0,
                        Wire.WIDTH_DEFAULT_OPTIONS
                );
                // Ext Offset (with auto)  Auto = 10x width
                RealValueListWidget2 extOffsetWidget = new RealValueListWidget2(
                        d.getExtOffsetProperty(), WidthProperty.Field.WIDTH,
                        "DIM_EXT_LINE_OFFSET", null, ToolModeWidget.EDITABLE,
                        d.getWidthProperty(), 10.0,
                        Wire.WIDTH_DEFAULT_OPTIONS
                );
                getChildren().addAll(
                        dimTypeWidget,
                        textSizeWidget, textRatioWidget,
                        unitWidget, precisionWidget,
                        lineWidthWidget,
                        extWidthWidget, extLengthWidget, extOffsetWidget
                );
            }
            default -> {
            }
        }
    }
}