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
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public abstract class ToolModeWidget extends HBox implements ElementListener {
    protected final ResourceBundle MSG; // Must be set in constructor or after.
    public static final double ICON_SIZE = 16;
    public static final boolean EDITABLE = true;

    public ToolModeWidget() {
        this.MSG = ResourceBundle.getBundle("i18n/Editor");

        setId("tool-mode-widget");

        setSpacing(0.0);
        setAlignment(Pos.BASELINE_CENTER);

        //setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2))));

    }

    public abstract void stopListening();

    @Override
    public abstract void elementChanged(Element e, Enum field, Object oldVal, Object newVal);

}
