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
import com.maehem.mangocad.model.ElementListener;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public abstract class ToolModeWidget extends HBox implements ElementListener {
    protected final ResourceBundle MSG; // Must be set in constructor or after.
    public static final double ICON_SIZE = 16;
    public static final double LABEL_PADDING = 8;
    //public static final double SPACING = 8;
    //private static final double LABEL_AREA_WIDTH = 80;
    public static final boolean EDITABLE = true;
    private final Label label = new Label("");
    //private final HBox labelBox = new HBox(label);

    public ToolModeWidget() {
        this(null);
    }

    public ToolModeWidget(String msgBundleKey) {
        this.MSG = ResourceBundle.getBundle("i18n/Editor");
        if (msgBundleKey != null) {
            try {
                String string = MSG.getString(msgBundleKey + "_LABEL") + ":";
                setLabel(string);
            } catch (MissingResourceException ex) { // tt can remain blank.
                LOGGER.log(Level.SEVERE, "Couldn''t find requested i18n: {0}_LABEL", msgBundleKey);
                setLabel("<ERR>:");
            }
        }

        setId("tool-mode-widget");

        setPadding(new Insets(2)); // Do in CSS
        setAlignment(Pos.BASELINE_LEFT);

        getChildren().add(label);
    }

    public final void setLabel(String labelValue) {
        this.label.setText(labelValue);
        label.requestLayout();
    }

    public abstract void stopListening();

    @Override
    public abstract void elementChanged(Element e, Enum field, Object oldVal, Object newVal);

}
