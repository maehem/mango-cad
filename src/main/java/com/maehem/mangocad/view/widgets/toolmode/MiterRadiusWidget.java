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
import com.maehem.mangocad.model.element.basic.ElementPolygon;
import com.maehem.mangocad.model.element.basic.Wire;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import static com.maehem.mangocad.view.widgets.toolmode.ToolModeWidget.ICON_SIZE;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class MiterRadiusWidget extends ToolModeWidget {

    private static final String ICON_BEVEL_PATH = "/icons/miter.png";
    private static final String ICON_FILLET_PATH = "/icons/fillet.png";
    private final ToggleGroup group = new ToggleGroup();

    // TODO: Cell Renderer that shows "inch (mm)mm", flips based on grid mode.
    private final ObservableList<Double> options
            = FXCollections.observableArrayList(
                    0.0,
                    0.01,
                    0.0125,
                    0.025,
                    0.03937008,
                    0.05,
                    0.10,
                    0.5,
                    1.0,
                    2.0,
                    5.0,
                    10.0
            );
    @SuppressWarnings("unchecked")
    private final ComboBox comboBox = new ComboBox(options);
    private final Wire wire;
    private final ElementPolygon poly;

    @SuppressWarnings({"unchecked"})
    public MiterRadiusWidget(Element e) {
        if (e instanceof Wire p) {
            this.wire = p;
            this.wire.addListener(this);
            this.poly = null;
        } else if (e instanceof ElementPolygon p) {
            this.poly = p;
            this.poly.addListener(this);
            this.wire = null;
        } else {
            this.wire = null;
            this.poly = null;
            LOGGER.log(Level.SEVERE, "MiterRadiusWidget: element is not of type Wire or ElementPolygon! type:{0}", e.getElementName());
        }

        Image imgBevel = ViewUtils.getImage(ICON_FILLET_PATH);
        Image imgFillet = ViewUtils.getImage(ICON_BEVEL_PATH);

        ImageView bevelImg = ViewUtils.createIcon(imgBevel, ICON_SIZE);
        ImageView filletImg = ViewUtils.createIcon(imgFillet, ICON_SIZE);

        ToggleButton bevelButton = new ToggleButton("", bevelImg); // TODO MSG Tooltip
        ToggleButton filletButton = new ToggleButton("", filletImg);

        bevelButton.setToggleGroup(group);
        bevelButton.setUserData("BEVEL");
        bevelButton.setSelected(true);
        bevelButton.setTooltip(new Tooltip(MSG.getString("MITER_BEVEL_TOOLTIP")));

        filletButton.setToggleGroup(group);
        filletButton.setUserData("ROUND");
        filletButton.setSelected(false);
        bevelButton.setTooltip(new Tooltip(MSG.getString("MITER_FILLET_TOOLTIP")));

        group.selectToggle(bevelButton);

        Tooltip radiusTooltip = new Tooltip(MSG.getString("MITER_RADIUS_TOOLTIP"));
        //setPrefWidth(170);
        Label iconLabel = new Label(MSG.getString("MITER_RADIUS") + ":");
        iconLabel.setTooltip(radiusTooltip);
        iconLabel.setPadding(new Insets(4));
        iconLabel.setAlignment(Pos.BASELINE_CENTER);
        double labelWidth = 55;
        iconLabel.setMinWidth(labelWidth);
        iconLabel.setPrefWidth(labelWidth);

        // TODO Miter buttons
        comboBox.setButtonCell(new EditableItemCell());
        comboBox.setTooltip(radiusTooltip);
        comboBox.setEditable(true);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setPrefWidth(120);

        getChildren().addAll(iconLabel, comboBox, bevelButton, filletButton);

        comboBox.setOnAction((t) -> {
            if (wire != null) {
                wire.setWidth((double) comboBox.getSelectionModel().getSelectedItem());
            } else if (poly != null) {
                poly.setWidth((double) comboBox.getSelectionModel().getSelectedItem());
            }
            t.consume();
        });
    }

    @SuppressWarnings("unchecked")
    private void updateComboState(double pl) {
        for (Double t : options) {
            if (t == pl) {
                comboBox.getSelectionModel().select(t);
                break;
            }
        }
    }

    @Override
    public void stopListening() {
        if (wire != null) {
            wire.removeListener(this);
        } else if (poly != null) {
            poly.removeListener(this);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.

        // TODO: Needs work!
//        if (!field.equals(WireField.CURVE) && !field.equals(ElementPolygonField.VERTEX)) {
//            return;
//        }
//        if (newVal == null) {
//            return;
//        }
//        LOGGER.log(Level.SEVERE, "MiterRadiusWidget: Wire miterRadius: ==> {0}", newVal.toString());
//
//        if (newVal instanceof Double pd) {
//            updateComboState(pd);
//        }
    }

    public class EditableItemCell extends ListCell<Double> {

        private final TextField textField = new TextField();

        public EditableItemCell() {
            textField.setPrefWidth(100);
            textField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
            textField.setOnAction(e -> {
                //getItem().setName(textField.getText());
                setText(textField.getText());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            });
            setGraphic(textField);
        }

        @Override
        protected void updateItem(Double client, boolean empty) {
            super.updateItem(client, empty);
            if (isEditing()) {
                textField.setText(String.valueOf(client));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(client));
                }
            }
        }

    }
}
