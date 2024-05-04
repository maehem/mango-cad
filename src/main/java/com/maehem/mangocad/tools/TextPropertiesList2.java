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
import com.maehem.mangocad.tools.widgets.ListSelectWidget;
import com.maehem.mangocad.tools.widgets.SliderWidget;
import com.maehem.mangocad.tools.widgets.TextWidget;
import com.maehem.mangocad.tools.widgets.ToggleWidget;
import com.maehem.mangocad.tools.widgets.Widget;
import com.maehem.mangocad.tools.widgets.WidgetListener;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TextPropertiesList2 extends VBox implements WidgetListener {

    ElementText et;
    TextTester2 parent;

    private final TextWidget textWidget = new TextWidget("Text", "Hello There.", this);
    private final SliderWidget ratioWidget = new SliderWidget("Ratio", 1, 100, 15, this);
    private final SliderWidget posXWidget = new SliderWidget("X", -5, 5, 0, this);
    private final SliderWidget posYWidget = new SliderWidget("Y", -5, 5, 0, this);
    private final SliderWidget rotWidget = new SliderWidget("Rotate", 0, 360, 0, this);
    private final ToggleWidget constrainWidget = new ToggleWidget("Constrained", this);
    private final ListSelectWidget alignWidget = new ListSelectWidget("Alignment", this);
    private final ToggleWidget spinWidget = new ToggleWidget("Spin", this);
    private final ToggleWidget mirrorWidget = new ToggleWidget("Mirror", this);

    public TextPropertiesList2(TextTester2 parent, ElementText et) {
        this.parent = parent;
        this.et = et;

        //ratioWidget.getSlider().setBlockIncrement(25);
        ratioWidget.getSlider().setMajorTickUnit(25);
        ratioWidget.getSlider().setMinorTickCount(0);
        ratioWidget.getSlider().setShowTickLabels(true);
        ratioWidget.getSlider().setSnapToTicks(false);

        setMinWidth(TextTester.WIDGET_WIDTH);
        getChildren().add(textWidget);
        getChildren().add(ratioWidget);
        getChildren().add(posXWidget);
        getChildren().add(posYWidget);
        getChildren().add(spinWidget);
        getChildren().add(mirrorWidget);
        getChildren().add(rotWidget);
        getChildren().add(constrainWidget);
        getChildren().add(alignWidget);

        updateWidget();
    }

    private void updateWidget() {
        textWidget.setText(et.getValue());
        ratioWidget.getSlider().setValue((double) et.getRatio());
        posXWidget.getSlider().setValue(et.getX());
        posYWidget.getSlider().setValue(et.getY());
        rotWidget.getSlider().setValue(et.getRot());
        spinWidget.getCheckBox().setSelected(et.isSpin());
        mirrorWidget.getCheckBox().setSelected(et.isMirrored());

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
            case "Ratio" -> {
                SliderWidget s = (SliderWidget) widget;
                et.setRatio((int) s.getSlider().getValue());
            }
            case "Rotate" -> {
                SliderWidget s = (SliderWidget) widget;
                et.setRot(s.getSlider().getValue());
            }
            case "Constrained" -> {
                ToggleWidget s = (ToggleWidget) widget;
                et.setConstrained(s.getValue());
                if (s.getValue()) {
                    Slider slider = rotWidget.getSlider();
                    slider.setSnapToTicks(true);
                    slider.setMajorTickUnit(90.0);
                    slider.setMinorTickCount(0);
                    slider.setBlockIncrement(90.0);
                } else {
                    Slider slider = rotWidget.getSlider();
                    slider.setSnapToTicks(true);
                    slider.setMajorTickUnit(0.1);
                }
            }
            case "Alignment" -> {
                ListSelectWidget s = (ListSelectWidget) widget;
                et.setAlign(s.getValue());
            }
            case "Spin" -> {
                ToggleWidget s = (ToggleWidget) widget;
                et.setSpin(s.getValue());
            }
            case "Mirror" -> {
                ToggleWidget s = (ToggleWidget) widget;
                et.setMirror(s.getValue());
            }
        }

    }

}
