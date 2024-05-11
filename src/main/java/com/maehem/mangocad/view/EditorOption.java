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

import java.util.ResourceBundle;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public enum EditorOption {
    GRID("/icons/grid.png", "OPTION_ICON_GRID"),
    LAYER_SETTINGS("/icons/layers.png", "OPTION_ICON_LAYERS"),
    LAYER_CHOOSER(null, "OPTION_LABEL_LAYER_CHOOSER"),
    GRID_MOUSE_INFO(null, "OPTION_TOOLTIP_GRID_MOUSE"),
    COMMAND_LINE("/icons/command.png", "OPTION_ICON_COMMAND"),
    // TODO: Maybe current document name?
    CONTEXT_MESSAGE(null, null),
    SEPARATOR(null, null);

    private static final ResourceBundle MSG = ResourceBundle.getBundle("i18n/Editor");

    private final String path;
    private final String bundleKey;

    private EditorOption(String path, String bundleKey) {
        this.path = path;
        this.bundleKey = bundleKey;
    }

    public String iconPath() {
        return path;
    }

    public String bundleKey() {
        return bundleKey;
    }

    public String bundleMessage() {
        return MSG.getString(bundleKey);
    }

}
