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
import com.maehem.mangocad.model.element.basic.ContactRef;
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementCircle;
import com.maehem.mangocad.model.element.basic.ElementElement;
import com.maehem.mangocad.model.element.basic.ElementPolygon;
import com.maehem.mangocad.model.element.basic.ElementRectangle;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.FrameElement;
import com.maehem.mangocad.model.element.basic.Hole;
import com.maehem.mangocad.model.element.basic.Instance;
import com.maehem.mangocad.model.element.basic.Junction;
import com.maehem.mangocad.model.element.basic.LabelElement;
import com.maehem.mangocad.model.element.basic.PadSMD;
import com.maehem.mangocad.model.element.basic.PadTHD;
import com.maehem.mangocad.model.element.basic.Part;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Probe;
import com.maehem.mangocad.model.element.basic.Spline;
import com.maehem.mangocad.model.element.basic.Vertex;
import com.maehem.mangocad.model.element.basic.Via;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.drawing.Board;
import com.maehem.mangocad.model.element.enums.DimensionType;
import static com.maehem.mangocad.model.element.enums.PadShape.*;
import com.maehem.mangocad.model.element.enums.PinFunction;
import static com.maehem.mangocad.model.element.enums.PinVisible.*;
import com.maehem.mangocad.model.element.enums.TextAlign;
import static com.maehem.mangocad.model.element.enums.TextAlign.*;
import com.maehem.mangocad.model.element.highlevel.Device;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Segment;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.misc.DesignRules;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.util.Rotation;
import com.maehem.mangocad.view.ColorUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
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
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryElementNode {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    //public static final String FONT_PATH = "/fonts/Share_Tech_Mono/ShareTechMono-Regular.ttf";
    public static final String FONT_PATH = "/fonts/mango-classic.ttf";  // Test Font
    //public static final double FONT_SCALE = 1.055; // Font height can vary depending on Family.
    public static final double FONT_SCALE = 1.000; // Mango Custom. Font height can vary depending on Family.
    //private static final double FONT_ASC_PCT = 0.61; // 53%   (0.0 - 1.0)
    private static final double FONT_ASC_PCT = 0.47; // 53%   (0.0 - 1.0)  == 450/960 from font.

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
    public static Shape createWireNode(Wire w, Color color, boolean mirror) {
        double strokeWidth = w.getWidth();
        double x1 = mirror ? -w.getX1() : w.getX1();
        double x2 = mirror ? -w.getX2() : w.getX2();
        double y1 = w.getY1();
        double y2 = w.getY2();

        if (strokeWidth < 0.03) {
            // Wires can't be 0 width.
            strokeWidth = 0.03; // 6mil
        }
        Shape s;
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

            s = path;
        } else {
            s = new Line(x1, -y1, x2, -y2);
        }

        s.setStrokeLineCap(StrokeLineCap.ROUND);
        s.setStrokeWidth(strokeWidth);
        List<Double> pattern = w.getStyle().getPattern();

        // TODO: Massage pettern nums to fit pattern into line's length.
        if (pattern != null) {
            s.getStrokeDashArray().addAll(pattern);
        }
        s.setStroke(color);
        s.setSmooth(true);

        return s;
    }

    public static Shape createRectangle(ElementRectangle r, Color color, boolean mirror) {
        double x1 = r.getX1();
        double x2 = r.getX2();

        Rectangle rr = new Rectangle(
                x1, -r.getY2(),
                Math.abs(x2 - x1), Math.abs(r.getY2() - r.getY1())
        );
        rr.setStrokeWidth(0);
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

    /**
     * @deprecated Use createPolygonCurved()
     *
     * @param poly
     * @param color
     * @param mirror
     * @return @Shape of polygon
     */
    @Deprecated
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
        p.setStroke(color);
        p.setFill(color);

        return p;
    }

    public static Shape createPolygonCurved(ElementPolygon poly, Color color, boolean mirror) {
        Shape shape;
        List<Vertex> vertices = poly.getVertices();
        if (vertices.isEmpty()) {
            return new Circle(2, Color.RED);
        }
        double verts[] = new double[vertices.size() * 2];

        boolean hasCurvedLines = false;
        for (Vertex vv : vertices) {
            hasCurvedLines = (vv.getCurve() != 0.0);
            if (hasCurvedLines) {
                break;
            }
        }

        double strokeWidth = poly.getWidth();

        if (hasCurvedLines) {
            Path path = new Path();
            double lastX = 0.0;
            double lastY = 0.0;
            double arc = 0.0;
            boolean isFirst = true;
            for (Vertex v : vertices) {
                if (isFirst) {
                    lastX = v.getX();
                    lastY = v.getY();
                    arc = v.getCurve();
                    isFirst = false;
                    MoveTo mt = new MoveTo(lastX, -lastY);
                    path.getElements().add(mt);
                    continue;
                }
                double x1 = mirror ? -v.getX() : v.getX();
                double y1 = v.getY();

                addPathEdge(path, arc, lastX, lastY, x1, y1);
                arc = v.getCurve();
                lastX = x1;
                lastY = y1;
            }

            // Close the path using the last curve.
            addPathEdge(path, arc, lastX, lastY, vertices.get(0).getX(), vertices.get(0).getY());

            shape = path;

//            if (strokeWidth < 0.03) {
//                // Wires can't be 0 width.
//                strokeWidth = 0.03; // 6mil
//            }
//            shape.setFill(color);
//            shape.setStrokeWidth(strokeWidth);
//            shape.setStroke(Color.FIREBRICK);
//            shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
            // Style does not apply to polygon. Always "Contnuous" but
            // may have a system defined style when not filled in editor mode.
            // TODO: Handle this type of display.
            //List<Double> pattern = poly.getStyle().getPattern();
            // TODO: Massage pettern nums to fit pattern into line's length.
//            if (pattern != null) {
//                s.getStrokeDashArray().addAll(pattern);
//            }
//            shape.setStrokeType(StrokeType.INSIDE);
//            shape.setStrokeLineCap(StrokeLineCap.ROUND);
//
//            shape.setSmooth(true);
            //return path;
        } else {
            for (int j = 0; j < verts.length; j += 2) {
                verts[j] = vertices.get(j / 2).getX();
                verts[j + 1] = -vertices.get(j / 2).getY();
            }
            Polygon p = new Polygon(verts);

            shape = p;
//            if (strokeWidth < 0.03) {
//                // Wires can't be 0 width.
//                strokeWidth = 0.03; // 6mil
//            }
            //shape = p;
        }

        if (strokeWidth < 0.03) {
            // Wires can't be 0 width.
            strokeWidth = 0.03; // 6mil
        }
        shape.setFill(color);
        shape.setStrokeWidth(strokeWidth);
        shape.setStroke(color);
        shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
        shape.setStrokeType(StrokeType.INSIDE);
        shape.setStrokeLineCap(StrokeLineCap.ROUND);

        shape.setSmooth(true);

        return shape;
    }

    public static Path createPath(List<Wire> wires, Color color, boolean mirror) {
        Path path = new Path();

        double lastX = 0.0;
        double lastY = 0.0;
//            double arc = 0.0;
        boolean isFirst = true;
        for (Wire w : wires) {
            // Check if first point or broken path.
            if (isFirst || w.getX1() != lastX | w.getY1() != lastY) {
                MoveTo mt = new MoveTo(w.getX1(), -w.getY1());
                path.getElements().add(mt);
                isFirst = false;
            }

            if (w.getCurve() != 0.0) {
                ArcTo arcTo = new ArcTo();
                arcTo.setX(w.getX2());
                arcTo.setY(-w.getY2());

                // SWEEP on negative curve value.
                arcTo.setSweepFlag(w.getCurve() < 0.0);

                double sin90 = Math.sin(Math.toRadians(90.0));
                double dist = distance(w.getX1(), -w.getY1(), w.getX2(), -w.getY2());
                double radius = (sin90 * dist / 2.0)
                        / Math.sin(Math.toRadians(w.getCurve() / 2.0));
                arcTo.setRadiusX(radius);
                arcTo.setRadiusY(radius);
                path.getElements().add(arcTo);
            } else {
                // LineTo
                LineTo lineTo = new LineTo();
                lineTo.setX(w.getX2());
                lineTo.setY(-w.getY2());
                path.getElements().add(lineTo);
            }
            lastX = w.getX2();
            lastY = w.getY2();
        }

        path.setFill(color);

        path.setStrokeLineCap(StrokeLineCap.ROUND);
        double strokeWidth = wires.getFirst().getWidth();
        if (strokeWidth < 0.03) {
            // Wires can't be 0 width.
            strokeWidth = 0.03; // 6mil
        }
        path.setStrokeWidth(strokeWidth);
        if (color != null) {
            path.setStroke(color.darker());
        }

        return path;
    }

    private static void addPathEdge(Path path, double arc, double lastX, double lastY, double x, double y) {
        if (arc == 0.0) { // First point or previous vert was non-curved.
            LineTo lineTo = new LineTo();
            lineTo.setX(x);
            lineTo.setY(-y);
            path.getElements().add(lineTo);
        } else {
            ArcTo arcTo = new ArcTo();
            // Curve to ARC
            arcTo.setX(x);
            arcTo.setY(-y);

            // SWEEP on negative curve value.
            arcTo.setSweepFlag(arc < 0.0);

            double sin90 = Math.sin(Math.toRadians(90.0));
            double dist = distance(lastX, -lastY, x, -y);
            double radius = (sin90 * dist / 2.0)
                    / Math.sin(Math.toRadians(arc / 2.0));
            arcTo.setRadiusX(radius);
            arcTo.setRadiusY(radius);
            path.getElements().add(arcTo);
        }

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
        Node textNode = createText(le, probeText, color, null, true);

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
        return createText(et, null, color, null, true);
    }

    public static Node createText(ElementText et, Color color, Rotation parentRotation) {
        return createText(et, null, color, parentRotation, true);
    }

    public static ArrayList<Shape> createText2(ElementText et, Color color) {
        return createText2(et, null, color, null, true);
    }

    public static ArrayList<Shape> createText2(ElementText et, Color color, Rotation parentRotation) {
        return createText2(et, null, color, parentRotation, true);
    }

    /**
     * Re-implementation of createText, but items are shapes.
     *
     * @param et
     * @param altText overrides text content of et element.
     * @param color
     * @param parentRotation if applicable, null if not used.
     * @param showCross display zero point anchor/marker.
     * @return
     */
    public static ArrayList<Shape> createText2(ElementText et, String altText, Color color, Rotation parentRotation, boolean showCross) {
        boolean showBorder = false;

        ArrayList<Shape> list = new ArrayList<>();

        double x = et.getX();
        double y = et.getY();

        Rotation rotation = et.getRotation();
        double rot = rotation.getValue();
        boolean mir = rotation.isMirror();

        double parentRot = parentRotation != null ? parentRotation.getValue() : 0.0;
        boolean parentMir = parentRotation != null ? parentRotation.isMirror() : false;

        double stroke = et.getDerivedStroke();

        double size = et.getSize();

        // 1 Point == 1/72   inch == 0.013888 inch == 0.35277 mm
        // 1 point == 1/25.4 inch ==  0.039 inch == 1 mm
        // MM to points   1 to 2.835
        double fontSize = size;
        //double fontSizeMult = 0.60; //0.7272; // INCH to Point ratio
        double fontSizeMult = 1.666; // JavaFX pixel units to font size ratio. Found experimentally.

        fontSize *= fontSizeMult;

        // Makes font fit requested height regardless of boldness.
        // Higher ratio text will be reduced in font size to make height fit size.
        fontSize *= ((100 - et.getRatio()) * 0.01);
        fontSize *= FONT_SCALE; // Font specific.

        //String fontPath = "/fonts/Source_Code_Pro/static/SourceCodePro-Bold.ttf";
        Font font = Font.loadFont(LibraryElementNode.class.getResourceAsStream(FONT_PATH), fontSize);

        // Text
        Text tt = new Text(altText != null ? altText : et.getValue());
        tt.setFont(font);
        tt.setFill(color);
        tt.setStrokeWidth(stroke);
        tt.setStrokeType(StrokeType.CENTERED);
        tt.setStrokeLineJoin(StrokeLineJoin.ROUND);
        tt.setStroke(color);
        tt.setTextOrigin(VPos.BASELINE);


        // JavaFX has not yet exposed FontMetrics so we make these assumtions.
        // Update should be fixed in Java 20.
        // TODO: Use Java Font Metrics.
        // Use known example text to deterimine line height.
        Text exLine = new Text("EXAMPLE");
        exLine.setFont(font);
        double lineHeight = exLine.getBoundsInLocal().getHeight();

        //double fontAsc = lineHeight * FONT_ASC_PCT; // Font ascends this much.
        //double fontAsc = et.getSize();
        //double fontAsc = lineHeight;
        //double fontDesc = lineHeight * (1.0 - FONT_ASC_PCT);
        //double fontDesc = lineHeight * 0.444;
        // Font line spacing value. In pixels.
        double lineSpaceFx = size * (et.getDistance() * 0.01 - 0.66);
        // Actual space height. In mm.
        double lineSpace = size * (et.getDistance() * 0.01);
        tt.setLineSpacing(lineSpaceFx); // Convert mm to  pixels.
        //tt.setLineSpacing(-0.66); // 1%
        //tt.setLineSpacing(-0.18); // 50%
        //tt.setLineSpacing(0.33);// 100%

        double textWidth = tt.getBoundsInLocal().getWidth();
        if (textWidth > 0.99) { // Bounds always seems to be one mm larger than actual text.
            textWidth -= 0.99;
        }
        double textHeight = tt.getBoundsInLocal().getHeight();

        double borderW = 0.0;
        if (showBorder) {
            borderW = 0.03;
        }

        double taWidth = textWidth + borderW * 2.0;
        //double taHeight = size + borderW * 2.0;

        // Full text height minus one line of text, minus extra line space
        double taHeight = textHeight - size + borderW * 2.0;

        double boxWidth = taWidth;
        //double boxHeight = lineHeight + borderW * 2.0;
        double boxHeight = taHeight;

        LOGGER.log(Level.SEVERE,
                "requested size: {0}  lineHeight: {1} textHeight: {2}  taHeight: {3}",
                new Object[]{et.getSize(), lineHeight, textHeight, taHeight}
        );

        // fontAsc, borderW and StrokeWidth can effect where the text lands by a few pixels.
        //tt.setLayoutY(fontAsc + borderW + tt.getStrokeWidth() / 2.0);
//        tt.setLayoutY(taHeight - tt.getStrokeWidth() / 2.0); // + borderW);//+ tt.getStrokeWidth() / 2.0);
//        tt.setLayoutX(tt.getStrokeWidth() / 2.0);
        //Translate ttT = new Translate(tt.getStrokeWidth() / 2.0, taHeight - tt.getStrokeWidth() / 2.0);

        //tt.getTransforms().add(ttT);

        // Flip text 180 for certain rotations.
        // This won't work at all!
//        if (rot == 180
//                || (mir && rotation.getValue() == 90)
//                || (!mir && rotation.getValue() == 270)) {
//            Rotate tR = new Rotate(180.0, textWidth / 2.0, -textHeight * FONT_ASC_PCT / 2.0);
//            tt.getTransforms().add(tR);
//        }
        list.add(tt);

        long lineCount = tt.getText().lines().count();
        if (lineCount > 1) {
            switch (et.getAlign()) {
                case BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT -> {
                    Transform ta = new Translate(0, (lineCount - 1) * -(size + lineSpace));
                    tt.getTransforms().add(ta);
                }
                case CENTER, CENTER_LEFT, CENTER_RIGHT -> {
                    Transform ta = new Translate(0, 0.5 * (lineCount - 1) * -(size + lineSpace));
                    tt.getTransforms().add(ta);
                }
                default -> { // TOP_
                    //Transform ta = new Translate(0, (lineCount - 1) * -(size + lineSpace));
                    //tt.getTransforms().add(ta);
                }
            }
        }

        tt.setLayoutX(x);
        tt.setLayoutY(-y);

        double pivotX;
        double pivotY = stroke / 2.0;
        double transX = stroke / 2.0;
        double transY = -stroke / 2.0;

        switch (et.getAlign()) {
            case BOTTOM_LEFT -> {
                pivotX = mir ? taWidth : -stroke / 2.0;
                //pivotY = -taHeight;// + 0.2;
            }
            case BOTTOM_CENTER -> {
                pivotX = taWidth / 2.0;
                //pivotY = -taHeight;
                transX = -textWidth / 2.0;
            }
            case BOTTOM_RIGHT -> {
                pivotX = mir ? 0 : taWidth;
                transX = -textWidth;
                //pivotY = -taHeight;
            }
            case CENTER_LEFT -> {
                pivotX = mir ? taWidth : 0;
                //pivotY = -taHeight / 2.0;
                transY = size / 2.0;
            }
            case CENTER -> {
                pivotX = taWidth / 2.0;
                //pivotY = -taHeight / 2.0;
                transX = -textWidth / 2.0;
                transY = size / 2.0;
            }
            case CENTER_RIGHT -> {
                pivotX = mir ? 0 : taWidth;
                //pivotY = -taHeight / 2.0;
                transX = -textWidth;
                transY = size / 2.0;
            }
            case TOP_LEFT -> {
                pivotX = mir ? taWidth : 0;
                pivotY = taHeight - stroke;// - borderW;
                transY = size;
                // TOP is to far for how we use it. There is no VPos.CAPS.
                // So we use pivotY to adjust text position acurately.
            }
            case TOP_CENTER -> {
                pivotX = taWidth / 2.0;
                pivotY = taHeight - stroke;// - borderW;
                transX = -textWidth / 2.0;
                transY = size;
            }
            default -> { // TOP_RIGHT
                pivotX = mir ? 0 : taWidth;
                pivotY = taHeight - stroke;// - borderW;
                transX = -textWidth;
                transY = size;
            }
        }

        if (rot > 90 && rot <= 270) {
            LOGGER.log(Level.SEVERE, "x: {0} y: {1}  taWidth: {2}  rot: {3}", new Object[]{x, y, taWidth, rot});

            //Rotate tR = new Rotate(180.0, -taWidth / 2.0, taHeight / 2.0);
            // Rotate tR = new Rotate(180, -2.4, -1.7);//  4.8 (2 + 2.8 ) ( x + tWidth/2.0),  3.4 ( 2 + 1.4 )
            //Rotate tR = new Rotate(180, x + textWidth / 2.0, -y - size / 2.0);
            //Translate tRt = new Translate(-taWidth / 2.0, -size / 2.0);
            //tt.getTransforms().add(tRt);
            Rotate tR = new Rotate(180, taWidth / 2.0, -size / 2.0);
            tt.getTransforms().add(tR);

            //Translate t = new Translate(taWidth, -size);
            //tt.getTransforms().add(t);
            //tt.setRotate(rot + 180.0);

            pivotX = taWidth;
            pivotY = -size;
            //transX = 0;
            //transY = 0;

            //pivotX = x;
            //pivotY = -y;
            //    pivotX += textWidth + stroke / 2.0;
            //    pivotY += -size + stroke / 2.0;
            //transX -= x;
            //transY -= y;

            //transX -= -textWidth;
            //transY += size;
            // Must change pivot of future rotations as we have flipped the text.
            //Rotate testR = new Rotate(-rot, taWidth, -size);
            //tt.getTransforms().add(testR);

        }

        //tt.setX(x - pivotX);
        //tt.setY(-y + pivotY);
        //tt.setLayoutX(x);
        //tt.setLayoutY(-y);

        Translate tranG = new Translate(transX, transY);

        double rotG = mir ? et.getRot() : -et.getRot();
        //Rotate rTTG = new Rotate(rotG, pivotX, -pivotY);
        //Rotate rTTG = new Rotate(rotG, taWidth / 2.0, -taHeight / 2.0);
        Rotate rTTG = new Rotate(rotG, pivotX, pivotY);

        tt.getTransforms().add(tranG);
        tt.getTransforms().add(rTTG);

        if (parentMir) {
            double trFact = 0.0;
            if (et.getAlign().name().endsWith("_RIGHT")) {
                trFact = 1.0;
            } else if (et.getAlign().name().endsWith("_LEFT")) {
                trFact = -1.0;
            }
            Translate tr = new Translate(trFact * tt.getBoundsInLocal().getWidth(), 0);
            tt.getTransforms().add(tr);
        }

        if (showBorder) {
            //Rectangle border = new Rectangle(x - pivotX, -y + pivotY + borderW, boxWidth, boxHeight);
            Rectangle border = new Rectangle(x - borderW, -y + borderW, boxWidth, boxHeight);
            border.setFill(null);
            border.setStroke(Color.BLUE);
            border.setStrokeWidth(borderW);
            //Rotate r = new Rotate(-rot, pivotX, -pivotY);
//            double rr = -rot;
//            if (rot > 90.0 && rot <= 270.0) {
//                rr = rot;
//            }
            int bPiv = 1;
            if (rot > 90 && rot <= 270) {
                bPiv = -1;
            }
            Rotate r = new Rotate(-rot, x + borderW, -y - borderW);
            //border.getTransforms().addAll(rTTG);
            Translate t = new Translate(transX, -taHeight + transY);
            border.getTransforms().addAll(r, t);

            list.add(border);
        }

//        if (showBorder) {  // RED Character height box
//            Rectangle border = new Rectangle(x + 1, -y - 4.5, 4.5, 4.5);
//            border.setFill(null);
//            border.setStroke(Color.RED);
//            border.setStrokeWidth(0.08);
//            border.getTransforms().add(rTTG);
//
//            list.add(border);
//
//        }

        if (showCross) {
            double chSize = 0.8; // Crosshairs size
            double chStroke = 0.01;

            // Crosshairs (original axis)
// For Debug
//            Line chP = new Line(-chSize, 0, chSize, 0);
//            chP.setStroke(Color.MAGENTA);
//            chP.setStrokeWidth(chStroke);
//            Line cvP = new Line(0, -chSize, 0, +chSize);
//            cvP.setStroke(Color.MAGENTA);
//            cvP.setStrokeWidth(chStroke);
//            ttG.getChildren().addAll(chP,cvP);
            // Crosshairs ( visible )
            Line ch = new Line(x - chSize, -y, x + chSize, -y);
            ch.setStroke(Color.WHITE);
            ch.setStrokeWidth(chStroke);
            //ch.getTransforms().add(rTTG);
            Line cv = new Line(x, -y - chSize, x, -y + chSize);
            cv.setStroke(Color.WHITE);
            cv.setStrokeWidth(chStroke);
            //cv.getTransforms().add(rTTG);

            list.add(ch);
            list.add(cv);
        }

        double chSize = 0.4; // Crosshairs size
        double chStroke = 0.01;
        Line ch = new Line(-chSize, 0, chSize, 0);
        ch.setStroke(Color.WHITE);
        ch.setStrokeWidth(chStroke);
        //ch.getTransforms().add(rTTG);
        Line cv = new Line(0, -chSize, 0, chSize);
        cv.setStroke(Color.WHITE);
        cv.setStrokeWidth(chStroke);
        //cv.getTransforms().add(rTTG);

        list.add(ch);
        list.add(cv);

        return list;
    }

    public static Node createText(ElementText et, String altText, Color color, Rotation parentRotation, boolean showCross) {
        boolean showBorder = false;

        Group g = new Group();
        Rotation rotation = et.getRotation();
        double rot = rotation.getValue();
        boolean mir = rotation.isMirror();

        double parentRot = parentRotation != null ? parentRotation.getValue() : 0.0;
        boolean parentMir = parentRotation != null ? parentRotation.isMirror() : false;

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

        if (parentMir) {
            double trFact = 0.0;
            if (et.getAlign().name().endsWith("_RIGHT")) {
                trFact = 1.0;
            } else if (et.getAlign().name().endsWith("_LEFT")) {
                trFact = -1.0;
            }
            Translate tr = new Translate(trFact * tt.getBoundsInLocal().getWidth(), 0);
            tt.getTransforms().add(tr);
        }
        if (showBorder) {
            ttG.setBorder(new Border(new BorderStroke(
                    Color.BLUE, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    new BorderWidths(borderW))));
        }
        ttG.setLayoutX(x - pivotX);
        ttG.setLayoutY(-y + pivotY);
        g.getChildren().addAll(ttG);

        if (showCross) {
            double chSize = 0.8; // Crosshairs size
            double chStroke = 0.05;

            // Crosshairs (original axis)
// For Debug
//            Line chP = new Line(-chSize, 0, chSize, 0);
//            chP.setStroke(Color.MAGENTA);
//            chP.setStrokeWidth(chStroke);
//            Line cvP = new Line(0, -chSize, 0, +chSize);
//            cvP.setStroke(Color.MAGENTA);
//            cvP.setStrokeWidth(chStroke);
//            ttG.getChildren().addAll(chP,cvP);
            // Crosshairs ( visible )
            Line ch = new Line(x - chSize, -y, x + chSize, -y);
            ch.setStroke(Color.WHITE);
            ch.setStrokeWidth(chStroke);
            Line cv = new Line(x, -y - chSize, x, -y + chSize);
            cv.setStroke(Color.WHITE);
            cv.setStrokeWidth(chStroke);

            g.getChildren().addAll(ch, cv);
        }
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

        double roundPct = smd.getRoundness() * 0.01;

        g.getChildren().add(createSmdPad(smd, color));

        g.getChildren().add(createSmdMask(smd, maskColor, true));

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

    private static Shape createSmdMask(PadSMD smd, Color color, boolean asHatch) {
        double roundPct = smd.getRoundness() * 0.01;
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

        if (asHatch) {
            mask.setStrokeWidth(0.02);
            mask.setStroke(color);
            ImagePattern maskPattern = makeHatch(10, true, color);
            mask.setFill(maskPattern);
        } else {
            mask.setStroke(null);
            mask.setFill(color);
        }
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
        return mask;
    }

    public static Shape createSmdPad(PadSMD smd, Color color) {
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

        return pad;
    }

    /**
     * Thermal lines minimum is polygon wire width. Thermal lines max is smaller
     * of pad w or h.
     *
     * @param smd
     * @param color
     * @param isolation
     * @return
     */
    public static ArrayList<Shape> createSmdThermal(PadSMD smd, Color color, double isolation, double tMin) {

        double w = smd.getWidth();
        double h = smd.getHeight();

        double stW = Math.max(tMin, Math.min(w / 2, h / 2));

        double cX = smd.getX();
        double cY = -smd.getY();

        Line lineH = new Line(
                cX - w / 2 - isolation, cY,
                cX + w / 2 + isolation, cY
        );
        Line lineV = new Line(
                cX, cY - w / 2 - isolation,
                cX, cY + w / 2 + isolation
        );

        lineH.setStroke(color);
        lineH.setStrokeWidth(stW);
        lineV.setStroke(color);
        lineV.setStrokeWidth(stW);

        ArrayList<Shape> lines = new ArrayList<>();
        lines.add(lineH);
        lines.add(lineV);

        return lines;
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
//        double padDia = thd.getDerivedDiameter();
//        ImagePattern maskPattern = makeHatch(10, true, maskColor);
        //padColor = Color.GREEN;
        Color drillColor = Color.BLACK; // TODO: Input background color.
        //int rot = (int) thd.getRot();

//        switch (thd.getShape()) {
//            case SQUARE -> {
//                Rectangle pad = new Rectangle(
//                        padDia, padDia,
//                        padColor
//                );
//                pad.setStroke(null);
//                pad.setLayoutX(thd.getX() - padDia / 2.0);
//                pad.setLayoutY(-thd.getY() - padDia / 2.0);
//
//                g.getChildren().add(pad);
//
//                // SolderMask
//                double maskWidth2 = MASK_W_DEFAULT * 2;
//                Rectangle mask = new Rectangle(
//                        padDia + maskWidth2, padDia + maskWidth2,
//                        maskColor
//                );
//                mask.setStrokeWidth(0.01);
//                mask.setStroke(maskColor);
//                mask.setFill(maskPattern);
//                mask.setLayoutX(thd.getX() - padDia / 2.0 - MASK_W_DEFAULT);
//                mask.setLayoutY(-thd.getY() - padDia / 2.0 - MASK_W_DEFAULT);
//
//                g.getChildren().add(mask);
//
//            }
//            case LONG -> {
//                double padLongMult = 2.0;
//                Rectangle pad = new Rectangle(
//                        padDia * padLongMult, padDia,
//                        padColor
//                );
//                pad.setArcHeight(padDia);
//                pad.setArcWidth(padDia);
//                pad.setStroke(null);
//
//                Rotate rotate = new Rotate(360 - thd.getRot());
//                rotate.setPivotX(padDia);
//                rotate.setPivotY(padDia / 2);
//                pad.setLayoutX(thd.getX() - pad.getWidth() / 2.0);
//                pad.setLayoutY(-thd.getY() - padDia / 2.0);
//                pad.getTransforms().add(rotate);
//
//                g.getChildren().add(pad);
//
//                // Mask
//                double maskWidth2 = MASK_W_DEFAULT * 2;
//                Rectangle mask = new Rectangle(
//                        padDia * padLongMult + maskWidth2, padDia + maskWidth2,
//                        maskColor
//                );
//                mask.setArcHeight(padDia + MASK_W_DEFAULT);
//                mask.setArcWidth(padDia + MASK_W_DEFAULT);
//                mask.setStrokeWidth(0.02);
//                mask.setStroke(maskColor);
//                mask.setFill(maskPattern);
//                Rotate rotateM = new Rotate(360 - thd.getRot());
//                rotateM.setPivotX(padDia + MASK_W_DEFAULT);
//                rotateM.setPivotY(padDia / 2 + MASK_W_DEFAULT);
//                mask.setLayoutX(thd.getX() - pad.getWidth() / 2.0 - MASK_W_DEFAULT);
//                mask.setLayoutY(-thd.getY() - padDia / 2.0 - MASK_W_DEFAULT);
//                mask.getTransforms().add(rotateM);
//
//                g.getChildren().add(mask);
//            }
//            case OCTOGON -> {
//                double r = padDia / 2.0;
//                double n = r * 0.383;
//                Polygon octo = new Polygon(
//                        -n, -r,
//                        n, -r,
//                        r, -n,
//                        r, n,
//                        n, r,
//                        -n, r,
//                        -r, n,
//                        -r, -n
//                );
//                octo.setFill(padColor);
//                octo.setStroke(null);
//                octo.setLayoutX(thd.getX());
//                octo.setLayoutY(-thd.getY());
//
//                g.getChildren().add(octo);
//
//                // Mask
//                double rr = padDia / 2.0 + MASK_W_DEFAULT;
//                double nn = rr * 0.383;
//                Polygon octoMask = new Polygon(
//                        -nn, -rr,
//                        nn, -rr,
//                        rr, -nn,
//                        rr, nn,
//                        nn, rr,
//                        -nn, rr,
//                        -rr, nn,
//                        -rr, -nn
//                );
//                octoMask.setStrokeWidth(0.02);
//                octoMask.setStroke(maskColor);
//                octoMask.setFill(maskPattern);
//                octoMask.setLayoutX(thd.getX());
//                octoMask.setLayoutY(-thd.getY());
//
//                g.getChildren().add(octoMask);
//            }
//            case OFFSET -> {
//                double padLongMult = 2.0;
//                Rectangle pad = new Rectangle(
//                        padDia * padLongMult, padDia,
//                        padColor
//                );
//                pad.setArcHeight(padDia);
//                pad.setArcWidth(padDia);
//                pad.setStroke(null);
//                pad.setLayoutX(thd.getX() - pad.getWidth() / 4.0);
//                pad.setLayoutY(-thd.getY() - padDia / 2);
//                Rotate rotate = new Rotate(360 - thd.getRot());
//                rotate.setPivotX(padDia / 2);
//                rotate.setPivotY(padDia / 2);
//                pad.getTransforms().add(rotate);
//                g.getChildren().add(pad);
//
//                double maskWidth2 = MASK_W_DEFAULT * 2;
//                Rectangle mask = new Rectangle(
//                        padDia * padLongMult + maskWidth2, padDia + maskWidth2,
//                        maskColor
//                );
//                mask.setArcHeight(padDia + MASK_W_DEFAULT);
//                mask.setArcWidth(padDia + MASK_W_DEFAULT);
//                mask.setStroke(maskColor);
//                mask.setStrokeWidth(0.02);
//                mask.setFill(maskPattern);
//                mask.setLayoutX(thd.getX() - padDia / 2.0 - MASK_W_DEFAULT);
//                mask.setLayoutY(-thd.getY() - mask.getHeight() / 2.0);
//                Rotate rotateMask = new Rotate(360 - thd.getRot());
//                rotateMask.setPivotX(padDia / 2 + MASK_W_DEFAULT);
//                rotateMask.setPivotY(padDia / 2 + MASK_W_DEFAULT);
//                mask.getTransforms().add(rotateMask);
//                g.getChildren().add(mask);
//            }
//            default -> {  // ROUND
//                Circle pad = new Circle(padDia / 2.0, padColor);
//                pad.setLayoutX(thd.getX());
//                pad.setLayoutY(-thd.getY());
//                pad.setStroke(null);
//                g.getChildren().add(pad);
//
//                // SolderMask
//                Circle mask = new Circle(padDia / 2.0 + MASK_W_DEFAULT, maskColor);
//
//                mask.setStrokeWidth(0.02);
//                mask.setStroke(maskColor);
//                mask.setFill(maskPattern);
//                mask.setLayoutX(thd.getX());
//                mask.setLayoutY(-thd.getY());
//
//                g.getChildren().add(mask);
//            }
//        }
        g.getChildren().add(createThdPad(thd, padColor));
        g.getChildren().add(createThdMask(thd, padColor, true));

        // Drill
//        Circle drill = new Circle(thd.getDrill() / 2.0, drillColor);
//        drill.setLayoutX(thd.getX());
//        drill.setLayoutY(-thd.getY());
//        drill.setStroke(null);
        g.getChildren().add(createThdDrill(thd, drillColor));

        // Name Text
        ElementText et = new ElementText();
        et.setValue(thd.getName());
        et.getRotation().setValue(thd.getRot());
        et.setAlign(TextAlign.CENTER);
        et.setSize(0.5);
        et.setX(thd.getX());
        et.setY(-thd.getY());
        g.getChildren().add(LibraryElementNode.createText(et, Color.LIGHTGREY));

        return g;
    }

    private static Shape createThdDrill(PadTHD thd, Color color) {
        Circle drill = new Circle(
                thd.getX(), -thd.getY(),
                thd.getDrill() / 2.0, color);
        //drill.setLayoutX(thd.getX());
        //drill.setLayoutY(-thd.getY());
        drill.setStroke(null);

        return drill;
    }

    public static Shape createThdPad(PadTHD thd, Color color) {
        double padDia = thd.getDerivedDiameter();

        LOGGER.log(Level.SEVERE, "Create TDH Pad: " + padDia);
        switch (thd.getShape()) {
            case SQUARE -> {
                Rectangle pad = new Rectangle(
                        padDia, padDia,
                        color
                );
                pad.setStroke(null);
                pad.setX(thd.getX() - padDia / 2.0);
                pad.setY(-thd.getY() - padDia / 2.0);

                return pad;
            }
            case LONG -> {
                double padLongMult = 2.0;
                Rectangle pad = new Rectangle(
                        padDia * padLongMult, padDia,
                        color
                );
                pad.setArcHeight(padDia);
                pad.setArcWidth(padDia);
                pad.setStroke(null);

                Rotate rotate = new Rotate(360 - thd.getRot());
                rotate.setPivotX(padDia);
                rotate.setPivotY(padDia / 2);
                pad.setX(thd.getX() - pad.getWidth() / 2.0);
                pad.setY(-thd.getY() - padDia / 2.0);
                pad.getTransforms().add(rotate);

                return pad;

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
                octo.setFill(color);
                octo.setStroke(null);
                octo.setLayoutX(thd.getX());
                octo.setLayoutY(-thd.getY());

                return octo;
            }
            case OFFSET -> {
                double padLongMult = 2.0;
                Rectangle pad = new Rectangle(
                        padDia * padLongMult, padDia,
                        color
                );
                pad.setArcHeight(padDia);
                pad.setArcWidth(padDia);
                pad.setStroke(null);
                pad.setX(thd.getX() - pad.getWidth() / 4.0);
                pad.setY(-thd.getY() - padDia / 2);
                Rotate rotate = new Rotate(360 - thd.getRot());
                rotate.setPivotX(padDia / 2);
                rotate.setPivotY(padDia / 2);
                pad.getTransforms().add(rotate);

                return pad;
            }
            default -> {  // ROUND
                Circle pad = new Circle(thd.getX(), -thd.getY(), padDia / 2.0, color);
                //LOGGER.log(Level.SEVERE, "Round Pad   dia: {0} loc:{1},{2}", new Object[]{padDia, thd.getX(), thd.getY()});
                //pad.setFill(color);
                //pad.setX(thd.getX());
                //pad.setLayoutY(-thd.getY());
                pad.setStroke(null);

                return pad;
            }
        }
    }

    private static Shape createThdMask(PadTHD thd, Color color, boolean asHatch) {
        Shape mask;
        ImagePattern maskPattern = makeHatch(10, true, color);
        double padDia = thd.getDerivedDiameter();

        switch (thd.getShape()) {
            case SQUARE -> {
                // SolderMask
                double maskWidth2 = MASK_W_DEFAULT * 2;
                mask = new Rectangle(
                        thd.getX() - padDia / 2.0 - MASK_W_DEFAULT,
                        -thd.getY() - padDia / 2.0 - MASK_W_DEFAULT,
                        padDia + maskWidth2, padDia + maskWidth2
                );
                mask.setFill(color);
                mask.setStroke(null);
                //mask.setLayoutX(thd.getX() - padDia / 2.0 - MASK_W_DEFAULT);
                //mask.setLayoutY(-thd.getY() - padDia / 2.0 - MASK_W_DEFAULT);

            }
            case LONG -> {
                double padLongMult = 2.0;

                // Mask
                double maskWidth2 = MASK_W_DEFAULT * 2;
                double w = padDia * padLongMult + maskWidth2;
                Rectangle r = new Rectangle(
                        thd.getX() - w / 2.0 - MASK_W_DEFAULT,
                        -thd.getY() - padDia / 2.0 - MASK_W_DEFAULT,
                        w,
                        padDia + maskWidth2
                );
                r.setFill(color);
                r.setArcHeight(padDia + MASK_W_DEFAULT);
                r.setArcWidth(padDia + MASK_W_DEFAULT);

                mask = r;
                Rotate rotateM = new Rotate(360 - thd.getRot());
                rotateM.setPivotX(padDia + MASK_W_DEFAULT);
                rotateM.setPivotY(padDia / 2 + MASK_W_DEFAULT);
                //mask.setLayoutX(thd.getX() - r.getWidth() / 2.0 - MASK_W_DEFAULT);
                //mask.setLayoutY(-thd.getY() - padDia / 2.0 - MASK_W_DEFAULT);
                mask.getTransforms().add(rotateM);
            }
            case OCTOGON -> {
                // Mask
                double rr = padDia / 2.0 + MASK_W_DEFAULT;
                double nn = rr * 0.383;
                mask = new Polygon(
                        -nn, -rr,
                        nn, -rr,
                        rr, -nn,
                        rr, nn,
                        nn, rr,
                        -nn, rr,
                        -rr, nn,
                        -rr, -nn
                );
                mask.setTranslateX(thd.getX());
                mask.setTranslateY(-thd.getY());
            }
            case OFFSET -> {
                double padLongMult = 2.0;

                double maskWidth2 = MASK_W_DEFAULT * 2;
                double w = padDia * padLongMult + maskWidth2;
                double h = padDia + maskWidth2;
                Rectangle r = new Rectangle(
                        thd.getX() - padDia / 2.0 - MASK_W_DEFAULT,
                        -thd.getY() - h / 2.0,
                        w, h
                );
                r.setArcHeight(padDia + MASK_W_DEFAULT);
                r.setArcWidth(padDia + MASK_W_DEFAULT);

                mask = r;
                //mask.setLayoutX(thd.getX() - padDia / 2.0 - MASK_W_DEFAULT);
                //mask.setLayoutY(-thd.getY() - r.getHeight() / 2.0);
                Rotate rotateMask = new Rotate(360 - thd.getRot()); // do we need the 360?
                rotateMask.setPivotX(padDia / 2 + MASK_W_DEFAULT);
                rotateMask.setPivotY(padDia / 2 + MASK_W_DEFAULT);
                mask.getTransforms().add(rotateMask);
            }
            default -> {  // ROUND
                // SolderMask
                mask = new Circle(
                        thd.getX(), -thd.getY(),
                        padDia / 2.0 + MASK_W_DEFAULT, color
                );

                //mask.setFill(color);
                //mask.setLayoutX(thd.getX());
                //mask.setLayoutY(-thd.getY());

            }
        }

        mask.setStrokeWidth(0.02);
        mask.setStroke(asHatch ? color : null);
        mask.setFill(asHatch ? maskPattern : color);

        return mask;
    }

    /**
     * Thermal lines minimum is polygon wire width. Thermal lines max is smaller
     * of pad w or h.
     *
     * @param pad
     * @param color
     * @param isolation
     * @return
     */
    public static ArrayList<Shape> createThdThermal(PadTHD pad, Color color, double isolation, double tMin) {
        ArrayList<Shape> lines = new ArrayList<>();
        double padDia = pad.getDerivedDiameter();
        double drillRad = pad.getDrill() / 2.0;
        isolation = 0.0;
        Line lineH;
        Line lineV;
        //double stW = drillRad;
        double stW = Math.min(tMin, drillRad);

        // stW is min 2X the stroke width of the poly we are thermaling up to
        // the drill radius.
        switch (pad.getShape()) {
            case LONG -> {
                final double padLongMult = 2.0;
                double w = padDia * padLongMult;
                double h = padDia;
                //stW = Math.min(tMin, drillRad);
                double cX = pad.getX();
                double cY = -pad.getY();
                lineH = new Line(
                        cX - w / 2 - isolation, cY,
                        cX + w / 2 + isolation, cY
                );
                lineV = new Line(
                        cX, cY - w / 2 - isolation,
                        cX, cY + w / 2 + isolation
                );
            }
            case OFFSET -> {
                final double padLongMult = 2.0;
                double w = padDia * padLongMult;
                double h = padDia;
                //stW = Math.min(tMin, drillRad);
                double cX = pad.getX() + padDia / 2.0;
                double cY = -pad.getY();
                lineH = new Line(
                        cX - w / 2 - isolation, cY,
                        cX + w / 2 + isolation, cY
                );
                lineV = new Line(
                        cX, cY - w / 2 - isolation,
                        cX, cY + w / 2 + isolation
                );
            }
            default -> {
                LOGGER.log(Level.SEVERE, "default THD thermals.");
                double w = padDia;
                //double h = padDia;

                //stW = Math.max(tMin, padDia / 2.0);
                double cX = pad.getX();
                double cY = -pad.getY();

                LOGGER.log(Level.SEVERE, "THD thermal: loc: {0},{1}  w/h: {2}   stroke: {3}", new Object[]{cX, cY, w, stW});
                lineH = new Line(
                        cX - w / 2 - isolation, cY,
                        cX + w / 2 + isolation, cY
                );
                lineV = new Line(
                        cX, cY - w / 2 - isolation,
                        cX, cY + w / 2 + isolation
                );

            }
        }
        lineH.setStroke(color);
        lineH.setStrokeWidth(stW);
        lineV.setStroke(color);
        lineV.setStrokeWidth(stW);

        lines.add(lineH);
        lines.add(lineV);

        return lines;
    }

    /**
     * Via Node
     *
     * TODO: pin name, drill legend. TODO: layer diameter (different for top,
     * inner and bottom).
     *
     * @param via element
     * @param padColor
     * @param maskTopColor
     * @param maskBottomColor
     * @param dr
     * @return JavaFX Node
     */
    public static Node createVia(Via via, Color padColor, Color maskTopColor, Color maskBottomColor, DesignRules dr) {
        Group g = new Group();
        double padDia = via.getDerivedDiameter(dr, Via.Layer.TOP);
        double maskDia = via.getMaskDiameter(dr, Via.Layer.TOP);

        ImagePattern maskTopPattern = makeHatch(5, true, maskTopColor);
        ImagePattern maskBotPattern = makeHatch(5, false, maskBottomColor);

        Color drillColor = Color.BLACK;

        switch (via.getShape()) {
            case SQUARE -> {
                Rectangle pad = new Rectangle(
                        padDia, padDia,
                        padColor
                );
                pad.setStroke(null);
                pad.setLayoutX(via.getX() - padDia / 2.0);
                pad.setLayoutY(-via.getY() - padDia / 2.0);

                g.getChildren().add(pad);

                // Drill
                Circle drill = new Circle(via.getDrill() / 2.0, drillColor);
                drill.setLayoutX(via.getX());
                drill.setLayoutY(-via.getY());
                drill.setStroke(null);

                g.getChildren().add(drill);

                // SolderMask Top
                double maskWidth2 = MASK_W_DEFAULT * 2;
                Rectangle mask = new Rectangle(
                        padDia + maskWidth2, padDia + maskWidth2,
                        maskTopColor
                );
                mask.setStrokeWidth(0.03);
                mask.setStroke(maskTopColor);
                mask.setFill(maskTopPattern);
                mask.setLayoutX(via.getX() - padDia / 2.0 - MASK_W_DEFAULT);
                mask.setLayoutY(-via.getY() - padDia / 2.0 - MASK_W_DEFAULT);

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
                octo.setLayoutX(via.getX());
                octo.setLayoutY(-via.getY());

                g.getChildren().add(octo);

                // Drill
                Circle drill = new Circle(via.getDrill() / 2.0, drillColor);
                drill.setLayoutX(via.getX());
                drill.setLayoutY(-via.getY());
                drill.setStroke(null);

                g.getChildren().add(drill);

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
                octoMask.setStroke(maskTopColor);
                octoMask.setFill(maskTopPattern);
                octoMask.setLayoutX(via.getX());
                octoMask.setLayoutY(-via.getY());

                g.getChildren().add(octoMask);
            }
            default -> {  // ROUND
                Circle pad = new Circle(padDia / 2.0, padColor);
                pad.setLayoutX(via.getX());
                pad.setLayoutY(-via.getY());
                pad.setStroke(null);
                g.getChildren().add(pad);

                // Drill
                Circle drill = new Circle(via.getDrill() / 2.0, drillColor);
                drill.setLayoutX(via.getX());
                drill.setLayoutY(-via.getY());
                drill.setStroke(null);

                g.getChildren().add(drill);

                // Top solder mask
                Circle topMask = new Circle(maskDia / 2.0, maskTopColor);
                topMask.setStrokeWidth(0.03);
                topMask.setStroke(maskTopColor);
                topMask.setFill(maskTopPattern);
                topMask.setLayoutX(via.getX());
                topMask.setLayoutY(-via.getY());
                // Bottom solder mask
                Circle bottomMask = new Circle(maskDia / 2.0, maskBottomColor);
                bottomMask.setStrokeWidth(0.03);
                bottomMask.setStroke(maskBottomColor);
                bottomMask.setFill(maskBotPattern);
                bottomMask.setLayoutX(via.getX());
                bottomMask.setLayoutY(-via.getY());

                g.getChildren().addAll(bottomMask, topMask);
            }
        }

        // Text is the "extent" the via layers passes through
        //      Normally: 1-16  Blind: ex. 2-4
        //
        // Name Text
        ElementText et = new ElementText();
        et.setValue(via.getExtent());
        et.setAlign(TextAlign.CENTER);
        et.setSize(via.getDrill() * 0.25);
        et.setX(via.getX());
        et.setY(via.getY());
        g.getChildren().add(LibraryElementNode.createText(
                et, null,
                new Color(1.0, 1.0, 1.0, 0.25),
                null, false
        ));

        return g;
    }

    public static Node createPinNode(Pin p, Color c, Rotation parentRotation, boolean showDetails) {
        Group g = new Group();

        double parentRot = parentRotation == null ? 0.0 : parentRotation.getValue();
        boolean parentMir = parentRotation == null ? false : parentRotation.isMirror();

        final Color ORIGIN_CIRCLE_COLOR = new Color(1.0, 1.0, 1.0, 0.2);
        final double ORIGIN_CIRCLE_RADIUS = 0.635;
        final double ORIGIN_CIRCLE_LINE_WIDTH = 0.07;

        final Color PAD_NAME_COLOR = new Color(0.8, 0.8, 0.2, 0.8);
        final Color PAD_COLOR_GHOST = new Color(0.8, 0.8, 0.2, 0.2);
        final double PAD_TEXT_ASCEND = 0.2;

        final double PIN_NAME_MARGIN = 1.5;
        final double PIN_STROKE_WIDTH = 0.1524; // 6 mil
        final double PIN_FONT_SIZE = 2.2;
        final Color PIN_NAME_COLOR = new Color(0.8, 0.8, 0.8, 0.8);
        final Color PIN_COLOR_GHOST = new Color(0.9, 0.9, 0.9, 0.1);
        final Color PIN_DIR_SWAP_COLOR = new Color(0.3, 1.0, 0.3, 0.2);

        final double DOT_CIRCLE_RADIUS = 0.7;
        final double DOT_CIRCLE_LINE_WIDTH = PIN_STROKE_WIDTH * 1.7;
        final double CLK_SIZE = DOT_CIRCLE_RADIUS * 2.0;

        double pX = p.getX();
        double pY = -p.getY();
        boolean pinMirror = p.getRotation().isMirror();
        double rawPinLen = p.getLength().lenMM();
        double symbX = pX + (pinMirror ? -rawPinLen : rawPinLen); // Symbol Outline X
        // There might be a dot on pin.
        double dotRadius = 0;
        if (p.getFunction() == PinFunction.DOT || p.getFunction() == PinFunction.DOTCLK) {
            dotRadius = DOT_CIRCLE_RADIUS;
        }
        double pinLen = rawPinLen - dotRadius * 2.0;

        double vizPinRot = (p.getRot() + parentRot) % 360;

        // Draw pin wire
        Line pinLine = new Line(pX, pY, pX + (pinMirror ? -pinLen : pinLen), pY);
        pinLine.setStroke(c);
        pinLine.setStrokeLineCap(StrokeLineCap.BUTT);
        pinLine.setStrokeWidth(PIN_STROKE_WIDTH);
        g.getChildren().add(pinLine);

        // Pin Origin Circle
        if (showDetails) {
            Circle originCirc = new Circle(
                    pX, pY, ORIGIN_CIRCLE_RADIUS, Color.TRANSPARENT
            );
            originCirc.setStroke(ORIGIN_CIRCLE_COLOR);
            originCirc.setStrokeWidth(ORIGIN_CIRCLE_LINE_WIDTH);
            g.getChildren().add(originCirc);
        }

        // Dot Function
        if (dotRadius > 0.0) {
            Circle dotC = new Circle(dotRadius, Color.TRANSPARENT);
            dotC.setLayoutX(symbX + (pinMirror ? dotRadius : -dotRadius));
            dotC.setLayoutY(pY);
            dotC.setStroke(c);
            dotC.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
            g.getChildren().add(dotC);
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
        if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
            Rotate r = new Rotate(180, pinNameTextWidth / 2.0, -pinNameTextHeight * 0.3);
            pinName.getTransforms().add(r);
        }
        if (pinMirror ^ parentMir) {
            Scale sc = new Scale(-1.0, 1.0, pinNameTextWidth / 2.0, 0.0);
            pinName.getTransforms().add(sc);
        }
        g.getChildren().add(pinName);

        // Pad Name (outside component, usually a number)
        // Use the padValue from DeviceSet if it exists.
        String padValue;
        if (p.getPadValue() != null) {
            padValue = p.getPadValue();
        } else { // Fill padValue with string that matches pinLength
            padValue = "9";
            for (int i = 1; i < p.getLength().ordinal(); i++) {
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
        //double padHeight = padName.getBoundsInLocal().getHeight();
        padName.setLayoutX(pX - (pinMirror ? padWidth : 0.0));
        padName.setLayoutY(pY - PAD_TEXT_ASCEND);
        if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
            Rotate r = new Rotate(180, padWidth / 2.0, PAD_TEXT_ASCEND);
            padName.getTransforms().add(r);
        }
        if (pinMirror ^ parentMir) {
            Scale sc = new Scale(-1.0, 1.0, padWidth / 2.0, 0.0);
            padName.getTransforms().add(sc);
        }
        g.getChildren().add(padName);

        // Direction and Swap-Level  ( ex.   io 0  )
        if (showDetails) {
            Text dirSwap = new Text(p.getDirection().code() + "  " + p.getSwapLevel());
            Font dirSwapFont = Font.loadFont(
                    LibraryElementNode.class.getResourceAsStream(FONT_PATH),
                    PIN_FONT_SIZE * 0.6
            );
            dirSwap.setFont(dirSwapFont);
            dirSwap.setFill(PIN_DIR_SWAP_COLOR);
            double dsWidth = dirSwap.getBoundsInLocal().getWidth();
            //double dsHeight = dirSwap.getBoundsInLocal().getHeight();
            double xyOffset = ORIGIN_CIRCLE_RADIUS * 0.71;
            dirSwap.setLayoutX(pX - (pinMirror ? 0.0 : dsWidth) + (pinMirror ? xyOffset : -xyOffset));
            dirSwap.setLayoutY(pY - xyOffset);
            if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
                //if (vizPinRot > 90.0) { // Flip Text
                Rotate r = new Rotate(180, dsWidth / 2.0, xyOffset);
                dirSwap.getTransforms().add(r);
            }
            if (pinMirror ^ parentMir) {
                Scale sc = new Scale(-1.0, 1.0, dsWidth / 2.0, 0.0);
                dirSwap.getTransforms().add(sc);
            }
            g.getChildren().add(dirSwap);
        }

        if (p.getRot() != 0) {
            Rotate r = new Rotate(-p.getRot(), pX, pY);
            g.getTransforms().add(r);
        }
        return g;
    }

    /**
     * xml ==> circle x="3.6068" y="0" radius="1.016" width="0.508" layer="94"
     *
     * @param ec ElementCircle object
     * @param color to make the circle
     * @param mirror mirror circle about x-axis
     * @return
     */
    public static Shape createCircleNode(ElementCircle ec, Color color, boolean mirror) {
        Circle c = new Circle(mirror ? -ec.getX() : ec.getX(), -ec.getY(), ec.getRadius());

        c.setStroke(color);
        double width = ec.getWidth();
        c.setStrokeWidth(width);
        if (width == 0.0) {
            c.setFill(color);
        } else {
            c.setFill(null);
        }

        return c;
    }

    /**
     * xml ==> x="16.51" y="8.89" drill="0.508"
     *
     * @param eh ElementHole object
     * @param outlineWidth
     * @param outlineColor
     * @param maskTopColor to make the circle
     * @param maskBottomColor
     * @param dr
     * @return
     */
    public static Node createHoleNode(Hole eh, double outlineWidth, Color outlineColor, Color maskTopColor, Color maskBottomColor, DesignRules dr) {
        Group g = new Group();

        Color drillColor = Color.BLACK;
        ImagePattern maskPatternTop = makeHatch(5, true, maskTopColor);
        ImagePattern maskPatternBot = makeHatch(5, false, maskTopColor);

        // TODO:  There is a drill symbol.
        // Symbol is from a hard coded table.
        Circle c = new Circle(eh.getX(), -eh.getY(), eh.getDrill() / 2.0);
        c.setStroke(outlineColor);
        c.setStrokeWidth(outlineWidth); // TODO: Get Stroke width and color from board outline.
        c.setFill(null);
        g.getChildren().add(c);

        // Drill
        Circle drill = new Circle(eh.getDrill() / 2.0, drillColor);
        drill.setLayoutX(eh.getX());
        drill.setLayoutY(-eh.getY());
        drill.setStroke(null);
        g.getChildren().add(drill);

        // SolderMask
        Circle maskTop = new Circle(eh.getMaskDiameter(dr) / 2.0, maskTopColor);
        maskTop.setStrokeWidth(0.03); // TODO: Get from some global source.
        maskTop.setStroke(maskTopColor);
        maskTop.setFill(maskPatternTop);
        maskTop.setLayoutX(eh.getX());
        maskTop.setLayoutY(-eh.getY());
        g.getChildren().add(maskTop);

        // Bottom Mask
        Circle maskBot = new Circle(eh.getMaskDiameter(dr) / 2.0, maskBottomColor);
        maskBot.setStrokeWidth(0.03); // TODO: Get from some global source.
        maskBot.setStroke(maskTopColor);
        maskBot.setFill(maskPatternBot);
        maskBot.setLayoutX(eh.getX());
        maskBot.setLayoutY(-eh.getY());
        g.getChildren().add(maskBot);

        return g;
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

    private static ImagePattern makeHatch(int nLines, boolean dir, Color c) {
        int size = 64;
        WritableImage wi = new WritableImage(size, size);

        Pane p = new Pane(); // Group?
        p.setBackground(Background.EMPTY);
        p.setClip(new Rectangle(size, size));

        double inc = (double) size / nLines;
        for (int i = 0; i < nLines * 2; i++) {
            Line l = new Line(
                    i * inc - (dir ? 0 : size), 0,
                    i * inc - (dir ? size : 0), size);
            l.setStrokeWidth(0.5);
            l.setStroke(c);
            p.getChildren().add(l);
        }

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        return new ImagePattern(p.snapshot(sp, wi), 0, 0, 2, 2, false);
    }

    public static Group createSymbolNode(Device device, Symbol symbol, Instance inst, Part part, Map<String, String> vars, LayerElement[] layers, ColorPalette palette) {
        Group elementGroup = new Group();
        Group textGroup = new Group();
        Group g = new Group(elementGroup, textGroup);

        final Rotation rotation = inst == null ? null : inst.getRotation();
        final int rot = (int) (inst == null ? 0.0 : rotation.getValue());
        final boolean mirror = inst == null ? false : inst.getRotation().isMirror();

        symbol.getElements().forEach((e) -> {
            LayerElement le = layers[e.getLayerNum()];
            if (le == null) {
                LOGGER.log(Level.SEVERE, "No Layer for: {0}", e.getLayerNum());
            }
            int colorIndex = le.getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));

            // (polygon | wire | text | dimension | pin | circle | rectangle | frame)
            if (e instanceof ElementPolygon ep) {
                elementGroup.getChildren().add(LibraryElementNode.createPolygon(ep, c, false));
            } else if (e instanceof Wire w) {
                elementGroup.getChildren().add(LibraryElementNode.createWireNode(w, c, false));
            } else if (e instanceof ElementText et) {

                final ElementText proxyText;
                if (inst == null) {
                    proxyText = et;
                } else {
                    proxyText = et.copy();
                }

                if (inst != null) {
                    for (Attribute attr : inst.getAttributes()) {
                        String name = ">" + attr.getName();
                        if (!et.getValue().equals(name)) {
                            continue;
                        }

                        // Perform substitute.
                        // Schematic instance of Part.
                        if (et.getValue().equals(">VALUE")) {
                            proxyText.setValue(vars.get("VALUE"));
                        } else if (et.getValue().equals(">NAME")) {
                            String gateStr = inst.getGate();
                            if (gateStr.startsWith("G$")) {
                                gateStr = "";
                            }
                            proxyText.setValue(inst.getPart() + gateStr);
                        } else {
                            Optional<Attribute> namedAttribute = part.getNamedAttribute(attr.getName());
                            if (!namedAttribute.isEmpty()) {
                                proxyText.setValue(namedAttribute.get().getValue());
                            } else if (vars.containsKey(et.getValue().substring(1))) {
                                proxyText.setValue(vars.get(et.getValue().substring(1)));
                            } else {
                                proxyText.setValue(attr.getValue());
                            }
                        }

                        proxyText.setX(attr.getX() - inst.getX());
                        proxyText.setY(attr.getY() - inst.getY());

                        proxyText.getRotation().setValue((attr.getRotation().getValue()) % 360.0);
                    }
                }

                Node elementTextNode = createText(proxyText, c, rotation);
                textGroup.getChildren().add(elementTextNode);

                textGroup.getChildren().add(LibraryElementNode.crosshairs(
                        proxyText.getX(), -proxyText.getY(), 0.5, 0.035, c
                ));
            } else if (e instanceof Dimension dim) {
                elementGroup.getChildren().add(createDimensionNode(dim, layers, palette));
            } else if (e instanceof Pin pin) {
                if (device != null) {
                    device.getConnections().forEach((con) -> {
                        if (inst.getGate().equals(con.getGate())
                                && con.getPin().equals(pin.getName())) {
                            pin.setPadValue(con.getPad());
                        }
                    });
                }
                //LOGGER.log(Level.SEVERE, "Pin: " + inst.getPart() + inst.getGate());
                elementGroup.getChildren().add(createPinNode(pin, c, rotation, inst == null));
            } else if (e instanceof ElementCircle ec) {
                elementGroup.getChildren().add(LibraryElementNode.createCircleNode(ec, c, false));
            } else if (e instanceof ElementRectangle rect) {
                elementGroup.getChildren().add(LibraryElementNode.createRectangle(rect, c, false));
            } else if (e instanceof FrameElement frm) {
                elementGroup.getChildren().add(LibraryElementNode.createFrameNode(frm, c));
            }

        });

        int cIdx = layers[symbol.getLayerNum()].getColorIndex();
        Color c = ColorUtils.getColor(palette.getHex(cIdx));
        elementGroup.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.035, c
        ));

        Rotate r = new Rotate(-rot, 0, 0);
        elementGroup.getTransforms().add(r);
        if (mirror && (rot == 0.0 || rot == 180)) {
            Scale sc = new Scale(-1.0, 1.0, 0.0, 0.0);
            elementGroup.getTransforms().add(sc);
        }
        if (mirror && (rot == 90.0 || rot == 270)) {
            Scale sc = new Scale(1.0, -1.0, 0.0, 0.0);
            elementGroup.getTransforms().add(sc);
        }

        return g;
    }

    public static Node createPackageNode(Footprint pkg, LayerElement[] layers, ColorPalette palette) {
        //Group p = new Group();
        Pane p = new Pane();

        pkg.getElements().forEach((e) -> {
            LayerElement le = layers[e.getLayerNum()];
            int colorIndex = le.getColorIndex();
            Color c = ColorUtils.getColor(palette.getHex(colorIndex));

            if (e instanceof PadSMD padSMD) {
                Color maskColor = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                Node n = LibraryElementNode.createSmd(padSMD, c, maskColor);
                p.getChildren().add(n);
                n.toBack();
            } else if (e instanceof PadTHD padTHD) {
                Color maskColor = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                Node n = LibraryElementNode.createThd(padTHD, c, maskColor);
                p.getChildren().add(n);
                n.toBack();
            } else if (e instanceof Wire wire) {
                p.getChildren().add(LibraryElementNode.createWireNode(wire, c, false));
            } else if (e instanceof ElementText elementText) {
                Node textNode = LibraryElementNode.createText(elementText, c);
                p.getChildren().add(textNode);
                // TODO: Get proper tOrigin/bOrigin layer info for crosshair color.
                p.getChildren().add(LibraryElementNode.crosshairs(elementText.getX(), -elementText.getY(), 0.5, 0.04, Color.DARKGREY));
            } else if (e instanceof ElementRectangle elementRectangle) {
                p.getChildren().add(LibraryElementNode.createRectangle(elementRectangle, c, false));
            } else if (e instanceof ElementPolygon elementPolygon) {
                p.getChildren().add(LibraryElementNode.createPolygon(elementPolygon, c, false));
            } else if (e instanceof ElementCircle elementCircle) {
                p.getChildren().add(LibraryElementNode.createCircleNode(elementCircle, c, false));
            } else {
                LOGGER.log(Level.SEVERE, "Encountered unhadled element: " + e.getElementName());
            }
        });

        // tOrigins 23   , bOrigins 24
        // TODO:  Need constants for layer numbers and names. And stroke widths/size.
        int cIdx = layers[23].getColorIndex();
        Color c = ColorUtils.getColor(palette.getHex(cIdx));
        p.getChildren().add(LibraryElementNode.crosshairs(
                0, 0, 0.5, 0.035, c
        ));

        return p;
    }

    public static List<Shape> createPackageUnusedPads(Footprint pkg, Board board, ElementElement el, int layer, Color c, double isolation) {
        ArrayList<Shape> list = new ArrayList<>();

        // element has a name --> i.e. "U1"
        // element has attributes that overide some pkg.elements.  i.e. >VALUE
        String partName = el.getName();

        pkg.getElements().forEach((e) -> {
            if (e instanceof PadSMD padSMD) {
                //Color maskColor = ColorUtils.getColor(palette.getHex(layers[29].getColorIndex()));
                if (padSMD.getLayerNum() == layer) {
                    ContactRef cr = new ContactRef();
                    cr.setElement(el.getName());
                    cr.setPad(padSMD.getName());

                    if (!board.hasContactRef(cr)) {
                        Shape n = LibraryElementNode.createSmdPad(padSMD, c);
                        if (isolation > 0.0) { // Add stroke around pad.
                            n.setStrokeWidth(isolation);
                            n.setStrokeType(StrokeType.OUTSIDE);
                            n.setStroke(c);
                        }
                        list.add(n);
                    }
                }

            } else if (e instanceof PadTHD padTHD) { // No layers, always top or bottom
                switch (layer) {
                    case 1 -> {
                        LOGGER.log(Level.SEVERE, "Render unused pad:" + padTHD.getName());
                        ContactRef cr = new ContactRef();
                        cr.setElement(el.getName());
                        cr.setPad(padTHD.getName());

                        if (!board.hasContactRef(cr)) {
                            Shape ss = LibraryElementNode.createThdPad(padTHD, c);
                            //ss.getTransforms().add(Transform.translate(el.getX(), -el.getY()));
                            if (isolation > 0.0) {
                                // Add stroke around pad.
                                ss.setStrokeWidth(isolation);
                                ss.setStrokeType(StrokeType.OUTSIDE);
                                ss.setStroke(c);
                            }
                            list.add(ss);
                        }
                    }
//                    case 29 -> { // Needed?
//                        if (!padTHD.isStopmask()) {
//                            break;
//                        }
//                        Shape ss = LibraryElementNode.createThdMask(padTHD, c, false);
//                        //ss.getTransforms().add(Transform.translate(el.getX(), -el.getY()));
//                        ss.setLayoutX(el.getX());
//                        ss.setLayoutY(-el.getY());
//                        ss.getTransforms().add(new Rotate(-el.getRot()));
//                        list.add(ss);
//                    }
                }
            }
        });
        return list;
    }

    public static List<Node> createPackageMfgPreviewTxtNode(Footprint pkg, ElementElement el, int layer, Color c, double isolation) {
        ArrayList<Node> list = new ArrayList<>();

        pkg.getElements().forEach((e) -> {
//            if (e instanceof PadSMD padSMD) {
//                if (layer == 29 && padSMD.getLayerNum() == 1) { // Top Mask
//                    Shape s = createSmdMask(padSMD, c, false);
//                    s.setLayoutX(el.getX());
//                    s.setLayoutY(-el.getY());
//                    s.getTransforms().add(new Rotate(-el.getRot()));
//                    list.add(s);
//                }
//            } else if (e instanceof PadTHD padTHD) { // No layers, always top or bottom
//                switch (layer) {
//                    case 45 -> {
//                        // Drills/Holes
//                        Shape drillShape = LibraryElementNode.createThdDrill(padTHD, c);
//                        drillShape.setLayoutX(el.getX());
//                        drillShape.setLayoutY(-el.getY());
//                        drillShape.getTransforms().add(new Rotate(-el.getRot()));
//                        list.add(drillShape);
//                    }
//                    case 29 -> { // Needed?
//                        if (!padTHD.isStopmask()) {
//                            break;
//                        }
//                        Shape ss = LibraryElementNode.createThdMask(padTHD, c, false);
//                        //ss.getTransforms().add(Transform.translate(el.getX(), -el.getY()));
//                        ss.setLayoutX(el.getX());
//                        ss.setLayoutY(-el.getY());
//                        ss.getTransforms().add(new Rotate(-el.getRot()));
//                        list.add(ss);
//                    }
//                }
//            } else if (e instanceof Wire wire) {
//                if (wire.getLayerNum() == layer) {
//                    Shape wireNode = LibraryElementNode.createWireNode(wire, c, false);
//                    if (isolation > 0.0) { // Add stroke around pad.
//                        wireNode.setStrokeWidth(isolation);
//                        wireNode.setStrokeType(StrokeType.OUTSIDE);
//                        wireNode.setStroke(c);
//                    }
//                    wireNode.setLayoutX(el.getX());
//                    wireNode.setLayoutY(-el.getY());
//                    wireNode.getTransforms().add(new Rotate(-el.getRot()));
//                    list.add(wireNode);
//                }
//            } else
            if (e instanceof ElementText et) {
                // Package Text Element
                // <text x="1.524" y="0" size="0.8128" layer="25" font="vector" ratio="15" align="center-left">&gt;NAME</text>

//                if (et.getLayerNum() == layer) {
                //Node textNode = LibraryElementNode.createText(et, null, c, null, false);
                //p.getChildren().add(textNode);
                final ElementText proxyText;
                proxyText = et.copy();

                String attrName = null;

                // et.value contains the mnemonic of attribute (name, value, etc.)
                if (et.getValue().equals(">NAME")) {
                    proxyText.setValue(el.getName());
                    attrName = "NAME";
                } else if (et.getValue().equals(">VALUE")) {
                    proxyText.setValue(el.getValue());
                    attrName = "VALUE";
                }

                if (attrName != null) {
                    // Attribute Element
                    //example:  <attribute name="NAME" x="10" y="18.858" size="1.27" layer="25" ratio="15" align="center" />

                    for (Attribute attr : el.getAttributes()) {
                        if (!attr.getName().equals(attrName) || attr.getLayerNum() != layer) {
                            // Skip if these aren't our attributes.
                            continue;
                        }

                        proxyText.setX(attr.getX() - el.getX());
                        proxyText.setY(attr.getY() - el.getY());
                        proxyText.setSize(attr.getSize());
                        proxyText.setAlign(attr.getAlign());
                        proxyText.setRatio(attr.getRatio());

                        proxyText.getRotation().setValue((attr.getRotation().getValue()) % 360.0);
                        //Node txtNode = createText(proxyText, null, c, null, false);
                        Node txtNode = createText(proxyText, null, c, el.getRotation(), false);

                        // Not sure why, but this needs to be rotated.
                        // but I think the parent applies the same rotation
                        // and other nodes don't seem to need this. Curious...
                        // update: supplied rotation to the createText(parentRotation) and it does the right thing now.
                        //txtNode.getTransforms().add(new Rotate(-el.getRot()));
                        txtNode.setLayoutX(el.getX());
                        txtNode.setLayoutY(-el.getY());
                        list.add(txtNode);
                    }
                }
//            } else if (e instanceof ElementRectangle elementRectangle) {
//                if (elementRectangle.getLayerNum() == layer) {
//                    Shape s = LibraryElementNode.createRectangle(elementRectangle, c, false);
//                    if (isolation > 0.0) {
//                        // Add stroke around pad.
//                        s.setStrokeWidth(isolation);
//                        s.setStrokeType(StrokeType.OUTSIDE);
//                        s.setStroke(c);
//                    }
//                    s.setLayoutX(el.getX());
//                    s.setLayoutY(-el.getY());
//                    s.getTransforms().add(new Rotate(-el.getRot()));
//                    list.add(s);
//                }
//            } else if (e instanceof ElementPolygon elementPolygon) {
//                if (elementPolygon.getLayerNum() == layer) {
//                    Shape s = LibraryElementNode.createPolygonCurved(elementPolygon, c, false);
//                    if (isolation > 0.0) {
//                        // Add stroke around pad.
//                        s.setStrokeWidth(isolation);
//                        s.setStrokeType(StrokeType.OUTSIDE);
//                        s.setStroke(c);
//                    }
//                    s.setLayoutX(el.getX());
//                    s.setLayoutY(-el.getY());
//                    s.getTransforms().add(new Rotate(-el.getRot()));
//                    list.add(s);
//                }
//            } else if (e instanceof ElementCircle elementCircle) {
//                if (elementCircle.getLayerNum() == layer) {
//                    Shape s = LibraryElementNode.createCircleNode(elementCircle, c, false);
//                    if (isolation > 0.0) {
//                        // Add stroke around pad.
//                        s.setStrokeWidth(isolation);
//                        s.setStrokeType(StrokeType.OUTSIDE);
//                        s.setStroke(c);
//                    }
//                    s.setLayoutX(el.getX());
//                    s.setLayoutY(-el.getY());
//                    s.getTransforms().add(new Rotate(-el.getRot()));
//                    list.add(s);
//                }
            } else {
                LOGGER.log(Level.SEVERE, "Encountered unhadled element: " + e.getElementName());
            }
        });

        // TODO:  Need constants for layer numbers and names. And stroke widths/size.
        return list;
    }

    public static List<Shape> createPackageMfgPreviewNode(Footprint pkg, ElementElement el, int layer, Color c, double isolation) {
        ArrayList<Shape> list = new ArrayList<>();

        pkg.getElements().forEach((e) -> {
            if (e instanceof PadSMD padSMD) {
                if (layer == 29 && padSMD.getLayerNum() == 1) { // Top Mask
                    Shape s = createSmdMask(padSMD, c, false);
                    s.setLayoutX(el.getX());
                    s.setLayoutY(-el.getY());
                    s.getTransforms().add(new Rotate(-el.getRot()));
                    list.add(s);
                }
            } else if (e instanceof PadTHD padTHD) { // No layers, always top or bottom
                switch (layer) {
                    case 45 -> {
                        // Drills/Holes
                        Shape drillShape = LibraryElementNode.createThdDrill(padTHD, c);
                        drillShape.setLayoutX(el.getX());
                        drillShape.setLayoutY(-el.getY());
                        drillShape.getTransforms().add(new Rotate(-el.getRot()));
                        list.add(drillShape);
                    }
                    case 29 -> { // Needed?
                        if (!padTHD.isStopmask()) {
                            break;
                        }
                        Shape ss = LibraryElementNode.createThdMask(padTHD, c, false);
                        //ss.getTransforms().add(Transform.translate(el.getX(), -el.getY()));
                        ss.setLayoutX(el.getX());
                        ss.setLayoutY(-el.getY());
                        ss.getTransforms().add(new Rotate(-el.getRot()));
                        list.add(ss);
                    }
                }
            } else if (e instanceof Wire wire) {
                if (wire.getLayerNum() == layer) {
                    Shape wireNode = LibraryElementNode.createWireNode(wire, c, false);
                    if (isolation > 0.0) { // Add stroke around pad.
                        wireNode.setStrokeWidth(isolation);
                        wireNode.setStrokeType(StrokeType.OUTSIDE);
                        wireNode.setStroke(c);
                    }
                    wireNode.setLayoutX(el.getX());
                    wireNode.setLayoutY(-el.getY());
                    wireNode.getTransforms().add(new Rotate(-el.getRot()));
                    list.add(wireNode);
                }
//            } else if (e instanceof ElementText et) {
//                // Package Text Element
//                // <text x="1.524" y="0" size="0.8128" layer="25" font="vector" ratio="15" align="center-left">&gt;NAME</text>
//
////                if (et.getLayerNum() == layer) {
//                //Node textNode = LibraryElementNode.createText(et, null, c, null, false);
//                //p.getChildren().add(textNode);
//                final ElementText proxyText;
//                proxyText = et.copy();
//
//                String attrName = null;
//
//                // et.value contains the mnemonic of attribute (name, value, etc.)
//                if (et.getValue().equals(">NAME")) {
//                    proxyText.setValue(el.getName());
//                    attrName = "NAME";
//                } else if (et.getValue().equals(">VALUE")) {
//                    proxyText.setValue(el.getValue());
//                    attrName = "VALUE";
//                }
//
//                if (attrName != null) {
//                    // Attribute Element
//                    //example:  <attribute name="NAME" x="10" y="18.858" size="1.27" layer="25" ratio="15" align="center" />
//
//                    for (Attribute attr : el.getAttributes()) {
//                        if (!attr.getName().equals(attrName) || attr.getLayerNum() != layer) {
//                            // Skip if these aren't our attributes.
//                            continue;
//                        }
//
//                        proxyText.setX(attr.getX() - el.getX());
//                        proxyText.setY(attr.getY() - el.getY());
//                        proxyText.setSize(attr.getSize());
//                        proxyText.setAlign(attr.getAlign());
//                        proxyText.setRatio(attr.getRatio());
//
//                        proxyText.getRotation().setValue((attr.getRotation().getValue()) % 360.0);
//                        //Node txtNode = createText(proxyText, null, c, null, false);
//                        Node txtNode = createText(proxyText, null, c, el.getRotation(), false);
//
//                        // Not sure why, but this needs to be rotated.
//                        // but I think the parent applies the same rotation
//                        // and other nodes don't seem to need this. Curious...
//                        // update: supplied rotation to the createText(parentRotation) and it does the right thing now.
//                        //txtNode.getTransforms().add(new Rotate(-el.getRot()));
//                        txtNode.setLayoutX(el.getX());
//                        txtNode.setLayoutY(-el.getY());
//                        list.add(txtNode);
//                    }
//                }
            } else if (e instanceof ElementRectangle elementRectangle) {
                if (elementRectangle.getLayerNum() == layer) {
                    Shape s = LibraryElementNode.createRectangle(elementRectangle, c, false);
                    if (isolation > 0.0) {
                        // Add stroke around pad.
                        s.setStrokeWidth(isolation);
                        s.setStrokeType(StrokeType.OUTSIDE);
                        s.setStroke(c);
                    }
                    s.setLayoutX(el.getX());
                    s.setLayoutY(-el.getY());
                    s.getTransforms().add(new Rotate(-el.getRot()));
                    list.add(s);
                }
            } else if (e instanceof ElementPolygon elementPolygon) {
                if (elementPolygon.getLayerNum() == layer) {
                    Shape s = LibraryElementNode.createPolygonCurved(elementPolygon, c, false);
                    if (isolation > 0.0) {
                        // Add stroke around pad.
                        s.setStrokeWidth(isolation);
                        s.setStrokeType(StrokeType.OUTSIDE);
                        s.setStroke(c);
                    }
                    s.setLayoutX(el.getX());
                    s.setLayoutY(-el.getY());
                    s.getTransforms().add(new Rotate(-el.getRot()));
                    list.add(s);
                }
            } else if (e instanceof ElementCircle elementCircle) {
                if (elementCircle.getLayerNum() == layer) {
                    Shape s = LibraryElementNode.createCircleNode(elementCircle, c, false);
                    if (isolation > 0.0) {
                        // Add stroke around pad.
                        s.setStrokeWidth(isolation);
                        s.setStrokeType(StrokeType.OUTSIDE);
                        s.setStroke(c);
                    }
                    s.setLayoutX(el.getX());
                    s.setLayoutY(-el.getY());
                    s.getTransforms().add(new Rotate(-el.getRot()));
                    list.add(s);
                }
            } else {
                LOGGER.log(Level.SEVERE, "Encountered unhadled element: " + e.getElementName());
            }
        });

        // TODO:  Need constants for layer numbers and names. And stroke widths/size.
        return list;
    }

    public static Node createDimensionNode(Dimension dim, LayerElement[] layers, ColorPalette palette) {
        LayerElement le = layers[dim.getLayerNum()];
        int colorIndex = le.getColorIndex();
        Color c = ColorUtils.getColor(palette.getHex(colorIndex));

        switch (dim.getDtype()) {
            case ANGLE -> {
                return angleDimensionNode(dim, c);
            }
            case LEADER -> {
                return leaderDimensionNode(dim, c);
            }
            case RADIUS -> {
                return radiusDimensionNode(dim, c);
            }
            default -> { // PARALLEL,HORIZONTAL,VERTICAL, DIAMETER
                return parallelDimension(dim, c);
            }
        }

    }

    private static Node leaderDimensionNode(Dimension dim, Color c) {
        Group g = new Group();
        double ang12 = Math.toDegrees(Math.atan2(dim.getY2() - dim.getY1(), dim.getX2() - dim.getX1()));

        Polyline pl = new Polyline(
                dim.getX1(), -dim.getY1(),
                dim.getX2(), -dim.getY2(),
                dim.getX3(), -dim.getY3()
        );
        pl.setStrokeWidth(dim.getWidth());
        pl.setStrokeLineCap(StrokeLineCap.ROUND);
        pl.setStroke(c);
        pl.setFill(null);

        g.getChildren().add(pl);

        double arrowDeg = 18;
        double arrowLen = 2.54;
        Arc arrow = new Arc(dim.getX1(), -dim.getY1(),
                arrowLen, arrowLen, ang12 - arrowDeg / 2.0, arrowDeg
        );
        arrow.setType(ArcType.ROUND);
        arrow.setStroke(c);
        arrow.setStrokeLineJoin(StrokeLineJoin.ROUND);
        arrow.setFill(c);
        arrow.setStrokeWidth(dim.getWidth() * 0.3);

        g.getChildren().add(arrow);

        return g;
    }

    private static Node angleDimensionNode(Dimension dim, Color c) {
        Group g = new Group();

        double hyp12 = Math.hypot(dim.getX1() - dim.getX2(), dim.getY1() - dim.getY2());
        //double hyp13 = Math.hypot(dim.getX1() - dim.getX3(), dim.getY1() - dim.getY3());
        double ang12 = Math.toDegrees(Math.atan2(dim.getY2() - dim.getY1(), dim.getX2() - dim.getX1()));
        double ang13 = Math.toDegrees(Math.atan2(dim.getY3() - dim.getY1(), dim.getX3() - dim.getX1()));
        double angD = ang13 - ang12;
        if (angD < 0.0) {
            angD = 360.0 + angD;
        }
        //double opp13 = Math.cos(Math.toRadians(angD)) * hyp13;
        double textHyp = hyp12 + dim.getTextsize() * 0.8;
        double oppTopp = Math.sin(Math.toRadians(angD / 2.0)) * textHyp;
        double oppTadj = Math.cos(Math.toRadians(angD / 2.0)) * textHyp;
        double ext = dim.getWidth() * 15.0;

        // Line
        Group lineGroup = new Group();
        Line line1 = new Line(dim.getX1(), -dim.getY1(), dim.getX1() + hyp12 + ext, -dim.getY1());
        line1.setStrokeWidth(dim.getWidth());
        line1.setStrokeLineCap(StrokeLineCap.ROUND);
        line1.setStroke(c);
        lineGroup.getChildren().add(line1);

        // Draw second line and rotate it into place.
        Line line2 = new Line(dim.getX1(), -dim.getY1(), dim.getX1() + hyp12 + ext, -dim.getY1());
        line2.setStrokeWidth(dim.getWidth());
        line2.setStrokeLineCap(StrokeLineCap.ROUND);
        line2.setStroke(c);
        Rotate rl2 = new Rotate(-angD, dim.getX1(), -dim.getY1());
        line2.getTransforms().add(rl2);
        lineGroup.getChildren().add(line2);

        g.getChildren().addAll(line1, line2);

        Arc arc = new Arc(dim.getX1(), -dim.getY1(),
                hyp12, hyp12, ang12, angD
        );
        arc.setType(ArcType.OPEN);
        arc.setStroke(c);
        arc.setFill(Color.TRANSPARENT);
        arc.setStrokeWidth(dim.getWidth());

        double arrowDeg = 18;
        double arrowLen = 2.54;
        Arc arrow1 = new Arc(dim.getX1() + hyp12, -dim.getY1(),
                arrowLen, arrowLen, ang12 + 90 - arrowDeg / 9.0, arrowDeg
        );
        arrow1.setType(ArcType.ROUND);
        arrow1.setStroke(c);
        arrow1.setStrokeLineJoin(StrokeLineJoin.ROUND);
        arrow1.setFill(c);
        arrow1.setStrokeWidth(dim.getWidth() * 0.3);

        Arc arrow2 = new Arc(dim.getX1() + hyp12, -dim.getY1(),
                arrowLen, arrowLen, ang12 - 90 + arrowDeg / 9.0, -arrowDeg
        );
        arrow2.setType(ArcType.ROUND);
        arrow2.setStroke(c);
        arrow2.setStrokeLineJoin(StrokeLineJoin.ROUND);
        arrow2.setFill(c);
        arrow2.setStrokeWidth(dim.getWidth() * 0.3);
        Rotate arr2 = new Rotate(-angD, dim.getX1(), -dim.getY1());
        arrow2.getTransforms().add(arr2);

        g.getChildren().addAll(arc, arrow1, arrow2);

        // Text
        BigDecimal bdUp = new BigDecimal(angD).setScale(
                1, RoundingMode.HALF_UP
        );
        String dimValueString = String.valueOf(bdUp.doubleValue());
        Text dimText = new Text(dimValueString);
        Font dimFont = Font.loadFont(
                LibraryElementNode.class.getResourceAsStream(FONT_PATH),
                dim.getTextsize() * 1.4
        );
        dimText.setFont(dimFont);
        dimText.setFill(c);
        dimText.setLayoutX(dim.getX1() + oppTadj - dimText.getBoundsInLocal().getWidth() / 2.0);
        dimText.setLayoutY(-dim.getY1() - oppTopp);
        Rotate tRot = new Rotate(90 - angD / 2.0, dimText.getBoundsInLocal().getWidth() / 2.0, 0);
        dimText.getTransforms().add(tRot);

        g.getChildren().add(dimText);

        Rotate gr = new Rotate(ang12, dim.getX1(), -dim.getY1());
        g.getTransforms().add(gr);
        return g;
    }

    private static Node radiusDimensionNode(Dimension dim, Color c) {
        Group g = new Group();
        // Cross Hair
        double hyp12 = Math.hypot(dim.getX1() - dim.getX2(), dim.getY1() - dim.getY2());
        double hyp13 = Math.hypot(dim.getX1() - dim.getX3(), dim.getY1() - dim.getY3());
        double ang1 = Math.toDegrees(Math.atan2(dim.getY2() - dim.getY1(), dim.getX2() - dim.getX1()));
        double ang13 = Math.toDegrees(Math.atan2(dim.getY3() - dim.getY1(), dim.getX3() - dim.getX1()));
        double ang3 = ang1 - ang13;
        double adj13 = Math.cos(Math.toRadians(ang3)) * hyp13;
        double opp13 = Math.sqrt(hyp13 * hyp13 - adj13 * adj13);

        double chSize = 0.35;
        Line chH = new Line(dim.getX1(), -dim.getY1() + chSize, dim.getX1(), -dim.getY1() - chSize);
        chH.setStrokeWidth(chSize / 2.0);
        chH.setStrokeLineCap(StrokeLineCap.ROUND);
        chH.setStroke(c);

        Line chV = new Line(dim.getX1() - chSize, -dim.getY1(), dim.getX1() + chSize, -dim.getY1());
        chV.setStrokeWidth(chSize / 2.0);
        chV.setStrokeLineCap(StrokeLineCap.ROUND);
        chV.setStroke(c);
        g.getChildren().addAll(chH, chV);

        // Line
        Group lineGroup = new Group();
        Line line = new Line(dim.getX1() + hyp12, -dim.getY1(), dim.getX1() + adj13, -dim.getY1());
        line.setStrokeWidth(dim.getWidth());
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStroke(c);
        lineGroup.getChildren().add(line);

        // Arrow
        Node arrowhead = arrowhead(dim.getX1() + hyp12, -dim.getY1(), dim.getWidth() * 20, dim.getWidth() * 6,
                0, dim.getWidth(), c);
        lineGroup.getChildren().add(arrowhead);

        g.getChildren().add(lineGroup);
        Rotate r = new Rotate(-ang1, dim.getX1(), -dim.getY1());
        lineGroup.getTransforms().add(r);

        // Text
        BigDecimal bdUp = new BigDecimal(hyp12).setScale(
                dim.getPrecision(), RoundingMode.HALF_UP
        );
        String dimValueString = String.valueOf(bdUp.doubleValue());
        Text dimText = new Text(dimValueString);
        Font dimFont = Font.loadFont(
                LibraryElementNode.class.getResourceAsStream(FONT_PATH),
                dim.getTextsize() * 1.4
        );
        dimText.setFont(dimFont);

        double dimWidth = dimText.getBoundsInLocal().getWidth();
        dimText.setLayoutX(dim.getX1() + adj13 - dimWidth);
        dimText.setLayoutY(-dim.getY1() - opp13);
        dimText.setFill(c);
        if (ang1 <= -90 && ang1 > -270) {
            Rotate rt = new Rotate(180, dimWidth / 2.0, 0);
            dimText.getTransforms().add(rt);
        }

        lineGroup.getChildren().add(dimText);

        return g;
    }

    private static Node parallelDimension(Dimension dim, Color c) {
        Group shapeGroup = new Group();
        Group g = new Group();

        double x1 = dim.getX1();
        double y1 = dim.getY1();
        double x2 = dim.getX2();
        double y2 = dim.getY2();
        double x3 = dim.getX3();
        double y3 = dim.getY3();

//        double ang;
//        if (x1 == x2) {
//            ang = 90;
//        } else {
//            ang = Math.toDegrees(Math.sin((y2 - y1) / (x2 - x1)));
//        }
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
        shapeGroup.getChildren().addAll(line1, line2, line3);

        double arrowSize = 2;
        shapeGroup.getChildren().add(
                arrowhead(x1, -yy3,
                        arrowSize, arrowSize * 0.4, 0,
                        dim.getWidth(), c
                ));
        shapeGroup.getChildren().add(
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
        shapeGroup.getChildren().add(dimText);

        // Rotate visual element
        Rotate rotate = new Rotate(-ang12, x1, -y1);
        shapeGroup.getTransforms().add(rotate);

        g.getChildren().add(shapeGroup);

        if (dim.getDtype() == DimensionType.DIAMETER) {
            // Add Crosshairs
            Point2D p1 = new Point2D(dim.getX1(), dim.getY1());
            Point2D mid = p1.midpoint(dim.getX2(), dim.getY2());
            g.getChildren().add(
                    crosshairs(mid.getX(), -mid.getY(), 0.5, dim.getWidth(), c)
            );
        }
        return g;
    }

    public static Node createSplineNode(Spline sp, Color c) {
        Group g = new Group();

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
