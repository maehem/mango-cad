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
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.enums.WireEnd;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.element.property.CurveProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.PickListener;
import static com.maehem.mangocad.view.node.ViewNode.LOGGER;
import java.util.List;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class WireNode extends ViewNode implements ElementListener {

    private static final double SIN90 = Math.sin(Math.toRadians(90.0));

    private final double WIRE_STROKE_WIDTH = 0.1524; // 6 mil

    private final Wire wire;

    private final MoveTo start = new MoveTo();
    private final ArcTo arcTo = new ArcTo();
    private final Path wireCurve = new Path(start, arcTo);
    private final Layers layers;
    private final ColorPalette palette;

    public WireNode(Wire w, Layers layers, ColorPalette palette, PickListener pickListener) {
        super(w, pickListener);

        this.wire = w;
        this.layers = layers;
        this.palette = palette;

        add(wireCurve);

        wireCurve.setSmooth(true);

        updateLine();
        updateCap();
        updateWidth();
        updateStyle();
        updateCurve();
        updateLayer(); // Color

        wireCurve.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            PickListener listener = getPickListener();
            if (listener != null) {
                getPickListener().nodePicked(this, me);
            }
        });
//        wireCurve.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
//            PickListener listener = getPickListener();
//            LOGGER.log(Level.SEVERE, "Pin Picked..");
//            if (listener != null) {
//                LOGGER.log(Level.SEVERE, "Notify Pin pick listener.");
//
//                // Set the picked end.
//                switch (closestEnd(me)) {
//                    case ONE -> {
//                        LOGGER.log(Level.SEVERE, "Closest End: " + WireEnd.ONE.name());
//                    }
//                    case TWO -> {
//                        LOGGER.log(Level.SEVERE, "Closest End: " + WireEnd.TWO.name());
//                    }
//                }
//
//                getPickListener().nodePicked(this, me);
//            }
//        });
        Platform.runLater(() -> {
            this.wire.addListener(this);
        });
    }

    private WireEnd closestEnd(MouseEvent me) {
        // me Distance to start.
        // me Distance to end.
        LOGGER.log(Level.SEVERE, "Closest to end: m:{0},{1}  1:{2},{3}  2:{4},{5}",
                new Object[]{me.getX(), me.getY(),
                    wire.getX1(), wire.getY1(),
                    wire.getX2(), wire.getY2()
                });

        double r = 1.27;
        double xM = me.getX();
        double yM = -me.getY();
        double x1 = wire.getX1();
        double y1 = wire.getY1();
        double x2 = wire.getX2();
        double y2 = wire.getY2();

        double diffX1 = Math.abs(xM - x1);
        double diffY1 = Math.abs(yM - y1);
        double diffX2 = Math.abs(xM - x2);
        double diffY2 = Math.abs(yM - y2);
        LOGGER.log(Level.SEVERE,
                "Diffs: 1:{0},{1}  2:{2},{3}",
                new Object[]{diffX1, diffY1, diffX2, diffY2});

        if (Math.abs(xM - x1) < r && Math.abs(yM - y1) < r) {
            return WireEnd.ONE;
        }
        if (Math.abs(xM - x2) < r && Math.abs(yM - y2) < r) {
            return WireEnd.TWO;
        }

        return WireEnd.NONE;
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
        LOGGER.log(Level.SEVERE, "Update Curve");
        double curve = wire.curveProperty.get();

        // SWEEP on negative curve value.
        arcTo.setSweepFlag(curve < 0.0);

//        double radius;
//        if (curve == 0.0) {
//            radius = 10000.0; // Big number that makes it look straight.
//        } else {
////            radius = (SIN90 * wire.getLength() / 2.0)
////                    / Math.sin(Math.toRadians(curve / 2.0));
//            radius = (wire.getLength() / 2.0)
//                    / Math.sin(Math.toRadians(curve / 2.0));
//        }
        double radius = wire.getRadius();
        arcTo.setLargeArcFlag(Math.abs(curve) > 180);
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);
    }

    private void updateLayer() {
        LayerElement layer = layers.get(wire.getLayerNum());
        Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));
        if (wire.isPicked()) {
            c = c.brighter().saturate();
        } else if (wire.isSelected()) {
            c = c.darker();
        }
        // Get new Color based on layer.
        //wireCurve.setStroke(wire.getSelectedEnd() != WireEnd.NONE ? c.brighter().brighter() : c);
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
        //Wire.Field f = (Wire.Field) field;

        LOGGER.log(Level.SEVERE,
                "WireNode:  Wire properties have changed! {0}: {1} => {2}",
                new Object[]{field, oldVal.toString(), newVal.toString()});

        switch (field) {
            case Wire.Field.X1, Wire.Field.Y1, Wire.Field.X2, Wire.Field.Y2 -> {
                updateLine();
            }
            case Wire.Field.END -> {
                updateLayer();
            }
            case Wire.Field.CAP -> {
                updateCap();
            }
            case CurveProperty.Field.VALUE -> {
                updateCurve();
            }
            case LayerNumberProperty.Field.LAYER, SelectableProperty.Field.PICKED -> {
                updateLayer();
            }
            case Wire.Field.STYLE -> {
                updateStyle();
            }
            case WidthProperty.Field.WIDTH -> {
                updateWidth();
            }
            default -> {
                // Ignore
            }
        }
    }

    @Override
    public String toString() {
        return "WireNode: "
                + " start:" + wire.getX1() + "," + wire.getY1()
                + "   end:" + wire.getX2() + "," + wire.getY2()
                + "   len:" + wire.getLength()
                + " style:" + wire.getStyle().name()
                + " width:" + wire.getWidth();
    }

}
