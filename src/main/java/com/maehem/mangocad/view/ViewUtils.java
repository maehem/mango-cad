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

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ViewUtils {

    public static final int ICON_IMG_SIZE = 24;
    public static final double DIALOG_GRAPHIC_SIZE = 48;
    public static final String CSS_FILE = "/style/dark.css";

    public static Button createIconButton(String name, Image img) {
        return (Button) createIconButton(name, img, false);
    }

    public static ToggleButton createIconToggleButton(String name, Image img) {
        return (ToggleButton) createIconButton(name, img, true);
    }

    public static ButtonBase createIconButton(String name, Image img, boolean asToggle) {
//        ImageView icon = new ImageView(img);
//        icon.setFitHeight(ICON_IMG_SIZE);
//        icon.setPreserveRatio(true);

//        ImageView clip = new ImageView(img);
//        clip.setFitHeight(ICON_IMG_SIZE);
//        clip.setPreserveRatio(true);
//        icon.setClip(clip);

//        ColorAdjust monochrome = new ColorAdjust();
//        monochrome.setSaturation(-1.0);

//        Blend blush = new Blend(
//                BlendMode.SCREEN,
//                monochrome,
//                new ColorInput(
//                        0,
//                        0,
//                        icon.getBoundsInLocal().getWidth(),
//                        icon.getBoundsInLocal().getHeight(),
//                        Color.LIGHTGRAY
//                )
//        );

        //icon.setEffect(blush);
//        icon.setEffect(getColorIconEffect(icon,
//                Color.LIGHTGRAY, ICON_IMG_SIZE, ICON_IMG_SIZE
//        ));
        ImageView icon = createIcon(img, ICON_IMG_SIZE);

        ButtonBase b;
        if (asToggle) {
            b = new ToggleButton();
        } else {
            b = new Button();
        }
        b.setUserData(name);
        b.setId("button-icon");
        b.setGraphic(icon);
        b.setTooltip(new Tooltip(name));
        b.setMaxSize(ICON_IMG_SIZE, ICON_IMG_SIZE);

        return b;
    }

    public static final Effect getColorIconEffect(ImageView imgView, Color c, double w, double h) {
        ImageView clip = new ImageView(imgView.getImage());
        clip.setFitHeight(h);
        clip.setPreserveRatio(true);
        imgView.setClip(clip);

        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend blush = new Blend(
                BlendMode.SCREEN,
                monochrome,
                new ColorInput(0, 0, w, h, c)
        );

        return blush;
    }

    public static final ImageView createIcon(Image img, double size) {
        ImageView graphic = new ImageView(img);
        graphic.setFitHeight(size);
        graphic.setPreserveRatio(true);
        graphic.setEffect(getColorIconEffect(graphic, Color.LIGHTGRAY, size, size));

        return graphic;
    }

    public static final void applyAppStylesheet(ObservableList<String> sylesheetList) {
        sylesheetList.add(ViewUtils.class.getResource(CSS_FILE).toExternalForm());
    }
}
