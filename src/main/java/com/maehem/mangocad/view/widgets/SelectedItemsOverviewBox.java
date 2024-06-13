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
import com.maehem.mangocad.view.widgets.selection.SelectionOverviewWidget;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SelectedItemsOverviewBox extends VBox {

    public SelectedItemsOverviewBox() {
        setSpacing(2);
        setFillWidth(true);
    }

    public final void updateItems(List<Element> items) {
        // Might not listen to these.
//        for (Node n : getChildren()) {
//            if (n instanceof SelectionOverviewWidget w) {
//                w.stopListening();
//            }
//        }

        getChildren().clear();
        if (items != null) {
//            Label label = new Label("Selected Objects");
//            label.setId("properties-list-heading");
//            label.setPadding(new Insets(10));
//            getChildren().add(label);
            generatePropertyNodes(items);
        } else {
            Label label = new Label("Nothing Selected");
            label.setId("properties-list-heading-nothing");
            label.setPadding(new Insets(10));
            getChildren().add(label);
        }

    }

    private void generatePropertyNodes(List<Element> items) {
        if (items != null) {
            for (Element element : items) {
                switch (element) {
                    default -> {
                        SelectionOverviewWidget widget = new SelectionOverviewWidget(element);
                        getChildren().add(widget);
                    }
                }
            }
        }
    }

}
