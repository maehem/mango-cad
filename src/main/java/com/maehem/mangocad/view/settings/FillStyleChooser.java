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
import java.util.ArrayList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.paint.Color;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class FillStyleChooser extends ComboBox<Integer> {

    private final static Color COLOR = Color.LIGHTGRAY; // TODO use CSS stylesheet

    public FillStyleChooser(int selected, double size) {

        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            list.add(i);
        }
        getItems().addAll(list);
        getSelectionModel().select(selected);

        setCellFactory((ListView<Integer> p) -> new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean bln) {
                super.updateItem(item, bln);
                if (item != null) {
                    setGraphic(FillStyle.getSwatch(item, COLOR, size));
                }
            }
        });

        setButtonCell(new ComboBoxListCell<Integer>() {

            @Override
            public void updateItem(Integer item, boolean bln) {
                super.updateItem(item, bln);
                if (item != null) {
                    setGraphic(FillStyle.getSwatch(item, COLOR, size));
                    setText(null);
                }
            }
        });
    }
}
