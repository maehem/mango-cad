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
import com.maehem.mangocad.model.element.basic.ElementRectangle;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.enums.ElementRectangleField;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.element.property.RotationProperty;
import com.maehem.mangocad.model.util.Rotation;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.PickListener;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RectangleNode extends ViewNode implements RotationProperty, ElementListener {

    private final ElementRectangle rectangle;
    private final Layers layers;
    private final ColorPalette palette;
    private final Rectangle rectangleShape = new Rectangle();
    private final Polygon rectShape = new Polygon();

    public RectangleNode(ElementRectangle er, Layers layers, ColorPalette palette, PickListener pickListener) {
        super(er, pickListener);

        this.rectangle = er;
        this.layers = layers;
        this.palette = palette;

        // TODO:  Maybe this is a polygon shape?
        rectangleShape.setStrokeWidth(1);

        //add(rectangleShape);
        add(rectShape);

        updatePoints();
        updateLayer();

        Platform.runLater(() -> {
            rectangle.addListener(this);
        });
    }

    private void updatePoints() {
        double x1 = rectangle.getX1();
        double y1 = -rectangle.getY1();
        double x2 = rectangle.getX2();
        double y2 = -rectangle.getY2();

        rectShape.getPoints().clear();
        rectShape.getPoints().addAll(
                x1, y1,
                x2, y1,
                x2, y2,
                x1, y2
        );

        double rot = getRot();
        if (isMirrored()) {
            rot += 180;
        }
        rot %= 360;
        rectShape.setRotate(rot);
    }

    private void updateLayer() {
        LayerElement layer = layers.get(rectangle.getLayerNum());
        Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));

        rectangleShape.setFill(rectangle.isSelected() ? c.brighter().brighter() : c);
        rectShape.setFill(rectangle.isSelected() ? c.brighter().brighter() : c);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        LOGGER.log(Level.SEVERE,
                "Rectangle properties have changed! {0}: {1} => {2}",
                new Object[]{field, oldVal != null ? oldVal.toString() : "null", newVal != null ? newVal.toString() : "null"});

        if (field instanceof ElementRectangleField erf) {
            switch (erf) {
                case ElementRectangleField.X1, ElementRectangleField.Y1, ElementRectangleField.X2, ElementRectangleField.Y2, ElementRectangleField.ALL_XY -> {
                    updatePoints();
                }
                case ElementRectangleField.SELECTED -> {
                    updateLayer();
                }
            }
        }
        if (field instanceof RotationProperty.Field rf) {
            updatePoints();
        }
    }

    @Override
    public String toString() {
        return "RectangleNode: Points:  x1:" + rectangle.getX1() + "," + rectangle.getY1() + "   " + rectangle.getX2() + "," + rectangle.getY2();
    }

    @Override
    public Rotation getRotation() {
        return rectangle.getRotation();
    }

    @Override
    public double getRot() {
        return rectangle.getRot();
    }

    @Override
    public void setRot(double value) {
        rectangle.setRot(value);
    }

    @Override
    public boolean isSpun() {
        return false; // Spin not allowed for rectangles.
    }

    @Override
    public void setSpin(boolean value) {
        // Does nothing.
    }

    @Override
    public boolean isSpin() {
        return false;  // Never true.
    }

    @Override
    public boolean isSpinAllowed() {
        return false;  // Not allowed.
    }

    @Override
    public void setAllowSpin(boolean value) {
        // Nothing happens.
    }

    @Override
    public void setMirror(boolean value) {
        rectangle.getRotation().setMirror(value);
    }

    @Override
    public boolean isMirrored() {
        return rectangle.getRotation().isMirror();
    }

    @Override
    public boolean isMirrorAllowed() {
        return rectangle.getRotation().isAllowMirror(); // Should be true.
    }

    @Override
    public void setAllowMirror(boolean value) {
        // Not an option in the UI.
    }

    @Override
    public boolean isConstrained() {
        return true;
    }

    @Override
    public void setConstrained(boolean value) {
        // Change not allowed. Always constrained.
    }

}
