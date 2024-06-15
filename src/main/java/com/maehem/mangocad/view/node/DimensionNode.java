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
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.enums.TextAlign;
import com.maehem.mangocad.model.element.enums.WireEnd;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.PickListener;
import static com.maehem.mangocad.view.node.ViewNode.LOGGER;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DimensionNode extends ViewNode implements ElementListener {

    private static final double SIN90 = Math.sin(Math.toRadians(90.0));

    private final double WIRE_STROKE_WIDTH = 0.1524; // 6 mil

    private final Dimension dimension;
    //ElementText et = new ElementText();

    private final MoveTo start = new MoveTo();
    private final ArcTo arcTo = new ArcTo();
    private final Path wireCurve = new Path(start, arcTo);
    private final Layers layers;
    private final ColorPalette palette;

    // Dim components. Some not displayed depending on mode.
    private final Line extLine1 = new Line();
    private final Rotate extLine1Rotate = new Rotate();

    private final Line extLine2 = new Line();
    private final Rotate extLine2Rotate = new Rotate();

    private final Line dimLine = new Line();
    private final Rotate dimLineRotate = new Rotate();

    private final Polygon dimArrow1 = new Polygon();
    private final Polygon dimArrow2 = new Polygon(0, 0, -2.54, 0.625, -2.54, -0.625);

    private final Text displayValue = new Text("0.0000000 mm");
    private final TextNode textNode;

    public DimensionNode(Dimension d, Layers layers, ColorPalette palette, PickListener pickListener) {
        super(d, pickListener);

        this.dimension = d;
        this.layers = layers;
        this.palette = palette;

        extLine1.getTransforms().add(extLine1Rotate);
        extLine2.getTransforms().add(extLine2Rotate);
        dimLine.getTransforms().add(dimLineRotate);
        dimLine.setStrokeLineCap(StrokeLineCap.BUTT);
        dimArrow1.getTransforms().add(dimLineRotate);
        dimArrow2.getTransforms().add(dimLineRotate);

        displayValue.setTextAlignment(TextAlignment.CENTER);
        displayValue.getTransforms().add(dimLineRotate);

        ElementText et = new ElementText();
        et.setAlign(TextAlign.BOTTOM_CENTER);
        et.setValue("???");
        et.setSize(dimension.getTextsize());
        et.setRatio(dimension.getTextratio());
        et.setLayerNum(dimension.getLayerNum());

        textNode = new TextNode(et, null,
                layers, palette,
                null,
                true, null);
        //textNode.setAscend(0.625);  // 3x width?

        generateShapes();

        Platform.runLater(() -> {
            this.dimension.addListener(this);
        });
    }

    private void generateShapes() {
        clear();
        double x1 = dimension.getX1();
        double y1 = dimension.getY1();
        double x2 = dimension.getX2();
        double y2 = dimension.getY2();
        double x3 = dimension.getX3();
        double y3 = dimension.getY3();

        double hyp13 = Math.hypot(x3 - x1, y3 - y1);
        double hyp12 = Math.hypot(x2 - x1, y2 - y1);
        double hh12 = hyp12 / 2.0;
        double arrowGap = dimension.getWidth() * 3; // A little space between ext. and arrow point.
        double extLineLen = Math.sqrt(hyp13 * hyp13 - hh12 * hh12);

        double aaa = (y2 - y1) / hyp12;
        double sinA = Math.asin(aaa);
        double angle = -Math.toDegrees(sinA);
        double textAngle = angle;
        if (y3 < y1) {
            angle = 180.0 - angle;
            textAngle = angle + 180;
        }
        double extLen = dimension.getExtlength();
        if (extLen == 0.0) { // Auto == 10x width
            extLen = dimension.getWidth() * 15.0;
        }
        double extOff = dimension.getExtoffset();
        textNode.setAscend(dimension.getWidth() * 5.0);

//        LOGGER.log(Level.SEVERE, "val: {4}   aaa = {0} deg:{1}  sinA = {2}  degrees: {3}",
//                new Object[]{aaa, Math.toDegrees(aaa), sinA, Math.toDegrees(sinA), dimension.getWidth()}
//        );
        switch (dimension.getDtype()) {
            case PARALLEL -> {
                double h2 = hh12 - arrowGap;

                LOGGER.log(Level.SEVERE, "Ext.Len.:{0}  off: {1}",
                        new Object[]{dimension.getExtlength(), dimension.getExtoffset()}
                );
                extLine1.setStartX(x1);
                extLine1.setStartY(-y1 - extOff);
                extLine1.setEndX(x1);
                extLine1.setEndY(-y1 - extLineLen - extLen);
                extLine1Rotate.setPivotX(x1);
                extLine1Rotate.setPivotY(-y1);
                extLine1Rotate.setAngle(angle);

                extLine2.setStartX(x2);
                extLine2.setStartY(-y2 - extOff);
                extLine2.setEndX(x2);
                extLine2.setEndY(-y2 - extLineLen - extLen);
                extLine2Rotate.setPivotX(x2);
                extLine2Rotate.setPivotY(-y2);
                extLine2Rotate.setAngle(angle);

                dimLine.setStartX(-h2);
                dimLine.setEndX(h2);
                dimLine.setStartY(0);
                dimLine.setEndY(0);
                dimLineRotate.setAngle(angle);
                dimLine.setTranslateX(x3);
                dimLine.setTranslateY(-y3);

                dimArrow1.getPoints().clear();
                Double[] d1 = new Double[]{-h2, 0.0, -h2 + 2.54, 0.625, -h2 + 2.54, -0.625};
                dimArrow1.getPoints().addAll(Arrays.asList(d1));
                dimArrow1.setTranslateX(x3);
                dimArrow1.setTranslateY(-y3);

                dimArrow2.getPoints().clear();
                Double[] d2 = new Double[]{h2, 0.0, h2 - 2.54, 0.625, h2 - 2.54, -0.625};
                dimArrow2.getPoints().addAll(Arrays.asList(d2));
                dimArrow2.setTranslateX(x3);
                dimArrow2.setTranslateY(-y3);

                displayValue.setLayoutX(x3);
                displayValue.setLayoutY(-y3);

                if (textNode.getElement() instanceof ElementText et) {
                    double tOff = 0.254;
                    et.setX(x3);//- Math.asin(Math.toRadians(angle)) * tOff);
                    et.setY(y3);//+ Math.asin(Math.toRadians(90 - angle)) * tOff);
                    et.setRot(-textAngle);
                    String unitLabel = "";
                    if (dimension.isVisible()) { // show inits label
                        unitLabel = dimension.getUnit().label;
                    }
                    BigDecimal bd = BigDecimal.valueOf(hyp12);
                    bd = bd.setScale(dimension.getPrecision(), RoundingMode.HALF_UP);
                    et.setValue(bd.toString() + unitLabel);
                } else {
                    LOGGER.log(Level.SEVERE, "Dimension's text element isn't type ElementText! But why?");
                }

                updateWidths();
                updateLayer();

                add(extLine1);
                add(extLine2);
                add(dimLine);
                add(dimArrow1);
                add(dimArrow2);
                addAll(textNode);
            }
            case HORIZONTAL -> { // Ext.Lines go up-down
                double extLen1 = extLen;
                double extLen2 = extLen;
                if (y3 < y1) {
                    extLen1 = -extLen1;
                }
                if (y3 < y2) {
                    extLen2 = -extLen2;
                }
                extLine1.setStartX(x1);
                extLine1.setStartY(-y1 - extOff);
                extLine1.setEndX(x1);
                extLine1.setEndY(-y3 - extLen1);
                extLine1Rotate.setPivotX(x1);
                extLine1Rotate.setPivotY(-y1);
                extLine1Rotate.setAngle(0);

                extLine2.setStartX(x2);
                extLine2.setStartY(-y2 - extOff);
                extLine2.setEndX(x2);
                extLine2.setEndY(-y3 - extLen2);
                extLine2Rotate.setPivotX(x2);
                extLine2Rotate.setPivotY(-y2);
                extLine2Rotate.setAngle(0);

                dimLine.setStartX(x1);
                dimLine.setStartY(-y3);
                dimLine.setEndX(x2);
                dimLine.setEndY(-y3);
                dimLineRotate.setAngle(0);
                dimLine.setTranslateX(0);
                dimLine.setTranslateY(0);

                dimArrow1.getPoints().clear();
                Double[] d1 = new Double[]{x1, -y3, x1 + 2.54, -y3 + 0.625, x1 + 2.54, -y3 - 0.625};
                dimArrow1.getPoints().addAll(Arrays.asList(d1));
                dimArrow1.setTranslateX(0);
                dimArrow1.setTranslateY(0);

                dimArrow2.getPoints().clear();
                Double[] d2 = new Double[]{x2, -y3, x2 - 2.54, -y3 + 0.625, x2 - 2.54, -y3 - 0.625};
                dimArrow2.getPoints().addAll(Arrays.asList(d2));
                dimArrow2.setTranslateX(0);
                dimArrow2.setTranslateY(0);

                displayValue.setLayoutX(x3);
                displayValue.setLayoutY(-y3);

                if (textNode.getElement() instanceof ElementText et) {
                    et.setX(x3);//- Math.asin(Math.toRadians(angle)) * tOff);
                    et.setY(y3);//+ Math.asin(Math.toRadians(90 - angle)) * tOff);
                    et.setRot(0);
                    String unitLabel = "";
                    if (dimension.isVisible()) { // show inits label
                        unitLabel = dimension.getUnit().label;
                    }
                    BigDecimal bd = BigDecimal.valueOf(Math.abs(x1 - x2));
                    bd = bd.setScale(dimension.getPrecision(), RoundingMode.HALF_UP);
                    et.setValue(bd.toString() + unitLabel);
                } else {
                    LOGGER.log(Level.SEVERE, "Dimension's text element isn't type ElementText! But why?");
                }

                updateWidths();
                updateLayer();

                add(extLine1);
                add(extLine2);
                add(dimLine);
                add(dimArrow1);
                add(dimArrow2);
                addAll(textNode);
            }
            case VERTICAL -> { // Ext.Lines go right-left
                double extLen1 = extLen;
                double extLen2 = extLen;
                if (x3 < x1) {
                    extLen1 = -extLen1;
                }
                if (x3 < x2) {
                    extLen2 = -extLen2;
                }
                extLine1.setStartX(x1 + extOff);
                extLine1.setStartY(-y1);
                extLine1.setEndX(x3 + extLen1);
                extLine1.setEndY(-y1);
                extLine1Rotate.setPivotX(x1);
                extLine1Rotate.setPivotY(-y1);
                extLine1Rotate.setAngle(0);

                extLine2.setStartX(x2 + extOff);
                extLine2.setStartY(-y2);
                extLine2.setEndX(x3 + extLen2);
                extLine2.setEndY(-y2);
                extLine2Rotate.setPivotX(x2);
                extLine2Rotate.setPivotY(-y2);
                extLine2Rotate.setAngle(0);

                dimLine.setStartX(x3);
                dimLine.setStartY(-y1);
                dimLine.setEndX(x3);
                dimLine.setEndY(-y2);
                dimLineRotate.setAngle(0);
                dimLine.setTranslateX(0);
                dimLine.setTranslateY(0);

                dimArrow1.getPoints().clear();
                Double[] d1 = new Double[]{
                    x3, -y1,
                    x3 - 0.625, -y1 + 2.54,
                    x3 + 0.625, -y1 + 2.54};
                dimArrow1.getPoints().addAll(Arrays.asList(d1));
                dimArrow1.setTranslateX(0);
                dimArrow1.setTranslateY(0);

                dimArrow2.getPoints().clear();
                Double[] d2 = new Double[]{
                    x3, -y2,
                    x3 - 0.625, -y2 - 2.54,
                    x3 + 0.625, -y2 - 2.54
                };
                dimArrow2.getPoints().addAll(Arrays.asList(d2));
                dimArrow2.setTranslateX(0);
                dimArrow2.setTranslateY(0);

                displayValue.setLayoutX(x3);
                displayValue.setLayoutY(-y3);

                if (textNode.getElement() instanceof ElementText et) {
                    et.setX(x3);//- Math.asin(Math.toRadians(angle)) * tOff);
                    et.setY(y3);//+ Math.asin(Math.toRadians(90 - angle)) * tOff);
                    et.setRot(90);
                    String unitLabel = "";
                    if (dimension.isVisible()) { // show inits label
                        unitLabel = dimension.getUnit().label;
                    }
                    BigDecimal bd = BigDecimal.valueOf(Math.abs(y1 - y2));
                    bd = bd.setScale(dimension.getPrecision(), RoundingMode.HALF_UP);
                    et.setValue(bd.toString() + unitLabel);
                } else {
                    LOGGER.log(Level.SEVERE, "Dimension's text element isn't type ElementText! But why?");
                }

                updateWidths();
                updateLayer();

                add(extLine1);
                add(extLine2);
                add(dimLine);
                add(dimArrow1);
                add(dimArrow2);
                addAll(textNode);
            }
        }
    }

    private void updateWidths() {
        double w = dimension.getWidth();
        extLine1.setStrokeWidth(w);
        extLine2.setStrokeWidth(w);
        dimLine.setStrokeWidth(w);
        dimLine.setStrokeWidth(w);
        dimArrow1.setStrokeWidth(w);
        dimArrow2.setStrokeWidth(w);
        displayValue.setStrokeWidth(w * (dimension.getTextratio() / 100.0));
        displayValue.setFont(Font.font(dimension.getTextsize()));
    }

    private void updateLayer() {
        LayerElement layer = layers.get(dimension.getLayerNum());
        Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));
        extLine1.setStroke(c);
        extLine2.setStroke(c);
        dimLine.setStroke(c);
        dimArrow1.setStroke(c);
        dimArrow2.setStroke(c);
        dimArrow1.setFill(c);
        dimArrow2.setFill(c);

        displayValue.setStroke(c);
        displayValue.setFill(c);

        if (textNode.getElement() instanceof ElementText et) {
            et.setLayerNum(dimension.getLayerNum());
        }
    }

    private WireEnd closestEnd(MouseEvent me) {
        // me Distance to start.
        // me Distance to end.
        LOGGER.log(Level.SEVERE, "Closest to end: m:{0},{1}  1:{2},{3}  2:{4},{5}",
                new Object[]{me.getX(), me.getY(),
                    dimension.getX1(), dimension.getY1(),
                    dimension.getX2(), dimension.getY2()
                });

        double r = 1.27;
        double xM = me.getX();
        double yM = -me.getY();
        double x1 = dimension.getX1();
        double y1 = dimension.getY1();
        double x2 = dimension.getX2();
        double y2 = dimension.getY2();

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

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        //Wire.Field f = (Wire.Field) field;

        LOGGER.log(Level.FINE,
                "Wire properties have changed! {0}: {1} => {2}",
                new Object[]{field, oldVal.toString(), newVal.toString()});

        switch (field) {
//            case Wire.Field.X1, Wire.Field.Y1, Wire.Field.X2, Wire.Field.Y2 -> {
//                updateLine();
//            }
//            case Wire.Field.END -> {
//                updateLayer();
//            }
//            case Wire.Field.CAP -> {
//                updateCap();
//            }
//            case CurveProperty.Field.CURVE -> {
//                updateCurve();
//            }
//            case LayerNumberProperty.Field.LAYER -> {
//                updateLayer();
//            }
//            case Wire.Field.STYLE -> {
//                updateStyle();
//            }
//            case WidthProperty.Field.WIDTH -> {
//                updateWidth();
//            }
            default -> {
                // Ignore
            }
        }
    }

    @Override
    public String toString() {
        return "WireNode: "
                + " start:" + dimension.getX1() + "," + dimension.getY1()
                + "   end:" + dimension.getX2() + "," + dimension.getY2()
                + " width:" + dimension.getWidth();
    }

}
