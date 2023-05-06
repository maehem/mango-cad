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

import com.maehem.mangocad.model.library.Library;
import com.maehem.mangocad.model.library.element.DeviceSet;
import com.maehem.mangocad.model.library.element.ElementType;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.Package3d;
import com.maehem.mangocad.model.library.element.Symbol;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Priority.ALWAYS;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryElementListView extends VBox {
    private static final Logger LOGGER = Logger.getLogger("LibraryElementListView");

    private final Library lib;
    private final ElementType type;
    private ListView listView;

    public LibraryElementListView(TableOfContentsPane listener, Library lib, ElementType type) { // Type?

        this.lib = lib;
        this.type = type;

        VBox.setVgrow(this, Priority.ALWAYS);
        ObservableList<String> list = FXCollections.observableArrayList();
        if (lib != null) {
            switch (type) {
                case DEVICE -> {
                    for (DeviceSet ds : lib.getDeviceSets()) {
                        list.add(ds.getName());
                    }
                }
                case FOOTPRINT -> { // Footprint
                    for (Footprint ds : lib.getPackages()) {
                        list.add(ds.getName());
                    }
                }
                case PACKAGE3D -> {
                    for (Package3d ds : lib.getPackages3d()) {
                        list.add(ds.getName());
                    }
                }
                case SYMBOL -> {
                    for (Symbol ds : lib.getSymbols()) {
                        list.add(ds.getName());
                    }
                }
                default ->
                    throw new AssertionError();
            }
        }
        
        listView = new ListView(list);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Your action here
                LOGGER.log(Level.SEVERE, "Selected item: {0}", newValue);
                listener.selectionChanged(type, newValue);
            }
        });
        
        // TODO: List in ScrollPane?
        ScrollPane sp = new ScrollPane(listView);
        sp.setMaxHeight(Double.MAX_VALUE);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getChildren().addAll(getHeader(), sp);
        VBox.setVgrow(sp, ALWAYS); // Make the ScrollPane fill height.

    }

    public void select( String item ) {
        LOGGER.log(Level.SEVERE, "{0} select: {1}", new Object[]{type, item});
        listView.getSelectionModel().select(item);     
    }
    
    private Node getHeader() {
        Label label = new Label(type.text());  // Type
        label.setPadding(new Insets(3, 0, 0, 6));
        Pane gapper = new Pane();
        HBox.setHgrow(gapper, Priority.ALWAYS);
        Button sortButton = new Button("^");
        Button addButton = new Button("+");
        HBox header = new HBox(label, gapper, sortButton, addButton);
        header.setMaxHeight(24);
        return header;

    }

    void clearSelections() {
        listView.getSelectionModel().clearSelection();
    }
}
