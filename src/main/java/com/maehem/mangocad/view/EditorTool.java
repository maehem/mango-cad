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
public enum EditorTool {
    ADD("/icons/add-symbol.png", "TOOL_ICON_ADD"),
    ARC("/icons/arc.png", "TOOL_ICON_ARC"),
    ARRAY("/icons/array.png", "TOOL_ICON_ARRAY"),
    CHANGE("/icons/wrench.png", "TOOL_ICON_CHANGE"),
    CIRCLE("/icons/circle.png", "TOOL_ICON_CIRCLE"),
    COPY("/icons/copy.png", "TOOL_ICON_COPY"),
    DIMENSION("/icons/dimension.png", "TOOL_ICON_DIMENSION"),
    GRID("/icons/grid.png", "TOOL_ICON_GRID"),
    INFO("/icons/information.png", "TOOL_ICON_INFO"),
    LAYERS("/icons/layers.png", "TOOL_ICON_LAYERS"),
    LOOK("/icons/eye.png", "TOOL_ICON_LOOK"),
    LINK("/icons/link.png", "TOOL_ICON_LINK"),
    LINE("/icons/line.png", "TOOL_ICON_LINE"),
    MARK("/icons/compass-tool.png", "TOOL_ICON_MARK"),
    MIRROR("/icons/flip-horizontal.png", "TOOL_ICON_MIRROR"),
    MITER("/icons/miter.png", "TOOL_ICON_MITER"),
    MOVE("/icons/move.png", "TOOL_ICON_MOVE"),
    NAME("/icons/name.png", "TOOL_ICON_NAME"),
    OPTIMIZE("/icons/optimize.png", "TOOL_ICON_OPTIMIZE"),
    PAINT("/icons/paint-roller.png", "TOOL_ICON_PAINT"),
    PASTE("/icons/paste.png", "TOOL_ICON_PASTE"),
    PIN("/icons/pin.png", "TOOL_ICON_PIN"),
    POLYGON("/icons/polygon.png", "TOOL_ICON_POLYGON"),
    RECTANGLE("/icons/rectangle.png", "TOOL_ICON_RECTANGLE"),
    ROTATE("/icons/rotate.png", "TOOL_ICON_ROTATE"),
    SELECT("/icons/selection.png", "TOOL_ICON_SELECT"),
    SPLIT("/icons/split.png", "TOOL_ICON_SPLIT"),
    TAG("/icons/tag.png", "TOOL_ICON_TAG"),
    TEXT("/icons/text.png", "TOOL_ICON_TEXT"),
    TRASH("/icons/trash-can.png", "TOOL_ICON_DELETE"),
    UNLINK("/icons/unlink.png", "TOOL_ICON_UNLINK"),
    SEPARATOR(null, null);

    private static final ResourceBundle MSG = ResourceBundle.getBundle("i18n/Editor");

    private final String path;
    private final String bundleKey;

    private EditorTool(String path, String bundleKey) {
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