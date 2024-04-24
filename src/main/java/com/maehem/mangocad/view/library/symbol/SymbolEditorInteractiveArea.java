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

import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.enums.PinFunction;
import com.maehem.mangocad.model.element.enums.PinLength;
import com.maehem.mangocad.model.element.enums.PinVisible;
import com.maehem.mangocad.view.library.symbol.node.PinNode;
import com.maehem.mangocad.view.node.WireNode;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorInteractiveArea extends ScrollPane {

    private static final double SCALE_MAX = 40.0;
    private static final double SCALE_MIN = 5.0;
    private static final double SCALE_FACTOR = 1.0;
    private static final int WORK_AREA = (int) (1000.0 / SCALE_MIN);
    private static final double WA2 = WORK_AREA / 2.0;
    private static final double GRID_SIZE = 2.54;
    private static final Color GRID_COLOR = new Color(1.0, 1.0, 1.0, 0.1);
    private static final double GRID_STROKE_WIDTH = 0.05;

    private final Circle shadow = new Circle(1, Color.RED);
    private final Text scaleText = new Text("x1.0");
    private final Group workArea = new Group(shadow, scaleText);
    private final Group crossHairArea = new Group();
    private final Group scrollArea = new Group(workArea, crossHairArea);
    private final LibrarySymbolSubEditor parentEditor;
    private double scale = 10.0;
    private Line hLine;
    private Line vLine;
    private Scale workScale = new Scale();

    public SymbolEditorInteractiveArea(LibrarySymbolSubEditor parentEditor) {
        this.parentEditor = parentEditor;

        // Things put into scrollArea will keep a constant size (0,0 crosshair)
        // Things put into workArea will scale with mouse scroll.
        setFitToWidth(true);
        setFitToHeight(true);
        setHbarPolicy(ScrollBarPolicy.ALWAYS);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
        setContent(scrollArea);

        setVvalue(0.5);
        setHvalue(0.5);
        buildScene();

        scaleText.setLayoutX(100);
        scaleText.setLayoutY(100);
        scaleText.setScaleX(0.1);
        scaleText.setScaleY(0.1);

        workArea.getTransforms().add(workScale);

        addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
            double scaleOld = scale;
            double scrollAmt = event.getDeltaY();
            //LOGGER.log(Level.SEVERE, "Scroll Delta: {0},{1}", new Object[]{evt.getDeltaX(), evt.getDeltaY()});
            scale += scrollAmt * SCALE_FACTOR;
            if (scale > SCALE_MAX) {
                scale = SCALE_MAX;
            }
            if (scale < SCALE_MIN) {
                scale = SCALE_MIN;
            }
            //workArea.setScaleX(scale);
            //workArea.setScaleY(scale);
            workScale.setX(scale);
            workScale.setY(scale);
            scaleText.setText("x" + scale);

            double mX = event.getX();
            double mY = event.getY();

            double vaW = getBoundsInLocal().getWidth();
            double vaH = getBoundsInLocal().getHeight();

            double sbHV = getHvalue();
            double sbVV = getVvalue();

            double waX = (mX - (vaW * sbHV)) / scaleOld;
            waX += (sbHV * 2 - 1) * (WA2);

            double waY = (mY - (vaH * sbVV)) / scaleOld;
            waY += (sbVV * 2 - 1) * (WA2);

            //LOGGER.log(Level.SEVERE, "   WAX/Y: {0},{1}", new Object[]{waX, waY});
            //LOGGER.log(Level.SEVERE, " VAM X/Y: {0},{1}", new Object[]{(mX - vaW / 2.0) / scale, (mY - vaH / 2.0) / scale});
            // Derive the sb values after the scale.
            double sbX = 0.5 + (waX + (mX - vaW / 2.0) / scale) / WORK_AREA;
            double sbY = 0.5 + (waY + (mY - vaH / 2.0) / scale) / WORK_AREA;

            setHvalue(sbX);
            setVvalue(sbY);

            //LOGGER.log(Level.SEVERE, "SB: OLD: {0},{1}    NEW: {2},{3}", new Object[]{sbHV, sbVV, sbX, sbY});
            //LOGGER.log(Level.SEVERE, "Mouse: {0},{1}", new Object[]{mX, mY});
            event.consume();
        });
        addEventFilter(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
            double mX = me.getX();
            double mY = me.getY();

            double vaW = getBoundsInLocal().getWidth();
            double vaH = getBoundsInLocal().getHeight();

            double sbHV = getHvalue();
            double sbVV = getVvalue();

            double waX = (mX - (vaW * sbHV)) / scale;
            waX += (sbHV * 2 - 1) * (WA2);

            double waY = (mY - (vaH * sbVV)) / scale;
            waY += (sbVV * 2 - 1) * (WA2);

            if (waX > -WA2 && waX < WA2) {
                shadow.setLayoutX(waX);

            }
            if (waY > -WA2 && waY < WA2) {
                shadow.setLayoutY(waY);
            }

        });
        setOnMouseEntered((t) -> {
            getScene().setCursor(Cursor.CROSSHAIR); //Change cursor to crosshair
        });
        setOnMouseExited((t) -> {
            getScene().setCursor(Cursor.DEFAULT);
        });

