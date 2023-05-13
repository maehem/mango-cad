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
package com.maehem.mangocad.view.controlpanel;

import java.io.File;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public abstract class ControlPanelListItem {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty lastModified = new SimpleStringProperty();

    private final File file;

    private final BooleanProperty inUse = new SimpleBooleanProperty();

    //private boolean inUse = false;
//    public ControlPanelListItem() {
//    }
    public ControlPanelListItem(String name, String description) {
        this(name, description, null );
    }

    public ControlPanelListItem(String name, String description, File file) {
        this.name.set(name);
        this.description.set(description);
        this.inUse.set(false);
        this.file = file;
        //this.lastModified.set("");
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

    public final BooleanProperty inUseProperty() {
        return inUse;
    }

    public final Boolean getInUse() {
        return inUse.get();
    }

    public final void setInUse(boolean value) {
        inUse.set(value);
    }

    public final StringProperty lastModifiedProperty() {
        return lastModified;
    }

    public final String getLastModified() {
        return lastModified.get();
    }

    public final void setLastModified(String value) {
        lastModified.set(value);
    }
    
    public final File getFile() {
        return file;
    }

    public abstract ContextMenu getContextMenu();
    
    public abstract Image getImage();
    
    public Tooltip getTooltip() {
        String descString = ControlPanelUtils.getItemDescriptionFull(this);
        if ( descString != null && !descString.isEmpty() ) {
                Tooltip tt = new Tooltip();
                tt.setGraphic(ControlPanelUtils.markdownNode(0.75, descString));
                return tt;
            
        } else {
            return null;
        }

//        
//        
//        
//        if ( getDescription().isEmpty() ) {
//            return null;
//        }
//        Tooltip tt = new Tooltip();
//        tt.setGraphic(ControlPanelUtils.markdownNode(getDescription()));
//        return tt;
    }

}
