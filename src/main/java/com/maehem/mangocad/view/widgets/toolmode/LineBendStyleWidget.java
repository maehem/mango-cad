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
package com.maehem.mangocad.view.widgets.toolmode;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.Pin;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import static com.maehem.mangocad.view.widgets.toolmode.ToolModeWidget.ICON_SIZE;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LineBendStyleWidget extends ToolModeWidget {

    private final static Image bend90Img = ViewUtils.getImage("/icons/bend-90.png");
    private final static Image bend45Img = ViewUtils.getImage("/icons/bend-45.png");
    private final static Image bendFreeImg = ViewUtils.getImage("/icons/bend-free.png");
    private final static Image bendCurveImg = ViewUtils.getImage("/icons/bend-curve.png");
    private final static Image bendSImg = ViewUtils.getImage("/icons/bend-s.png");

    private final ImageView bend90View = ViewUtils.createIcon(bend90Img, ICON_SIZE);
    private final ImageView bend45View = ViewUtils.createIcon(bend45Img, ICON_SIZE);
    private final ImageView bendFreeView = ViewUtils.createIcon(bendFreeImg, ICON_SIZE);
    private final ImageView bendCurveAView = ViewUtils.createIcon(bendCurveImg, ICON_SIZE);
    private final ImageView bendCurveBView = ViewUtils.createIcon(bendCurveImg, ICON_SIZE);
    private final ImageView bendSView = ViewUtils.createIcon(bendSImg, ICON_SIZE);

    //Label bend90Label = new Label("", bend90View);
    private final ObservableList<ImageView> options
            = FXCollections.observableArrayList(
                    bend90View,
                    bend45View,
                    bendFreeView,
                    bendCurveAView,
                    bendCurveBView,
                    bendSView
            );
    @SuppressWarnings("unchecked")
    private final ComboBox comboBox = new ComboBox(options);
    private final Pin pin;

    @SuppressWarnings("unchecked")
    public LineBendStyleWidget(Element e) {
        if (e instanceof Pin p) {
            this.pin = p;
            this.pin.addListener(this);
        } else {
            this.pin = null;
            LOGGER.log(Level.SEVERE, "LineBendStyleWidget: element is not of type Pin!");
        }

        comboBox.getSelectionModel().selectFirst();
        comboBox.setButtonCell(new SelectedItemCell());

        bendCurveBView.setRotate(180);

        getChildren().addAll(comboBox);

        comboBox.setOnAction((t) -> {
            LOGGER.log(Level.SEVERE, "LineBendStyleWidget ComboBox changed.");
            //pin.setDirection(PinDirection.fromCode((String) comboBox.getSelectionModel().getSelectedItem()));
            t.consume();
        });
    }

    @Override
    public void stopListening() {
        //pin.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
//        // Update widgets.
//        if (!field.equals(PinField.DIRECTION)) {
//            return;
//        }
//        if (newVal == null) {
//            return;
//        }
//        LOGGER.log(Level.SEVERE, "PinDirectionWidget: Pin dir: ==> {0}", newVal.toString());
//
//        if (newVal instanceof PinDirection pd) {
//            updateComboState(pd);
//        }
    }

    private static class SelectedItemCell extends ListCell<ImageView> {
        @Override
        protected void updateItem(ImageView item, boolean btl) {
            super.updateItem(item, btl);
            if (item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(ViewUtils.createIcon(item.getImage(), ICON_SIZE));
                setText(null);
            }
        }
    }
}
