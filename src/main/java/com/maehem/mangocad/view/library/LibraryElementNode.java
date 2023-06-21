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

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementCircle;
import com.maehem.mangocad.model.element.basic.ElementPolygon;
import com.maehem.mangocad.model.element.basic.ElementRectangle;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.FrameElement;
import com.maehem.mangocad.model.element.basic.PadSMD;
import com.maehem.mangocad.model.element.basic.PadTHD;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Vertex;
import com.maehem.mangocad.model.element.basic.Wire;
import static com.maehem.mangocad.model.element.enums.PadShape.*;
import com.maehem.mangocad.model.element.enums.PinFunction;
import static com.maehem.mangocad.model.element.enums.PinLength.*;
import com.maehem.mangocad.model.element.enums.TextAlign;
import static com.maehem.mangocad.model.element.enums.TextAlign.*;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.ControlPanel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryElementNode {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private enum PatternStyle {
        DARK_THIN, DARK_MED, DARK_THICK, LIGHT_THIN, LIGHT_MED, LIGHT_THICK
    }

    // Should come from a DRC object?
    private static final double MASK_W_DEFAULT = 0.1;

    /**
     * <pre>
     * ATTLIST wire 
     *     x1 %Coord; #REQUIRED 
     *     y1 %Coord; #REQUIRED 
     *     x2 %Coord; #REQUIRED 
     *     y2 %Coord; #REQUIRED 
     *     width %Dimension; #REQUIRED 
     *     layer %Layer; #REQUIRED 
     *     extent %Extent; #IMPLIED 
     *     style %WireStyle; "continuous" 
     *     curve %WireCurve; "0" 
     *     cap %WireCap; "round" 
     *     grouprefs IDREFS #IMPLIED
     *
     *     extent: Only applicable for airwires 
     *     cap : Only applicable if 'curve' is not zero
     * </pre>
     * 
     * @param w
     * @param color
     * @return
     */
    public static Node createWireNode(Wire w, Color color) {
        if (w.getCurve() != 0.0) {
            Path path = new Path();

            MoveTo moveTo = new MoveTo();
            moveTo.setX(w.getX1());
            moveTo.setY(-w.getY1());

            ArcTo arc = new ArcTo();
            // Curve to ARC
            arc.setX(w.getX2());
            arc.setY(-w.getY2());

            // SWEEP on negative curve value.
            arc.setSweepFlag(w.getCurve() < 0.0);

            double sin90 = Math.sin(Math.toRadians(90.0));
            double dist = distance(w.getX1(), -w.getY1(), w.getX2(), -w.getY2());
            double radius = (sin90 * dist / 2.0)
                    / Math.sin(Math.toRadians(w.getCurve() / 2.0));
            arc.setRadiusX(radius);
            arc.setRadiusY(radius);

            path.getElements().add(moveTo);
            path.getElements().add(arc);

            path.setStrokeLineCap(StrokeLineCap.ROUND);
            path.setStrokeWidth(w.getWidth());
            path.setStroke(color);

            return path;
        } else {
            Line line = new Line(w.getX1(), -w.getY1(), w.getX2(), -w.getY2());
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setStrokeWidth(w.getWidth());
            line.setStroke(color);

            return line;
        }
    }

    public static Node createRectangle(ElementRectangle r, Color color) {
        Rectangle rr = new Rectangle(
                r.getX1(), -r.getY2(),
                r.getX2() - r.getX1(), r.getY2() - r.getY1()
        );
        rr.setStrokeWidth(0);
        //rr.setStrokeLineCap(StrokeLineCap.ROUND);
        rr.setFill(color);
        rr.setRotate(r.getRotation());
        return rr;
    }

    public static Node createFrameNode(FrameElement fe, Color color) {
        Group frameGroup = new Group();
        Polygon border = new Polygon(
                fe.getX1(), -fe.getY1(),
                fe.getX2(), -fe.getY1(),
                fe.getX2(), -fe.getY2(),
                fe.getX1(), -fe.getY2()
        );
        //border.setLayoutY(-fe.getY2());
        LOGGER.log(Level.SEVERE, "Make a frame: {0},{1} .. {2},{3}", new Object[]{fe.getX1(), fe.getY1(), fe.getX2(), fe.getY2()});
        border.setStroke(color);
        border.setFill(Color.TRANSPARENT);
        border.setStrokeType(StrokeType.CENTERED);
        border.setStrokeWidth(0.1524); // 6 mil
        frameGroup.getChildren().add(border);
        
        return frameGroup;
    }

    public static Node createPolygon(ElementPolygon poly, Color color) {
        List<Vertex> vertices = poly.getVertices();
        double verts[] = new double[vertices.size() * 2];

        for (int j = 0; j < verts.length; j += 2) {
            verts[j] = vertices.get(j / 2).getX();
            verts[j + 1] = -vertices.get(j / 2).getY();
        }
        Polygon p = new Polygon(verts);
        p.setStrokeWidth(poly.getWidth());
        p.setStrokeLineCap(StrokeLineCap.ROUND);
        p.setFill(color);

        return p;
    }

    public static Node createText(ElementText et, Color color) {
        return createText(et, null, color);
    }
    
    public static Node createText(ElementText et, String altText, Color color) {
        double fontSizeMult =  0.72272; // Multiply font size
        double fontSize = et.getSize()/fontSizeMult;
        
        //String fontPath = "/fonts/Source_Code_Pro/SourceCodePro-VariableFont_wght.ttf";
        String fontPath = "/fonts/Source_Code_Pro/static/SourceCodePro-Bold.ttf";
        Font font = Font.loadFont(LibraryElementNode.class.getResourceAsStream(fontPath), fontSize);
        Text tt = new Text(altText!=null?altText:et.getValue());
        tt.setFont(font);
        tt.setFill(color);
        
        
        double width = tt.getBoundsInLocal().getWidth();
        double height = tt.getBoundsInLocal().getHeight();
        // JavaFX has not yet exposed FontMetrics so we make these assumtions.
        double fontAsc = height * 0.58; // Font ascends this much.
        double fontDes = height * 0.27;   // Font descends this much.

        int rot = (int) et.getRotation().getValue();
        boolean mir = et.getRotation().isMirror();

        double rotX = 0.0;
        double rotY = 0.0;

        switch (rot) {
            case 270 -> {
                tt.setRotate(270);
                rotX = -width / 2.0 + fontDes;
                rotY = width / 2.0 + fontDes;
            }
            case 180 -> {
                if (mir) {
                    rot = 0;
                    rotY = fontAsc;
                }
            }
            case 90 -> {
                tt.setRotate(270);
                rotX = -width / 2.0 - fontDes;
                rotY = -width / 2.0 + fontDes;
            }
            default -> {
                if (mir) {
                    rot = 180;
                    switch( et.getAlign() ) {
                        case TOP_CENTER,TOP_LEFT,TOP_RIGHT -> {
                            rotY = fontAsc;
                        }
                        default -> {
                            rotY = -fontAsc;
                        }
                    }
                }
            }
        }

        double pxL = et.getX() + rotX;
        double pxR = et.getX() - width + rotX;
        double pL = rot == 180 ? pxR : pxL;
        double pR = rot == 180 ? pxL : pxR;

        double pyT = -et.getY() + rotY + fontAsc;
        double pyB = -et.getY() + rotY;
        double pT = rot == 180 ? pyB : pyT;
        double pB = rot == 180 ? pyT : pyB;

        double cX = et.getX() - width / 2.0;
        double cY = -et.getY() + fontDes;

        Pane ttG = new Pane(tt);
        ttG.setPrefHeight(et.getSize());

        // Text alignment.
        switch (et.getAlign()) {
            case BOTTOM_CENTER -> {
                ttG.setLayoutX(cX);
                ttG.setLayoutY(pB);
            }
            case BOTTOM_LEFT -> {
                ttG.setLayoutX(pL);
                ttG.setLayoutY(pB);
            }
            case BOTTOM_RIGHT -> {
                ttG.setLayoutX(pR);
                ttG.setLayoutY(pB);
            }
            case CENTER -> {
                ttG.setLayoutX(cX);
                ttG.setLayoutY(cY);
            }
            case CENTER_LEFT -> {
                ttG.setLayoutX(pL);
                ttG.setLayoutY(cY);
            }
            case CENTER_RIGHT -> {
                ttG.setLayoutX(pR);
                ttG.setLayoutY(cY);
            }
            case TOP_CENTER -> {
                ttG.setLayoutX(cX);
                ttG.setLayoutY(pT);
            }
            case TOP_LEFT -> {
                ttG.setLayoutX(pL);
                ttG.setLayoutY(pT);
            }
            case TOP_RIGHT -> {
                ttG.setLayoutX(pR);
                ttG.setLayoutY(pT);
            }

        }

        return ttG;
    }

    /**
     * Create a SMD node for the pattern: 
     * xml ==> smd name="1" x="-0.751840625" y="0" dx="0.7112" dy="0.762" layer="1" roundness="20"
     *
     * TODO: Solder Mask
     *
     * @param smd
     * @param color
     * @param maskColor
     * @return JavaFX Node.
     */
    public static Node createSmd(PadSMD smd, Color color, Color maskColor) {
        Group g = new Group();

        ImagePattern maskPattern = makeHatch(10, maskColor);
        double roundPct = smd.getRoundness() * 0.01;

        Rectangle pad = new Rectangle();
        pad.setFill(color);

        double w = smd.getWidth();
        double h = smd.getHeight();

        double cX = smd.getX() - w / 2.0;
        double cY = -smd.getY() - h / 2.0;
        //Setting the properties of the rectangle 
        pad.setX(cX);
        pad.setY(cY);
        pad.setWidth(w);
        pad.setHeight(h);
        pad.setRotate(smd.getRotation());

        // arcW/H is half of the shortest side.
        // TODO: Won't render right if I use h/2 or w/2. Not sure why.
        if (smd.getRoundness() > 0) {
            double arcR;
            if (w < h) {
                arcR = w * roundPct;
            } else {
                arcR = h * roundPct;
            }
            pad.setArcWidth(arcR);
            pad.setArcHeight(arcR);
        }
        g.getChildren().add(pad);

        // Mask
        double maskWidth2 = MASK_W_DEFAULT * 2;
        double maskW = smd.getWidth() + maskWidth2;
        double maskH = smd.getHeight() + maskWidth2;

        double maskCX = smd.getX() - maskW / 2.0;
        double maskCY = -smd.getY() - maskH / 2.0;

        Rectangle mask = new Rectangle(
                maskCX, maskCY,
                maskW, maskH
        );
        mask.setStrokeWidth(0.02);
        mask.setStroke(maskColor);
        mask.setFill(maskPattern);
        mask.setRotate(smd.getRotation());

        // arcW/H is half of the shortest side.
        // TODO: Won't render right if I use h/2 or w/2. Not sure why.
        if (smd.getRoundness() > 0) {
            double maskArcR;
            if (maskW < maskH) {
                maskArcR = maskW * roundPct;
            } else {
                maskArcR = maskH * roundPct;
            }
            mask.setArcWidth(maskArcR);
            mask.setArcHeight(maskArcR);
        }
        g.getChildren().add(mask);

        // Name Text
        ElementText et = new ElementText();
        et.setValue(smd.getName());
        et.getRotation().setValue(smd.getRotation());
        et.setAlign(TextAlign.CENTER);
        et.setSize(0.5);
        et.setX(smd.getX());
        et.setY(smd.getY());
        g.getChildren().add(LibraryElementNode.createText(et, Color.LIGHTGRAY));

        return g;
    }

    /**
     * THD Pad
     *
     * TODO: Solder Mask, pin name, drill legend.
     *
     * @param thd element
     * @param padColor
     * @param maskColor
     * @return JavaFX Node
     */
    public static Node createThd(PadTHD thd, Color padColor, Color maskColor) {
        Group g = new Group();
        double padDia = thd.getDerivedDiameter();
        ImagePattern maskPattern = makeHatch(10, maskColor);

        //padColor = Color.GREEN;
        Color drillColor = Color.BLACK;
        //int rot = (int) thd.getRotation();

        switch (thd.getShape()) {
            case SQUARE -> {
                Rectangle pad = new Rectangle(
                        padDia, padDia,
                        padColor
                );
                pad.setStroke(null);
                pad.setLayoutX(thd.getX() - padDia / 2.0);
                pad.setLayoutY(-thd.getY() - padDia / 2.0);

                g.getChildren().add(pad);

                // SolderMask
                double maskWidth2 = MASK_W_DEFAULT * 2;
                Rectangle mask = new Rectangle(
                        padDia + maskWidth2, padDia + maskWidth2,
                        maskColor
                );
                mask.setStrokeWidth(0.01);
                mask.setStroke(maskColor);
                mask.setFill(maskPattern);
                mask.setLayoutX(thd.getX() - padDia / 2.0 - MASK_W_DEFAULT);
                mask.setLayoutY(-thd.getY() - padDia / 2.0 - MASK_W_DEFAULT);

                g.getChildren().add(mask);

            }
            case LONG -> {
                double padLongMult = 2.0;
                Rectangle pad = new Rectangle(
                        padDia * padLongMult, padDia,
                        padColor
                );
                pad.setArcHeight(padDia);
                pad.setArcWidth(padDia);
                pad.setStroke(null);

                Rotate rotate = new Rotate(360 - thd.getRotation());
                rotate.setPivotX(padDia);
                rotate.setPivotY(padDia / 2);
                pad.setLayoutX(thd.getX() - pad.getWidth() / 2.0);
                pad.setLayoutY(-thd.getY() - padDia / 2.0);
                pad.getTransforms().add(rotate);

                g.getChildren().add(pad);

                // Mask
                double maskWidth2 = MASK_W_DEFAULT * 2;
                Rectangle mask = new Rectangle(
                        padDia * padLongMult + maskWidth2, padDia + maskWidth2,
                        maskColor
                );
                mask.setArcHeight(padDia + MASK_W_DEFAULT);
                mask.setArcWidth(padDia + MASK_W_DEFAULT);
                mask.setStrokeWidth(0.02);
                mask.setStroke(maskColor);
                mask.setFill(maskPattern);
                Rotate rotateM = new Rotate(360 - thd.getRotation());
                rotateM.setPivotX(padDia + MASK_W_DEFAULT);
                rotateM.setPivotY(padDia / 2 + MASK_W_DEFAULT);
                mask.setLayoutX(thd.getX() - pad.getWidth() / 2.0 - MASK_W_DEFAULT);
                mask.setLayoutY(-thd.getY() - padDia / 2.0 - MASK_W_DEFAULT);
                mask.getTransforms().add(rotateM);

                g.getChildren().add(mask);
            }
            case OCTOGON -> {
                double r = padDia / 2.0;
                double n = r * 0.383;
                Polygon octo = new Polygon(
                        -n, -r,
                        n, -r,
                        r, -n,
                        r, n,
                        n, r,
                        -n, r,
                        -r, n,
                        -r, -n
                );
                octo.setFill(padColor);
                octo.setStroke(null);
                octo.setLayoutX(thd.getX());
                octo.setLayoutY(-thd.getY());

                g.getChildren().add(octo);

                // Mask
                double rr = padDia / 2.0 + MASK_W_DEFAULT;
                double nn = rr * 0.383;
                Polygon octoMask = new Polygon(
                        -nn, -rr,
                        nn, -rr,
                        rr, -nn,
                        rr, nn,
                        nn, rr,
                        -nn, rr,
                        -rr, nn,
                        -rr, -nn
                );
                octoMask.setStrokeWidth(0.02);
                octoMask.setStroke(maskColor);
                octoMask.setFill(maskPattern);
                octoMask.setLayoutX(thd.getX());
                octoMask.setLayoutY(-thd.getY());

                g.getChildren().add(octoMask);
            }
            case OFFSET -> {
                double padLongMult = 2.0;
                Rectangle pad = new Rectangle(
                        padDia * padLongMult, padDia,
                        padColor
                );
                pad.setArcHeight(padDia);
                pad.setArcWidth(padDia);
                pad.setStroke(null);
                pad.setLayoutX(thd.getX() - pad.getWidth() / 4.0);
                pad.setLayoutY(-thd.getY() - padDia / 2);
                Rotate rotate = new Rotate(360 - thd.getRotation());
                rotate.setPivotX(padDia / 2);
                rotate.setPivotY(padDia / 2);
                pad.getTransforms().add(rotate);
                g.getChildren().add(pad);

                double maskWidth2 = MASK_W_DEFAULT * 2;
                Rectangle mask = new Rectangle(
                        padDia * padLongMult + maskWidth2, padDia + maskWidth2,
                        maskColor
                );
                mask.setArcHeight(padDia + MASK_W_DEFAULT);
                mask.setArcWidth(padDia + MASK_W_DEFAULT);
                mask.setStroke(maskColor);
                mask.setStrokeWidth(0.02);
                mask.setFill(maskPattern);
                mask.setLayoutX(thd.getX() - padDia / 2.0 - MASK_W_DEFAULT);
                mask.setLayoutY(-thd.getY() - mask.getHeight() / 2.0);
                Rotate rotateMask = new Rotate(360 - thd.getRotation());
                rotateMask.setPivotX(padDia / 2 + MASK_W_DEFAULT);
                rotateMask.setPivotY(padDia / 2 + MASK_W_DEFAULT);
                mask.getTransforms().add(rotateMask);
                g.getChildren().add(mask);
            }
            default -> {  // ROUND
                Circle pad = new Circle(padDia / 2.0, padColor);
                pad.setLayoutX(thd.getX());
                pad.setLayoutY(-thd.getY());
                pad.setStroke(null);
                g.getChildren().add(pad);

                // SolderMask
                Circle mask = new Circle(padDia / 2.0 + MASK_W_DEFAULT, maskColor);

                mask.setStrokeWidth(0.02);
                mask.setStroke(maskColor);
                mask.setFill(maskPattern);
                mask.setLayoutX(thd.getX());
                mask.setLayoutY(-thd.getY());

                g.getChildren().add(mask);
            }
        }

        // Drill
        Circle drill = new Circle(thd.getDrill() / 2.0, drillColor);
        drill.setLayoutX(thd.getX());
        drill.setLayoutY(-thd.getY());
        drill.setStroke(null);

        g.getChildren().add(drill);

        // Name Text
        ElementText et = new ElementText();
        et.setValue(thd.getName());
        et.getRotation().setValue(thd.getRotation());
        et.setAlign(TextAlign.CENTER);
        et.setSize(0.5);
        et.setX(thd.getX());
        et.setY(thd.getY());
        g.getChildren().add(LibraryElementNode.createText(et, Color.LIGHTGREY));

        return g;
    }

    public static Node createPinNode(Pin p, Color c) {
        final double PIN_NAME_MARGIN = 1.5;
        final double PIN_STROKE_WIDTH = 0.1524; // 6 mil
        final double PIN_FONT_SIZE = 2.0;
        //final Color PIN_COLOR = new Color(0.2, 0.2, 0.2, 1.0);
        final Color PAD_NAME_COLOR = new Color(0.8, 0.8, 0.2, 0.8);
        final Color PIN_NAME_COLOR = new Color(0.8, 0.8, 0.8, 0.8);
        final Color PIN_COLOR_GHOST = new Color(0.9, 0.9, 0.9, 0.2);
        final Color PIN_DIR_SWAP_COLOR = new Color(0.3, 1.0, 0.3, 0.5);
        final double PIN_DIR_SWAP_OFFSET = PIN_FONT_SIZE * 0.2;
        final Color ORIGIN_CIRCLE_COLOR = new Color(1.0, 1.0, 1.0, 0.2);
        final double ORIGIN_CIRCLE_RADIUS = 0.635;
        final double ORIGIN_CIRCLE_LINE_WIDTH = 0.07;
        final double DOT_CIRCLE_RADIUS = 0.7;
        final double DOT_CIRCLE_LINE_WIDTH = PIN_STROKE_WIDTH * 1.7;
        final double CLK_SIZE = 1.3;

        int padHang = 0;
        switch (p.getLength()) {
            case LONG -> {
                padHang = 3;
            }
            case MIDDLE -> {
                padHang = 2;
            }
            case SHORT -> {
                padHang = 1;
            }
            case POINT -> {
                padHang = 0;
            }
        }

        // Use the padValue from DeviceSet if it exists.
        String padValue;
        if (p.getPadValue() != null) {
            padValue = p.getPadValue();
        } else { // Fill padValue with string that matches pinLength
            padValue = "9";
            for (int i = 1; i < padHang; i++) {
                padValue += "9";
            }
        }

        // There might be a dot on pin.
        double dotRadius = 0;
        if (p.getFunction() == PinFunction.DOT || p.getFunction() == PinFunction.DOTCLK) {
            dotRadius = DOT_CIRCLE_RADIUS;
        }

        double pinLen = padHang * 2.54 - dotRadius * 2.0;

        int rot = (int) p.getRotation(); // 0, 90, 180, 270

        Group g = new Group();

        Line line = new Line(p.getX(), -p.getY(), p.getX(), -p.getY());
        line.setStroke(c);
        line.setStrokeLineCap(StrokeLineCap.BUTT);
        line.setStrokeWidth(PIN_STROKE_WIDTH);

        Color pinNameColor = PIN_NAME_COLOR;
        Color padColor = PAD_NAME_COLOR;

        switch (p.getVisible()) {
            case BOTH -> {
            }
            case PAD -> {
                pinNameColor = PIN_COLOR_GHOST;
            }
            case PIN -> {
                padColor = PIN_COLOR_GHOST;
            }
            case OFF -> {
                pinNameColor = PIN_COLOR_GHOST;
                padColor = PIN_COLOR_GHOST;
            }
        }

        switch (rot) {
            case 270 ->
                line.setEndY(-p.getY() + pinLen);
            case 180 ->
                line.setEndX(p.getX() - pinLen);
            case 90 ->
                line.setEndY(-p.getY() - pinLen);
            default ->
                line.setEndX(p.getX() + pinLen);
        }

        // When you need some dots.
        if (dotRadius > 0.0) {
            // Dot Function
            ElementCircle dotCircle = new ElementCircle();
            dotCircle.setRadius(dotRadius);
            dotCircle.setWidth(DOT_CIRCLE_LINE_WIDTH);

            switch (rot) {
                case 270 -> {
                    dotCircle.setX(line.getEndX());
                    dotCircle.setY(-line.getEndY() - dotRadius);
                }
                case 180 -> {
                    dotCircle.setX(line.getEndX() - dotRadius);
                    dotCircle.setY(-line.getEndY());
                }
                case 90 -> {
                    dotCircle.setX(line.getEndX());
                    dotCircle.setY(-line.getEndY() + dotRadius);
                }
                default -> {
                    dotCircle.setX(line.getEndX() + dotRadius);
                    dotCircle.setY(-line.getEndY());
                }
            }

            g.getChildren().add(createCircleNode(dotCircle, c));
        }

        // Clock Function
        if (p.getFunction() == PinFunction.CLK || p.getFunction() == PinFunction.DOTCLK) {
            Line line1 = new Line(0, 0, 0, 0);
            line1.setStroke(c);
            line1.setStrokeLineCap(StrokeLineCap.ROUND);
            line1.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);

            Line line2 = new Line();
            line2.setStroke(c);
            line2.setStrokeLineCap(StrokeLineCap.ROUND);
            line2.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);

            switch (rot) {
                case 270 -> {
                    line1.setStartX(line.getEndX() - CLK_SIZE / 2.0);
                    line1.setStartY(line.getEndY() + dotRadius * 2.0);
                    line1.setEndX(line.getEndX());
                    line1.setEndY(line1.getStartY() + CLK_SIZE);
                    line2.setStartX(line.getEndX() + CLK_SIZE / 2.0);
                    line2.setStartY(line.getEndY() + dotRadius * 2.0);
                    line2.setEndX(line.getEndX());
                    line2.setEndY(line1.getStartY() + CLK_SIZE);
                }
                case 180 -> {
                    line1.setStartX(line.getEndX());
                    line1.setStartY(line.getEndY() + CLK_SIZE / 2.0);
                    line1.setEndX(line1.getStartX() - CLK_SIZE);
                    line1.setEndY(-p.getY());
                    line2.setStartX(line.getEndX());
                    line2.setStartY(line.getEndY() - CLK_SIZE / 2.0);
                    line2.setEndX(line1.getStartX() - CLK_SIZE);
                    line2.setEndY(-p.getY());
                }
                case 90 -> {
                    line1.setStartX(line.getEndX() - CLK_SIZE / 2.0);
                    line1.setStartY(line.getEndY() - dotRadius * 2.0);
                    line1.setEndX(line.getEndX());
                    line1.setEndY(line1.getStartY() - CLK_SIZE);
                    line2.setStartX(line.getEndX() + CLK_SIZE / 2.0);
                    line2.setStartY(line.getEndY() - dotRadius * 2.0);
                    line2.setEndX(line.getEndX());
                    line2.setEndY(line1.getStartY() - CLK_SIZE);
                }
                default -> {
                    line1.setStartX(line.getEndX() + dotRadius * 2.0);
                    line1.setStartY(line.getEndY() + CLK_SIZE / 2.0);
                    line1.setEndX(line1.getStartX() + CLK_SIZE);
                    line1.setEndY(-p.getY());
                    line2.setStartX(line.getEndX() + dotRadius * 2.0);
                    line2.setStartY(line.getEndY() - CLK_SIZE / 2.0);
                    line2.setEndX(line1.getStartX() + CLK_SIZE);
                    line2.setEndY(-p.getY());
                }
            }

            g.getChildren().addAll(line1, line2);
        }

        // Pin Name
        Text pinName = new Text(p.getName());
        pinName.setFont(Font.font(PIN_FONT_SIZE));
        pinName.setFill(pinNameColor);
        double width = pinName.getBoundsInLocal().getWidth();
        double height = pinName.getBoundsInLocal().getHeight();
        g.getChildren().add(pinName);

        //Text padName = new Text(padText);
        Text padName = new Text(padValue);
        padName.setFont(Font.font(PIN_FONT_SIZE * 0.8));
        padName.setFill(padColor);
        double padWidth = padName.getBoundsInLocal().getWidth();
        double padHeight = padName.getBoundsInLocal().getHeight();
        g.getChildren().add(padName);

        Text padChar = new Text("A");
        padChar.setFont(padName.getFont());
        double padCharWidth = padChar.getBoundsInLocal().getWidth();

        // Direction and Swap-Level
        Text dirSwap = new Text(p.getDirection().code() + "  " + p.getSwapLevel());
        dirSwap.setFont(Font.font(PIN_FONT_SIZE * 0.7));
        dirSwap.setFill(PIN_DIR_SWAP_COLOR);
        double dsWidth = dirSwap.getBoundsInLocal().getWidth();
        double dsHeight = dirSwap.getBoundsInLocal().getHeight();
        g.getChildren().add(dirSwap);

        switch (rot) {
            case 270 -> {
                pinName.setLayoutX(p.getX() - width / 2);
                pinName.setLayoutY(-p.getY() + width / 2 + height * 0.3 + pinLen + dotRadius * 2.0 + PIN_NAME_MARGIN);
                pinName.setRotate(90);

                padName.setLayoutX(p.getX() - padWidth / 2 + padHeight * 0.5);
                padName.setLayoutY(-p.getY() - padWidth / 2 + padHeight * 0.3 + padCharWidth * padHang);
                padName.setRotate(90);

                dirSwap.setLayoutX(p.getX() - PIN_DIR_SWAP_OFFSET - dsHeight / 2 - dsWidth / 2);
                dirSwap.setLayoutY(-p.getY() - dsWidth / 3 - PIN_DIR_SWAP_OFFSET);
                dirSwap.setRotate(270);
            }
            case 180 -> {
                pinName.setLayoutX(p.getX() - pinLen - dotRadius * 2.0 - width - PIN_NAME_MARGIN);
                pinName.setLayoutY(-p.getY() + height * 0.3);

                padName.setLayoutX(p.getX() - padCharWidth * padHang);
                padName.setLayoutY(-p.getY() - padHeight * 0.2);

                dirSwap.setLayoutX(p.getX() + PIN_DIR_SWAP_OFFSET);
                dirSwap.setLayoutY(-p.getY() - PIN_DIR_SWAP_OFFSET);
            }
            case 90 -> {
                // Rotate Node rotates on center, so we need to compensate for that.
                pinName.setLayoutX(p.getX() - width / 2);
                pinName.setLayoutY(-p.getY() - width / 2 + height * 0.3 - pinLen - dotRadius * 2.0 - PIN_NAME_MARGIN);
                pinName.setRotate(90);

                padName.setLayoutX(p.getX() - padWidth / 2 + padHeight / 2 /*+ padHeight*0.3*/);
                padName.setLayoutY(-p.getY() + padHeight * 0.3 - padCharWidth * padHang + padWidth / 2.0);
                padName.setRotate(90);

                dirSwap.setLayoutX(p.getX() + PIN_DIR_SWAP_OFFSET + dsHeight / 2 - dsWidth / 2);
                dirSwap.setLayoutY(-p.getY() + dsWidth / 2 + dsHeight / 3 + PIN_DIR_SWAP_OFFSET);
                dirSwap.setRotate(90);
            }
            default -> {
                pinName.setLayoutX(p.getX() + pinLen + dotRadius * 2.0 + PIN_NAME_MARGIN);
                pinName.setLayoutY(-p.getY() + height * 0.3);

                padName.setLayoutX(p.getX() - padWidth + padCharWidth * padHang); // Hang over the pin by one char.
                padName.setLayoutY(-p.getY() - padHeight * 0.2);

                dirSwap.setLayoutX(p.getX() - PIN_DIR_SWAP_OFFSET - dsWidth);
                dirSwap.setLayoutY(-p.getY() - PIN_DIR_SWAP_OFFSET);
            }
        }

        g.getChildren().add(line);

        ElementCircle originCircle = new ElementCircle();
        originCircle.setX(p.getX());
        originCircle.setY(p.getY());
        originCircle.setRadius(ORIGIN_CIRCLE_RADIUS);
        originCircle.setWidth(ORIGIN_CIRCLE_LINE_WIDTH);
        // Origin Circle
        g.getChildren().add(createCircleNode(originCircle, ORIGIN_CIRCLE_COLOR));

        return g;
    }

    /**
     * xml ==> circle x="3.6068" y="0" radius="1.016" width="0.508" layer="94"
     *
     * @param ec ElementCircle object
     * @param color to make the circle
     * @return
     */
    public static Node createCircleNode(ElementCircle ec, Color color) {
        Circle c = new Circle(ec.getX(), -ec.getY(), ec.getRadius());

        c.setStroke(color);
        c.setStrokeWidth(ec.getWidth());
        c.setFill(null);

        return c;
    }

    public static Node crosshairs(double x, double y, double size, double strokeWidth, Color color) {
        // Crosshairss
        Group g = new Group();
        Line cH = new Line(x - 0.5, y, x + 0.5, y);
        cH.setStrokeWidth(strokeWidth);
        cH.setStroke(color);
        g.getChildren().add(cH);
        Line cV = new Line(x, y - 0.5, x, y + 0.5);
        cV.setStrokeWidth(strokeWidth);
        cV.setStroke(color);
        g.getChildren().add(cV);

        return g;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double ac = Math.abs(y2 - y1);
        double cb = Math.abs(x2 - x1);

        return Math.hypot(ac, cb);
    }

    private static ImagePattern makeHatch(int nLines, Color c) {
        int size = 64;
        WritableImage wi = new WritableImage(size, size);

        Pane p = new Pane(); // Group?
        p.setBackground(Background.EMPTY);
        p.setClip(new Rectangle(64, 64));


        double inc = (double) size / nLines;
        for (int i = 0; i < nLines * 2; i++) {
            Line l = new Line(i * inc, 0, i * inc - size, size);
            l.setStrokeWidth(0.5);
            l.setStroke(c);
            p.getChildren().add(l);
        }

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        return new ImagePattern(p.snapshot(sp, wi), 0, 0, 2, 2, false);
    }

    public static Node createSymbolNode( Symbol symbol, LayerElement[] layers, ColorPalette palette) {
        Group g = new Group();
        StackPane pane = new StackPane(g);

        symbol.getElements().forEach((e) -> {
            int layerNum = e.getLayerNum();
            LayerElement le = layers[e.getLayerNum()];
            if (le == null) {
                LOGGER.log(Level.SEVERE, "No Layer for: {0}", e.getLayerNum());
            }
            int colorIndex = le.getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));

            // (polygon | wire | text | dimension | pin | circle | rectangle | frame)
            if (e instanceof ElementPolygon ep) {
                g.getChildren().add(LibraryElementNode.createPolygon(ep, c));
            } else if (e instanceof Wire w) {
                g.getChildren().add(LibraryElementNode.createWireNode(w, c));
            } else if (e instanceof ElementText et) {
                g.getChildren().add(LibraryElementNode.createText(et, c));
                g.getChildren().add(LibraryElementNode.crosshairs(et.getX(), -et.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if (e instanceof Dimension dim) {
                //g.getChildren().add(LibraryElementNode.createDimensionNode(dim, c));
                LOGGER.log(Level.SEVERE, "TODO: Create Dimension Node.");
            } else if (e instanceof Pin pin) {
                g.getChildren().add(LibraryElementNode.createPinNode(pin, c));
            } else if (e instanceof ElementCircle ec) {
                g.getChildren().add(LibraryElementNode.createCircleNode(ec, c));
            } else if (e instanceof ElementRectangle rect) {
                g.getChildren().add(LibraryElementNode.createRectangle(rect, c));
            } else if (e instanceof FrameElement frm) {
                g.getChildren().add(LibraryElementNode.createFrameNode(frm, c));
            }
        });
        g.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.05, Color.RED
        ));
        
        return g;
    }
}
