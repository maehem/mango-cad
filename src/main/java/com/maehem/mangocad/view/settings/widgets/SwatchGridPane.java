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
package com.maehem.mangocad.view.settings.widgets;

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.FillStyle;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SwatchGridPane extends GridPane {

    private final static double SIZE = 16;
    private static final Tooltip SWATCH_TIP = new Tooltip(
            """
            Tip: Adjusting the color within a selected square changes all
            layers that point to this place in the palette.\n
            Clicking a different swatch will change the layer to use the selected color.
            """);

    private int currentIndex;

    private final ArrayList<SwatchGridListener> listeners = new ArrayList<>();

    public SwatchGridPane(ColorPalette palette, int startColorIndex) {
        this.currentIndex = startColorIndex;
        setHgap(4);
        setVgap(4);
        ToggleGroup toggleGroup = new ToggleGroup();

        for (int i = 0; i < 64; i++) {
            ToggleButton toggleButton = new ToggleButton("", FillStyle.getSwatch(
                    1,
                    ColorUtils.getColor(palette.getHex(i)),
                    SIZE
            ));
            if (startColorIndex == i) {
                toggleButton.setSelected(true);
            }
            toggleButton.setUserData(i);
            toggleButton.setToggleGroup(toggleGroup);
            toggleButton.setId("color-swatch-button");
            toggleButton.setTooltip(SWATCH_TIP);
            add(toggleButton, i % 8, i / 8);
        }
        toggleGroup.selectedToggleProperty().addListener((
                ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            } else {
                currentIndex = (int) newValue.getUserData();
                //LOGGER.log(Level.SEVERE, "Toggle Changed:\n\tobservable: {0}\n\t      old: {1}\n\t      new: {2}", new Object[]{observable, oldValue, newValue});

                listeners.forEach((l) -> {
                    l.swatchIndexChanged(currentIndex);
                });
            }
        }
        );
    }

    public void addListener(SwatchGridListener l) {
        listeners.add(l);
    }
}
