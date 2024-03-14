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
package com.maehem.mangocad.tools.widgets;

import java.text.DecimalFormat;
import javafx.geometry.Insets;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SliderWidget extends Widget {

    private final Slider slider;
    private final WidgetListener listener;
    private final Text valueText = new Text("999");

    public SliderWidget(String title, double min, double max, double val, WidgetListener listener) {
        super(title);

        this.listener = listener;

        //setId(title);
        //setMaxWidth(WIDGET_WIDTH);
//        setBorder(new Border(new BorderStroke(
//                Color.GRAY,
//                BorderStrokeStyle.SOLID,
//                CornerRadii.EMPTY,
//                new BorderWidths(1),
//                Insets.EMPTY
//        )));

        slider = new Slider(min, max, val);
        slider.setId(title);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        if (max == 360) {
            slider.setMajorTickUnit(90);
        } else {
            slider.setMajorTickUnit(((int) max - min) / 4);
        }
        slider.setBlockIncrement(0.1f);
        //slider.setPrefWidth(WIDGET_WIDTH - 100);
        HBox.setHgrow(slider, Priority.ALWAYS);
        HBox.setMargin(slider, new Insets(1, 4, 1, 8));

//        Text titleText = new Text(title);
//        titleText.setFont(Font.font(20.0));
//        titleText.setFill(Color.GREY);

        valueText.setFont(Font.font(12.0));
        valueText.setFill(Color.GREY);
        HBox.setMargin(valueText, new Insets(1, 8, 1, 12));

        HBox valueZone = new HBox(valueText, slider);

        getChildren().addAll(/*titleText, */valueZone);

        slider.valueProperty().addListener((ov, n, n2) -> {
            DecimalFormat df = new DecimalFormat("0.00");

            valueText.setText(df.format(n));
            listener.widgetChanged(this);
            //LOGGER.log(Level.SEVERE, "Widget Update: {0}  v:{1}", new Object[]{slider.getId(), n});
        });
    }

    public Slider getSlider() {
        return slider;
    }

    @Override
    public void updateValue(double val) {
        slider.setValue(val);
        valueText.setText(String.valueOf(val));
    }

    @Override
    public void updateValue(String val) {
        try {
            slider.setValue(Double.valueOf(val));
            valueText.setText(val);
        } catch (NumberFormatException ex) {
            valueText.setText("NaN");
        }
    }

}
