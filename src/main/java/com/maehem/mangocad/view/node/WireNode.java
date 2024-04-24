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
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.enums.WireField;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.List;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class WireNode extends Group implements ElementListener {

    private static final double SIN90 = Math.sin(Math.toRadians(90.0));

    private final double WIRE_STROKE_WIDTH = 0.1524; // 6 mil

    private final Wire wire;

    private final MoveTo start = new MoveTo();
    private final ArcTo arcTo = new ArcTo();
    private final Path wireCurve = new Path(start, arcTo);
    private final Layers layers;
    private final ColorPalette palette;

    public WireNode(Wire w, Layers layers, ColorPalette palette) {
        this.wire = w;
        this.layers = layers;
        this.palette = palette;

        getChildren().addAll(wireCurve);

        wireCurve.setSmooth(true);

        updateLine();
        updateCap();
        updateWidth();
        updateCurve();
        updateLayer(); // Color

        Platform.runLater(() -> {
            this.wire.addListener(this);
        });
    }

    private void updateLine() {
        start.setX(wire.getX1());
        start.setY(-wire.getY1());
        arcTo.setX(wire.getX2());
        arcTo.setY(-wire.getY2());
    }

    private void updateCap() {
        switch (wire.getCap()) {
            case FLAT -> {
                wireCurve.setStrokeLineCap(StrokeLineCap.SQUARE);
            }
            case ROUND -> {
                wireCurve.setStrokeLineCap(StrokeLineCap.ROUND);
            }
        }
    }

    private void updateWidth() {
        double strokeWidth = wire.getWidth();
        if (strokeWidth < WIRE_STROKE_WIDTH) {
            // Wires can't be 0 width.
            strokeWidth = WIRE_STROKE_WIDTH; // 6mil
        }
        wireCurve.setStrokeWidth(strokeWidth);
    }

    private void updateCurve() {
        double curve = wire.getCurve();

        // SWEEP on negative curve value.
        arcTo.setSweepFlag(curve < 0.0);

        double radius;
        if (curve == 0.0) {
            radius = 10000.0; // Big number that makes it look straight.
        } else {
            radius = (SIN90 * wire.getLength() / 2.0)
                    / Math.sin(Math.toRadians(curve / 2.0));
        }
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);
    }

    private void updateLayer() {
        LayerElement layer = layers.get(wire.getLayerNum());
        Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));
        // Get new Color based on layer.
        wireCurve.setStroke(c);
    }

    private void updateStyle() {
        List<Double> pattern = wire.getStyle().getPattern();

        // TODO: Massage pettern nums to fit pattern into line's length.
        if (pattern != null) {
            wireCurve.getStrokeDashArray().addAll(pattern);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        WireField f = (WireField) field;

        LOGGER.log(Level.SEVERE,
                "Pin properties have changed!{0}: {1} => {2}",
                new Object[]{field, oldVal.toString(), newVal.toString()});

        switch (f) {
            case WireField.X1, WireField.Y1, WireField.X2, WireField.Y2 -> {
                updateLine();
            }
            case WireField.CAP -> {
                updateCap();
            }
            case WireField.CURVE -> {
                updateCurve();
            }
            case WireField.LAYER -> {
                updateLayer();
            }
            case WireField.STYLE -> {
                updateStyle();
            }
            case WireField.WIDTH -> {
                updateWidth();
            }
        }
    }

}
