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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.ElementDualXY;
import com.maehem.mangocad.model.ElementListener;
import com.maehem.mangocad.model.ElementRotation;
import com.maehem.mangocad.model.ElementSelectable;
import com.maehem.mangocad.model.ElementXY;
import com.maehem.mangocad.model.element.basic.ElementCircle;
import com.maehem.mangocad.model.element.basic.ElementPolygon;
import com.maehem.mangocad.model.element.basic.ElementRectangle;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Vertex;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.enums.WireEnd;
import static com.maehem.mangocad.model.element.enums.WireEnd.ONE;
import static com.maehem.mangocad.model.element.enums.WireEnd.TWO;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.misc.Grid;
import com.maehem.mangocad.view.EditorTool;
import static com.maehem.mangocad.view.EditorTool.CIRCLE;
import static com.maehem.mangocad.view.EditorTool.INFO;
import static com.maehem.mangocad.view.EditorTool.LOOK;
import static com.maehem.mangocad.view.EditorTool.SELECT;
import static com.maehem.mangocad.view.EditorTool.TRASH;
import com.maehem.mangocad.view.PickListener;
import com.maehem.mangocad.view.TextEditDialog;
import com.maehem.mangocad.view.library.MouseMovementListener;
import com.maehem.mangocad.view.node.CircleNode;
import com.maehem.mangocad.view.node.PinNode;
import com.maehem.mangocad.view.node.PolygonNode;
import com.maehem.mangocad.view.node.RectangleNode;
import com.maehem.mangocad.view.node.TextNode;
import com.maehem.mangocad.view.node.ViewNode;
import com.maehem.mangocad.view.node.WireNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import static javafx.scene.input.MouseButton.MIDDLE;
import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Scale;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorInteractiveArea extends ScrollPane implements PickListener, ElementListener {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    private static final double SCALE_MAX = 40.0;
    private static final double SCALE_MIN = 5.0;
    private static final double SCALE_FACTOR = 0.5;
    private static final int WORK_AREA = (int) (1000.0 / SCALE_MIN);
    private static final double WA2 = WORK_AREA / 2.0;
    //private static final double GRID_SIZE = 2.54;
    private static final Color GRID_COLOR = new Color(1.0, 1.0, 1.0, 0.1);
    private static final double GRID_STROKE_WIDTH = 0.05;
    private static final double PICK_SIZE = 1.0;

    private final Circle shadow = new Circle(1, Color.RED);
    //private final Text scaleText = new Text("x1.0");
    private final Group workArea = new Group(shadow);//, scaleText);
    private final Group crossHairArea = new Group();
    private final Group scrollArea = new Group(workArea, crossHairArea);
    private final SymbolEditorContextMenu contextMenu = new SymbolEditorContextMenu();
    private final LibrarySymbolSubEditor parentEditor;
    private double scale = 10.0;
    private Line hLine;
    private Line vLine;
    private Scale workScale = new Scale();
    private final ArrayList<Element> selectedElements = new ArrayList<>();
    private final ArrayList<Element> movingElements = new ArrayList<>();
    private final ArrayList<ViewNode> nodes = new ArrayList<>();
    private final Rectangle selectionRectangle = new Rectangle();
    private final ArrayList<Line> gridLines = new ArrayList<>();
    private final Rectangle background;

    private ViewNode ephemeralNode;
    private Element lastElementAdded = null;
    private double mouseDownX = Double.MIN_VALUE;
    private double mouseDownY = Double.MIN_VALUE;
    private double movingMouseStartX;
    private double movingMouseStartY;
    private MouseMovementListener mouseListener = null;

    // TODO get from control panel settings.
    private final Color selectionRectangleColor = new Color(1.0, 1.0, 1.0, 0.5);
    private EditorTool toolMode = EditorTool.SELECT;

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

        background = new Rectangle(-WA2, -WA2, WORK_AREA, WORK_AREA);
        // TODO: Get color from control panel settings -> options -> colors -> symbolCanvasColor.
        background.setFill(new Color(0.2, 0.2, 0.2, 1.0));
        workArea.getChildren().add(background);

        buildScene();

        workScale.setX(scale);
        workScale.setY(scale);

        shadow.setVisible(false);

        //scaleText.setLayoutX(10);
        //scaleText.setLayoutY(10);
        //scaleText.setScaleX(0.5);
        //scaleText.setScaleY(0.5);
        selectionRectangle.setFill(null);
        selectionRectangle.setStroke(selectionRectangleColor);
        selectionRectangle.setStrokeType(StrokeType.CENTERED);
        selectionRectangle.setStrokeWidth(0.05);
        selectionRectangle.setVisible(false);
        ObservableList<Double> strokeDashArray = selectionRectangle.getStrokeDashArray();
        strokeDashArray.addAll(0.5, 0.5);
        workArea.getChildren().add(selectionRectangle);

        workArea.getTransforms().add(workScale);

        // Listen to changes in the Grid settings.
        parentEditor.getDrawing().getGrid().addListener(this);

        workArea.addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
            double scrollAmt = event.getDeltaY();
            scale += scrollAmt * SCALE_FACTOR;
            if (scale > SCALE_MAX) {
                scale = SCALE_MAX;
            }
            if (scale < SCALE_MIN) {
                scale = SCALE_MIN;
            }
            workScale.setX(scale);
            workScale.setY(scale);
            //scaleText.setText("x" + String.format("%.2f", scale));

            double mX = event.getX();
            double mY = event.getY();

            // Derive the sb values after the scale.
            double sbX = 0.5 + mX / WA2 / scale / 2;// + (waX + (mX - vaW / 2.0) / scale) / WORK_AREA;
            double sbY = 0.5 + mY / WA2 / scale / 2;// + (waY + (mY - vaH / 2.0) / scale) / WORK_AREA;

            setHvalue(sbX);
            setVvalue(sbY);

            event.consume();
        });

