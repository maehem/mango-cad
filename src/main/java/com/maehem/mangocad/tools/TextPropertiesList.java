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

import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.enums.TextAlign;
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
public class TextPropertiesList extends VBox implements WidgetListener {

    ElementText et;
    TextTester parent;

    private final TextWidget textWidget = new TextWidget("Text", "Hello There.", this);
    private final SliderWidget posXWidget = new SliderWidget("X", -5, 5, 0, this);
    private final SliderWidget posYWidget = new SliderWidget("Y", -5, 5, 0, this);
    private final SliderWidget rotWidget = new SliderWidget("Rotate", 0, 360, 0, this);
    private final ListSelectWidget alignWidget = new ListSelectWidget("Alignment", TextAlign.asStringList(), this);

    public TextPropertiesList(TextTester parent, ElementText et) {
        this.parent = parent;
        this.et = et;


        setMinWidth(TextTester.WIDGET_WIDTH);
        getChildren().add(textWidget);
        getChildren().add(posXWidget);
        getChildren().add(posYWidget);
        getChildren().add(rotWidget);
        getChildren().add(alignWidget);

        updateWidget();
    }

    private void updateWidget() {
        textWidget.setText(et.getValue());
        posXWidget.getSlider().setValue(et.getX());
        posYWidget.getSlider().setValue(et.getY());
        rotWidget.getSlider().setValue(et.getRot());

    }

    @Override
    public void widgetChanged(Widget widget) {
        switch (widget.getId()) {
            case "X" -> {
                SliderWidget s = (SliderWidget) widget;
                et.setX(s.getSlider().getValue());
            }
            case "Y" -> {
                SliderWidget s = (SliderWidget) widget;
                et.setY(s.getSlider().getValue());
            }
            case "Rotate" -> {
                SliderWidget s = (SliderWidget) widget;
                et.getRotation().setValue(s.getSlider().getValue());
            }
            case "Alignment" -> {
                ListSelectWidget s = (ListSelectWidget) widget;
                et.setAlign(s.getValue());
            }
        }

        parent.updateContent();
    }

}
