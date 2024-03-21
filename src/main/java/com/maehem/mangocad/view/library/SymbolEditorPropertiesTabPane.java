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

import com.maehem.mangocad.model._AQuantum;
import javafx.scene.control.TabPane;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorPropertiesTabPane extends TabPane {

    private SymbolEditorPropertiesListTab propertiesTab;

    public SymbolEditorPropertiesTabPane() {
        propertiesTab = new SymbolEditorPropertiesListTab(null);
        propertiesTab.setClosable(false);

        getTabs().addAll(propertiesTab);
    }

    public void setPropertiesItem(_AQuantum item) {
        getSelectionModel().select(propertiesTab);

        // Set
        // propertiesTab.setItem( item );
        // Refill the properties widgets.
        propertiesTab.updateContent(item);

    }
}