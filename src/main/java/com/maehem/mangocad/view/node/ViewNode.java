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
package com.maehem.mangocad.view.node;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.view.PickListener;
import java.util.ArrayList;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.shape.Shape;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public abstract class ViewNode extends ArrayList<Shape> {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    private final Element element;
    private PickListener listener;
    private boolean moving = false;
    private boolean selected = false;

    public ViewNode(Element e, PickListener l) {
        element = e;
        setListener(l);
    }

    public Element getElement() {
        return element;
    }

    public final void setListener(PickListener l) {
        listener = l;
    }

    public PickListener getPickListener() {
        return listener;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean value) {
        moving = value;
    }

    public void addTo(Group node) {
        for (Shape s : this) {
            node.getChildren().add(s);
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public abstract String toString();
}
