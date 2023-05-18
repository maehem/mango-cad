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
import com.maehem.mangocad.model.library.element.quantum.enums.PinFunction;
import static com.maehem.mangocad.model.library.element.quantum.enums.PinLength.LONG;
import static com.maehem.mangocad.model.library.element.quantum.enums.PinLength.MIDDLE;
import static com.maehem.mangocad.model.library.element.quantum.enums.PinLength.POINT;
import static com.maehem.mangocad.model.library.element.quantum.enums.PinLength.SHORT;
import static com.maehem.mangocad.model.library.element.quantum.enums.PinVisible.BOTH;
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
    
    public static Node createText(ElementText et, Color color) {
        
        Text tt = new Text(et.getValue());
        tt.setFont(Font.font(et.getSize()));
        tt.setFill(color);
        double width = tt.getBoundsInLocal().getWidth();
        double height = tt.getBoundsInLocal().getHeight();
        
        int rot = (int) et.getRotation();
        
        double rotX = 0.0;
        double rotY = 0.0;
        
        switch (rot) {
            case 270 -> {
                tt.setRotate(270);
                rotX = -width/2.0 + height*0.3;
                rotY =  width/2.0 + height*0.3;
            }
            case 180 -> {
                if ( et.isMirror() ) {
                    rot = 0;
                    rotY = height*0.66;
                }
            }
            case 90-> {
                tt.setRotate(270);
                rotX = -width/2.0 - height*0.3;
                rotY = -width/2.0 + height*0.3;
            }
            default -> {
                if ( et.isMirror() ) {
                    rot = 180;
                    rotY = -height*0.66;
                }
            }
        }
        
        double pxL = et.getX() + rotX;
        double pxR = et.getX() - width + rotX;
        double pL = rot==180?pxR:pxL;
        double pR = rot==180?pxL:pxL;
        
        double pyT = -et.getY() + height * 0.66 + rotY;
        double pyB = -et.getY() + rotY;
        double pT = rot==180?pyB:pyT;
        double pB = rot==180?pyT:pyB;
        
        double cX = et.getX() - width / 2.0;
        double cY = -et.getY() + height * 0.3;
        
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
        
        // TODO:  VISIBLE:  Ghost Text and smaller font
        //                
                        
        final double PIN_NAME_MARGIN = 1.5;
        final double PIN_STROKE_WIDTH = 0.1524; // 6 mil
        final double PIN_FONT_SIZE = 2.0;
        final Color PIN_COLOR = new Color(0.2,0.2,0.2,1.0);
        final Color PIN_COLOR_GHOST = new Color(0.2,0.2,0.2,0.3);
        final Color PIN_NAME_COLOR = new Color(1.0,1.0,1.0,0.2);
        final Color PIN_DIR_SWAP_COLOR = new Color(0.3,1.0,0.3,0.5);
        final double PIN_DIR_SWAP_OFFSET = PIN_FONT_SIZE * 0.2;
        final Color ORIGIN_CIRCLE_COLOR = new Color(1.0,1.0,1.0,0.1);
        final double ORIGIN_CIRCLE_RADIUS = 0.635;
        final double ORIGIN_CIRCLE_LINE_WIDTH = 0.07;
        final double DOT_CIRCLE_RADIUS = 0.7;
        final double DOT_CIRCLE_LINE_WIDTH = PIN_STROKE_WIDTH*1.7;
        final double CLK_SIZE = 1.3;
        
        // There might be a dot on pin.
        double dotRadius = 0;
        if ( p.getFunction() == PinFunction.DOT || p.getFunction() == PinFunction.DOTCLK ) {
            dotRadius = DOT_CIRCLE_RADIUS;
        }
        
        int rot = (int) p.getRotation(); // 0, 90, 180, 270

        Group g = new Group();
        
        Line line = new Line(p.getX(), -p.getY(), p.getX(), -p.getY());
        line.setStroke(PIN_COLOR);
        line.setStrokeLineCap(StrokeLineCap.BUTT);
        line.setStrokeWidth(PIN_STROKE_WIDTH);               
                

        Color pinNameColor = PIN_COLOR;
        Color padColor = PIN_COLOR;
        
        switch ( p.getVisible() ) {
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
        
        // X, Y, Length
        double pinLen = 0;
        switch (p.getLength()) {
            case LONG   -> { pinLen = 7.52; } // 0.3 inch
            case MIDDLE -> { pinLen = 5.08; } // 0.2 inch
            case SHORT  -> { pinLen = 2.54; } // 0.1 inch
            case POINT  -> {} // Already zero.
        }
        pinLen -= dotRadius*2.0;
        
        switch (rot) {
            case 270 -> line.setEndY(-p.getY() + pinLen);
            case 180 -> line.setEndX(p.getX() - pinLen );
            case 90  -> line.setEndY(-p.getY() - pinLen);
            default  -> line.setEndX(p.getX() + pinLen);
        }

        // When you need some dots.
        if ( dotRadius > 0.0 ) {
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
            
            g.getChildren().add( createCircleNode(dotCircle, PIN_COLOR) );
        }
                
        // Clock Function
        if ( p.getFunction() == PinFunction.CLK || p.getFunction() == PinFunction.DOTCLK ) {
            Line line1 = new Line(0,0,0,0);
            line1.setStroke(PIN_COLOR);
            line1.setStrokeLineCap(StrokeLineCap.ROUND);
            line1.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
            
            Line line2 = new Line();
            line2.setStroke(PIN_COLOR);
            line2.setStrokeLineCap(StrokeLineCap.ROUND);
            line2.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
            
            switch (rot) {
                case 270 -> {
                    line1.setStartX(line.getEndX() - CLK_SIZE/2.0);
                    line1.setStartY(line.getEndY() + dotRadius*2.0);
                    line1.setEndX(line.getEndX() );
                    line1.setEndY(line1.getStartY()+CLK_SIZE);
                    line2.setStartX(line.getEndX() + CLK_SIZE/2.0);
                    line2.setStartY(line.getEndY() + dotRadius*2.0);
                    line2.setEndX(line.getEndX());
                    line2.setEndY(line1.getStartY()+CLK_SIZE);
                }
                case 180 -> {
                    line1.setStartX(line.getEndX());
                    line1.setStartY(line.getEndY() + CLK_SIZE/2.0);
                    line1.setEndX(line1.getStartX() - CLK_SIZE );
                    line1.setEndY(-p.getY());
                    line2.setStartX(line.getEndX());
                    line2.setStartY(line.getEndY() - CLK_SIZE/2.0);
                    line2.setEndX(line1.getStartX() - CLK_SIZE );
                    line2.setEndY(-p.getY());
                }
                case 90 -> {
                    line1.setStartX(line.getEndX() - CLK_SIZE/2.0);
                    line1.setStartY(line.getEndY()-dotRadius*2.0);
                    line1.setEndX(line.getEndX());
                    line1.setEndY(line1.getStartY()-CLK_SIZE);
                    line2.setStartX(line.getEndX() + CLK_SIZE/2.0);
                    line2.setStartY(line.getEndY()-dotRadius*2.0);
                    line2.setEndX(line.getEndX());
                    line2.setEndY(line1.getStartY()-CLK_SIZE);
                }
                default -> {
                    line1.setStartX(line.getEndX() + dotRadius*2.0 );
                    line1.setStartY(line.getEndY() + CLK_SIZE/2.0);
                    line1.setEndX(line1.getStartX() + CLK_SIZE );
                    line1.setEndY(-p.getY());
                    line2.setStartX(line.getEndX() + dotRadius*2.0 );
                    line2.setStartY(line.getEndY() - CLK_SIZE/2.0);
                    line2.setEndX(line1.getStartX() + CLK_SIZE );
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
        
        String padText = "?";
        switch (p.getLength()) {
            case LONG   -> { padText = "999"; }
            case MIDDLE -> { padText = "99"; }
            case SHORT  -> { padText = "9"; }
            case POINT  -> { padText = "x"; } 
        }
        
        Text padName = new Text(padText);
        padName.setFont(Font.font(PIN_FONT_SIZE*0.8));
        padName.setFill(padColor);
        double padWidth = padName.getBoundsInLocal().getWidth();
        double padHeight = padName.getBoundsInLocal().getHeight();
        g.getChildren().add(padName);

        // Direction and Swap-Level
        Text dirSwap = new Text(p.getDirection().code() + "  " + p.getSwapLevel());
        dirSwap.setFont(Font.font(PIN_FONT_SIZE * 0.7));
        dirSwap.setFill(PIN_DIR_SWAP_COLOR);
        double dsWidth = dirSwap.getBoundsInLocal().getWidth();
        double dsHeight = dirSwap.getBoundsInLocal().getHeight();
        g.getChildren().add(dirSwap);
        
        
        switch (rot) {
            case 270 -> {
                pinName.setLayoutX(  p.getX() - width/2 );
                pinName.setLayoutY( -p.getY() + width/2 + height * 0.3 + pinLen + dotRadius*2.0 + PIN_NAME_MARGIN);
                pinName.setRotate(90);
                
                padName.setLayoutX(  p.getX() - padWidth/2);
                padName.setLayoutY( -p.getY() - padWidth/2 - padHeight*0.3 );
                padName.setRotate(90);
                
                dirSwap.setLayoutX(  p.getX() - PIN_DIR_SWAP_OFFSET - dsHeight/2 - dsWidth/2  );
                dirSwap.setLayoutY( -p.getY() - dsWidth/3 - PIN_DIR_SWAP_OFFSET );
                dirSwap.setRotate(270);
            }
            case 180 -> {
                pinName.setLayoutX( p.getX() - pinLen - dotRadius*2.0 - width - PIN_NAME_MARGIN);
                pinName.setLayoutY(-p.getY() + height * 0.3);
                
                padName.setLayoutX( p.getX() - padWidth);
                padName.setLayoutY(-p.getY() - padHeight*0.2 );
                
                dirSwap.setLayoutX( p.getX() + PIN_DIR_SWAP_OFFSET  );
                dirSwap.setLayoutY(-p.getY() - PIN_DIR_SWAP_OFFSET );
            }
            case 90 -> {
                // Rotate Node rotates on center, so we need to compensate for that.
                pinName.setLayoutX(  p.getX() - width/2 );
                pinName.setLayoutY( -p.getY() - width/2 + height*0.3 -  pinLen - dotRadius*2.0 - PIN_NAME_MARGIN );
                pinName.setRotate(90);

                padName.setLayoutX(  p.getX() - padWidth/2);
                padName.setLayoutY( -p.getY() - padWidth/2 + padHeight*0.3 );
                padName.setRotate(90);

                dirSwap.setLayoutX(  p.getX() + PIN_DIR_SWAP_OFFSET + dsHeight/2 - dsWidth/2 );
                dirSwap.setLayoutY( -p.getY() + dsWidth/2 + dsHeight/3  + PIN_DIR_SWAP_OFFSET );
                dirSwap.setRotate(90);
            }
            default -> {
                pinName.setLayoutX( p.getX() + pinLen + dotRadius*2.0 + PIN_NAME_MARGIN);
                pinName.setLayoutY(-p.getY() + height*0.3);
                
                padName.setLayoutX( p.getX() );
                padName.setLayoutY(-p.getY() - padHeight*0.2);
                
                dirSwap.setLayoutX( p.getX() - PIN_DIR_SWAP_OFFSET - dsWidth );
                dirSwap.setLayoutY(-p.getY() - PIN_DIR_SWAP_OFFSET );
            }
        }
        
        g.getChildren().add(line);
        
        ElementCircle originCircle = new ElementCircle();
        originCircle.setX(p.getX());
        originCircle.setY(p.getY());
        originCircle.setRadius(ORIGIN_CIRCLE_RADIUS);
        originCircle.setWidth(ORIGIN_CIRCLE_LINE_WIDTH);
        // Origin Circle
        g.getChildren().add( createCircleNode(originCircle, ORIGIN_CIRCLE_COLOR) );
        
        return g;
    }
    
    /**
     * <circle x="3.6068" y="0" radius="1.016" width="0.508" layer="94"/>
     * 
     * @param ec ElementCircle object
     * @param color to make the circle
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
