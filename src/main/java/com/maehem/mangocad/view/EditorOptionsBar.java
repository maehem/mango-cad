/*
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF
    licenses this file to you under the Apache License, Version 2.0
    (the "License"), you may not use this file except in compliance with the
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
    License for the specific language governing permissions and limitations
    under the License.
 */
package com.maehem.mangocad.view;

import com.maehem.mangocad.model.element.drawing.Drawing;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.widgets.CommandFieldWidget;
import com.maehem.mangocad.view.widgets.LayerChooser;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Separator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class EditorOptionsBar extends ToolBar {

    private static final int TOOLBAR_ICON_SIZE = 24;

    private final List<EditorOption> options;
    private final ArrayList<EditorOptionsBarListener> listeners = new ArrayList<>();
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private EditorOption currentOption;

    public EditorOptionsBar(Drawing drawing, List<EditorOption> options, EditorOptionsBarListener listener) {
        this.options = options;
        listeners.add(listener);

        for (EditorOption option : options) {
            switch (option) {
                case SEPARATOR -> {
                    getItems().add(new Separator());
                }
                case LAYER_SETTINGS, GRID -> {
                    ToggleButton b = createToolbarToggleButton(
                            option.bundleMessage(), option.iconPath()
                    );
                    b.setUserData(option);
                    b.setToggleGroup(toggleGroup);
                    getItems().add(b);
                }
                case LAYER_CHOOSER -> {
                    LayerChooser layerChooser = new LayerChooser(drawing.getPalette(), drawing.getLayers()); // Does nothing for this editor.
                    getItems().add(layerChooser);

                    layerChooser.getChooser().setOnAction((event) -> {
                        listeners.forEach((l) -> {
                            l.editorOptionBarWidgetAction(option, event);
                        });
                    });

                }
                case GRID_MOUSE_INFO -> {

                }
                case COMMAND_LINE -> {
                    CommandFieldWidget commandField = new CommandFieldWidget(option.bundleMessage(), option.iconPath());
                    getItems().add(commandField);
                }
                default -> {
                    LOGGER.log(Level.SEVERE, "Unknown EditorOption in List: " + option.name());
                }
            }
        }

        toggleGroup.selectedToggleProperty().addListener(
                (ObservableValue<? extends Toggle> observable,
                        Toggle oldValue, Toggle newValue) -> {
                    if (newValue == null) {
                        oldValue.setSelected(true);
                    } else {
                        EditorOption oldOption = currentOption;
                        currentOption = (EditorOption) newValue.getUserData();
                        listeners.forEach((l) -> {
                            l.editorOptionBarToggleButtonChanged(oldOption, currentOption);
                        });
                    }
                }
        );

    }

    private static ToggleButton createToolbarToggleButton(String name, String iconPath) {
        Image img = ViewUtils.getImage(iconPath);

        ToggleButton b = (ToggleButton) ViewUtils.createIconButton(name, img, TOOLBAR_ICON_SIZE, true);
        b.setTooltip(new Tooltip(name));
        b.getGraphic().setId("toolbar-button-icon");
        b.setId("toolbar-button");

        return b;
    }
}
