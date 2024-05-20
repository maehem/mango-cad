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
package com.maehem.mangocad.view;

import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class EditorToolbar extends ToolBar {

    private static final int TOOLBAR_ICON_SIZE = 20;
    private static final int GAP = 8;

    //private final List<EditorTools> tools;
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private EditorTool currentTool = EditorTool.SELECT;
    private final ArrayList<EditorToolbarListener> listeners = new ArrayList<>();

    public EditorToolbar(List<EditorTool> tools, EditorToolbarListener listener) {
        //this.tools = tools;
        listeners.add(listener);

        setOrientation(Orientation.VERTICAL);
        setPrefWidth(TOOLBAR_ICON_SIZE * 2 + GAP);

        FlowPane toolGroup = makeToolGroup();

        getItems().add(toolGroup);
        for (EditorTool tool : tools) {
            if (tool.equals(EditorTool.SEPARATOR)) {
                // End current FlowPane and Insert new Separator
                getItems().add(new Separator());

                toolGroup = makeToolGroup(); // Start new Flow Pane
                getItems().add(toolGroup);
            } else {
                ToggleButton b = createToolbarToggleButton(tool.bundleMessage(), tool.iconPath());
                b.setUserData(tool);
                if (tool.equals(currentTool)) {
                    b.setSelected(true);
                }
                toolGroup.getChildren().add(b);
                b.setToggleGroup(toggleGroup);

            }
        }

        toggleGroup.selectedToggleProperty().addListener(
                (ObservableValue<? extends Toggle> observable,
                        Toggle oldValue, Toggle newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            } else {
                EditorTool oldTool = currentTool;
                currentTool = (EditorTool) newValue.getUserData();
                listeners.forEach((l) -> {
                    l.editorToolBarButtonChanged(oldTool, currentTool);
                });
            }
        });
    }

    public void setCurrentTool(EditorTool tool) {
        LOGGER.log(Level.SEVERE, "Toolbar setCurrentTool: old:{0}  new:{1}", new Object[]{currentTool.name(), tool.name()});
        //EditorTool oldTool = currentTool;
        //currentTool = tool;
        for (Toggle t : toggleGroup.getToggles()) {
            if (t.getUserData() == tool) {
                t.setSelected(true);
                break;
            }
        }
//        listeners.forEach((l) -> {
//            l.editorToolBarButtonChanged(oldTool, currentTool);
//        });
    }

    private FlowPane makeToolGroup() {
        FlowPane toolGroup = new FlowPane();
        toolGroup.setPrefWrapLength(TOOLBAR_ICON_SIZE * 2 + GAP * 3);
        toolGroup.setHgap(GAP);
        toolGroup.setVgap(GAP);

        return toolGroup;
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
