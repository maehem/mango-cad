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
package com.maehem.mangocad.view.widgets.toolbar;

import com.maehem.mangocad.model.element.LibraryElement;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.widgets.LayerChooser;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Layer Chooser Widget.
 *
 * A pull-down combo-box for selecting a layer in the design.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LayerChooserWidget extends HBox {

    //private static final String CELL_CSS_ID = "layer-chooser-button";

    private final Label label = new Label("Layers: ");

    private final LayerChooser chooser;

    public LayerChooserWidget(Layers layers, LibraryElement libraryElement) {

        chooser = new LayerChooser(layers, libraryElement);
        //chooser.getItems().addAll(layers.getElements());
        //chooser.setButtonCell(new ColorRectCell(palette));
        //chooser.setCellFactory((ListView<LayerElement> list) -> new ColorRectCell(palette));
        //chooser.getSelectionModel().select(0);
        //chooser.setPrefWidth(140); // TODO move to CSS?
        //chooser.setPrefHeight(20);

        getChildren().addAll(label, chooser);

        setSpacing(4);
        setAlignment(Pos.CENTER);
    }

    public ComboBox<LayerElement> getChooser() {
        return chooser;
    }

//    private static class ColorRectCell extends ListCell<LayerElement> {
//
//        ColorPalette palette;
//
//        public ColorRectCell(ColorPalette palette) {
//            this.palette = palette;
//            setId(CELL_CSS_ID);
//        }
//
//        @Override
//        public void updateItem(LayerElement item, boolean empty) {
//            super.updateItem(item, empty);
//            Rectangle rect = new Rectangle(16, 16);
//            if (item != null) {
//                Color c = ColorUtils.getColor(palette.getHex(item.getColorIndex()));
//                rect.setFill(c);
//                setGraphic(rect);
//                setText(item.getNumber() + "  " + item.getName());
//            }
//        }
//    }
}
