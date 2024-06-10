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
package com.maehem.mangocad.view.library.symbol;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.view.widgets.toolmode.LineStyleWidget;
import com.maehem.mangocad.view.widgets.toolmode.LineWidthWidget;
import com.maehem.mangocad.view.widgets.inspector.LocationXYWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinDirectionWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinFuncToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinLengthToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinRotationToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinSwapLevelWidget;
import com.maehem.mangocad.view.widgets.toolmode.PinVisibilityToggleWidget;
import com.maehem.mangocad.view.widgets.toolmode.StringValueWidget;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorPropertiesListTab extends Tab {

    private Element element;
    private final VBox propertyNodes = new VBox();

    public SymbolEditorPropertiesListTab(Element item) {
        super("Inspector");
        this.setContent(propertyNodes);

        this.element = item;

        updateContent(item);
    }

    protected final void updateContent(Element item) {
        this.element = item;

        propertyNodes.getChildren().clear();
        if (item != null) {
            Label label = new Label(item.getElementName());
            label.setId("properties-list-heading");
            label.setPadding(new Insets(10));
            propertyNodes.getChildren().add(label);
            generatePropertyNodes();
        } else {
            Label label = new Label("Nothing Selected");
            label.setId("properties-list-heading-nothing");
            label.setPadding(new Insets(10));
            propertyNodes.getChildren().add(label);
        }

    }

    private void generatePropertyNodes() {
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

                propertyNodes.getChildren().addAll(nw, lxy, prw, pdw, psw, plw, pfw, pvw);
            }
            case Wire w -> {
                LocationXYWidget lxy1 = new LocationXYWidget(w.x1Property, w.y1Property, "LINE_LOCATION_1");
                LocationXYWidget lxy2 = new LocationXYWidget(w.x2Property, w.y2Property, "LINE_LOCATION_2");
                //LineBendStyleWidget lbsW = new LineBendStyleWidget(w);
                LineWidthWidget lwW = new LineWidthWidget(w);
                LineStyleWidget lsW = new LineStyleWidget(w);
                //MiterRadiusWidget mrW = new MiterRadiusWidget(w);

                propertyNodes.getChildren().addAll(lxy1, lxy2, lwW, lsW);
            }
            default -> {
            }
        }
    }

}
