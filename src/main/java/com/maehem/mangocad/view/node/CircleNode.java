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

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.ElementListener;
import com.maehem.mangocad.model.element.basic.ElementCircle;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.enums.ElementCircleField;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class CircleNode extends Circle implements ElementListener {

    private final ElementCircle circle;
    private final Layers layers;
    private final ColorPalette palette;

    public CircleNode(ElementCircle ec, Layers layers, ColorPalette palette) {
        super();

        this.circle = ec;
        this.layers = layers;
        this.palette = palette;

        updateLocation();
        updateWidth();
        updateRadius();
        updateLayer();

        Platform.runLater(() -> {
            circle.addListener(this);
        });
    }

    private void updateLocation() {
        setLayoutX(circle.getX());
        setLayoutY(-circle.getY());
    }

    private void updateWidth() {
        double strokeWidth = circle.getWidth();

        setStrokeWidth(strokeWidth);
    }

    private void updateRadius() {
        setRadius(circle.getRadius());
    }

    private void updateLayer() {
        LayerElement layer = layers.get(circle.getLayerNum());
        Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));

        setStroke(c);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        LOGGER.log(Level.SEVERE,
                "Pin properties have changed!{0}: {1} => {2}",
                new Object[]{field, oldVal.toString(), newVal.toString()});

        switch ((ElementCircleField) field) {
            case ElementCircleField.X, ElementCircleField.Y -> {
                updateLocation();
            }
            case ElementCircleField.RADIUS -> {
                updateRadius();
            }
            case ElementCircleField.WIDTH -> {
                updateWidth();
            }
        }
    }

}
