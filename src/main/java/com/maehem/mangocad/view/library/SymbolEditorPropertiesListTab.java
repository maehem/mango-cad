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
package com.maehem.mangocad.view.library;

import com.maehem.mangocad.model.LibraryElement;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorPropertiesListTab extends Tab {

    private final LibraryElement element;
    private final VBox propertyNodes = new VBox();

    public SymbolEditorPropertiesListTab(LibraryElement item) {
        super("Inspector");
        this.setContent(propertyNodes);

        this.element = item;

        updateContent(item);
    }

    protected void updateContent(LibraryElement item) {
        propertyNodes.getChildren().clear();
        if (item != null) {
            propertyNodes.getChildren().add(new Text("Symbol: " + element.getName()));
        } else {
            propertyNodes.getChildren().add(new Text("Nothing selected."));
        }

    }

}
