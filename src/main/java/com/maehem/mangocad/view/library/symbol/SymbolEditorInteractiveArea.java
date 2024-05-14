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
import com.maehem.mangocad.model.ElementRotation;
import com.maehem.mangocad.model.ElementXY;
import com.maehem.mangocad.model.element.basic.ElementCircle;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.enums.WireEnd;
import static com.maehem.mangocad.model.element.enums.WireEnd.ONE;
import static com.maehem.mangocad.model.element.enums.WireEnd.TWO;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.view.PickListener;
import com.maehem.mangocad.view.node.CircleNode;
import com.maehem.mangocad.view.node.PinNode;
import com.maehem.mangocad.view.node.TextNode;
import com.maehem.mangocad.view.node.ViewNode;
import com.maehem.mangocad.view.node.WireNode;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import static javafx.scene.input.KeyCode.ESCAPE;
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
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SymbolEditorInteractiveArea extends ScrollPane implements PickListener {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    private static final double SCALE_MAX = 40.0;
    private static final double SCALE_MIN = 5.0;
    private static final double SCALE_FACTOR = 0.5;
    private static final int WORK_AREA = (int) (1000.0 / SCALE_MIN);
    private static final double WA2 = WORK_AREA / 2.0;
    private static final double GRID_SIZE = 2.54;
    private static final Color GRID_COLOR = new Color(1.0, 1.0, 1.0, 0.1);
    private static final double GRID_STROKE_WIDTH = 0.05;
    private static final double PICK_SIZE = 1.0;

    private final Circle shadow = new Circle(1, Color.RED);
    private final Text scaleText = new Text("x1.0");
    private final Group workArea = new Group(shadow, scaleText);
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
    private double mouseDownX;
    private double mouseDownY;

    // TODO get from control panel settings.
    private final Color selectionRectangleColor = new Color(1.0, 1.0, 1.0, 0.5);

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

        workScale.setX(scale);
        workScale.setY(scale);

        shadow.setVisible(false);

        scaleText.setLayoutX(10);
        scaleText.setLayoutY(10);
        scaleText.setScaleX(0.5);
        scaleText.setScaleY(0.5);

        selectionRectangle.setFill(null);
        selectionRectangle.setStroke(selectionRectangleColor);
        selectionRectangle.setStrokeType(StrokeType.CENTERED);
        selectionRectangle.setStrokeWidth(0.05);
        ObservableList<Double> strokeDashArray = selectionRectangle.getStrokeDashArray();
        strokeDashArray.addAll(0.5, 0.5);
        workArea.getChildren().add(selectionRectangle);

        workArea.getTransforms().add(workScale);

//        addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
//            double scaleOld = scale;
//            double scrollAmt = event.getDeltaY();
//            scale += scrollAmt * SCALE_FACTOR;
//            if (scale > SCALE_MAX) {
//                scale = SCALE_MAX;
//            }
//            if (scale < SCALE_MIN) {
//                scale = SCALE_MIN;
//            }
//            workScale.setX(scale);
//            workScale.setY(scale);
//            scaleText.setText("x" + String.format("%.2f", scale));
//
//            double mX = event.getX();
//            double mY = event.getY();
//
//            double vaW = getBoundsInLocal().getWidth();
//            double vaH = getBoundsInLocal().getHeight();
//
//            double sbHV = getHvalue();
//            double sbVV = getVvalue();
//
//            double waX = (mX - (vaW * sbHV)) / scaleOld;
//            waX += (sbHV * 2 - 1) * (WA2);
//
//            double waY = (mY - (vaH * sbVV)) / scaleOld;
//            waY += (sbVV * 2 - 1) * (WA2);
//
//            // Derive the sb values after the scale.
//            double sbX = 0.5 + (waX + (mX - vaW / 2.0) / scale) / WORK_AREA;
//            double sbY = 0.5 + (waY + (mY - vaH / 2.0) / scale) / WORK_AREA;
//
//            setHvalue(sbX);
//            setVvalue(sbY);
//
//            event.consume();
//        });
        workArea.addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
            double scaleOld = scale;
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
            scaleText.setText("x" + String.format("%.2f", scale));

            double mX = event.getX();
            double mY = event.getY();

            //double vaW = getBoundsInLocal().getWidth();
            //double vaH = getBoundsInLocal().getHeight();
//
//            double sbHV = getHvalue();
//            double sbVV = getVvalue();
            //double waX = mX;
            //waX += (sbHV * 2 - 1) * (WA2);
            //double waY = mY;
            //waY += (sbVV * 2 - 1) * (WA2);
            // Derive the sb values after the scale.
            double sbX = 0.5 + mX / WA2 / scale / 2;// + (waX + (mX - vaW / 2.0) / scale) / WORK_AREA;
            double sbY = 0.5 + mY / WA2 / scale / 2;// + (waY + (mY - vaH / 2.0) / scale) / WORK_AREA;

            setHvalue(sbX);
            setVvalue(sbY);

            event.consume();
        });

        setOnKeyPressed((ke) -> {
            if (!movingElements.isEmpty() && ke.getCode() == ESCAPE) {
                for (Element e : movingElements) {
                    if (e instanceof ElementXY exy) {
                        exy.restoreSnapshot();
                    } else if (e instanceof ElementDualXY exy) {
                        exy.restoreSnapshot();
                    }
                }
                movingElements.clear(); // End move of node.
                LOGGER.log(Level.SEVERE, "Abandon move.");
                ke.consume();
            }
        });
        workArea.setOnMouseMoved((me) -> {
            // Move any selected node.
            if (!movingElements.isEmpty()) {
                //LOGGER.log(Level.SEVERE, "Work Area: mXY: {0},{1}", new Object[]{me.getX(), me.getY()});
                //LOGGER.log(Level.SEVERE, "Moving the things around.");
                // TODO: Is 'option' key held down? then use altGrid.
                double snap = parentEditor.getDrawing().getGrid().getSizeMM();

                double xxx = (int) (me.getX() / snap) * snap; // Snap to grid
                double yyy = (int) (me.getY() / snap) * snap; // Snap to grid

//                LOGGER.log(Level.SEVERE, "     moving: {0},{1}", new Object[]{movingOriginX, movingOriginY});
                //LOGGER.log(Level.SEVERE, "    xxx/yyy: {0},{1}", new Object[]{xxx, yyy});

                for (Element e : movingElements) {
                    switch (e) {
                        case ElementXY ee -> {
                            double[] snapshot = ee.getSnapshot();
                            ee.setX(xxx + snapshot[0] % snap);
                            ee.setY(-(yyy + snapshot[1] % snap));
                        }
                        case ElementDualXY ee -> {
                            double[] snapshot = ee.getSnapshot();
                            switch (ee.getSelectedEnd()) {
                                case ONE -> {
                                    ee.setX1(xxx + snapshot[0] % snap);
                                    ee.setY1(-(yyy + snapshot[1] % snap));
                                }
                                case TWO -> {
                                    ee.setX2(xxx + snapshot[2] % snap);
                                    ee.setY2(-(yyy + snapshot[3] % snap));
                                }
                                default -> {
                                }
                            }
                        }
                        default -> {
                        }
                    }
                }
            }
        });

        // Toggle CrossHair
        setOnMouseEntered((t) -> {
            getScene().setCursor(Cursor.CROSSHAIR); //Change cursor to crosshair
        });
        setOnMouseExited((t) -> {
            getScene().setCursor(Cursor.DEFAULT);
        });

        workArea.setOnMouseClicked((me) -> {
            if (!movingElements.isEmpty()) {
                return;
            }

            LOGGER.log(Level.SEVERE, "Editor Work Area Clicked: {0}", me.getButton().name());
            //LOGGER.log(Level.SEVERE, "Mouse: {0},{1}  scene: {2},{3}", new Object[]{me.getX(), -me.getY(), me.getSceneX(), -me.getSceneY()});
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
                    default -> {
                    }
                }
            });
            LOGGER.log(Level.SEVERE, "Pick count: " + picks.size());

