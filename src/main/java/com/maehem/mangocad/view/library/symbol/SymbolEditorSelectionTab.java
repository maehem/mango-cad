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
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorSelectionTab extends Tab {

    private final VBox propertyNodes = new VBox();

    public SymbolEditorSelectionTab(List<Element> selectedItems) {
        super("Selection");

        this.setContent(propertyNodes);

        updateContent(selectedItems);
    }

    protected final void updateContent(List<Element> selectedItems) {
//        for (Node n : propertyNodes.getChildren()) {
//            if (n instanceof InspectorWidget w) {
//                w.stopListening();
//            }
//        }

        propertyNodes.getChildren().clear();
        if (selectedItems != null) {
            for (Element item : selectedItems) {
                Label label = new Label(item.getElementName());
                label.setId("properties-list-heading");
                label.setPadding(new Insets(10));
                propertyNodes.getChildren().add(label);
            }
        }
    }

}