//        setOnKeyPressed((ke) -> {
//            if (ke.getCode() == ESCAPE) {
//                abandonOperation();
//                ke.consume();
//            }
//        });
        // Toggle CrossHair
        setOnMouseEntered((t) -> {
            getScene().setCursor(Cursor.CROSSHAIR); //Change cursor to crosshair
        });
        setOnMouseExited((t) -> {
            getScene().setCursor(Cursor.DEFAULT);
        });

        initMouseMoved();
        initMouseClicked();
        initMouseDragged();
        initMouseReleased();
    }

    public void setMouseMoveListener(MouseMovementListener listener) {
        this.mouseListener = listener;
    }

    private void initMouseMoved() {
        workArea.setOnMouseMoved((MouseEvent me) -> {
            if (mouseListener != null) {
                mouseListener.workAreaMouseMoved(me.getX(), me.getY());
            }
            // Move any selected node.
            if (!movingElements.isEmpty()) {
                //LOGGER.log(Level.SEVERE, "Work Area: mXY: {0},{1}", new Object[]{me.getX(), me.getY()});
                //LOGGER.log(Level.SEVERE, "Moving the things around.");
                // TODO: Is 'option' key held down? then use altGrid.
                double snap = parentEditor.getDrawing().getGrid().getSizeMM();

//                double xxx = (int) (me.getX() / snap) * snap; // Snap to grid
//                double yyy = (int) (me.getY() / snap) * snap; // Snap to grid
                //LOGGER.log(Level.SEVERE, "Mouse Moved:    xxx/yyy: {0},{1}", new Object[]{xxx, yyy});
                //double moveDistX = me.getX() - movingMouseStartX;
                //double moveDistY = -(me.getY() - movingMouseStartY);
                //double moveDistSnappedX = (int) (moveDistX / snap) * snap;
                //double moveDistSnappedY = (int) (moveDistY / snap) * snap;
                double moveDistSnappedX = getSnappedLocation(me.getX(), movingMouseStartX);
                double moveDistSnappedY = -getSnappedLocation(me.getY(), movingMouseStartY);

                for (Element e : movingElements) {
                    if (e instanceof ElementSelectable es) {
                        switch (es) {
                            case ElementCircle circ -> {
                                Element snapshot = es.getSnapshot();
                                if (ephemeralNode != null) { // Could be a move or a radius adjust(new circles)
                                    // New circle, not yet placed. Adjust Radius
                                    double hypot = Math.hypot(moveDistSnappedX, moveDistSnappedY);
                                    circ.setRadius(hypot);
                                } else {
                                    // User moving circle at center X,Y
                                    // TODO needs snapshot?
                                    circ.setX(circ.getX() + moveDistSnappedX);
                                    circ.setY(circ.getY() + moveDistSnappedY);
                                }
                            }
                            case ElementRectangle rect -> {
                                Element snapshot = es.getSnapshot();
                                if (snapshot instanceof ElementRectangle snapRect) {
                                    if (ephemeralNode != null) { // Could be a move or a radius adjust(new circles)
                                        // New rect, not yet placed. Adjust Width and height.
                                        rect.setX2(rect.getX1() + moveDistSnappedX);
                                        rect.setY2(rect.getY1() + moveDistSnappedY);
                                    } else {
                                        // User moving circle at center X,Y
                                        rect.setAllXY(
                                                snapRect.getX1() + moveDistSnappedX,
                                                snapRect.getY1() + moveDistSnappedY,
                                                snapRect.getX2() + moveDistSnappedX,
                                                snapRect.getY2() + moveDistSnappedY
                                        );
                                    }
                                }
                            }
                            case ElementPolygon poly -> {
                                Element snapshot = es.getSnapshot();
                                if (snapshot instanceof ElementPolygon snapPoly) {
                                    if (ephemeralNode != null) {
                                        // Poly with new Vertex
                                        Vertex v0 = poly.getVertices().getFirst();
                                        Vertex vMoving = poly.getVertices().getLast();
                                        vMoving.setX(v0.getX() + moveDistSnappedX);
                                        vMoving.setY(v0.getY() + moveDistSnappedY);
                                        LOGGER.log(Level.SEVERE, "Moving Poly Vertex: {0},{1}   obj:{2}", new Object[]{vMoving.getX(), vMoving.getY(), vMoving.hashCode()});
                                    } else {
                                        Vertex[] selectedVertices = poly.getSelectedVertices();
                                        // Move selected vertices together.
                                        LOGGER.log(Level.SEVERE, "Polygon: Move selected vertices. Not implemented yet!");
                                    }
                                } else {
                                    LOGGER.log(Level.SEVERE, "Mouse moved: Snapshot is not ElementPolygon: " + snapshot.getElementName());
                                }
                            }
                            case ElementXY exy -> {
                                //LOGGER.log(Level.SEVERE, "Move elementXY.");
                                Element snapshot = es.getSnapshot();
                                if (snapshot instanceof ElementXY snapXY) {
                                    //LOGGER.log(Level.SEVERE, "    Move relative to snapXY.");
                                    exy.setX(snapXY.getX() + moveDistSnappedX);
                                    exy.setY(snapXY.getY() + moveDistSnappedY);
                                }
                            }
                            case ElementDualXY exy -> {
                                Element snapshot = es.getSnapshot();
                                if (snapshot instanceof ElementDualXY snapXY) {
                                    switch (exy.getSelectedEnd()) {
                                        case ONE -> {
                                            exy.setX1(snapXY.getX1() + moveDistSnappedX);
                                            exy.setY1(snapXY.getY1() + moveDistSnappedY);
                                        }
                                        case TWO -> {
                                            exy.setX2(snapXY.getX2() + moveDistSnappedX);
                                            exy.setY2(snapXY.getY2() + moveDistSnappedY);
                                        }
                                        default -> {
                                        }
                                    }
                                }
                            }
                            default -> {
                                // Non-movable thing.
                            }
                        }
                    }
                }
            }
            me.consume();
        });
    }

    private double getSnappedLocation(double loc, double startLoc) {
        double snap = parentEditor.getDrawing().getGrid().getSizeMM();
        double moveDist = loc - startLoc;

        return (int) (moveDist / snap) * snap;
    }

    private void initMouseDragged() {
        workArea.setOnMouseDragged(e -> {
            if (!movingElements.isEmpty()) {
                e.consume();
                return;
            }
            if (!selectionRectangle.isVisible()) { // Begin selection.
                LOGGER.log(Level.SEVERE, "Begin Selection.");
                mouseDownX = e.getX();
                mouseDownY = e.getY();
                selectionRectangle.setVisible(true);
                selectionRectangle.setX(mouseDownX);
                selectionRectangle.setY(mouseDownY);
                selectionRectangle.setWidth(0);
                selectionRectangle.setHeight(0);
            } else {
                selectionRectangle.setX(Math.min(e.getX(), mouseDownX));
                selectionRectangle.setWidth(Math.abs(e.getX() - mouseDownX));
                selectionRectangle.setY(Math.min(e.getY(), mouseDownY));
                selectionRectangle.setHeight(Math.abs(e.getY() - mouseDownY));
            }
            e.consume();
        });
    }

    private void initMouseClicked() {
        workArea.setOnMouseClicked((me) -> {
            if (!movingElements.isEmpty()) {
                if (null != me.getButton()) {
                    switch (me.getButton()) {
                        case PRIMARY -> {
                            if (ephemeralNode != null) {
                                if (toolMode.equals(EditorTool.PIN)) {
                                    // New node. Add to symbol.
                                    Symbol symbol = parentEditor.getSymbol();
                                    symbol.getElements().add(ephemeralNode.getElement());
                                    nodes.add(ephemeralNode);
                                    lastElementAdded = ephemeralNode.getElement();
                                    LOGGER.log(Level.SEVERE, "Placed new {0}.", ephemeralNode.getElement().getElementName());
                                    ephemeralNode = null;
                                    LOGGER.log(Level.SEVERE, "Clear movingElements. 1");
                                    movingElements.clear(); // End move of node.
                                    // Initiate another placemet of this node type.
                                    setEditorTool(toolMode); // Trigger another pin placement.
                                } else if (toolMode.equals(EditorTool.LINE)) {
                                    if (ephemeralNode instanceof WireNode wn) {
                                        Wire wire = (Wire) wn.getElement();
                                        wire.setSelectedEnd(WireEnd.NONE);
                                        Symbol symbol = parentEditor.getSymbol();
                                        symbol.getElements().add(ephemeralNode.getElement());
                                        nodes.add(ephemeralNode);
                                        lastElementAdded = ephemeralNode.getElement();
                                        LOGGER.log(Level.SEVERE, "Remember ephemeral element as lastAdded.");
                                        LOGGER.log(Level.SEVERE, "Placed new {0}.", ephemeralNode.getElement().getElementName());
                                        ephemeralNode = null;
                                        LOGGER.log(Level.SEVERE, "Clear movingElements. 3");
                                        movingElements.clear(); // End move of node.
                                        // Initiate new line.
                                        initiateNewLineSegment(me, wire.getX2(), -wire.getY2());
                                        LOGGER.log(Level.SEVERE, "Set tool mode element.");
                                        toolMode.setToolElement(wire); ////   Looks wrong?  ???????
                                        setEditorTool(toolMode); // Refreshes with lastElementAdded values.
                                    }
                                } else if (toolMode.equals(EditorTool.TEXT)) {
                                    if (ephemeralNode instanceof TextNode tn) {
                                        ElementText text = (ElementText) tn.getElement();
                                        Symbol symbol = parentEditor.getSymbol();
                                        symbol.getElements().add(text);
                                        nodes.add(ephemeralNode);
                                        lastElementAdded = text;
                                        ephemeralNode = null;
                                        movingElements.clear();
                                        //text = initiateNewText();  // Initiate new text.
                                        setEditorTool(toolMode); // Reset widget.
                                    }
                                } else if (toolMode.equals(EditorTool.CIRCLE)) { // Finish sizing a new circle.
                                    if (ephemeralNode instanceof CircleNode cn) {
                                        ElementCircle circ = (ElementCircle) cn.getElement();
                                        Symbol symbol = parentEditor.getSymbol();
                                        symbol.getElements().add(circ);
                                        nodes.add(ephemeralNode);
                                        lastElementAdded = circ;
                                        ephemeralNode = null;
                                        movingElements.clear();
                                        setEditorTool(toolMode);
                                    }
                                } else if (toolMode.equals(EditorTool.RECTANGLE)) { // Finish sizing a new Rectangle
                                    if (ephemeralNode instanceof RectangleNode wn) {
                                        ElementRectangle rect = (ElementRectangle) wn.getElement();
                                        Symbol symbol = parentEditor.getSymbol();
                                        symbol.getElements().add(rect);
                                        nodes.add(ephemeralNode);
                                        lastElementAdded = rect;
                                        LOGGER.log(Level.SEVERE, "Remember ephemeral rectangle as lastAdded.");
                                        LOGGER.log(Level.SEVERE, "Placed new {0}.", rect.getElementName());
                                        ephemeralNode = null;
                                        movingElements.clear(); // End move of node.
                                        setEditorTool(toolMode); // Refreshes with lastElementAdded values.
                                    }
                                } else if (toolMode.equals(EditorTool.POLYGON)) { // Finish adding vertices to new Polygon
                                    if (ephemeralNode instanceof PolygonNode wn) {
                                        ElementPolygon poly = (ElementPolygon) wn.getElement();
                                        List<Vertex> verts = poly.getVertices();
                                        Vertex last = verts.getLast();
                                        if (verts.size() > 3) {
                                            Vertex first = verts.getFirst();

                                            // check if x,y is same as first()
                                            if (first.getX() == last.getX() && first.getY() == last.getY()) {
                                                // Closure!
                                                LOGGER.log(Level.SEVERE, "Polygon CLOSE detected.");
                                                verts.remove(last);
                                                // Nail it down
                                                Symbol symbol = parentEditor.getSymbol();
                                                symbol.getElements().add(poly);
                                                nodes.add(wn);
                                                wn.setClosePath(true); // Close it up.
                                                //wn.rebuildPath();
                                                ElementPolygon lastPolygon = new ElementPolygon();
                                                lastPolygon.setWidth(poly.getWidth());
                                                lastElementAdded = lastPolygon;
                                                ephemeralNode = null;
                                                movingElements.clear();
                                                setEditorTool(toolMode);
                                            } else {
                                                LOGGER.log(Level.SEVERE, "Mouse clicked: polygon add vert.");
                                                // Add a new Vertex
                                                Vertex newVert = new Vertex();
                                                newVert.setX(last.getX());
                                                newVert.setY(last.getY());
                                                // TODO Curve?
                                                poly.addVertex(newVert);
                                                setEditorTool(toolMode);
                                            }
                                        } else {
                                            LOGGER.log(Level.SEVERE, "Mouse clicked: polygon add vert.");
                                            // Add a new Vertex
                                            Vertex newVert = new Vertex();
                                            newVert.setX(last.getX());
                                            newVert.setY(last.getY());
                                            poly.addVertex(newVert);
                                            setEditorTool(toolMode);
                                        }
                                    }
                                }
                            } else {
                                LOGGER.log(Level.SEVERE, "Clear movingElements. 2");
                                movingElements.clear(); // End move of node.
                                LOGGER.log(Level.SEVERE, "End of move.");
                            }
                            me.consume();
                        }
                        case MIDDLE -> { // Mirror
                            // All Nodes should be Rotatable. break if not.
                            if (elementsCanRotate(movingElements)) {
                                for (Element e : movingElements) {
                                    if (e instanceof ElementRotation er) {
                                        er.setMirror(!er.isMirrored());
                                    }
                                }                                //Rotation rotation = er.getRotation();
                                LOGGER.log(Level.SEVERE, "Mirror/180 Operation.");
                                me.consume();
                            }
                        }
                        case SECONDARY -> {
                            // Rotate  add 90 (actually "angle" from top of viewport)
                            if (elementsCanRotate(movingElements)) {
                                for (Element e : movingElements) {
                                    if (e instanceof ElementRotation er) {
                                        er.setRot(er.getRot() + 90);
                                    }
                                }
                                LOGGER.log(Level.SEVERE, "Rotate 90 Operation.");
                                me.consume();
                            }
                        }
                        default -> {
                        }
                    }
                }
            } else {  // Nothing currently happening pick things and do something.

                //LOGGER.log(Level.SEVERE, "Editor Work Area Clicked: {0}", me.getButton().name());
                ArrayList<Element> picks = new ArrayList<>();
                parentEditor.getSymbol().getElements().forEach((e) -> {
                    switch (e) {
                        case ElementXY ee -> {
                            if (Math.abs(me.getX() - ee.getX()) < PICK_SIZE
                                    && Math.abs(-me.getY() - ee.getY()) < PICK_SIZE) {
                                picks.add(e);
                            }
                        }
                        case ElementDualXY ee -> {
                            if (Math.abs(me.getX() - ee.getX1()) < PICK_SIZE
                                    && Math.abs(-me.getY() - ee.getY1()) < PICK_SIZE) {
                                picks.add(e);
                                ((ElementDualXY) e).setSelectedEnd(ONE);
                            } else if (Math.abs(me.getX() - ee.getX2()) < PICK_SIZE
                                    && Math.abs(-me.getY() - ee.getY2()) < PICK_SIZE) {
                                picks.add(e);
                                ((ElementDualXY) e).setSelectedEnd(TWO);
                            }
                        }
                        case ElementRectangle er -> {
                            double[] p = er.getPoints();

                            if ((Math.abs(me.getX() - p[0]) < PICK_SIZE && Math.abs(-me.getY() - p[1]) < PICK_SIZE)) {
                                er.setSelectedCorner(0); // Lower left. Y is inverted
                                picks.add(e);
                                er.setSelected(true);
                                //LOGGER.log(Level.SEVERE, "Chose point at: {0},{1}", new Object[]{p[0], p[1]});
                            } else if ((Math.abs(me.getX() - p[2]) < PICK_SIZE && Math.abs(-me.getY() - p[3]) < PICK_SIZE)) {
                                er.setSelectedCorner(1);
                                picks.add(e);
                                er.setSelected(true);
                                //LOGGER.log(Level.SEVERE, "Chose point at: {0},{1}", new Object[]{p[2], p[3]});
                            } else if ((Math.abs(me.getX() - p[4]) < PICK_SIZE && Math.abs(-me.getY() - p[5]) < PICK_SIZE)) {
                                er.setSelectedCorner(2);
                                picks.add(e);
                                er.setSelected(true);
                                //LOGGER.log(Level.SEVERE, "Chose point at: {0},{1}", new Object[]{p[4], p[5]});
                            } else if ((Math.abs(me.getX() - p[6]) < PICK_SIZE && Math.abs(-me.getY() - p[7]) < PICK_SIZE)) {
                                er.setSelectedCorner(3);
                                picks.add(e);
                                er.setSelected(true);
                                //LOGGER.log(Level.SEVERE, "Chose point at: {0},{1}", new Object[]{p[6], p[7]});
                            } else {
                                er.setSelected(false);
                            }
                            if (er.isSelected()) {
                                //LOGGER.log(Level.SEVERE, "Selected Corner: {0}", er.getSelectedCorner());
                            }
                        }
                        case ElementPolygon ep -> {
                            // Which vertex was selected?
                            // ep.setSelectedVertex.
                            ep.selectVerticesIn(me.getX(), -me.getY(), PICK_SIZE);

                            if (ep.hasSelections()) {
                                picks.addAll(Arrays.asList(ep.getSelectedVertices()));
                            }
                        }
                        default -> {
                        }
                    }
                });
                LOGGER.log(Level.SEVERE, "Pick count: {0}", picks.size());

                switch (me.getButton()) {
                    case PRIMARY -> { // Move or choose what to move.
                        switch (toolMode) {
                            case SELECT ->
                                LOGGER.log(Level.SEVERE, "Inspect: ");
                            case INFO ->
                                LOGGER.log(Level.SEVERE, " Edit Properties: ");
                            case LOOK ->
                                LOGGER.log(Level.SEVERE, " Highlight: ");
                            case TRASH -> {
                                LOGGER.log(Level.SEVERE, " Trash: ");
                                if (picks.size() == 1) {  // TODO: movingNodes.isEmpty() not needed.
                                    initiateTrashElement(picks.getFirst(), me.getX(), me.getY());
                                } else { // Picks was a different size. Popup selector?
                                    if (picks.isEmpty()) {
                                        contextMenu.hide();
                                    }
                                }
                            }
                            case LINE -> {
                                initiateNewLineSegment(me,
                                        getSnappedLocation(me.getX(), 0),
                                        getSnappedLocation(me.getY(), 0)
                                );
                                toolMode.setToolElement(ephemeralNode.getElement());
                                Platform.runLater(() -> {
                                    setEditorTool(toolMode);
                                });
                            }
                            case ARC -> {
                                initiateNewLineSegment(me,
                                        getSnappedLocation(me.getX(), 0),
                                        getSnappedLocation(me.getY(), 0)
                                );
                                toolMode.setToolElement(ephemeralNode.getElement());
                                Platform.runLater(() -> {
                                    setEditorTool(toolMode);
                                });
                            }
                            case CIRCLE -> {
                                LOGGER.log(Level.SEVERE, " Circle: ");
                                initiateNewCircle(me,
                                        getSnappedLocation(me.getX(), 0),
                                        getSnappedLocation(me.getY(), 0)
                                );
                                toolMode.setToolElement(ephemeralNode.getElement());
                                Platform.runLater(() -> {
                                    setEditorTool(toolMode);
                                });
                            }
                            case RECTANGLE -> {
                                LOGGER.log(Level.SEVERE, " Rectangle: ");
                                initiateNewRectangle(me,
                                        getSnappedLocation(me.getX(), 0),
                                        getSnappedLocation(me.getY(), 0)
                                );
                                toolMode.setToolElement(ephemeralNode.getElement());
                                Platform.runLater(() -> {
                                    setEditorTool(toolMode);
                                });
                            }
                            case POLYGON -> {
                                LOGGER.log(Level.SEVERE, " Create new Polygon: ");
                                initiateNewPolygon(me,
                                        getSnappedLocation(me.getX(), 0),
                                        getSnappedLocation(me.getY(), 0)
                                );
                                toolMode.setToolElement(ephemeralNode.getElement());
                                Platform.runLater(() -> {
                                    setEditorTool(toolMode);
                                });
                            }
                            case MOVE -> {
                                // If one pick, pick it.
                                if (picks.isEmpty()) {
                                    contextMenu.hide();
                                } else if (picks.size() == 1) {  // TODO: movingNodes.isEmpty() not needed.
                                    initiateElementMove(picks, me.getX(), me.getY());
                                } else if (isOnlyWires(picks)) { // Wires converge and nothing else there.
                                    // If more than one pick,
                                    // If all are wires, select them.
                                    // Add all picks to moving list.
                                    for (Element e : picks) {
                                        movingElements.add(e);
                                        if (e instanceof ElementSelectable es) {
                                            es.createSnapshot();
                                        }
                                    }
                                    LOGGER.log(Level.SEVERE, "Moving some wires.");
                                } else {
                                    // otherwise,  highlight first (grey out rest) and wait for either
                                    // another click or right-click to highlight next item.
                                    LOGGER.log(Level.SEVERE, "Mixed items. need to choose item.");
                                }
                            }
                            case ROTATE -> {
                                LOGGER.log(Level.SEVERE, "Rotate: ");
                                if (picks.isEmpty()) {
                                    contextMenu.hide();
                                } else if (picks.size() == 1) {
                                    initiateElementRotate(picks.getFirst(), toolMode.getToolElement());
                                    toolMode.setToolElement(picks.getFirst());
                                    Platform.runLater(() -> {
                                        setEditorTool(toolMode);
                                    });
                                } else {
                                    LOGGER.log(Level.SEVERE, "TODO: Handle rotation of a selected group.");
                                }
                            }
                            case MIRROR -> {
                                LOGGER.log(Level.SEVERE, "Mirror: ");
                                if (picks.isEmpty()) {
                                    contextMenu.hide();
                                } else if (picks.size() == 1) {
                                    initiateElementMirror(picks.getFirst(), toolMode.getToolElement());
                                    toolMode.setToolElement(picks.getFirst());
                                    Platform.runLater(() -> {
                                        setEditorTool(toolMode);
                                    });
                                } else {
                                    LOGGER.log(Level.SEVERE, "TODO: Handle rotation of a selected group.");
                                }
                            }
                            case MITER ->
                                LOGGER.log(Level.SEVERE, " Miter: ");
                            case SPLIT -> // If in between ends of wire then split at this location
                                // Add isInBetween() method to Wire.
                                LOGGER.log(Level.SEVERE, "Check for wire here to split.");
                            default -> {
                                LOGGER.log(Level.SEVERE, "Tool not handled yet! ==> {0}", toolMode.name());
                            }
                        }
                        me.consume();
                    } // end case PRIMARY

                    case SECONDARY -> { // Present pop-up menu.
                        contextMenu.getItems().forEach((menuItem) -> {
                            menuItem.setVisible(false);
                        });

                        // TODO: Things in Group list?
                        contextMenu.MOVE_GROUP.setDisable(true);

                        // Always there.
                        contextMenu.MOVE_GROUP.setVisible(true);

                        if (!picks.isEmpty()) {
                            contextMenu.COPY.setVisible(true);
                            contextMenu.DELETE.setVisible(true);
                            contextMenu.MIRROR.setVisible(true);
                            contextMenu.MOVE.setVisible(true);
                            contextMenu.NAME.setVisible(true);
                            contextMenu.ROTATE.setVisible(true);
                            contextMenu.SHOW.setVisible(true);
                            contextMenu.SEP1.setVisible(true);
                            contextMenu.SEP2.setVisible(true);
                            contextMenu.PROPERTIES.setVisible(true);
                        }

                        if (picks.size() > 1) {
                            // TODO: Highlight first item and present options menu with "next" option at top.
                            contextMenu.NEXT.setVisible(true);
                            contextMenu.NEXT_SEPARATOR.setVisible(true);
                        }

                        contextMenu.show(workArea, me.getScreenX(), me.getScreenY());
                        me.consume();
                    } // end case SECONDARY
                } // end switch()
            }
        }
        );
    }

    private ElementText initiateNewText() {
        ElementText text = new ElementText();
        text.setLayer(94);  // TODO needs enum.  Get from layer chooser.

        return text;
    }

    private void initiateNewLineSegment(MouseEvent me, double x, double y) {
        // Start new line at mouse.
        Wire wire = new Wire();
        wire.setLayer(94);  // TODO needs enum
        if (lastElementAdded != null && lastElementAdded instanceof Wire lastWire) {
            wire.setWidth(lastWire.getWidth());
            wire.setCurve(lastWire.getCurve());
            wire.setStyle(lastWire.getStyle());
            LOGGER.log(Level.SEVERE, "I see a last added element.");
        }
        wire.setX1(x);
        wire.setY1(-y);
        wire.setX2(x);
        wire.setY2(-y);
        wire.setSelectedEnd(TWO);
        wire.createSnapshot();
        //LOGGER.log(Level.SEVERE, "New Wire");
        WireNode wireNode = new WireNode(wire,
                parentEditor.getDrawing().getLayers(),
                parentEditor.getDrawing().getPalette(),
                this);
        wireNode.addTo(workArea);
        ephemeralNode = wireNode;
        movingMouseStartX = me.getX(); // Is this used?
        movingMouseStartY = me.getY();
        movingElements.add(wire);
    }

    private void initiateElementMove(List<Element> picks, double startX, double startY) {

        // If one pick, pick it.
        if (picks.isEmpty()) {
            contextMenu.hide();
        } else if (picks.size() == 1) {  // TODO: movingNodes.isEmpty() not needed.
            movingMouseStartX = startX;
            movingMouseStartY = startY;
            Element pick = picks.getFirst();

            movingElements.add(pick);
            if (pick instanceof ElementSelectable es) {
                es.createSnapshot();
            }

            if (lastElementAdded != null && lastElementAdded instanceof ElementRotation tmpRot) {
                //LOGGER.log(Level.SEVERE, "I see a last added element.");
                //LOGGER.log(Level.SEVERE, "    lastElement: r:{0}  mir:{1}", new Object[]{tmpRot.getRot(), tmpRot.isMirrored() ? "Y" : "N"});
                if (pick instanceof ElementRotation pickRot) {
                    //LOGGER.log(Level.SEVERE, "    It's a ElementRotation.");
                    //LOGGER.log(Level.SEVERE, "    pickRot: r:{0}  mir:{1}", new Object[]{pickRot.getRot(), pickRot.isMirrored() ? "Y" : "N"});
                    //LOGGER.log(Level.SEVERE, "    New Rot: {0} +  tmpRot: {1} = {2}", new Object[]{pickRot.getRot(), tmpRot.getRot(), (pickRot.getRot() + tmpRot.getRot())});
                    pickRot.setRot(pickRot.getRot() + tmpRot.getRot());
                    pickRot.setMirror(tmpRot.isMirrored());
                }
            }

            LOGGER.log(Level.SEVERE, "Moving a thing.");
        }
    }

    private void initiateNewCircle(MouseEvent me, double x, double y) {
        // Start new line at mouse.
        ElementCircle circle = new ElementCircle();
        circle.setLayer(94);  // TODO needs enum. Read from Layer chooser.
        if (lastElementAdded != null && lastElementAdded instanceof ElementCircle lastCircle) {
            circle.setWidth(lastCircle.getWidth());
            LOGGER.log(Level.SEVERE, "I see a last added circle element.");
        }
        circle.setX(x);
        circle.setY(-y);
        circle.createSnapshot();
        //LOGGER.log(Level.SEVERE, "New Wire");
        CircleNode circleNode = new CircleNode(circle,
                parentEditor.getDrawing().getLayers(),
                parentEditor.getDrawing().getPalette(),
                this);
        circleNode.addTo(workArea);
        ephemeralNode = circleNode;
        movingMouseStartX = me.getX(); // Is this used?
        movingMouseStartY = me.getY();
        movingElements.add(circle);
    }

    private void initiateNewRectangle(MouseEvent me, double x, double y) {
        // Start new line at mouse.
        ElementRectangle rect = new ElementRectangle();
        rect.setLayer(94);  // TODO needs enum. Read from Layer chooser.
        rect.setX1(x);
        rect.setY1(-y);
        rect.createSnapshot();
        //LOGGER.log(Level.SEVERE, "New Wire");
        RectangleNode rectNode = new RectangleNode(rect,
                parentEditor.getDrawing().getLayers(),
                parentEditor.getDrawing().getPalette(),
                this);
        rectNode.addTo(workArea);
        ephemeralNode = rectNode;
        movingMouseStartX = me.getX(); // Is this used?
        movingMouseStartY = me.getY();
        movingElements.add(rect);
    }

    private void initiateNewPolygon(MouseEvent me, double x, double y) {
        LOGGER.log(Level.SEVERE, "Initiate new Polygon.");
        ElementPolygon poly = new ElementPolygon();
        //poly.setSelected(true);
        poly.setLayer(94);
        if (lastElementAdded != null && lastElementAdded instanceof ElementPolygon lastPoly) {
            poly.setWidth(lastPoly.getWidth());
            LOGGER.log(Level.SEVERE, "I see a last added element.");
        }
        PolygonNode node = new PolygonNode(poly,
                parentEditor.getDrawing().getLayers(),
                parentEditor.getDrawing().getPalette(),
                this);
        node.addTo(workArea);
        ephemeralNode = node;
        movingMouseStartX = me.getX(); // Is this used?
        movingMouseStartY = me.getY();
        movingElements.add(poly);

        // Create snapshot?
        initiateNewPolygonSegment(me, x, y, poly);
        initiateNewPolygonSegment(me, 0, 0, poly);

        node.rebuildPath();
    }

    private void initiateNewPolygonSegment(MouseEvent me, double x, double y, ElementPolygon poly) {
        LOGGER.log(Level.SEVERE, "Initiate new Vertex for Polygon at: {0},{1}", new Object[]{x, y});
        // Start new line at mouse.
        Vertex vertex = new Vertex();
        vertex.setX(x);
        vertex.setY(-y);
        vertex.createSnapshot();
        //LOGGER.log(Level.SEVERE, "New Wire");
        poly.addVertex(vertex);

        //movingMouseStartX = me.getX(); // Is this used?
        //movingMouseStartY = me.getY();
    }

    private void initiateElementRotate(Element pick, Element copyRotFrom) {
        if (pick instanceof ElementRotation rotE) {
            if (copyRotFrom instanceof ElementRotation er) {
                rotE.setRot(rotE.getRot() + er.getRot());
            } else {
                LOGGER.log(Level.SEVERE, "Tried to copy rot value from a non-rotational element!");
            }
        }
    }

    private void initiateElementMirror(Element pick, Element copyRotFrom) {
        if (pick instanceof ElementRotation rotE) {
            if (copyRotFrom instanceof ElementRotation er) {
                if (rotE.isMirrorAllowed()) {
                    rotE.setMirror(er.isMirrored());
                } else {
                    double angle = er.isMirrored() ? 180.0 : 0.0;
                    rotE.setRot(rotE.getRot() + angle);
                }
            } else {
                LOGGER.log(Level.SEVERE, "Tried to copy mir value from a non-rotational element!");
            }
        }
    }

    private void initiateTrashElement(Element pick, double startX, double startY) {
        movingMouseStartX = startX;  // TODO:  Not used???
        movingMouseStartY = startY;

        if (pick instanceof ElementSelectable es) {
            ViewNode node = getNode(es);
            if (node != null) {
                if (node instanceof TextNode tn) {
                    LOGGER.log(Level.SEVERE, "Remove text: {0}", tn.getValue());
                }
                node.removeFrom(workArea);
                parentEditor.getSymbol().getElements().remove(node.getElement());
                nodes.remove(node); // TODO nodes needs listener and do this automatically.
                LOGGER.log(Level.SEVERE, "Trashed: {0}", node.toString());
            } else {
                LOGGER.log(Level.SEVERE, "Oops! Trash Failed on {0}", es.toString());
            }
        } else {
            LOGGER.log(Level.SEVERE, "Cen't delete this element! ==> {0}", pick.toString());
        }
    }

    private void initMouseReleased() {
        workArea.setOnMouseReleased((e) -> {

            if (selectionRectangle.isVisible()) {
                // clear selection list
                selectedElements.clear();
                e.consume();

                // Place things inside recangle into selection list.
                parentEditor.getSymbol().getElements().forEach((element) -> {
                    if (element instanceof ElementSelectable es) {
                        switch (es) {
                            case ElementXY exy -> {
                                if (isInsideSelection(exy.getX(), -exy.getY())) {
                                    selectedElements.add(element);
                                    es.setSelected(true);
                                    //LOGGER.log(Level.SEVERE, "ElementXY: {0},{1}", new Object[]{exy.getX(), exy.getY()});
                                } else {
                                    es.setSelected(false);
                                    //LOGGER.log(Level.SEVERE, "ElementXY: {0},{1} not in rect area.", new Object[]{exy.getX(), exy.getY()});
                                }
                            }
                            case ElementDualXY exy -> {
                                boolean p1 = isInsideSelection(exy.getX1(), -exy.getY1());
                                boolean p2 = isInsideSelection(exy.getX2(), -exy.getY2());

                                if (p1 && p2) {
                                    //LOGGER.log(Level.SEVERE, "ElementDualXY BOTH");
                                    exy.setSelectedEnd(WireEnd.BOTH);
                                } else if (p1) {
                                    //LOGGER.log(Level.SEVERE, "ElementDualXY ONE");
                                    exy.setSelectedEnd(WireEnd.ONE);
                                } else if (p2) {
                                    exy.setSelectedEnd(WireEnd.TWO);
                                    //LOGGER.log(Level.SEVERE, "ElementDualXY TWO");
                                } else {
                                    exy.setSelectedEnd(WireEnd.NONE);
                                    //LOGGER.log(Level.SEVERE, "ElementDualXY NONE");
                                }

                                if (exy.getSelectedEnd() != WireEnd.NONE) {
                                    selectedElements.add(element);
                                }
                            }
                            case ElementRectangle er -> {
                                // Check that at least one set of XY points are in selection.
                                double[] p = er.getPoints();
                                if (isInsideSelection(p[0], -p[1])
                                        || isInsideSelection(p[2], -p[3])
                                        || isInsideSelection(p[4], -p[5])
                                        || isInsideSelection(p[6], -p[7])) {
                                    es.setSelected(true);
                                    selectedElements.add(element);
                                    LOGGER.log(Level.SEVERE, "Rectangle selection true.");
                                } else {
                                    es.setSelected(false);
                                }
                            }
                            default -> {
                                LOGGER.log(Level.SEVERE, "Element not evaluated: {0}", element.getElementName());
                            }
                        }
                    }
                });
                selectionRectangle.setVisible(false);
                mouseDownX = Double.MIN_VALUE;
                mouseDownY = Double.MIN_VALUE;
                LOGGER.log(Level.SEVERE, "Selected {0} elements.", selectedElements.size());
            }
        });
    }

    private boolean isInsideSelection(double x, double y) {
        double sX = selectionRectangle.getX();
        double sY = selectionRectangle.getY();
        double sW = selectionRectangle.getWidth();
        double sH = selectionRectangle.getHeight();

        if (selectionRectangle.isVisible()) {
            if ((x > sX && y > sY) && (x < (sX + sW) && y < (sY + sH))) {
                return true;
            }
        }
        return false;
    }

    public void abandonOperation(boolean setToolMode) {
        if (ephemeralNode != null) {
            // TODO: Make this part of listener on 'nodes'.
            ephemeralNode.removeFrom(workArea);
            LOGGER.log(Level.SEVERE, "Remove ephemeral {0} from work area.", ephemeralNode.getElement().getElementName());
            ephemeralNode = null;
        }
        if (!movingElements.isEmpty()) {
            for (Element e : movingElements) {
                if (e instanceof ElementSelectable exy) {
                    exy.restoreSnapshot();
                }
            }
            movingElements.clear(); // End move of node.
            LOGGER.log(Level.SEVERE, "Abandon move.");
        }
        if (setToolMode) {
            parentEditor.setToolMode(EditorTool.SELECT);
        }
    }

    private void buildScene() {
        //scaleText.setText("x" + scale);

        double dash = 2.5;
        double space = 3.0;
        double crossW2 = (5.0 * dash + 4.0 * space) * 0.5;
        hLine = new Line(-crossW2, 0, crossW2, 0);
        hLine.getStrokeDashArray().addAll(dash, space);
        vLine = new Line(0, -crossW2, 0, crossW2);
        vLine.getStrokeDashArray().addAll(dash, space);

        crossHairArea.getChildren().addAll(hLine, vLine);

        rebuildGrid();
//        // Add Grid
//        workArea.getChildren().add(gridLine(0, true));
//        workArea.getChildren().add(gridLine(0, false));
//        for (int n = 1; n <= nGrids; n++) {
//            workArea.getChildren().add(gridLine(n, true));
//            workArea.getChildren().add(gridLine(-n, true));
//            workArea.getChildren().add(gridLine(n, false));
//            workArea.getChildren().add(gridLine(-n, false));
//        }

        Symbol symbol = parentEditor.getSymbol();

        symbol.getElements().forEach((element) -> {
            switch (element) {
                case Pin p -> {
                    PinNode pinNode = new PinNode(p,
                            Color.RED, Color.DARKGREEN, Color.DARKGREY,
                            null, true, this
                    );
                    nodes.add(pinNode);
                    pinNode.addTo(workArea);
                }
                case Wire w -> {
                    WireNode wireNode = new WireNode(w,
                            parentEditor.getDrawing().getLayers(),
                            parentEditor.getDrawing().getPalette(),
                            this);
                    nodes.add(wireNode);
                    wireNode.addTo(workArea);
                }
                case ElementText t -> {
                    TextNode textNode = new TextNode(t, null,
                            parentEditor.getDrawing().getLayers(),
                            parentEditor.getDrawing().getPalette(),
                            null, true,
                            this);
                    nodes.add(textNode);
                    textNode.addTo(workArea);
                }
                case ElementCircle c -> {
                    CircleNode circleNode = new CircleNode(c,
                            parentEditor.getDrawing().getLayers(),
                            parentEditor.getDrawing().getPalette(),
                            this
                    );
                    nodes.add(circleNode);
                    circleNode.addTo(workArea);
                }
                case ElementRectangle c -> {
                    RectangleNode rectNode = new RectangleNode(c,
                            parentEditor.getDrawing().getLayers(),
                            parentEditor.getDrawing().getPalette(),
                            this
                    );
                    nodes.add(rectNode);
                    rectNode.addTo(workArea);
                }
                case ElementPolygon p -> {
                    PolygonNode polyNode = new PolygonNode(p,
                            parentEditor.getDrawing().getLayers(),
                            parentEditor.getDrawing().getPalette(),
                            this);
                    nodes.add(polyNode);
                    polyNode.addTo(workArea);
                }
                default -> {
                }
            }
        });
    }

    public void rebuildGrid() {
        clearGridLines();
        Grid grid = parentEditor.getDrawing().getGrid();
        int multiple = grid.getMultiple();
        double sizeMM = grid.getSizeMM();
        int nGrids = (int) (WA2 / (multiple * sizeMM));

        // Add Grid
        addGridLine(0, true);
        addGridLine(0, false);
        for (int n = 1; n <= nGrids; n++) {
            addGridLine(n, true);
            addGridLine(-n, true);
            addGridLine(n, false);
            addGridLine(-n, false);
        }

        background.toBack();
    }

    private void addGridLine(int pos, boolean horiz) {
        Line gl = gridLine(pos, horiz);
        workArea.getChildren().add(gl);
        gl.toBack();
        gridLines.add(gl);
    }

    private void clearGridLines() {
        for (Line l : gridLines) {
            workArea.getChildren().remove(l);
        }
        gridLines.clear();
    }

    private Line gridLine(int n, boolean horiz) {
        double x1, x2, y1, y2;

        Grid grid = parentEditor.getDrawing().getGrid();
        double gridInMM = grid.getSizeMM();
        int multiple = grid.getMultiple();

        if (horiz) { // H line
            x1 = -WA2;
            x2 = WA2;
            y1 = n * gridInMM * multiple;
            y2 = n * gridInMM * multiple;
        } else {
            x1 = n * gridInMM * multiple;
            x2 = n * gridInMM * multiple;
            y1 = -WA2;
            y2 = WA2;
        }
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(GRID_COLOR);
        l.setStrokeWidth(GRID_STROKE_WIDTH);

        return l;
    }

    private boolean elementsCanRotate(ArrayList<Element> elements) {
        // All Nodes should be Rotatable. break if not.
        boolean canRotate = false;
        for (Element e : elements) {
            if (e instanceof ElementRotation) {
                canRotate = true;
            } else {
                canRotate = false;
                break;
            }
        }

        return canRotate;
    }

    private boolean isOnlyWires(ArrayList<Element> picks) {
        for (Element e : picks) {
            if (!(e instanceof Wire)) {
                return false;
            }
        }
        return true;
    }

    public void setEditorTool(EditorTool tool) {
        if (!toolMode.equals(tool)) {
            // Abandon any current tool operations.
            abandonOperation(false);

            this.toolMode.clearToolElement();
            this.toolMode = tool;
        } else {
            parentEditor.setToolMode(tool);
        }

        LOGGER.log(Level.SEVERE, "setEditorTool: handle tool mode: {0}", this.toolMode.name());
        switch (this.toolMode) {
            case PIN -> {
                // New Pin
                Pin pin = new Pin();
                if (lastElementAdded != null && lastElementAdded instanceof Pin lastP) {
                    pin.setRot(lastP.getRot());
                    pin.setDirection(lastP.getDirection());
                    pin.setLength(lastP.getLength());
                    pin.setFunction(lastP.getFunction());
                    pin.setVisible(lastP.getVisible());
                }
                PinNode pinNode = new PinNode(pin,
                        Color.RED, Color.DARKGREEN, Color.DARKGREY,
                        null, true, this
                );
                //nodes.add(pinNode);
                pinNode.addTo(workArea);
                ephemeralNode = pinNode;
                // add pin to moving elements.
                movingMouseStartX = 0;
                movingMouseStartY = 0;
                movingElements.add(pin);
                this.toolMode.setToolElement(pin);
                parentEditor.setToolMode(toolMode);
            }
            case TEXT -> {
                ElementText text = initiateNewText();
                // TODO: Dialog for new text value.
                TextEditDialog editDialog = new TextEditDialog(text);
                if (editDialog.getResult() == ButtonType.OK) {
                    //text.setValue(String.valueOf((int) (Math.random() * 10000)));
                    LOGGER.log(Level.SEVERE, "    New Text: " + text.getValue());
                    TextNode textNode = new TextNode(text, null,
                            parentEditor.getDrawing().getLayers(),
                            parentEditor.getDrawing().getPalette(),
                            null, true, this);
                    textNode.addTo(workArea);
                    ephemeralNode = textNode;
                    movingMouseStartX = 0;
                    movingMouseStartY = 0;
                    movingElements.add(text);
                    this.toolMode.setToolElement(text);
                } else {
                    parentEditor.setToolMode(SELECT);
                }
            }
            case EditorTool.LINE -> {
                // Line created at first click.
                LOGGER.log(Level.SEVERE, "    New Wire...");
                // Create a placeholder Wire to hold Widget settings that
                // will be used once the user clicks in the workspace.
                if (lastElementAdded == null || !(lastElementAdded instanceof Wire)) {
                    Wire tempWire = new Wire();
                    lastElementAdded = tempWire;
                    LOGGER.log(Level.SEVERE, "Add temp wire. It is the lastAdded.");
                }

                if (ephemeralNode == null) {
                    this.toolMode.setToolElement(lastElementAdded);
                    LOGGER.log(Level.SEVERE, "Set tool element to last added.");
                } else {
                    this.toolMode.setToolElement(ephemeralNode.getElement());
                }
                parentEditor.setToolMode(toolMode);
            }
            case EditorTool.CIRCLE -> {
                // Center X,Y created at first click.
                LOGGER.log(Level.SEVERE, "    New Circle...");
                // Create a placeholder Circle to hold Widget settings that
                // will be used once the user clicks in the workspace.
                if (lastElementAdded == null || !(lastElementAdded instanceof ElementCircle)) {
                    ElementCircle tempCircle = new ElementCircle();
                    lastElementAdded = tempCircle;
                    LOGGER.log(Level.SEVERE, "Add temp circle. It is the lastAdded.");
                }

                if (ephemeralNode == null) {
                    this.toolMode.setToolElement(lastElementAdded);
                    LOGGER.log(Level.SEVERE, "Set tool element to last added.");
                } else {
                    this.toolMode.setToolElement(ephemeralNode.getElement());
                }
                parentEditor.setToolMode(toolMode);
            }
            case EditorTool.RECTANGLE -> {
                // Center X,Y created at first click.
                LOGGER.log(Level.SEVERE, "    New Rectangle...");
                // Create a placeholder Recangle to hold Widget settings that
                // will be used once the user clicks in the workspace.
                if (lastElementAdded == null || !(lastElementAdded instanceof ElementCircle)) {
                    ElementRectangle tempRect = new ElementRectangle();
                    lastElementAdded = tempRect;
                    LOGGER.log(Level.SEVERE, "Add temp rectangle. It is the lastAdded.");
                }

                if (ephemeralNode == null) {
                    this.toolMode.setToolElement(lastElementAdded);
                    LOGGER.log(Level.SEVERE, "Set tool element to last added.");
                } else {
                    this.toolMode.setToolElement(ephemeralNode.getElement());
                }
                parentEditor.setToolMode(toolMode);
            }
            case EditorTool.POLYGON -> {
                // Line created at first click.
                LOGGER.log(Level.SEVERE, "    Editor Tool Polygon");
                // Create a placeholder Wire to hold Widget settings that
                // will be used once the user clicks in the workspace.
                if (lastElementAdded == null || !(lastElementAdded instanceof ElementPolygon)) {
                    ElementPolygon tempPoly = new ElementPolygon();
                    lastElementAdded = tempPoly;
                    LOGGER.log(Level.SEVERE, "Add temp polygon. It is the lastAdded.");
                }

                if (ephemeralNode == null) {
                    this.toolMode.setToolElement(lastElementAdded);
                    LOGGER.log(Level.SEVERE, "Assign lastAdded to the toolElement.");
                } else {
                    LOGGER.log(Level.SEVERE, "Assign ephemeral node element to the toolElement.");
                    this.toolMode.setToolElement(ephemeralNode.getElement());
                }
                parentEditor.setToolMode(toolMode);
            }
            case EditorTool.MOVE -> {
                LOGGER.log(Level.SEVERE, "    Handle 'Move' EditorTool...");
                if (lastElementAdded == null || !(lastElementAdded instanceof ElementRotation)) {
                    ElementText tempText = new ElementText();
                    lastElementAdded = tempText;
                    LOGGER.log(Level.SEVERE, "Add temp text(moveable/rotatable). It is the lastElementAdded.");
                }

                if (ephemeralNode == null) {
                    this.toolMode.setToolElement(lastElementAdded);
                    LOGGER.log(Level.SEVERE, "Set tool element to last added.");
                } else {
                    this.toolMode.setToolElement(ephemeralNode.getElement());
                }
                parentEditor.setToolMode(toolMode);
            }
            case EditorTool.ROTATE -> {
                LOGGER.log(Level.SEVERE, "    Handle 'Rotate' EditorTool...");
                ElementText e = new ElementText();
                e.setRot(90.0);
                lastElementAdded = e; // Temp item for basis of rotations.
                this.toolMode.setToolElement(lastElementAdded);
                parentEditor.setToolMode(toolMode);
            }
            case EditorTool.MIRROR -> {
                LOGGER.log(Level.SEVERE, "    Handle 'Mirror' EditorTool...");
                ElementText e = new ElementText();
                e.setMirror(true);
                lastElementAdded = e; // Temp item for basis of rotations.
                this.toolMode.setToolElement(lastElementAdded);
                parentEditor.setToolMode(toolMode);
            }
        }

    }

    private ViewNode getNode(ElementSelectable e) {
        for (ViewNode vn : nodes) {
            if (vn.getElement().equals(e)) {
                return vn;
            }
        }
        return null;
    }

    @Override
    public void nodePicked(ViewNode node, MouseEvent me) {

        // TODO:  No more node picking.
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        if (e instanceof Grid g) {
            rebuildGrid();
        }
    }
}
