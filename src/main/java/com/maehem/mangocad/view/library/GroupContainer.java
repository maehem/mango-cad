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
package com.maehem.mangocad.view.library;

import javafx.scene.Group;
import javafx.scene.layout.BorderPane;

/**
 * A Pane that will always scale the group to fit the parent regardless of
 * aspect ratio.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class GroupContainer extends BorderPane {

    private final Group shapeGroup;

    private final double shapW;
    private final double shapH;

    public GroupContainer(Group g) {
        super(g);
        this.shapeGroup = g;

        shapW = shapeGroup.getBoundsInLocal().getWidth();
        shapH = shapeGroup.getBoundsInLocal().getHeight();

    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double wScale = getWidth() / shapW;
        double hScale = getHeight() / shapH;

        //double wScale = getBoundsInParent().getWidth() / shapW;
        //double hScale = getBoundsInParent().getHeight() / shapH;
        double scale = hScale;
        if (scale * shapW > getWidth()) {
            scale = wScale;
        }

        shapeGroup.setScaleX(scale * 0.8);
        shapeGroup.setScaleY(scale * 0.8);
    }
}
