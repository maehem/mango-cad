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
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.enums.DimensionType;
import com.maehem.mangocad.model.element.enums.TextAlign;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.PickListener;
import static com.maehem.mangocad.view.node.ViewNode.LOGGER;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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

    private static final double CROSSHAIR_EXT = 2.54;

    private final Dimension dimension;

    //private final MoveTo start = new MoveTo();
    //private final ArcTo arcTo = new ArcTo();
    //private final Path wireCurve = new Path(start, arcTo);
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
    private final Polygon dimArrow2 = new Polygon();

    Line chH = new Line();
    Line chW = new Line();

    private final Text displayValue = new Text("0.0000000 mm");
    private final TextNode textNode;
    private final ElementText et = new ElementText();

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
        double hyp23 = Math.hypot(x2 - x3, y2 - y3);
        double hh12 = hyp12 / 2.0;
        double arrowGap = dimension.getWidth() * 2; // A little space between ext. and arrow point.
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
//                new Object[]{aaa, Math.toDegrees(aaa), sinA, Math.toDegrees(sinA), hyp12}
//        );
        switch (dimension.getDtype()) {
            case PARALLEL, DIAMETER -> {
                //double textRot = angle;
                double extLineRot = angle;
                double h2 = hh12 - arrowGap;

                if (dimension.getDtype() == DimensionType.DIAMETER) {
                    // Crosshair at point between 1-2.
                    double a2 = (y2 - y1) * 0.5;
                    double b2 = (x2 - x1) * 0.5;

                    chH.setLayoutX(x1 + b2); // TODO: This trick wih arrows?
                    chH.setLayoutY(-y1 - a2);
                    chW.setLayoutX(x1 + b2);
                    chW.setLayoutY(-y1 - a2);

                    add(chH);
                    add(chW);
                }

                if (x1 < x2) { // TOP
                    if (y1 > y2) { // Right
                        if (y3 < y1 || y3 < y2) {
                            extLineRot = 360.0 - angle;
                            textAngle = 180.0 - angle;
                        } else {
                            extLineRot = angle;
                        }
                    } else { // Left
                        if (y3 < y1 || y3 < y2) {
                            extLineRot = 360.0 - angle;
                            textAngle = 180.0 - angle;
                        } else {
                            extLineRot = angle;
                        }
                    }
                } else {  // Left
                    if (y1 > y2) { // Top
                        if (y3 < y1 || y3 < y2) {
                            extLineRot = angle;
                            textAngle = 180.0 + angle;
                        } else {
                            extLineRot = -angle;
                            textAngle = 360.0 - angle;
                        }
                    } else {
                        if (y3 < y1 || y3 < y2) {
                            extLineRot = angle;
                            textAngle = 180.0 + angle;
                        } else {
                            extLineRot = 360.0 - angle;
                            textAngle = 360.0 - angle;
                        }
                    }
                }

                LOGGER.log(Level.SEVERE, "Ext.Len.:{0}  off: {1}",
                        new Object[]{dimension.getExtlength(), dimension.getExtoffset()}
                );
                extLine1.setStartX(x1);
                extLine1.setStartY(-y1 - extOff);
                extLine1.setEndX(x1);
                extLine1.setEndY(-y1 - extLineLen - extLen);
                extLine1Rotate.setPivotX(x1);
                extLine1Rotate.setPivotY(-y1);
                extLine1Rotate.setAngle(extLineRot);

                extLine2.setStartX(x2);
                extLine2.setStartY(-y2 - extOff);
                extLine2.setEndX(x2);
                extLine2.setEndY(-y2 - extLineLen - extLen);
                extLine2Rotate.setPivotX(x2);
                extLine2Rotate.setPivotY(-y2);
                extLine2Rotate.setAngle(extLineRot);

                dimLine.setStartX(-h2);
                dimLine.setEndX(h2);
                dimLine.setStartY(0);
                dimLine.setEndY(0);
                dimLineRotate.setAngle(extLineRot);
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

                et.setX(x3);
                et.setY(y3);
                et.rotation.set(-textAngle);
                String unitLabel = "";
                if (dimension.isVisible()) { // show inits label
                    unitLabel = dimension.getUnit().label;
                }
                BigDecimal bd = BigDecimal.valueOf(hyp12);
                bd = bd.setScale(dimension.getPrecision(), RoundingMode.HALF_UP);
                et.setValue(bd.toString() + unitLabel);

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

                et.setX(x3);//- Math.asin(Math.toRadians(angle)) * tOff);
                et.setY(y3);//+ Math.asin(Math.toRadians(90 - angle)) * tOff);
                et.rotation.set(0);
                String unitLabel = "";
                if (dimension.isVisible()) { // show inits label
                    unitLabel = dimension.getUnit().label;
                }
                BigDecimal bd = BigDecimal.valueOf(Math.abs(x1 - x2));
                bd = bd.setScale(dimension.getPrecision(), RoundingMode.HALF_UP);
                et.setValue(bd.toString() + unitLabel);

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

                et.setX(x3);//- Math.asin(Math.toRadians(angle)) * tOff);
                et.setY(y3);//+ Math.asin(Math.toRadians(90 - angle)) * tOff);
                et.rotation.set(90);
                String unitLabel = "";
                if (dimension.isVisible()) { // show inits label
                    unitLabel = dimension.getUnit().label;
                }
                BigDecimal bd = BigDecimal.valueOf(Math.abs(y1 - y2));
                bd = bd.setScale(dimension.getPrecision(), RoundingMode.HALF_UP);
                et.setValue(bd.toString() + unitLabel);

                updateWidths();
                updateLayer();

                add(extLine1);
                add(extLine2);
                add(dimLine);
                add(dimArrow1);
                add(dimArrow2);
                addAll(textNode);
            }
            case RADIUS -> {
                double rot = angle;
                // Crosshair at 1.
                chH.setLayoutX(x1); // TODO: This trick wih arrows?
                chH.setLayoutY(-y1);
                chW.setLayoutX(x1);
                chW.setLayoutY(-y1);

                //LOGGER.log(Level.SEVERE, "hyp12: " + hyp12 + "   hyp13: " + hyp13);
                Double[] arrowPoints;
                if (hyp13 < hyp12) { // Inside Dim Line
                    dimLine.setStartX(0);
                    dimLine.setStartY(0);
                    dimLine.setEndX(hyp12);
                    dimLine.setEndY(0);
                    dimLine.setTranslateX(x1);
                    dimLine.setTranslateY(-y1);

                    if (x1 < x2) { // Right
                        if (y1 < y2) { // Top
                            dimLineRotate.setAngle(angle);
                        } else { // Bottom
                            dimLineRotate.setAngle(180.0 - angle);
                            rot = 180.0 - angle;
                        }
                    } else {  // Left
                        if (y1 < y2) { // Top
                            dimLineRotate.setAngle(180.0 - angle);
                            rot = 360 - angle;
                        } else {
                            dimLineRotate.setAngle(angle);
                            rot = angle + 180;
                        }
                    }

                    arrowPoints = new Double[]{
                        0.0, 0.0,
                        -2.54, -0.625,
                        -2.54, 0.625
                    };
                    dimArrow2.getPoints().clear();
                    dimArrow2.getPoints().addAll(Arrays.asList(arrowPoints));

                    dimArrow2.setTranslateX(x2);
                    dimArrow2.setTranslateY(-y2);
                    add(dimArrow2);
                } else { // hyp12 > hyp13,  Outside Dim Line
                    //LOGGER.log(Level.SEVERE, "Outside dimLine.");
                    //double len = dimension.getTextsize() * 5.0;

                    dimLine.setStartX(x2);
                    dimLine.setStartY(-y2);
                    dimLine.setEndX(x2 + hyp23);
                    dimLine.setEndY(-y2);
                    dimLineRotate.setPivotX(x2);
                    dimLineRotate.setPivotY(-y2);
                    dimLineRotate.setAngle(angle);
                    dimLine.setTranslateX(0);
                    dimLine.setTranslateY(0);

                    if (x1 < x2) { // Right
                        et.setAlign(TextAlign.BOTTOM_RIGHT);
                        if (y1 < y2) { // Top
                            dimLineRotate.setAngle(angle);
                        } else { // Bottom
                            dimLineRotate.setAngle(180.0 - angle);
                            rot = 180.0 - angle;
                        }
                    } else {  // Left
                        et.setAlign(TextAlign.BOTTOM_LEFT);
                        if (y1 < y2) { // Top
                            dimLineRotate.setAngle(180.0 - angle);
                            rot = 360 - angle;
                        } else {
                            dimLineRotate.setAngle(angle);
                            rot = angle + 180;
                        }
                    }
                    arrowPoints = new Double[]{
                        x2, -y2,
                        x2 + 2.54, -y2 - 0.625,
                        x2 + 2.54, -y2 + 0.625
                    };
                    dimArrow1.getPoints().clear();
                    dimArrow1.getPoints().addAll(Arrays.asList(arrowPoints));

                    dimArrow1.setTranslateX(0);
                    dimArrow1.setTranslateY(0);
                    add(dimArrow1);

                }

                et.setX(x3);//- Math.asin(Math.toRadians(angle)) * tOff);
                et.setY(y3);//+ Math.asin(Math.toRadians(90 - angle)) * tOff);
                et.rotation.set(-rot);
                String unitLabel = "";
                if (dimension.isVisible()) { // show inits label
                    unitLabel = dimension.getUnit().label;
                }
                BigDecimal bd = BigDecimal.valueOf(Math.abs(hyp12));
                bd = bd.setScale(dimension.getPrecision(), RoundingMode.HALF_UP);
                et.setValue(bd.toString() + unitLabel);

                updateWidths();
                updateLayer();

                add(chH);
                add(chW);
                add(dimLine);
                addAll(textNode);
            }
            case LEADER -> {
                dimLine.setStartX(arrowGap);
                dimLine.setStartY(0);
                dimLine.setEndX(hyp12);
                dimLine.setEndY(0);
                dimLineRotate.setPivotX(0);
                dimLineRotate.setPivotY(0);
                //dimLineRotate.setAngle(angle);
                dimLine.setTranslateX(x1);
                dimLine.setTranslateY(-y1);

                extLine1.setStartX(x1);
                extLine1.setStartY(-y1);
                extLine1.setEndX(x2);
                extLine1.setEndY(-y2);
                extLine1Rotate.setPivotX(0);
                extLine1Rotate.setPivotY(0);
                extLine1Rotate.setAngle(0);

                extLine2.setStartX(x2);
                extLine2.setStartY(-y2);
                extLine2.setEndX(x3);
                extLine2.setEndY(-y3);
                extLine2Rotate.setPivotX(0);
                extLine2Rotate.setPivotY(0);
                extLine2Rotate.setAngle(0);

                dimArrow1.getPoints().clear();
                Double[] d1 = new Double[]{arrowGap, 0.0, 2.54, 0.625, 2.54, -0.625};
                dimArrow1.getPoints().addAll(Arrays.asList(d1));
                dimArrow1.setTranslateX(x1);
                dimArrow1.setTranslateY(-y1);

                if (x1 < x2) { // Right
                    if (y1 < y2) { // Top
                        dimLineRotate.setAngle(angle);
                    } else { // Bottom
                        dimLineRotate.setAngle(180.0 - angle);
                    }
                } else {  // Left
                    if (y1 < y2) { // Top
                        dimLineRotate.setAngle(180.0 - angle);
                    } else {
                        dimLineRotate.setAngle(angle);
                    }
                }

                updateWidths();
                updateLayer();

                add(dimLine);
                add(extLine2);
                add(dimArrow1);
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
        chH.setStrokeWidth(w * 1.666);
        chW.setStrokeWidth(w * 1.666);
        chH.setStartX(-CROSSHAIR_EXT * w);
        chH.setEndX(CROSSHAIR_EXT * w);
        chW.setStartY(-CROSSHAIR_EXT * w);
        chW.setEndY(CROSSHAIR_EXT * w);
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

        chH.setStroke(c);
        chW.setStroke(c);

        et.setLayerNum(dimension.getLayerNum());
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
