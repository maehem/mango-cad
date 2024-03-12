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

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TextWidget extends Widget {

    TextField tf;

    public TextWidget(String title, String val, WidgetListener listener) {
        setId(title);

        //setMaxWidth(WIDGET_WIDTH);
        setBorder(new Border(new BorderStroke(
                Color.GRAY,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(1),
                Insets.EMPTY
        )));

        tf = new TextField(val);

        HBox.setHgrow(tf, Priority.ALWAYS);
        HBox.setMargin(tf, new Insets(1, 4, 1, 8));

        Text titleText = new Text(title);
        titleText.setFont(Font.font(20.0));
        titleText.setFill(Color.GREY);

        HBox valueZone = new HBox(tf);

        getChildren().addAll(titleText, valueZone);
    }

    public void setText(String text) {
        tf.setText(text);
    }

    @Override
    public void updateValue(double val) {
        tf.setText(String.valueOf(val));
    }

    @Override
    public void updateValue(String val) {
        tf.setText(val);
    }
    
}
