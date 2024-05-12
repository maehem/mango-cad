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
package com.maehem.mangocad.view.library.symbol;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorContextMenu extends ContextMenu {

    public final MenuItem NEXT = new MenuItem("Next");
    public final MenuItem NEXT_SEPARATOR = new SeparatorMenuItem();
    public final MenuItem COPY = new MenuItem("Copy");
    public final MenuItem DELETE = new MenuItem("Delete");
    public final MenuItem MIRROR = new MenuItem("Mirror");
    public final MenuItem MOVE = new MenuItem("Move");
    public final MenuItem NAME = new MenuItem("Name");
    public final MenuItem ROTATE = new MenuItem("Rotate");
    public final MenuItem SHOW = new MenuItem("Show");
    public final MenuItem SEP1 = new SeparatorMenuItem();
    public final MenuItem MOVE_GROUP = new MenuItem("Move Group");
    public final MenuItem SEP2 = new SeparatorMenuItem();
    public final MenuItem PROPERTIES = new MenuItem("Properties...");

    public SymbolEditorContextMenu() {
        getItems().addAll(
                NEXT,
                NEXT_SEPARATOR,
                COPY,
                DELETE,
                MIRROR,
                MOVE,
                NAME,
                ROTATE,
                SHOW,
                SEP1,
                MOVE_GROUP,
                SEP2,
                PROPERTIES
        );
    }

}