//            if (picks.isEmpty()) {
//                contextMenu.hide();
//                return;
//            }
            // Remember where we started.
//            movingOriginX = me.getX();
//            movingOriginY = me.getY();
//            LOGGER.log(Level.SEVERE, "Changed movingOrigin.");
            switch (me.getButton()) {
                case PRIMARY -> { // Move or choose what to move.
                    // If one pick, pick it.
                    if (picks.isEmpty()) {
                        contextMenu.hide();
                        me.consume();
                        return;
                    } else if (picks.size() == 1) {  // TODO: movingNodes.isEmpty() not needed.
                        Element pick = picks.getFirst();
                        movingElements.add(pick);
                        if (pick instanceof ElementXY exy) {
                            exy.createSnapshot();
                        }

                        LOGGER.log(Level.SEVERE, "Moving a thing.");
                        me.consume();
                        return;
                    } else if (isOnlyWires(picks)) { // Wires converge and nothing else there.
                        // If more than one pick,
                        // If all are wires, select them.
                        // Add all picks to moving list.
                        for (Element e : picks) {
                            movingElements.add(e);
                            if (e instanceof ElementDualXY exy) {
                                exy.createSnapshot();
                            }
                        }
                        LOGGER.log(Level.SEVERE, "Moving some wires.");
                        me.consume();
                    } else {
                        // otherwise,  highlight first (grey out rest) and wait for either
                        // another click or right-click to highlight next item.
                        LOGGER.log(Level.SEVERE, "Mixed items. need to choose item.");
                        me.consume();
                    }
                }
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
                }
            }
        });
        setOnMouseClicked((me) -> {
            LOGGER.log(Level.SEVERE, "Editor Window Area Clicked: " + me.getButton().name());
            if (!movingElements.isEmpty()) {
                if (null != me.getButton()) {
                    switch (me.getButton()) {
                        case PRIMARY -> {
                            movingElements.clear(); // End move of node.
                            LOGGER.log(Level.SEVERE, "End of move.");
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
            }
        });
        workArea.setOnMousePressed(e -> {
            LOGGER.log(Level.SEVERE, "Begin Selection.");
            mouseDownX = e.getX();
            mouseDownY = e.getY();
            selectionRectangle.setVisible(true);
            selectionRectangle.setX(mouseDownX);
            selectionRectangle.setY(mouseDownY);
            selectionRectangle.setWidth(0);
            selectionRectangle.setHeight(0);
        });

        workArea.setOnMouseDragged(e -> {
            selectionRectangle.setX(Math.min(e.getX(), mouseDownX));
            selectionRectangle.setWidth(Math.abs(e.getX() - mouseDownX));
            selectionRectangle.setY(Math.min(e.getY(), mouseDownY));
            selectionRectangle.setHeight(Math.abs(e.getY() - mouseDownY));
        });
        workArea.setOnMouseReleased((e) -> {

            // clear selection list
            selectedElements.clear();
            e.consume();

//            LOGGER.log(Level.SEVERE, "Selection Rectangle: {0},{1}  ({2}x{3})",
//                    new Object[]{
//                        selectionRectangle.getX(), selectionRectangle.getY(),
//                        selectionRectangle.getWidth(), selectionRectangle.getHeight()
//                    });
            // Place things inside recangle into selection list.
            parentEditor.getSymbol().getElements().forEach((element) -> {
                switch (element) {
                    case ElementXY exy -> {
                        if (isInsideSelection(exy.getX(), -exy.getY())) {
                            selectedElements.add(element);
                            exy.setSelected(true);
                            //LOGGER.log(Level.SEVERE, "ElementXY: {0},{1}", new Object[]{exy.getX(), exy.getY()});
                        } else {
                            exy.setSelected(false);
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
                    default -> {
                        LOGGER.log(Level.SEVERE, "Element not evaluated: " + element.getElementName());
                    }
                }
            });
            selectionRectangle.setVisible(false);
            LOGGER.log(Level.SEVERE, "Selected {0} elements.", selectedElements.size());
        });
    }

    private boolean isInsideSelection(double x, double y) {
        double sX = selectionRectangle.getX();
        double sY = selectionRectangle.getY();
        double sW = selectionRectangle.getWidth();
        double sH = selectionRectangle.getHeight();

        //LOGGER.log(Level.SEVERE, "     X Range: {0} .. {1}   <=== x: {2}", new Object[]{sX, sX + sW, x});
        //LOGGER.log(Level.SEVERE, "     Y Range: {0} .. {1}   <=== y: {2}", new Object[]{sY, sY + sH, y});

        if (selectionRectangle.isVisible()) {
            if ((x > sX && y > sY) && (x < (sX + sW) && y < (sY + sH))) {
                return true;
            }
        }
        return false;
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

        Rectangle background = new Rectangle(-WA2, -WA2, WORK_AREA, WORK_AREA);
        // TODO: Get color from control panel settings.
        background.setFill(new Color(0.2, 0.2, 0.2, 1.0));
        workArea.getChildren().add(background);

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

        Symbol symbol = parentEditor.getSymbol();

        symbol.getElements().forEach((element) -> {
            if (element instanceof Pin p) {
                PinNode pinNode = new PinNode(p,
                        Color.RED, Color.DARKGREEN, Color.DARKGREY,
                        null, true, this
                );
                nodes.add(pinNode);
                pinNode.addTo(workArea);
            } else if (element instanceof Wire w) {
                WireNode wireNode = new WireNode(w,
                        parentEditor.getDrawing().getLayers(),
                        parentEditor.getDrawing().getPalette(),
                        this);
                nodes.add(wireNode);
                wireNode.addTo(workArea);
            } else if (element instanceof ElementText t) {
                TextNode textNode = new TextNode(t, null,
                        parentEditor.getDrawing().getLayers(),
                        parentEditor.getDrawing().getPalette(),
                        null, true,
                        this);
                nodes.add(textNode);
                textNode.addTo(workArea);
            } else if (element instanceof ElementCircle c) {
                CircleNode circleNode = new CircleNode(c,
                        parentEditor.getDrawing().getLayers(),
                        parentEditor.getDrawing().getPalette(),
                        this
                );
                nodes.add(circleNode);
                circleNode.addTo(workArea);
            }
        });
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

    private ViewNode findViewNode(Element e) {
        for (ViewNode vn : nodes) {
            if (vn.getElement().equals(e)) {
                return vn;
            }
        }
        return null;
    }

    private boolean isOnlyWires(ArrayList<Element> picks) {
        for (Element e : picks) {
            if (!(e instanceof Wire)) {
                return false;
            }
        }
        return true;
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

    @Override
    public void nodePicked(ViewNode node, MouseEvent me) {

        // TODO:  No more node picking.
//        //LOGGER.log(Level.SEVERE, "Event: " + me.getEventType().getName());
//        if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
//            if (movingNodes == null) {
//                // Pick
//                if (PICK_SIZE > Math.abs(me.getX()) && PICK_SIZE > Math.abs(me.getY())) {
//                    movingNodes = node;
//                    LOGGER.log(Level.SEVERE, "Moving Node: {0}", movingNodes.getElement().getElementName());
//                    me.consume();
//                }
//            }
//        }
    }
}
