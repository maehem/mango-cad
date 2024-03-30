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
package com.maehem.mangocad.view.settings;

import com.maehem.mangocad.view.FillStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class FillStyleChooser extends ComboBox {

    private final static Color COLOR = Color.DARKGREY;
    private final static double SIZE = 32;

    @SuppressWarnings("unchecked")
    public FillStyleChooser(int selected) {

        ObservableList<Shape> list = FXCollections.observableArrayList(
                FillStyle.getSwatch(0, COLOR, SIZE),
                FillStyle.getSwatch(1, COLOR, SIZE),
                FillStyle.getSwatch(2, COLOR, SIZE),
                FillStyle.getSwatch(3, COLOR, SIZE),
                FillStyle.getSwatch(4, COLOR, SIZE),
                FillStyle.getSwatch(5, COLOR, SIZE),
                FillStyle.getSwatch(6, COLOR, SIZE),
                FillStyle.getSwatch(7, COLOR, SIZE),
                FillStyle.getSwatch(8, COLOR, SIZE),
                FillStyle.getSwatch(9, COLOR, SIZE),
                FillStyle.getSwatch(10, COLOR, SIZE),
                FillStyle.getSwatch(11, COLOR, SIZE),
                FillStyle.getSwatch(12, COLOR, SIZE),
                FillStyle.getSwatch(13, COLOR, SIZE),
                FillStyle.getSwatch(14, COLOR, SIZE),
                FillStyle.getSwatch(15, COLOR, SIZE)
        );

        getItems().addAll(list);
    }

}
