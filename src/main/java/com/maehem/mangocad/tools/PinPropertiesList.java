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
package com.maehem.mangocad.tools;

import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.enums.PinDirection;
import com.maehem.mangocad.model.element.enums.PinFunction;
import com.maehem.mangocad.model.element.enums.PinLength;
import com.maehem.mangocad.tools.widgets.ListSelectWidget;
import com.maehem.mangocad.tools.widgets.SliderWidget;
import com.maehem.mangocad.tools.widgets.TextWidget;
import com.maehem.mangocad.tools.widgets.Widget;
import com.maehem.mangocad.tools.widgets.WidgetListener;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinPropertiesList extends VBox implements WidgetListener {

    Pin pinElement;
    PinTester parent;

    private final TextWidget nameWidget = new TextWidget("Pin Name", "Hello There.", this);
    private final TextWidget padWidget = new TextWidget("Pad Name", "999", this);
    private final SliderWidget posXWidget = new SliderWidget("X", -10.16, 10.16, 0, this);
    private final SliderWidget posYWidget = new SliderWidget("Y", -10.16, 10.16, 0, this);
    private final SliderWidget rotWidget = new SliderWidget("Rotate", 0, 360, 0, this);

    private final ListSelectWidget directionWidget = new ListSelectWidget("Direction",
            PinDirection.asStringList(), this);
    private final ListSelectWidget functionWidget = new ListSelectWidget("Function",
            PinFunction.asStringList(), this);
    private final ListSelectWidget lengthWidget = new ListSelectWidget("Length",
            PinLength.asStringList(), this);

    public PinPropertiesList(PinTester parent, Pin et) {
        this.parent = parent;
        this.pinElement = et;

        posXWidget.setSnap(2.54);
        posYWidget.setSnap(2.54);

        setMinWidth(TextTester.WIDGET_WIDTH);
        getChildren().add(nameWidget);
        getChildren().add(padWidget);
        getChildren().add(posXWidget);
        getChildren().add(posYWidget);
        getChildren().add(rotWidget);
        getChildren().add(directionWidget);
        getChildren().add(functionWidget);
        getChildren().add(lengthWidget);

        updateWidget();
    }

    private void updateWidget() {
        nameWidget.setText(pinElement.getName());
        posXWidget.getSlider().setValue(pinElement.getX());
        posYWidget.getSlider().setValue(pinElement.getY());
        rotWidget.getSlider().setValue(pinElement.getRot());
    }

    @Override
    public void widgetChanged(Widget widget) {
        switch (widget.getId()) {
            case "X" -> {
                SliderWidget s = (SliderWidget) widget;
                pinElement.setX(s.getSlider().getValue());
            }
            case "Y" -> {
                SliderWidget s = (SliderWidget) widget;
                pinElement.setY(s.getSlider().getValue());
            }
            case "Rotate" -> {
                SliderWidget s = (SliderWidget) widget;
                pinElement.setRot(s.getSlider().getValue());
            }
            case "Direction" -> {
                ListSelectWidget s = (ListSelectWidget) widget;
                pinElement.setDirection(PinDirection.fromCode(s.getValue()));
            }
            case "Function" -> {
                ListSelectWidget s = (ListSelectWidget) widget;
                pinElement.setFunction(PinFunction.fromCode(s.getValue()));
            }
            case "Length" -> {
                ListSelectWidget s = (ListSelectWidget) widget;
                pinElement.setLength(PinLength.fromCode(s.getValue()));
            }

        }

    }

}