//        addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
//            // Add a circle where we clicked.
//            LOGGER.log(Level.SEVERE, "sBar: {0},{1}  mouse: {2},{3}   window: {4},{5}",
//                    new Object[]{
//                        getHvalue(), getVvalue(),
//                        me.getX(), me.getY(),
//                        getBoundsInLocal().getWidth(), getBoundsInLocal().getHeight()
//                    }
//            );
//            double mX = me.getX();
//            double mY = me.getY();
//
//            double vaW = getBoundsInLocal().getWidth();
//            double vaH = getBoundsInLocal().getHeight();
//            double sbHV = getHvalue();
//            double sbVV = getVvalue();
//
//            LOGGER.log(Level.SEVERE, "SB: {0},{1} VA: {2}x{3}  M: {4},{5}", new Object[]{sbHV, sbVV, vaW, vaH, mX, mY});
//            //double waX = me.getX() * (1 + getHvalue() * getBoundsInLocal().getWidth());
//            //double waY = me.getX() * (1 + getVvalue() * getBoundsInLocal().getHeight());
//            //double waX = mX * -(sbHV - 1) - vaW / 2;
//            //double waX = mX - vaW / 2;
//            double waX = (sbHV * 2 - 1) * (WA2) - vaW * sbHV + mX;
//            LOGGER.log(Level.SEVERE, "waX = {0} * {1} - {2} + {3}", new Object[]{sbHV * 2 - 1, WORK_AREA / 2.0, vaW * sbHV, mX});
//
//            waX /= scale;
//            //waX *= waScaleX;
//
//            //double waY = mY * -(sbVV - 1) - vaH / 2;
//            //double waY = mY - vaH / 2;
//            double waY = (sbVV * 2 - 1) * (WA2) - vaH * sbVV + mY;
//            LOGGER.log(Level.SEVERE, "waY = {0} * {1} - {2} + {3}", new Object[]{sbVV * 2 - 1, WORK_AREA / 2.0, vaW * sbVV, mY});
//
//            waY /= scale;
//            //waY *= waScaleY;
//
//            Circle c = new Circle(5, Color.ALICEBLUE);
//            c.setLayoutX(waX);
//            c.setLayoutY(waY);
//            if (waX > -WA2 && waX < WA2 && waY > -WA2 && waY < WA2) {
//                workArea.getChildren().add(c);
//                LOGGER.log(Level.SEVERE, "Circle at: {0},{1}", new Object[]{waX, waY});
//            } else {
//                LOGGER.log(Level.SEVERE, "Click was outside WORK_AREA!");
//            }
//
//        });
    }

    private void buildScene() {
        //workArea.setScaleX(scale);
        //workArea.setScaleY(scale);
        scaleText.setText("x" + scale);

        double dash = 2.5;
        double space = 3.0;
        double crossW2 = (5.0 * dash + 4.0 * space) * 0.5;
        hLine = new Line(-crossW2, 0, crossW2, 0);
        hLine.getStrokeDashArray().addAll(dash, space);
        vLine = new Line(0, -crossW2, 0, crossW2);
        vLine.getStrokeDashArray().addAll(dash, space);

        crossHairArea.getChildren().addAll(hLine, vLine);

        int nGrids = (int) (WA2 / GRID_SIZE);
        // Add Grid

        workArea.getChildren().add(gridLine(0, true));
        workArea.getChildren().add(gridLine(0, false));
        for (int n = 1; n <= nGrids; n++) {
            workArea.getChildren().add(gridLine(n, true));
            workArea.getChildren().add(gridLine(-n, true));
            workArea.getChildren().add(gridLine(n, false));
            workArea.getChildren().add(gridLine(-n, false));
        }
//        for (int x = 0; x <= WORK_AREA; x += GRID_SIZE) {
//            workArea.getChildren().add(gridLine(x, 0));
//        }

        // Add Zero point crosshair
        // Add symbol elements.
        workArea.getChildren().add(new Text("\nSymbol Editor"));

        Text t1 = new Text(-20, -20, "1");
        Text t2 = new Text(20, -20, "2");
        Text t3 = new Text(-20, 20, "3");
        Text t4 = new Text(20, 20, "4");

        workArea.getChildren().addAll(t1, t2, t3, t4);

        Pin p1 = new Pin();
        p1.setX(-10.16);
        p1.setY(2.54);
        p1.setName("A");
        p1.setLength(PinLength.MIDDLE);
        p1.setFunction(PinFunction.NONE);
        p1.setVisible(PinVisible.BOTH);
        PinNode pinNode1 = new PinNode(p1,
                Color.RED, Color.DARKGREEN, Color.DARKGREY,
                null, true
        );

        Pin p2 = new Pin();
        p2.setX(-10.16);
        p2.setY(-2.54);
        p2.setName("B");
        p2.setLength(PinLength.MIDDLE);
        p2.setFunction(PinFunction.NONE);
        p2.setVisible(PinVisible.BOTH);
        PinNode pinNode2 = new PinNode(p2,
                Color.RED, Color.DARKGREEN, Color.DARKGREY,
                null, true
        );

        Pin p3 = new Pin();
        p3.setX(10.16);
        p3.setY(0);
        p3.setName("Y");
        p3.setLength(PinLength.MIDDLE);
        p3.setFunction(PinFunction.DOT);
        p3.setVisible(PinVisible.BOTH);
        p3.setRot(180);
        PinNode pinNode3 = new PinNode(p3,
                Color.RED, Color.DARKGREEN, Color.DARKGREY,
                null, true
        );

        Wire w1 = new Wire();
        w1.setX1(-5.08);
        w1.setY1(5.08);
        w1.setX2(-5.08);
        w1.setY2(-5.08);
        w1.setWidth(0.64);

        Wire w2 = new Wire();
        w2.setX1(-5.08);
        w2.setY1(5.08);
        w2.setX2(0.0);
        w2.setY2(5.08);
        w2.setWidth(0.64);

        Wire w3 = new Wire();
        w3.setX1(-5.08);
        w3.setY1(-5.08);
        w3.setX2(0.0);
        w3.setY2(-5.08);
        w3.setWidth(0.64);

        Wire w4 = new Wire();
        w4.setX1(0.0);
        w4.setY1(-5.08);
        w4.setX2(0.0);
        w4.setY2(5.08);
        w4.setWidth(0.64);
        w4.setCurve(180.0);

        WireNode wireNode1 = new WireNode(w1,
                parentEditor.getDrawing().getLayers(),
                parentEditor.getDrawing().getPalette());
        WireNode wireNode2 = new WireNode(w2,
                parentEditor.getDrawing().getLayers(),
                parentEditor.getDrawing().getPalette());
        WireNode wireNode3 = new WireNode(w3,
                parentEditor.getDrawing().getLayers(),
                parentEditor.getDrawing().getPalette());
        WireNode wireNode4 = new WireNode(w4,
                parentEditor.getDrawing().getLayers(),
                parentEditor.getDrawing().getPalette());

        workArea.getChildren().addAll(
                pinNode1, pinNode2, pinNode3,
                wireNode1, wireNode2, wireNode3, wireNode4);

    }

    private Line gridLine(int n, boolean horiz) {
        double x1, x2, y1, y2;

        if (horiz) { // H line
            x1 = -WA2;
            x2 = WA2;
            y1 = n * GRID_SIZE;
            y2 = n * GRID_SIZE;
        } else {
            x1 = n * GRID_SIZE;
            x2 = n * GRID_SIZE;
            y1 = -WA2;
            y2 = WA2;
        }
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(GRID_COLOR);
        l.setStrokeWidth(GRID_STROKE_WIDTH);

        return l;
    }
}
