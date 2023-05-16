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

import com.maehem.mangocad.model.library.element.quantum.ElementCircle;
import com.maehem.mangocad.model.library.element.quantum.ElementPolygon;
import com.maehem.mangocad.model.library.element.quantum.ElementRectangle;
import com.maehem.mangocad.model.library.element.quantum.ElementText;
import com.maehem.mangocad.model.library.element.quantum.PadSMD;
import com.maehem.mangocad.model.library.element.quantum.Pin;
import com.maehem.mangocad.model.library.element.quantum.Vertex;
import com.maehem.mangocad.model.library.element.quantum.Wire;
import static com.maehem.mangocad.model.library.element.quantum.enums.PinLength.POINT;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.BOTTOM_CENTER;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.BOTTOM_LEFT;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.BOTTOM_RIGHT;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.CENTER;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.CENTER_LEFT;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.CENTER_RIGHT;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.TOP_CENTER;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.TOP_LEFT;
import static com.maehem.mangocad.model.library.element.quantum.enums.TextAlign.TOP_RIGHT;
import java.util.List;
import java.util.logging.Level;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryElementNode {

    /**
     *
     * ATTLIST wire x1 %Coord; #REQUIRED y1 %Coord; #REQUIRED x2 %Coord;
     * #REQUIRED y2 %Coord; #REQUIRED width %Dimension; #REQUIRED layer %Layer;
     * #REQUIRED extent %Extent; #IMPLIED style %WireStyle; "continuous" curve
     * %WireCurve; "0" cap %WireCap; "round" grouprefs IDREFS #IMPLIED
     *
     * extent: Only applicable for airwires cap : Only applicable if 'curve' is
     * not zero
     *
     * @param w
     * @return
     */
    public static Node createWireNode(Wire w, Color color) {
        if (w.getCurve() != 0.0) {
            Path path = new Path();
            
            MoveTo moveTo = new MoveTo();
            moveTo.setX(w.getX());
            moveTo.setY(-w.getY());
            
            ArcTo arc = new ArcTo();
            // Curve to ARC
            arc.setX(w.getX2());
            arc.setY(-w.getY2());

            // SWEEP
            arc.setSweepFlag(w.getX() < w.getX2());
            
            double sin90 = Math.sin(Math.toRadians(90.0));
            double dist = distance(w.getX(), -w.getY(), w.getX2(), -w.getY2());
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
            Line line = new Line(w.getX(), -w.getY(), w.getX2(), -w.getY2());
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setStrokeWidth(w.getWidth());
            line.setStroke(color);
            
            return line;
        }
    }
    
    public static Node createRectangle(ElementRectangle r, Color color) {
        Rectangle rr = new Rectangle(
                r.getX(), r.getY(),
                r.getX2() - r.getX(), r.getY2() - r.getY()
        );
        rr.setStrokeWidth(0);
        //rr.setStrokeLineCap(StrokeLineCap.ROUND);
        rr.setFill(color);
        return rr;
    }
    
    public static Node createPolygon(ElementPolygon poly) {
        List<Vertex> vertices = poly.getVertices();
        double verts[] = new double[vertices.size() * 2];
        
        for (int j = 0; j < verts.length; j += 2) {
            verts[j] = vertices.get(j / 2).getX();
            verts[j + 1] = -vertices.get(j / 2).getY();
        }
        Polygon p = new Polygon(verts);
        p.setStrokeWidth(poly.getWidth());
        p.setStrokeLineCap(StrokeLineCap.ROUND);
        
        return p;
    }
    
    public static Node createText(ElementText et) {
        
        Text tt = new Text(et.getValue());
        tt.setFont(Font.font(et.getSize()));
        double width = tt.getBoundsInLocal().getWidth();
        double height = tt.getBoundsInLocal().getHeight();
        
        Pane ttG = new Pane(tt);
        ttG.setPrefHeight(et.getSize());

        // Text alignment.
        switch (et.getAlign()) {
            case BOTTOM_CENTER -> {
                ttG.setLayoutX(et.getX() - width / 2.0);
                ttG.setLayoutY(-et.getY());
            }
            case BOTTOM_LEFT -> {
                ttG.setLayoutX(et.getX());
                ttG.setLayoutY(-et.getY());
            }
            case BOTTOM_RIGHT -> {
                ttG.setLayoutX(et.getX() - width);
                ttG.setLayoutY(-et.getY());
            }
            case CENTER -> {
                ttG.setLayoutX(et.getX() - width / 2.0);
                ttG.setLayoutY(-et.getY() + height * 0.3);
            }
            case CENTER_LEFT -> {
                ttG.setLayoutX(et.getX());
                ttG.setLayoutY(-et.getY() + height * 0.3);
            }
            case CENTER_RIGHT -> {
                ttG.setLayoutX(et.getX() - width);
                ttG.setLayoutY(-et.getY() + height * 0.3);
            }
            case TOP_CENTER -> {
                ttG.setLayoutX(et.getX() - width / 2.0);
                ttG.setLayoutY(-et.getY() + height * 0.66);
            }
            case TOP_LEFT -> {
                ttG.setLayoutX(et.getX());
                ttG.setLayoutY(-et.getY() + height * 0.66);
            }
            case TOP_RIGHT -> {
                ttG.setLayoutX(et.getX() - width);
                ttG.setLayoutY(-et.getY() + height * 0.66);
            }
            
        }
        
        return ttG;
    }

    /**
     * Create a SMD node for the pattern:
     * <smd name="1" x="-0.751840625" y="0" dx="0.7112" dy="0.762" layer="1" roundness="20"/>
     *
     * @return
     */
    public static Node createSmd(PadSMD smd) {
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(Color.LIGHTSALMON);
        
        double w = smd.getWidth();
        double h = smd.getHeight();
        
        double cX = smd.getX() - w / 2.0;
        double cY = smd.getY() - h / 2.0;
        //Setting the properties of the rectangle 
        rectangle.setX(cX);
        rectangle.setY(cY);
        rectangle.setWidth(w);
        rectangle.setHeight(h);

        // arcW/H is half of the shortest side.
        // TODO: Won't render right if I use h/2 or w/2. Not sure why.
        double arcR = 0.0;
        double roundPct = smd.getRoundness() * 0.01;
        
        if (smd.getRoundness() > 0) {
            if (w < h) {
                arcR = w * roundPct;
            } else {
                arcR = h * roundPct;
            }
            rectangle.setArcWidth(arcR);
            rectangle.setArcHeight(arcR);
        }
        
        return rectangle;
    }
    
    public static Node createPinNode(Pin p) {
        Group g = new Group();
        Line line = new Line(p.getX(), -p.getY(), p.getX(), -p.getY());
        line.setStroke(Color.DARKGREEN);
        // X, Y, Length
        double pinLen = 0;
        switch (p.getLength()) {
            case LONG   -> { pinLen = 7.52; }
            case MIDDLE -> { pinLen = 5.08; }
            case SHORT  -> { pinLen = 2.54; }
            case POINT  -> {} // Already zero.
        }
        
        double rot = p.getRotation(); // 0, 90, 180, 270
        if (rot == 270) {
            line.setEndY(-p.getY() + pinLen);
        } else if (rot == 180) {
            line.setEndX(p.getX() - pinLen);
        } else if ( rot == 90 ) {
            line.setEndY(-p.getY() - pinLen);
        } else {
            line.setEndX(p.getX() + pinLen);
        }
        
        line.setStrokeLineCap(StrokeLineCap.BUTT);
        line.setStrokeWidth(0.1524);
        
        ElementCircle originCircle = new ElementCircle();
        originCircle.setX(p.getX());
        originCircle.setY(p.getY());
        originCircle.setRadius(0.635);
        originCircle.setWidth(0.07);
        
        
        g.getChildren().add(line);
        // Origin Circle
        g.getChildren().add( createCircleNode(originCircle, new Color(1.0,1.0,1.0,0.1)) );
        
        return g;
    }
    
    /**
     * <circle x="3.6068" y="0" radius="1.016" width="0.508" layer="94"/>
     * 
     * @param c
     * @return 
     */
    public static Node createCircleNode( ElementCircle ec, Color color ) {
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
}
