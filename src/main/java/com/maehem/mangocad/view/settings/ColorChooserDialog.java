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
package com.maehem.mangocad.view.settings;

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.view.ViewUtils;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ColorChooserDialog extends Dialog<ButtonType> {

    private final ButtonType okButtonType = ButtonType.OK;
    private final ButtonType cancelButtonType = ButtonType.CANCEL;

    private final ColorChooserPanel colorChooserPanel;

    public ColorChooserDialog(ColorPalette palette, int index) {
        setTitle("Color Chooser");
        ViewUtils.applyAppStylesheet(getDialogPane().getStylesheets());

        this.colorChooserPanel = new ColorChooserPanel(palette, index);
        getDialogPane().setContent(colorChooserPanel);

        getDialogPane().getButtonTypes().addAll(
                okButtonType, cancelButtonType
        );

        showAndWait();
    }

}
