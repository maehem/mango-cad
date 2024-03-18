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

import com.maehem.mangocad.view.ControlPanel;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public abstract class LibraryElementListViewItem {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    public LibraryElementListViewItem(String name, String description) {
        this.name.set(name);
        this.description.set(description);
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public final String getName() {
        return name.get();
    }

    public final void setName(String value) {
        name.set(value);
    }

    public final StringProperty descriptionProperty() {
        return description;
    }

    public final String getDescription() {
        return description.get();
    }

    public final void setDescription(String value) {
        description.set(value);
    }

    public abstract ContextMenu getContextMenu();

    public Tooltip getTooltip() {
        return new Tooltip("Tootip for: " + name.get());
//        String descString = ControlPanelUtils.getItemDescriptionFull(this);
//        if (descString != null && !descString.isEmpty()) {
//            Tooltip tt = new Tooltip();
//            String path = getFile().toPath().toUri().toString();
//            tt.setGraphic(MarkdownUtils.markdownNode(0.75, descString, path));
//            return tt;
//
//        } else {
//            return null;
//        }

    }

}
