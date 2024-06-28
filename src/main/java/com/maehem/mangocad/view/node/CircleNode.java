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
import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.ElementListener;
import com.maehem.mangocad.model.element.basic.CircleElement;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.element.property.CoordinateProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.PickListener;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class CircleNode extends ViewNode implements ElementListener {

    private final CircleElement circle;
    private final Layers layers;
    private final ColorPalette palette;
    private final Circle circleShape = new Circle();

    public CircleNode(CircleElement ec, Layers layers, ColorPalette palette, PickListener pickListener) {
        super(ec, pickListener);

        this.circle = ec;
        this.layers = layers;
        this.palette = palette;

        add(circleShape);

        updateLocation();
        updateWidth();
        updateRadius();
        updateLayer();

        circleShape.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            PickListener listener = getPickListener();
            if (listener != null) {
                getPickListener().nodePicked(this, me);
            }
        });

        Platform.runLater(() -> {
            circle.addListener(this);
        });
    }

    private void updateLocation() {
        circleShape.setLayoutX(circle.getX());
        circleShape.setLayoutY(-circle.getY());
    }

    private void updateWidth() {
        double strokeWidth = circle.getWidth();

        circleShape.setStrokeWidth(strokeWidth);
    }

    private void updateRadius() {
        circleShape.setRadius(circle.getRadius());
    }

    private void updateLayer() {
        LayerElement layer = layers.get(circle.getLayerNum());
        Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));
        if (circle.isPicked()) {
            c = c.brighter().saturate();
        } else if (circle.isSelected()) {
            c = c.darker();
        }
        circleShape.setStroke(c);
        if ( circle.getWidth() > 0.0 ) {
            circleShape.setFill(Color.TRANSPARENT);
        } else {
            circleShape.setFill(c);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        LOGGER.log(Level.SEVERE,
                "Circle properties have changed! {0}: {1} => {2}",
                new Object[]{field, oldVal.toString(), newVal.toString()});

        switch (field) {
            case CoordinateProperty.Field.X, CoordinateProperty.Field.Y -> {
                updateLocation();
            }
            case SelectableProperty.Field.SELECTED, SelectableProperty.Field.PICKED, LayerNumberProperty.Field.LAYER -> {
                updateLayer();
            }
            case CircleElement.Field.RADIUS -> {
                updateRadius();
            }
            case WidthProperty.Field.WIDTH -> {
                updateWidth();
            }
            default -> {
            }
        }
    }

    @Override
    public String toString() {
        return "CircleNode: radius:" + circle.getRadius() + " @ " + circle.getX() + "," + circle.getY();
    }

}
