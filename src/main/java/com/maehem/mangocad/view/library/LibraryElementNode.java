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
import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.basic.Attribute;
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementCircle;
import com.maehem.mangocad.model.element.basic.ElementPolygon;
import com.maehem.mangocad.model.element.basic.ElementRectangle;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.FrameElement;
import com.maehem.mangocad.model.element.basic.Instance;
import com.maehem.mangocad.model.element.basic.Junction;
import com.maehem.mangocad.model.element.basic.LabelElement;
import com.maehem.mangocad.model.element.basic.PadSMD;
import com.maehem.mangocad.model.element.basic.PadTHD;
import com.maehem.mangocad.model.element.basic.Part;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Probe;
import com.maehem.mangocad.model.element.basic.Vertex;
import com.maehem.mangocad.model.element.basic.Wire;
import static com.maehem.mangocad.model.element.enums.PadShape.*;
import com.maehem.mangocad.model.element.enums.PinFunction;
import static com.maehem.mangocad.model.element.enums.PinLength.*;
import com.maehem.mangocad.model.element.enums.TextAlign;
import static com.maehem.mangocad.model.element.enums.TextAlign.*;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Segment;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.util.Rotation;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.ControlPanel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
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
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryElementNode {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    //public static final String FONT_PATH = "/fonts/OCR-A/OCRA.ttf";
    //public static final String FONT_PATH = "/fonts/Source_Code_Pro/static/SourceCodePro-Medium.ttf";
    //private static final double FONT_ASC_PCT = 0.526; // 53%   (0.0 - 1.0)
    public static final String FONT_PATH = "/fonts/Share_Tech_Mono/ShareTechMono-Regular.ttf";
    public static final double FONT_SCALE = 1.055; // Font height can vary depending on Family.
    private static final double FONT_ASC_PCT = 0.61; // 53%   (0.0 - 1.0)

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
     * @param mirror
     * @return
     */
    public static Node createWireNode(Wire w, Color color, boolean mirror) {
        double strokeWidth = w.getWidth();
        double x1 = mirror ? -w.getX1() : w.getX1();
        double x2 = mirror ? -w.getX2() : w.getX2();
        double y1 = w.getY1();
        double y2 = w.getY2();

        if (strokeWidth < 0.03) {
            // Wires can't be 0 width.
            strokeWidth = 0.03; // 6mil
        }
        if (w.getCurve() != 0.0) {
            Path path = new Path();

            MoveTo moveTo = new MoveTo();
            moveTo.setX(x1);
            moveTo.setY(-y1);

            ArcTo arc = new ArcTo();
            // Curve to ARC
            arc.setX(x2);
            arc.setY(-y2);

            // SWEEP on negative curve value.
            arc.setSweepFlag(w.getCurve() < 0.0);

            double sin90 = Math.sin(Math.toRadians(90.0));
            double dist = distance(x1, -y1, x2, -y2);
            double radius = (sin90 * dist / 2.0)
                    / Math.sin(Math.toRadians(w.getCurve() / 2.0));
            arc.setRadiusX(radius);
            arc.setRadiusY(radius);

            path.getElements().add(moveTo);
            path.getElements().add(arc);

            path.setStrokeLineCap(StrokeLineCap.ROUND);
            path.setStrokeWidth(strokeWidth);
            path.setStroke(color);

            return path;
        } else {
            Line line = new Line(x1, -y1, x2, -y2);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setStrokeWidth(strokeWidth);
            line.setStroke(color);
            line.setSmooth(true);

            return line;
        }
    }

    public static Node createRectangle(ElementRectangle r, Color color, boolean mirror) {
        double x1 = mirror ? -r.getX1() : r.getX1();
        double x2 = mirror ? -r.getX2() : r.getX2();

        Rectangle rr = new Rectangle(
                x1, -r.getY2(),
                x2 - x1, r.getY2() - r.getY1()
        );
        rr.setStrokeWidth(0);
        //rr.setStrokeLineCap(StrokeLineCap.ROUND);
        rr.setFill(color);
        rr.setRotate(r.getRot());
        return rr;
    }

    public static Node createFrameNode(FrameElement fe, Color color) {
        double thick = 3.81;
        double strokeOuter = 0.2;
        double strokeInner = 0.1;

        Group frameGroup = new Group();

        if (fe.isBorderLeft()) {
            Line line = new Line(
                    fe.getX1() + thick, -fe.getY1() - thick,
                    fe.getX1() + thick, -fe.getY2() + thick
            );
            line.setStrokeWidth(strokeInner);
            line.setStroke(color);
            frameGroup.getChildren().add(line);

            // Row Legend
            addFrameRows(frameGroup,
                    fe.getX1(), fe.getY1(), fe.getX2(), fe.getY2(),
                    thick, strokeInner, color, fe.getRows()
            );
        }
        if (fe.isBorderRight()) {
            Line line = new Line(
                    fe.getX2() - thick, -fe.getY1() - thick,
                    fe.getX2() - thick, -fe.getY2() + thick
            );
            line.setStrokeWidth(strokeInner);
            line.setStroke(color);
            frameGroup.getChildren().add(line);
            // Row Legend
            addFrameRows(frameGroup,
                    fe.getX2() - thick, fe.getY1(), fe.getX2(), fe.getY2(),
                    thick, strokeInner, color, fe.getRows()
            );
        }
        if (fe.isBorderTop()) {
            Line line = new Line(
                    fe.getX1() + thick, -fe.getY1() - thick,
                    fe.getX2() - thick, -fe.getY1() - thick
            );
            line.setStrokeWidth(strokeInner);
            line.setStroke(color);
            frameGroup.getChildren().add(line);

            addFrameColumns(frameGroup,
                    fe.getX1(), fe.getY1(), fe.getX2(), fe.getY2(),
                    thick, strokeInner, color, fe.getColumns()
            );
        }
        if (fe.isBorderBottom()) {
            Line line = new Line(
                    fe.getX1() + thick, -fe.getY2() + thick,
                    fe.getX2() - thick, -fe.getY2() + thick
            );
            line.setStrokeWidth(strokeInner);
            line.setStroke(color);
            frameGroup.getChildren().add(line);

            addFrameColumns(frameGroup,
                    fe.getX1(), fe.getY2() - thick, fe.getX2(), fe.getY2(),
                    thick, strokeInner, color, fe.getColumns()
            );
        }

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
        border.setStrokeWidth(strokeOuter); // 6 mil
        frameGroup.getChildren().add(border);

        return frameGroup;
    }

    private static void addFrameRows(Group g, double x1, double y1, double x2, double y2, double thick, double stroke, Color color, double nRows) {
        if (nRows <= 0) {
            return;
        }
        double rowH = (Math.abs(y1 - y2) - 2 * thick) / nRows;
        for (int r = 0; r < nRows; r++) {
            Text t = new Text(String.valueOf((char) ('A' + r)));
            t.setFont(Font.font(3));
            t.setLayoutX(x1 + 0.8);
            t.setLayoutY(-y2 + thick + rowH * r + rowH / 2.0);
            t.setFill(color);
            g.getChildren().add(t);

            // Row divider (no first row, placed above)
            if (r > 0) {
                Line divLine = new Line(
                        x1, -thick - rowH * r,
                        x1 + thick, -thick - rowH * r
                );
                divLine.setStrokeWidth(stroke);
                divLine.setStroke(color);
                g.getChildren().add(divLine);
            }
        }
    }

    private static void addFrameColumns(Group g, double x1, double y1, double x2, double y2, double thick, double stroke, Color color, double nCols) {
        if (nCols <= 0) {
            return;
        }
        double colW = (Math.abs(x1 - x2) - 2 * thick) / nCols;
        for (int c = 0; c < nCols; c++) {
            Text t = new Text(String.valueOf(1 + c));
            t.setFont(Font.font(3));
            t.setLayoutX(x1 + thick + colW * c + colW / 2.0);
            t.setLayoutY(-y1 - 0.8);
            t.setFill(color);
            g.getChildren().add(t);

            // Row divider (no first row, placed above)
            if (c > 0) {
                Line divLine = new Line(
                        thick + colW * c, -y1,
                        thick + colW * c, -y1 - thick
                );
                divLine.setStrokeWidth(stroke);
                divLine.setStroke(color);
                g.getChildren().add(divLine);
            }
        }
    }

    public static Node createPolygon(ElementPolygon poly, Color color, boolean mirror) {
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

    public static Node createProbeNode(Probe le, Color color, Segment seg) {
        Group labelGroup = new Group();
        int rot = (int) le.getRot();
        double dotRadius = 0.8;
        double lineW = 0.5;

        // Dot at x,y
        // Label with align bottom/left
        // Use mirror to medify align.
        // Line to segment. Nearest?
        // rounded square at segment x,y.
        // Spec says "xref" possible, but Eagle has no option to enable it.
        le.setAlign(BOTTOM_LEFT);

        String probeText;
        switch (le.getProbeType()) {
            case Probe.PROBETYPE_VOLTAGE ->
                probeText = "V(" + le.getValue() + ")";
            case Probe.PROBETYPE_PHASE ->
                probeText = "Vp(" + le.getValue() + ")";
            default ->
                probeText = "V???(" + le.getValue() + ")";
        }
        Node textNode = createText(le, probeText, color, null);

        double x = le.getX();
        double y = -le.getY();
        double size = le.getSize();
        double length = le.getValue().length();
        double spacer = le.getSize() / 2.0;
        switch (rot) {
            case 270 -> {
                textNode.setTranslateY(spacer);
            }
            case 180 -> {
                textNode.setTranslateX(-spacer);
            }
            case 90 -> {
                textNode.setTranslateY(-spacer);
            }
            default -> {
                textNode.setTranslateX(spacer);
            }
        }

        labelGroup.getChildren().add(textNode);

        Circle c = new Circle(le.getX(), -le.getY(), dotRadius, color);
        labelGroup.getChildren().add(c);

        // Pretty sure this is not ever used in Eagle.
        if (le.isXref()) {
            Polygon outline = new Polygon(
                    x, y,
                    x + size, y + size,
                    x + ((length + 1) * size), y + size,
                    x + ((length + 1) * size), y - size,
                    x + size, y - size
            );
            outline.setStroke(color);
            outline.setStrokeWidth(0.3);
            outline.setFill(Color.TRANSPARENT);
            Transform rTf = new Rotate(-le.getRot(), x, y);
            outline.getTransforms().add(rTf);
            labelGroup.getChildren().add(outline);
        }

        // Find closest x,y on a Wire Segment and make that the anchor.
        // Calculate Probe line and square-anchor location.
        Wire closestWire = null;
        for (_AQuantum element : seg) {
            if (element instanceof Wire w) {
                if (closestWire == null) {
                    closestWire = w;
                    //LOGGER.log(Level.SEVERE, "Closest: " + closestWire.toString());
                } else {
                    double dX = le.getX() - w.getAverageX();
                    double dY = le.getY() - w.getAverageY();
                    double distW = Math.abs(Math.hypot(dX, dY));

                    double dCX = le.getX() - closestWire.getAverageX();
                    double dCY = le.getY() - closestWire.getAverageY();
                    double distCW = Math.abs(Math.hypot(dCX, dCY));

                    // If this is closer than the last one make it the new reference.
                    if (distW < distCW) {
                        closestWire = w;
                        //LOGGER.log(Level.SEVERE, "New closest: " + closestWire.toString());
                    }
                }
            }
        }
        if (closestWire == null) {
            LOGGER.log(Level.SEVERE, "Could not determine closest wire to Probe!");
        } else {
            // Find halfway point on line and make it X2,Y2 for probe line.
            double averageX = closestWire.getAverageX();
            double averageY = -closestWire.getAverageY();

            Line l = new Line(le.getX(), -le.getY(), averageX, averageY);
            l.setStrokeWidth(lineW);
            l.setStroke(color);

            Rectangle r = new Rectangle(dotRadius * 2.0, dotRadius * 2.0, color);
            r.setLayoutX(averageX - dotRadius);
            r.setLayoutY(averageY - dotRadius);

            labelGroup.getChildren().addAll(l, r);
        }

        return labelGroup;
    }

    public static Node createLabelNode(LabelElement le, Color color) {
        Group labelGroup = new Group();
        double x = le.getX();
        double y = -le.getY();
        double size = le.getSize();
        double length = le.getValue().length();

        Text t = new Text(le.getValue());
        double fontSizeMult = 0.72272; // INCH to Point ratio
        double fontSize = le.getSize() / fontSizeMult;
        fontSize *= FONT_SCALE;

        //String fontPath = "/fonts/Source_Code_Pro/static/SourceCodePro-Bold.ttf";
        Font font = Font.loadFont(LibraryElementNode.class.getResourceAsStream(FONT_PATH), fontSize);
        t.setFont(font);
        labelGroup.getChildren().add(t);
        t.setFill(color);
        double height = t.getBoundsInLocal().getHeight();
        double width = t.getBoundsInLocal().getWidth();
        t.setLayoutX(le.getX() + le.getSize());
        t.setLayoutY(-le.getY() + height * 0.25);

        if ((le.getRot() == 0 && le.getRotation().isMirror())
                || (le.getRot() == 180 && !le.getRotation().isMirror())
                || le.getRot() == 270) { // Flip Text
            Rotate r = new Rotate(180, width / 2.0, -height * 0.25);
            t.getTransforms().add(r);
        }

        if (le.isXref()) {
            Polygon outline = new Polygon(
                    x, y,
                    x + size, y + size,
                    x + ((length + 1) * size), y + size,
                    x + ((length + 1) * size), y - size,
                    x + size, y - size
            );
            outline.setStroke(color);
            outline.setStrokeWidth(0.3);
            outline.setFill(Color.TRANSPARENT);
            labelGroup.getChildren().add(outline);
        }

        double rot = -le.getRot();
        if (le.getRotation().isMirror() && (le.getRot() == 0 || le.getRot() == 180)) {
            rot += 180.00;
            rot %= 360;
        }
        Rotate r = new Rotate(rot, le.getX(), -le.getY());
        labelGroup.getTransforms().add(r);

        return labelGroup;
    }

    public static Node createLabelNodeOld(LabelElement le, Color color) {
        Group labelGroup = new Group();
        int rot = (int) le.getRot();

        le.setAlign(le.isXref() ? CENTER_LEFT : BOTTOM_LEFT);
        Node textNode = createText(le, color);
        double x = le.getX();
        double y = -le.getY();
        double size = le.getSize();
        double length = le.getValue().length();
        switch (rot) {
            case 270 -> {
                textNode.setTranslateY(le.getSize());
            }
            case 180 -> {
                textNode.setTranslateX(-size);
            }
            case 90 -> {
                textNode.setTranslateY(-le.getSize());
            }
            default -> {
                textNode.setTranslateX(le.getSize());
            }
        }

        labelGroup.getChildren().add(textNode);

        if (le.isXref()) {
            Polygon outline = new Polygon(
                    x, y,
                    x + size, y + size,
                    x + ((length + 1) * size), y + size,
                    x + ((length + 1) * size), y - size,
                    x + size, y - size
            );
            outline.setStroke(color);
            outline.setStrokeWidth(0.3);
            outline.setFill(Color.TRANSPARENT);
            Transform rTf = new Rotate(-le.getRot(), x, y);
            outline.getTransforms().add(rTf);
            labelGroup.getChildren().add(outline);
        }

        return labelGroup;
    }

    public static Node createText(ElementText et, Color color) {
        return createText(et, null, color, null);
    }

    public static Node createText(ElementText et, Color color, Rotation parentRotation) {
        return createText(et, null, color, parentRotation);
    }

    public static Node createText(ElementText et, String altText, Color color, Rotation parentRotation) {
        boolean showBorder = false;

        Group g = new Group();
        Rotation rotation = et.getRotation();
        double rot = rotation.getValue();
        boolean mir = rotation.isMirror();

        //double fontSizeMult = 0.666; // INCH to Point ratio
        double fontSizeMult = 0.7272; // INCH to Point ratio
        double fontSize = et.getSize() / fontSizeMult;
        fontSize *= FONT_SCALE;

        //String fontPath = "/fonts/Source_Code_Pro/static/SourceCodePro-Bold.ttf";
        Font font = Font.loadFont(LibraryElementNode.class.getResourceAsStream(FONT_PATH), fontSize);
        Text tt = new Text(altText != null ? altText : et.getValue());
        tt.setFont(font);
        tt.setFill(color);

        Text exLine = new Text("EXAMPLE");
        exLine.setFont(font);
        double lineHeight = exLine.getBoundsInLocal().getHeight();

        // JavaFX has not yet exposed FontMetrics so we make these assumtions.
        double fontAsc = lineHeight * FONT_ASC_PCT; // Font ascends this much.
        double fontDesc = lineHeight * (1.0 - FONT_ASC_PCT);
        tt.setLineSpacing(fontAsc * et.getDistance() * 0.01 - fontDesc);

        double textWidth = tt.getBoundsInLocal().getWidth();
        double textHeight = tt.getBoundsInLocal().getHeight();
        double borderW = 0.05;

//        LOGGER.log(Level.SEVERE, 
//                "Font Size: {0}   Text Hight: {1}  Line Height: {2}", 
//                new Object[]{fontSize, textHeight, lineHeight}
//        );
        tt.setLayoutY(fontAsc + borderW);

        // Flip text 180 for certain rotations.
        if (rot == 180
                || (mir && rotation.getValue() == 90)
                || (!mir && rotation.getValue() == 270)) {
            Rotate tR = new Rotate(180.0, textWidth / 2.0, -textHeight * FONT_ASC_PCT / 2.0);
            tt.getTransforms().add(tR);
        }

        // Text lives inside a Pane area that might be 
        // colored/backgrounded based on DRC error.
        Pane ttG = new Pane(tt);

        // Debug Box for entire text area.
//        Rectangle textAreaRect = new Rectangle(textWidth, textHeight, Color.TRANSPARENT);
//        textAreaRect.setStroke(Color.GREENYELLOW);
//        textAreaRect.setStrokeWidth(0.08);
//        ttG.getChildren().add(textAreaRect);
        ttG.setPrefHeight(textHeight - fontDesc + borderW * 2.0);
        ttG.setPrefWidth(textWidth);

        // Text Area Width
        //double taWidth = tt.getBoundsInLocal().getWidth();
        //double taHeight = ttG.getBoundsInLocal().getHeight();
        double taWidth = ttG.getPrefWidth();
        double taHeight = ttG.getPrefHeight();

        double x = et.getX();
        double y = et.getY();

        double pivotX;
        double pivotY;

        switch (et.getAlign()) {
            case BOTTOM_LEFT -> {
                pivotX = mir ? taWidth : 0;
                pivotY = -taHeight;
            }
            case BOTTOM_CENTER -> {
                pivotX = taWidth / 2.0;
                pivotY = -taHeight;
            }
            case BOTTOM_RIGHT -> {
                pivotX = mir ? 0 : taWidth;
                pivotY = -taHeight;
                tt.setTextAlignment(TextAlignment.RIGHT);
            }
            case CENTER_LEFT -> {
                pivotX = mir ? taWidth : 0;
                pivotY = -taHeight / 2.0;

            }
            case CENTER -> {
                pivotX = taWidth / 2.0;
                pivotY = -taHeight / 2.0;
            }
            case CENTER_RIGHT -> {
                pivotX = mir ? 0 : taWidth;
                pivotY = -taHeight / 2.0;
                tt.setTextAlignment(TextAlignment.RIGHT);
            }
            case TOP_LEFT -> {
                pivotX = mir ? taWidth : 0;
                pivotY = 0;

            }
            case TOP_CENTER -> {
                pivotX = taWidth / 2.0;
                pivotY = 0;
            }
            default -> { // TOP_RIGHT
                pivotX = mir ? 0 : taWidth;
                pivotY = 0;
                tt.setTextAlignment(TextAlignment.RIGHT);
            }
        }

        if (showBorder) {
            ttG.setBorder(new Border(new BorderStroke(
                    Color.BLUE, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(borderW))));
        }
        ttG.setLayoutX(x - pivotX);
        ttG.setLayoutY(-y + pivotY);

        double chSize = 0.8; // Crosshairs size
        double chStroke = 0.05;

        // Crosshairs (original axis)
// For Debug
//        Line chP = new Line(-chSize, 0, chSize, 0);
//        chP.setStroke(Color.MAGENTA);
//        chP.setStrokeWidth(chStroke);
//        Line cvP = new Line(0, -chSize, 0, +chSize);
//        cvP.setStroke(Color.MAGENTA);
//        cvP.setStrokeWidth(chStroke);
//        ttG.getChildren().addAll(chP,cvP);
        // Crosshairs ( visible )
        Line ch = new Line(x - chSize, -y, x + chSize, -y);
        ch.setStroke(Color.WHITE);
        ch.setStrokeWidth(chStroke);
        Line cv = new Line(x, -y - chSize, x, -y + chSize);
        cv.setStroke(Color.WHITE);
        cv.setStrokeWidth(chStroke);

        g.getChildren().addAll(ttG, ch, cv);
        double rotG = mir ? et.getRot() : -et.getRot();
        Rotate rTTG = new Rotate(rotG, pivotX, -pivotY);
        ttG.getTransforms().add(rTTG);

        return g;
    }

    public static Node createTextOld(ElementText et, String altText, Color color, boolean leftIsRight, boolean upIsDown) {
        boolean showBorder = false;

        double fontSizeMult = 0.72272; // INCH to Point ratio
        double fontSize = et.getSize() / fontSizeMult;

        String fontPath = "/fonts/Source_Code_Pro/static/SourceCodePro-Bold.ttf";
        Font font = Font.loadFont(LibraryElementNode.class.getResourceAsStream(fontPath), fontSize);
        Text tt = new Text(altText != null ? altText : et.getValue());
        tt.setFont(font);
        tt.setFill(color);

        double width = tt.getBoundsInLocal().getWidth();
        double height = tt.getBoundsInLocal().getHeight();
        double borderW = 0.1;
        // JavaFX has not yet exposed FontMetrics so we make these assumtions.
        double fontAsc = height * 0.53; // Font ascends this much.
        //double fontDes = height * 0.27; // Font descends this much.

        double rot = et.getRot();
        tt.setLayoutY(fontAsc + borderW);
        if (upIsDown) {
            Rotate rT = new Rotate(180, width / 2.0, -fontAsc / 2.0);
            tt.getTransforms().add(rT);
        }
        if (et.getRot() > 90.0 && et.getRot() <= 270.0) {
            Rotate rT = new Rotate(180, width / 2.0, -fontAsc / 2.0);
            tt.getTransforms().add(rT);
        }

        // Apply parent mirror
        boolean mir = leftIsRight ? !et.getRotation().isMirror() : et.getRotation().isMirror();

        // jfxRot is the JavaFX rotation and is visually mirroed from EagleCAD rotation.
        double jfxRot = 360.0 - rot;

        // Text lives inside a Pane area that might be 
        // colored/backgrounded based on DRC error.
        Pane ttG = new Pane(tt);
        ttG.setPrefHeight(fontAsc + borderW * 2.0);

        double x = mir ? -et.getX() : et.getX();

        // Pivot Sizes
        double pivL = borderW;
        double pivR = borderW + width;
        double pivT = borderW;
        double pivB = fontAsc + borderW;
        double pivXC = borderW + width / 2.0;
        double pivYC = fontAsc / 2.0 + borderW;

        // Locations
        double pivotX = borderW;
        double pivotY = fontAsc;
        double left = x - borderW;
        double right = x - width;
        double centerX = x - (width / 2.0);
        double top = -et.getY() - borderW;
        double bottom = -et.getY() - fontAsc - borderW;
        double centerY = -et.getY() - fontAsc / 2.0 - borderW;

        boolean sideways = (rot > 45.0 && rot < 135.0)
                || (rot > 225.0 && rot < 315.0);
        boolean gt135 = et.getRotation().getValue() > 135.0;

        switch (et.getAlign()) {
            case BOTTOM_LEFT -> {
                pivotX = pivL;
                pivotY = pivB;
                ttG.setLayoutY(bottom);
                if (sideways) {
                    if (gt135) { // LOWER
                        ttG.setLayoutX(mir ? left - fontAsc : left); // BOT-LEFT-270-*
                    } else {
                        ttG.setLayoutX(mir ? left + fontAsc : left); // BOT-LEFT-90-*
                    }
                } else {
                    if (gt135) {
                        ttG.setLayoutX(mir ? left + width : right + width); // BOT-LEFT-180-*
                    } else {
                        ttG.setLayoutX(mir ? right : left);  // BOT-LEFT-0-*
                    }
                }
            }
            case CENTER_LEFT -> {
                pivotX = pivL;
                pivotY = pivYC;
                ttG.setLayoutY(centerY);
                if (sideways) {
                    ttG.setLayoutX(left);
                } else {
                    if (gt135) {
                        ttG.setLayoutX(mir ? left + width : right + width);
                    } else {
                        ttG.setLayoutX(mir ? right : left);
                    }
                }
            }
            case TOP_LEFT -> {
                pivotX = pivL;
                pivotY = pivT;
                ttG.setLayoutY(top);
                if (sideways) {
                    if (gt135) { // LOWER
                        ttG.setLayoutX(mir ? left + fontAsc : left);
                    } else {
                        ttG.setLayoutX(mir ? left - fontAsc : left);
                    }
                } else {
                    if (gt135) {
                        ttG.setLayoutX(mir ? left + width : right + width);
                    } else {
                        ttG.setLayoutX(mir ? right : left);
                    }
                }
            }
            case BOTTOM_RIGHT -> {
                pivotX = pivR;
                pivotY = pivB;
                ttG.setLayoutY(bottom);
                if (sideways) {
                    if (gt135) {
                        ttG.setLayoutX(mir ? right - fontAsc : right);
                    } else {
                        ttG.setLayoutX(mir ? right + fontAsc : right);
                    }
                } else {
                    if (gt135) {
                        ttG.setLayoutX(mir ? right - width : left - width);
                    } else {
                        ttG.setLayoutX(mir ? left : right);
                    }
                }
            }
            case CENTER_RIGHT -> {
                pivotX = pivR;
                pivotY = pivYC;
                ttG.setLayoutY(centerY);
                if (sideways) {
                    ttG.setLayoutX(right);
                } else {
                    if (gt135) {
                        ttG.setLayoutX(mir ? right - width : left - width);
                    } else {
                        ttG.setLayoutX(mir ? left : right);
                    }
                }
            }
            case TOP_RIGHT -> {
                pivotX = pivR;
                pivotY = borderW;
                ttG.setLayoutY(top);
                if (sideways) {
                    if (gt135) {
                        ttG.setLayoutX(mir ? right + fontAsc : right);
                    } else {
                        ttG.setLayoutX(mir ? right - fontAsc : right);
                    }
                } else {
                    if (gt135) {
                        ttG.setLayoutX(mir ? right - width : left - width);
                    } else {
                        ttG.setLayoutX(mir ? left : right);
                    }
                }
            }
            case BOTTOM_CENTER -> {
                pivotX = pivXC;
                pivotY = pivB;
                ttG.setLayoutY(bottom);
                if (sideways) {
                    if (gt135) {
                        ttG.setLayoutX(mir ? centerX - fontAsc : centerX);
                    } else {
                        ttG.setLayoutX(mir ? centerX + fontAsc : centerX);
                    }
                } else {
                    if (gt135) {
                        ttG.setLayoutX(centerX);
                    } else {
                        ttG.setLayoutX(centerX);
                    }
                }
            }
            case CENTER -> {
                pivotX = pivXC;
                pivotY = pivYC;
                ttG.setLayoutX(centerX);
                ttG.setLayoutY(centerY);
            }
            case TOP_CENTER -> {
                pivotX = pivXC;
                pivotY = pivT;
                ttG.setLayoutY(top);
                if (sideways) {
                    if (gt135) {
                        ttG.setLayoutX(mir ? centerX + fontAsc : centerX);
                    } else {
                        ttG.setLayoutX(mir ? centerX - fontAsc : centerX);
                    }
                } else {
                    if (gt135) {
                        ttG.setLayoutX(centerX);
                    } else {
                        ttG.setLayoutX(centerX);
                    }
                }
            }
        }

        Rotate rTT = new Rotate(jfxRot, pivotX, pivotY);
        ttG.getTransforms().add(rTT);
        if (showBorder) {
            ttG.setBorder(new Border(new BorderStroke(
                    Color.BLUE, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(0.1))));
        }
        return ttG;
    }

    /**
     * Create a SMD node for the pattern: xml ==> smd name="1" x="-0.751840625"
     * y="0" dx="0.7112" dy="0.762" layer="1" roundness="20"
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
        pad.setRotate(smd.getRot());

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
        mask.setRotate(smd.getRot());

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
        et.getRotation().setValue(smd.getRot());
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
        //int rot = (int) thd.getRot();

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

                Rotate rotate = new Rotate(360 - thd.getRot());
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
                Rotate rotateM = new Rotate(360 - thd.getRot());
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
                Rotate rotate = new Rotate(360 - thd.getRot());
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
                Rotate rotateMask = new Rotate(360 - thd.getRot());
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
        et.getRotation().setValue(thd.getRot());
        et.setAlign(TextAlign.CENTER);
        et.setSize(0.5);
        et.setX(thd.getX());
        et.setY(thd.getY());
        g.getChildren().add(LibraryElementNode.createText(et, Color.LIGHTGREY));

        return g;
    }

    /**
     *
     * @param p pin to create node for
     * @param c color of pin.
     * @param parentRot @Rotation of parent node. Might be null.
     * @param showDetails show pin details like hidden name/pin/direction.
     * Usually off in schematic, on in library.
     * @return
     */
    public static Node createPinNode(Pin p, Color c, Rotation parentRot, boolean showDetails) {
        final Group g = new Group();

        final Color ORIGIN_CIRCLE_COLOR = new Color(1.0, 1.0, 1.0, 0.2);
        final double ORIGIN_CIRCLE_RADIUS = 0.635;
        final double ORIGIN_CIRCLE_LINE_WIDTH = 0.07;

        final Color PAD_NAME_COLOR = new Color(0.8, 0.8, 0.2, 0.8);
        final Color PAD_COLOR_GHOST = new Color(0.8, 0.8, 0.2, 0.2);

        final double PIN_NAME_MARGIN = 1.5;
        final double PIN_STROKE_WIDTH = 0.1524; // 6 mil
        final double PIN_FONT_SIZE = 2.0;
        final Color PIN_NAME_COLOR = new Color(0.8, 0.8, 0.8, 0.8);
        final Color PIN_COLOR_GHOST = new Color(0.9, 0.9, 0.9, 0.1);
        final Color PIN_DIR_SWAP_COLOR = new Color(0.3, 1.0, 0.3, 0.2);

        final double DOT_CIRCLE_RADIUS = 0.7;
        final double DOT_CIRCLE_LINE_WIDTH = PIN_STROKE_WIDTH * 1.7;
        final double CLK_SIZE = DOT_CIRCLE_RADIUS * 2.0;

        double pX = p.getX();
        double pY = -p.getY();

        double pRot = 0;
        boolean pMir = false;
        if (parentRot != null) {
            pRot = parentRot.getValue();
            pMir = parentRot.isMirror();
            if (pMir) {
                pX = -p.getX();
            }
        }

        boolean pinMirror = false;
        // How the pin appears to be rotated after 
        // parent and local rotations cosidered.
        double vizPinRot = p.getRot() + pRot;
        if (pMir && (pRot == 90 || pRot == 270)) { // Sideways mirrored.
            vizPinRot += 180.0;
        }
        vizPinRot %= 360;

        if (pMir && (p.getRot() == 0 || p.getRot() == 180)) {
            pinMirror = true;
        }

        // Pin Length Multiplier
        int padHang = 0;
        switch (p.getLength()) {
            case LONG -> {  // TODO: Unum for Length getValue() for pinHang.
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

        // There might be a dot on pin.
        double dotRadius = 0;
        if (p.getFunction() == PinFunction.DOT || p.getFunction() == PinFunction.DOTCLK) {
            dotRadius = DOT_CIRCLE_RADIUS;
        }

        double rawPinLen = padHang * 2.54;
        double symbX = pX + (pinMirror ? -rawPinLen : rawPinLen); // Symbol Outline X
        double pinLen = rawPinLen - dotRadius * 2.0;

        // Draw wire
        Line pinLine = new Line(pX, pY, pX + (pinMirror ? -pinLen : pinLen), pY);
        pinLine.setStroke(c);
        pinLine.setStrokeLineCap(StrokeLineCap.BUTT);
        pinLine.setStrokeWidth(PIN_STROKE_WIDTH);
        g.getChildren().add(pinLine);

        // Dot Function
        if (dotRadius > 0.0) {
            Circle dotC = new Circle(dotRadius, Color.TRANSPARENT);
            dotC.setLayoutX(symbX + (pinMirror ? dotRadius : -dotRadius));
            dotC.setLayoutY(pY);
            dotC.setStroke(c);
            dotC.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
            g.getChildren().add(dotC);
        }

        // Pin Origin Circle
        if (showDetails) {
            Circle originCirc = new Circle(
                    pX, pY, ORIGIN_CIRCLE_RADIUS, Color.TRANSPARENT
            );
            originCirc.setStroke(ORIGIN_CIRCLE_COLOR);
            originCirc.setStrokeWidth(ORIGIN_CIRCLE_LINE_WIDTH);
            g.getChildren().add(originCirc);
        }
        // Clock Function
        if (p.getFunction() == PinFunction.CLK || p.getFunction() == PinFunction.DOTCLK) {
            Line line1 = new Line(symbX, pY - CLK_SIZE / 2.0, symbX + (pinMirror ? -CLK_SIZE : CLK_SIZE), pY);
            line1.setStroke(c);
            line1.setStrokeLineCap(StrokeLineCap.ROUND);
            line1.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);

            Line line2 = new Line(symbX, pY + CLK_SIZE / 2.0, symbX + (pinMirror ? -CLK_SIZE : CLK_SIZE), pY);
            line2.setStroke(c);
            line2.setStrokeLineCap(StrokeLineCap.ROUND);
            line2.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);

            g.getChildren().addAll(line1, line2);
        }

        Color pinNameColor = PIN_NAME_COLOR;
        Color padColor = PAD_NAME_COLOR;

        switch (p.getVisible()) {
            case BOTH -> {
            }
            case PAD -> {
                pinNameColor = showDetails ? PIN_COLOR_GHOST : Color.TRANSPARENT;
            }
            case PIN -> {
                padColor = showDetails ? PAD_COLOR_GHOST : Color.TRANSPARENT;
            }
            case OFF -> {
                pinNameColor = showDetails ? PIN_COLOR_GHOST : Color.TRANSPARENT;
                padColor = showDetails ? PAD_COLOR_GHOST : Color.TRANSPARENT;
            }
        }

        // Pin Name (inside component, pin function name)
        Text pinName = new Text(p.getName());

        Font font = Font.loadFont(LibraryElementNode.class.getResourceAsStream(FONT_PATH), PIN_FONT_SIZE);
        pinName.setFont(font);
        pinName.setFill(pinNameColor);
        double pinNameTextWidth = pinName.getBoundsInLocal().getWidth();
        double pinNameTextHeight = pinName.getBoundsInLocal().getHeight();
        pinName.setLayoutX(symbX + (pinMirror ? -PIN_NAME_MARGIN : PIN_NAME_MARGIN) + (pinMirror ? -pinNameTextWidth : 0.0));
        pinName.setLayoutY(pY + pinNameTextHeight * 0.3);
        if (vizPinRot > 90.0) { // Flip Text
            Rotate r = new Rotate(180, pinNameTextWidth / 2.0, -pinNameTextHeight * 0.3);
            pinName.getTransforms().add(r);
        }
        g.getChildren().add(pinName);

        // Pad Name (outside component, usually a number)
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
        Text padName = new Text(padValue);
        Font padFont = Font.loadFont(
                LibraryElementNode.class.getResourceAsStream(FONT_PATH),
                PIN_FONT_SIZE * 0.8
        );
        padName.setFont(padFont);
        padName.setFill(padColor);
        double padWidth = padName.getBoundsInLocal().getWidth();
        double padHeight = padName.getBoundsInLocal().getHeight();
        double textDescend = 0.15;
        padName.setLayoutX(pX - (pinMirror ? padWidth : 0.0));
        padName.setLayoutY(pY - textDescend);
        if (vizPinRot > 90.0) { // Flip Text
            Rotate r = new Rotate(180, padWidth / 2.0, textDescend);
            padName.getTransforms().add(r);
        }
        g.getChildren().add(padName);

        // Direction and Swap-Level  ( ex.   io 0  )
        if (showDetails) {
            Text dirSwap = new Text(p.getDirection().code() + "  " + p.getSwapLevel());
            Font dirSwapFont = Font.loadFont(
                    LibraryElementNode.class.getResourceAsStream(FONT_PATH),
                    PIN_FONT_SIZE * 0.7
            );
            dirSwap.setFont(dirSwapFont);
            dirSwap.setFill(PIN_DIR_SWAP_COLOR);
            double dsWidth = dirSwap.getBoundsInLocal().getWidth();
            double dsHeight = dirSwap.getBoundsInLocal().getHeight();
            double xyOffset = ORIGIN_CIRCLE_RADIUS * 0.71;
            dirSwap.setLayoutX(pX - (pinMirror ? 0.0 : dsWidth) + (pinMirror ? xyOffset : -xyOffset));
            dirSwap.setLayoutY(pY - xyOffset);
            if (vizPinRot > 90.0) { // Flip Text
                Rotate r = new Rotate(180, dsWidth / 2.0, xyOffset);
                dirSwap.getTransforms().add(r);
            }
            g.getChildren().add(dirSwap);
        }

        double rot = 360 - p.getRot();

        Rotate r = new Rotate(rot, pX, pY);
        g.getTransforms().add(r);

        return g;
    }

    public static Node createPinNodeOld(Pin p, Color c, boolean mirror, boolean upIsDown) {

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

        double pX = mirror ? -p.getX() : p.getX();

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

        int rot = (int) p.getRot(); // 0, 90, 180, 270

        Group g = new Group();

        Line line = new Line(pX, -p.getY(), pX, -p.getY());
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
                line.setEndX(pX - (mirror ? -pinLen : pinLen));
            case 90 ->
                line.setEndY(-p.getY() - pinLen);
            default ->
                line.setEndX(pX + (mirror ? -pinLen : pinLen));
        }

        g.getChildren().add(line);

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

            g.getChildren().add(createCircleNode(dotCircle, c, mirror));
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

            // TODO: Maybe put lines in a group and then rotate that?
            line1.setStartX(line.getEndX() + dotRadius * 2.0);
            line1.setStartY(line.getEndY() + CLK_SIZE / 2.0);
            line1.setEndX(line1.getStartX() + CLK_SIZE);
            line1.setEndY(-p.getY());
            line2.setStartX(line.getEndX() + dotRadius * 2.0);
            line2.setStartY(line.getEndY() - CLK_SIZE / 2.0);
            line2.setEndX(line1.getStartX() + CLK_SIZE);
            line2.setEndY(-p.getY());

            Group lineGroup = new Group(line1, line2);
            Rotate r = new Rotate(rot, pX, p.getY());
            lineGroup.getTransforms().add(r);

            g.getChildren().add(lineGroup);
        }

        // Pin Name
        Text pinName = new Text(p.getName());
        pinName.setFont(Font.font(PIN_FONT_SIZE));
        pinName.setFill(pinNameColor);
        double width = pinName.getBoundsInLocal().getWidth();
        double height = pinName.getBoundsInLocal().getHeight();
//        if (  upIsDown ) {
//            //Rotate rT = new Rotate(15, width / 2.0, -height / 2.0);
//            LOGGER.log(Level.SEVERE, "Pin Height: " + height);
//            Rotate rT = new Rotate(90, 0, -height*0.65);
//            pinName.getTransforms().add(rT);
//        }
        g.getChildren().add(pinName);

        //Text padName = new Text(padText);
        Text padName = new Text(padValue);
        padName.setFont(Font.font(PIN_FONT_SIZE * 0.8));
        padName.setFill(padColor);
        double padWidth = padName.getBoundsInLocal().getWidth();
        double padHeight = padName.getBoundsInLocal().getHeight();
        if (upIsDown) {
            Rotate rT = new Rotate(180, padWidth / 2.0, -padHeight / 2.0);
            padName.getTransforms().add(rT);
        }
        g.getChildren().add(padName);

        // Used to compute character width on pad.
        Text padChar = new Text("A");
        padChar.setFont(padName.getFont());
        double padCharWidth = padChar.getBoundsInLocal().getWidth();

        // Direction and Swap-Level
        Text dirSwap = new Text(p.getDirection().code() + "  " + p.getSwapLevel());
        dirSwap.setFont(Font.font(PIN_FONT_SIZE * 0.7));
        dirSwap.setFill(PIN_DIR_SWAP_COLOR);
        double dsWidth = dirSwap.getBoundsInLocal().getWidth();
        double dsHeight = dirSwap.getBoundsInLocal().getHeight();
        if (upIsDown) {
            //Rotate rT = new Rotate(180, dsWidth / 2.0, -dsHeight / 2.0);
            Rotate rT = new Rotate(180, dsWidth / 2.0, -dsHeight / 2.0);
            dirSwap.getTransforms().add(rT);
        }
        g.getChildren().add(dirSwap);

        double pinNameOffset = pinLen + dotRadius * 2.0 + PIN_NAME_MARGIN;

        double mir = mirror ? -1.0 : 1.0;
        double upDown = upIsDown ? -1.0 : 1.0;
        switch (rot) {
            case 270 -> {
                pinName.setText("DDD");
                pinName.setLayoutX(mir * (p.getX() - width / 2));
                pinName.setLayoutY(upDown * (-p.getY() + width / 2 + height * 0.3 + pinLen + dotRadius * 2.0 + PIN_NAME_MARGIN));
                pinName.setRotate(90);

                padName.setText("D");
                //padName.setLayoutX(mir * (p.getX() - padWidth / 2 + padHeight * 0.5));
                padName.setLayoutX(mir * (p.getX()));
                //padName.setLayoutY(upDown * (-p.getY() - padWidth / 2 - padHeight * 0.3 + padCharWidth * padHang));
                padName.setLayoutY(upDown * (-p.getY()) + padHeight);
                padName.setRotate(90);

                dirSwap.setLayoutX(mir * (p.getX() - PIN_DIR_SWAP_OFFSET - dsHeight / 2 - dsWidth / 2));
                dirSwap.setLayoutY(upDown * (-p.getY() - dsWidth / 3 - PIN_DIR_SWAP_OFFSET));
                dirSwap.setRotate(270);
            }
            case 180 -> {
                //pinName.setText("CCC");
                if (upIsDown) {
                    Rotate rT = new Rotate(180);
                    pinName.getTransforms().add(rT);
                    pinName.setLayoutX(mir * (p.getX() - pinNameOffset) + (mirror ? width : 0.0));
                    pinName.setLayoutY(-p.getY() - height * 0.30);
                } else {
                    pinName.setLayoutX(mir * (p.getX() - pinNameOffset) - (mirror ? 0.0 : width));
                    pinName.setLayoutY(-p.getY() + height * 0.30);
                }

                padName.setText("C");
                padName.setLayoutX(mir * (p.getX() - padCharWidth * padHang) - (mirror ? 0.0 : padWidth));
                padName.setLayoutY(upDown * (-p.getY() - padHeight * 0.2) + (upIsDown ? padHeight : 0.0));

                dirSwap.setLayoutX(mir * (p.getX() + PIN_DIR_SWAP_OFFSET) - (mirror ? dsWidth : 0.0));
                dirSwap.setLayoutY(upDown * (-p.getY() - PIN_DIR_SWAP_OFFSET) + (upIsDown ? dsHeight : 0.0));
            }
            case 90 -> {
                // Rotate Node rotates on center, so we need to compensate for that.
                pinName.setText("BBB");
                pinName.setLayoutX(mir * (p.getX() - width / 2));
                pinName.setLayoutY(-p.getY() - width / 2 + height * 0.3 - pinLen - dotRadius * 2.0 - PIN_NAME_MARGIN);
                pinName.setRotate(90);

                padName.setText("B");
                padName.setLayoutX(mir * (p.getX() - padWidth / 2 + padHeight / 2 /*+ padHeight*0.3*/));
                padName.setLayoutY(-p.getY() + padHeight * 0.3 - padCharWidth * padHang + padWidth / 2.0);
                padName.setRotate(90);

                dirSwap.setLayoutX(mir * (p.getX() + PIN_DIR_SWAP_OFFSET + dsHeight / 2 - dsWidth / 2));
                dirSwap.setLayoutY(-p.getY() + dsWidth / 2 + dsHeight / 3 + PIN_DIR_SWAP_OFFSET);
                dirSwap.setRotate(90);
            }
            default -> {
                //pinName.setText("T");
                //pinName.setLayoutX(mir * (p.getX() + pinNameOffset));
                //pinName.setLayoutY(-p.getY() + height * 0.30 - (mirror?0.0:width));
                if (upIsDown) {
                    //pinName.setText("W");
                    Rotate rT = new Rotate(180);
                    pinName.getTransforms().add(rT);
                    pinName.setLayoutX(mir * (p.getX() + pinNameOffset) + (mirror ? width : 0.0));
                    pinName.setLayoutY(-p.getY() - height * 0.30);
                } else {
                    pinName.setText("X");
                    pinName.setLayoutX(mir * (p.getX() + pinNameOffset) - (mirror ? width : 0.0));
                    pinName.setLayoutY(-p.getY() + height * 0.30);
                }

                padName.setText("A");
                padName.setLayoutX(mir * (p.getX() - padWidth + padCharWidth * padHang) - (mirror ? padWidth : 0.0)); // Hang over the pin by one char.
                padName.setLayoutY(upDown * (-p.getY() - padHeight * 0.2) + (upIsDown ? padHeight : 0.0));

                dirSwap.setLayoutX(mir * (p.getX() - PIN_DIR_SWAP_OFFSET) - (mirror ? 0.0 : dsWidth));
                dirSwap.setLayoutY(-p.getY() - upDown * PIN_DIR_SWAP_OFFSET + (upIsDown ? dsHeight : 0.0));
            }
        }

        Line cH = new Line(pinName.getLayoutX() - 0.5, pinName.getLayoutY(), pinName.getLayoutX() + 0.5, pinName.getLayoutY());
        Line cV = new Line(pinName.getLayoutX(), pinName.getLayoutY() - 0.5, pinName.getLayoutX(), pinName.getLayoutY() + 0.5);
        cH.setStroke(Color.WHITE);
        cH.setStrokeWidth(0.1);
        cV.setStroke(Color.WHITE);
        cV.setStrokeWidth(0.1);
        g.getChildren().addAll(cH, cV);

        ElementCircle originCircle = new ElementCircle();
        originCircle.setX(p.getX()); // mirror happens in the createCircleNode()
        originCircle.setY(p.getY());
        originCircle.setRadius(ORIGIN_CIRCLE_RADIUS);
        originCircle.setWidth(ORIGIN_CIRCLE_LINE_WIDTH);
        // Origin Circle
        g.getChildren().add(createCircleNode(originCircle, ORIGIN_CIRCLE_COLOR, mirror));

        return g;
    }

    /**
     * xml ==> circle x="3.6068" y="0" radius="1.016" width="0.508" layer="94"
     *
     * @param ec ElementCircle object
     * @param color to make the circle
     * @return
     */
    public static Node createCircleNode(ElementCircle ec, Color color, boolean mirror) {
        Circle c = new Circle(mirror ? -ec.getX() : ec.getX(), -ec.getY(), ec.getRadius());

        c.setStroke(color);
        c.setStrokeWidth(ec.getWidth());
        c.setFill(null);

        return c;
    }

    /**
     *
     * @param junc Junction object
     * @param color to make the circle
     * @return
     */
    public static Node createJunctionNode(Junction junc, Color color) {

        Circle c = new Circle(junc.getX(), -junc.getY(), Junction.DOT_RADIUS);
        c.setFill(color);

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

    public static Group createSymbolNode(Symbol symbol, Instance inst, Part part, Map<String, String> vars, LayerElement[] layers, ColorPalette palette) {
        Group g = new Group();

        boolean lr = false;
        boolean ud = false;

        final Rotation rotation = inst == null ? null : inst.getRotation();

        if (inst != null) {
            boolean instMir = rotation.isMirror();
            double rot = inst.getRot();
            if (rot == 270.0) {
                Rotate r = new Rotate(instMir ? 270 : 90);
                g.getTransforms().add(r);
                lr = instMir;
                ud = !instMir;
            } else if (rot == 180) {
                Rotate r = new Rotate(180);
                g.getTransforms().add(r);
                lr = instMir;
                ud = true;
            } else if (rot == 90) {
                Rotate r = new Rotate(instMir ? 90 : 270);
                g.getTransforms().add(r);
                lr = instMir;
                ud = instMir;
            } else {
                lr = instMir;
                //ud = false;
            }
        }

        final boolean leftIsRight = lr;
        final boolean upIsDown = ud;

        symbol.getElements().forEach((e) -> {
            LayerElement le = layers[e.getLayerNum()];
            if (le == null) {
                LOGGER.log(Level.SEVERE, "No Layer for: {0}", e.getLayerNum());
            }
            int colorIndex = le.getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));

            // (polygon | wire | text | dimension | pin | circle | rectangle | frame)
            if (e instanceof ElementPolygon ep) {
                g.getChildren().add(LibraryElementNode.createPolygon(ep, c, leftIsRight));
            } else if (e instanceof Wire w) {
                g.getChildren().add(LibraryElementNode.createWireNode(w, c, leftIsRight));
            } else if (e instanceof ElementText et) {
                // Replace Text for things that start with '>'

                if (inst == null || !et.getValue().startsWith(">")) {
                    // Library preview of Part 
                    //  or @value was over-ridden (like <text> in a part).
                    g.getChildren().add(LibraryElementNode.createText(et, c));
                } else {
                    // There is an instance or >VALUE/>NAME was set.
                    String altText = et.getValue();
                    ElementText proxyTxt = et.copy();
                    for (Attribute attr : inst.getAttributes()) {
                        // Attr like >NAME or >VALUE matches the textValue.
                        String name = ">" + attr.getName();
                        if (name.equals(et.getValue())) {

                            // Perform substitute.
                            // Schematic instance of Part.
                            if (et.getValue().equals(">VALUE")) {
                                altText = vars.get("VALUE");
                            } else if (et.getValue().equals(">NAME")) {
                                altText = inst.getPart();
                            } else {
                                Optional<Attribute> namedAttribute = part.getNamedAttribute(attr.getName());
                                if (!namedAttribute.isEmpty()) {
                                    altText = namedAttribute.get().getValue();
                                } else if (vars.containsKey(et.getValue().substring(1))) {
                                    altText = vars.get(et.getValue().substring(1));
                                } else {
                                    altText = attr.getValue();
                                }
                            }

                            // Set position of element.  attr x/y minus inst x/y
                            // TODO install this at ingest and store difference?
                            proxyTxt.setX(attr.getX() - inst.getX());
                            proxyTxt.setY(attr.getY() - inst.getY());
                            //rot = inst.getRot() - attr.getRotation().getValue();
                            proxyTxt.setRot(attr.getRotation().getValue() - inst.getRot());
                            // attr  layer
                            proxyTxt.setLayer(attr.getLayerNum());
                            // attr  align
                            proxyTxt.setAlign(attr.getAlign());

                            break;
                        }
                    }

                    Node elementText = createText(proxyTxt, altText, c, rotation);
                    g.getChildren().add(elementText);
                }
                g.getChildren().add(LibraryElementNode.crosshairs(et.getX(), -et.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if (e instanceof Dimension dim) {
                g.getChildren().add(createDimensionNode(dim, layers, palette));
                //LOGGER.log(Level.SEVERE, "TODO: Create Dimension Node.");
            } else if (e instanceof Pin pin) {
                g.getChildren().add(createPinNode(pin, c, rotation, inst == null));
            } else if (e instanceof ElementCircle ec) {
                g.getChildren().add(LibraryElementNode.createCircleNode(ec, c, leftIsRight));
            } else if (e instanceof ElementRectangle rect) {
                g.getChildren().add(LibraryElementNode.createRectangle(rect, c, leftIsRight));
            } else if (e instanceof FrameElement frm) {
                g.getChildren().add(LibraryElementNode.createFrameNode(frm, c));
            }
        });

        int cIdx = layers[symbol.getLayerNum()].getColorIndex();
        Color c = ColorUtils.getColor(palette.getHex(cIdx));
        g.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.035, c
        ));

        return g;
    }

    public static Node createPackageNode(Footprint pkg, LayerElement[] layers, ColorPalette palette) {
        Group g = new Group();

        pkg.getElements().forEach((e) -> {
            LayerElement le = layers[e.getLayerNum()];
            int colorIndex = le.getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));

            if (e instanceof PadSMD padSMD) {
                Color maskColor = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                Node n = LibraryElementNode.createSmd(padSMD, c, maskColor);
                g.getChildren().add(n);
                n.toBack();
            } else if (e instanceof PadTHD padTHD) {
                Color maskColor = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                Node n = LibraryElementNode.createThd(padTHD, c, maskColor);
                g.getChildren().add(n);
                n.toBack();
            } else if (e instanceof Wire wire) {
                g.getChildren().add(LibraryElementNode.createWireNode(wire, c, false));
            } else if (e instanceof ElementText elementText) {
                g.getChildren().add(LibraryElementNode.createText(elementText, c));
                g.getChildren().add(LibraryElementNode.crosshairs(elementText.getX(), -elementText.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if (e instanceof ElementRectangle elementRectangle) {
                g.getChildren().add(LibraryElementNode.createRectangle(elementRectangle, c, false));
            } else if (e instanceof ElementPolygon elementPolygon) {
                g.getChildren().add(LibraryElementNode.createPolygon(elementPolygon, c, false));
            } else if (e instanceof ElementCircle elementCircle) {
                g.getChildren().add(LibraryElementNode.createCircleNode(elementCircle, c, false));
            }
        });

        g.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.05, Color.RED
        ));

        return g;
    }

    public static Node createDimensionNode(Dimension dim, LayerElement[] layers, ColorPalette palette) {
        LayerElement le = layers[dim.getLayerNum()];
        int colorIndex = le.getColorIndex();
        Color c = ColorUtils.getColor(palette.getHex(colorIndex));

        switch (dim.getDtype()) {
            case ANGLE -> {
                LOGGER.log(Level.SEVERE, "Make Angle Dimension");
            }
            case DIAMETER -> {
                LOGGER.log(Level.SEVERE, "Make Diameter Dimension");
            }
            case LEADER -> {
                LOGGER.log(Level.SEVERE, "Make Leader Dimension");
            }
            case RADIUS -> {
                LOGGER.log(Level.SEVERE, "Make Radius Dimension");
            }
            default -> { // PARALLEL,HORIZONTAL,VERTICAL
                LOGGER.log(Level.SEVERE, "Make Parallel Dimension");
                return parallelDimension(dim, c);
            }
        }

        return new Group();
    }

    private static Node parallelDimension(Dimension dim, Color c) {
        Group g = new Group();

        double x1 = dim.getX1();
        double y1 = dim.getY1();
        double x2 = dim.getX2();
        double y2 = dim.getY2();
        double x3 = dim.getX3();
        double y3 = dim.getY3();

        double ang;
        if (x1 == x2) {
            ang = 90;
        } else {
            ang = Math.toDegrees(Math.sin((y2 - y1) / (x2 - x1)));
        }
        //LOGGER.log(Level.SEVERE, "Apparent Angle: " + ang);
        //double absAng = Math.abs(ang);

        double opp12;
        double adj12;
        double rat12;
        double hyp12;
        double ang12;
        double xx2;
        //double yy2;
        double opp13;
        double adj13;
        double hyp13;
        double xx3;
        double yy3;
        double toggle = 1.0;

        if (x1 > x2) { //x1 is larger than x2. Swap point 1 <--> 2.
            //LOGGER.log(Level.SEVERE, "x1 is larger than x2");
            double tx2 = x2;
            x2 = x1;
            x1 = tx2;
            double ty2 = y2;
            y2 = y1;
            y1 = ty2;
            toggle = -1.0;
        }
        opp12 = y2 - y1;
        adj12 = x2 - x1;
        rat12 = opp12 / adj12;
        //rat12 = adj12 / opp12;
        hyp12 = Math.hypot(adj12, opp12);
        ang12 = Math.toDegrees(Math.atan(rat12));

        xx2 = x1 + hyp12;
        //yy2 = y1;
        opp13 = y3 - y1;
        adj13 = x3 - x1;
        hyp13 = Math.hypot(adj13, opp13);
        xx3 = x1 + hyp12 / 2.0;
        yy3 = y1 + toggle * Math.sqrt(
                Math.pow(hyp13, 2) - Math.pow(hyp12 / 2.0, 2)
        );

        double lExt = dim.getWidth() * 15; // Amount to extend lines by.        // New unrotated points are x1,y1, xx2, yy2, xx3, yy3
        BigDecimal bdUp = new BigDecimal(hyp12).
                setScale(dim.getPrecision(), RoundingMode.UP
        );
        String dimValueString = String.valueOf(bdUp.doubleValue());

        Line line1 = new Line(x1, -y1, x1, -yy3 - toggle * lExt);
        line1.setStroke(c);
        line1.setStrokeWidth(dim.getWidth());
        Line line2 = new Line(xx2, -y1, xx2, -yy3 - toggle * lExt);
        line2.setStroke(c);
        line2.setStrokeWidth(dim.getWidth());
        Line line3 = new Line(x1, -yy3, xx2, -yy3);
        line3.setStroke(c);
        line3.setStrokeWidth(dim.getWidth());
        g.getChildren().addAll(line1, line2, line3);

        double arrowSize = 2;
        g.getChildren().add(
                arrowhead(x1, -yy3,
                        arrowSize, arrowSize * 0.4, 0,
                        dim.getWidth(), c
                ));
        g.getChildren().add(
                arrowhead(xx2, -yy3,
                        arrowSize, arrowSize * 0.4, 180,
                        dim.getWidth(), c
                ));

        Text dimText = new Text(dimValueString);
        Font dimFont = Font.loadFont(
                LibraryElementNode.class.getResourceAsStream(FONT_PATH),
                dim.getTextsize() * 1.4
        );
        dimText.setFont(dimFont);

        double dimWidth = dimText.getBoundsInLocal().getWidth();
        dimText.setLayoutX(xx3 - dimWidth / 2.0);
        dimText.setLayoutY(-yy3 - dimText.getBoundsInLocal().getHeight() * 0.2);
        dimText.setFill(c);
        if (ang12 == 90) { // only for 90 flip text.
            Rotate r = new Rotate(180,
                    dimText.getBoundsInLocal().getWidth() / 2.0,
                    -dimText.getBoundsInLocal().getHeight() * 0.3);
            dimText.getTransforms().add(r);
        }
        g.getChildren().add(dimText);

        // Rotate visual element
        Rotate rotate = new Rotate(-ang12, x1, -y1);
        g.getTransforms().add(rotate);

        return g;
    }

    private static Node arrowhead(
            double x, double y, double len, double width,
            double rot, double thick, Color c) {

        Polygon p = new Polygon(
                x, y,
                x + len, y + width / 2.0,
                x + len, y - width / 2.0
        );
        p.setStroke(c);
        p.setFill(c);
        p.setStrokeWidth(thick * 0.2);
        p.setStrokeLineJoin(StrokeLineJoin.ROUND);
        Rotate rotate = new Rotate(rot, x, y);
        p.getTransforms().add(rotate);

        return p;
    }
}
