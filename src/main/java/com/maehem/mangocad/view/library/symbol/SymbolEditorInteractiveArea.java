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
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
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
    private static final double SCALE_FACTOR = 1.0;
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
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem nextItem = new MenuItem("Next");
    private final MenuItem nextSeparatorItem = new SeparatorMenuItem();
    private final MenuItem copyItem = new MenuItem("Copy");
    private final MenuItem deleteItem = new MenuItem("Delete");
    private final MenuItem mirrorItem = new MenuItem("Mirror");
    private final MenuItem moveItem = new MenuItem("Move");
    private final MenuItem nameItem = new MenuItem("Name");
    private final MenuItem rotateItem = new MenuItem("Rotate");
    private final MenuItem showItem = new MenuItem("Show");
    private final MenuItem sep1Item = new SeparatorMenuItem();
    private final MenuItem moveGroupItem = new MenuItem("Move Group");
    private final MenuItem sep2Item = new SeparatorMenuItem();
    private final MenuItem propertiesItem = new MenuItem("Properties");
    private final LibrarySymbolSubEditor parentEditor;
    private double scale = 10.0;
    private Line hLine;
    private Line vLine;
    private Scale workScale = new Scale();
    private final ArrayList<Element> movingNodes = new ArrayList<>();
//    private double movingOriginX = 0;
//    private double movingOriginY = 0;
    private final ArrayList<ViewNode> nodes = new ArrayList<>();

    public SymbolEditorInteractiveArea(LibrarySymbolSubEditor parentEditor) {
        this.parentEditor = parentEditor;

        contextMenu.getItems().addAll(
                nextItem, nextSeparatorItem,
                copyItem, deleteItem, mirrorItem,
                moveItem, nameItem, rotateItem,
                showItem, sep1Item, moveGroupItem,
                sep2Item, propertiesItem
        );
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

        shadow.setVisible(false);

        scaleText.setLayoutX(10);
        scaleText.setLayoutY(10);
        scaleText.setScaleX(0.5);
        scaleText.setScaleY(0.5);

        workArea.getTransforms().add(workScale);

        addEventFilter(ScrollEvent.ANY, (ScrollEvent event) -> {
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

            double vaW = getBoundsInLocal().getWidth();
            double vaH = getBoundsInLocal().getHeight();

            double sbHV = getHvalue();
            double sbVV = getVvalue();

            double waX = (mX - (vaW * sbHV)) / scaleOld;
            waX += (sbHV * 2 - 1) * (WA2);

            double waY = (mY - (vaH * sbVV)) / scaleOld;
            waY += (sbVV * 2 - 1) * (WA2);

            // Derive the sb values after the scale.
            double sbX = 0.5 + (waX + (mX - vaW / 2.0) / scale) / WORK_AREA;
            double sbY = 0.5 + (waY + (mY - vaH / 2.0) / scale) / WORK_AREA;

            setHvalue(sbX);
            setVvalue(sbY);

            event.consume();
        });
        addEventFilter(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
//            double mX = me.getX();
//            double mY = me.getY();
//
//            double vaW = getBoundsInLocal().getWidth();
//            double vaH = getBoundsInLocal().getHeight();
//
//            double sbHV = getHvalue();
//            double sbVV = getVvalue();

//            double waX = (mX - (vaW * sbHV)) / scale;
//            waX += (sbHV * 2 - 1) * (WA2);
//
//            double waY = (mY - (vaH * sbVV)) / scale;
//            waY += (sbVV * 2 - 1) * (WA2);
//            LOGGER.log(Level.SEVERE, "Main Area: mXY: {0}{1}   waXY: {2},{3}", new Object[]{mX, mY, waX, waY});
//            if (waX > -WA2 && waX < WA2) {
//                shadow.setLayoutX(waX);
//
//            }
//            if (waY > -WA2 && waY < WA2) {
//                shadow.setLayoutY(waY);
//            }
//            // Move any selected node.
//            if (!movingNodes.isEmpty()) {
//                LOGGER.log(Level.SEVERE, "Moving the things around.");
//                // TODO: Is 'option' key held down? then use altGrid.
//                double snap = parentEditor.getDrawing().getGrid().getSizeMM();
//                //double xGrids = (int) ((movingOriginX - waX) / snap);
//                //double yGrids = (int) ((movingOriginY - waY) / snap);
//
//                double xxx = (int) (waX / snap) * snap; // Snap to grid
//                double yyy = (int) (waY / snap) * snap; // Snap to grid
//
//                LOGGER.log(Level.SEVERE, " moving: {0},{1}", new Object[]{movingOriginX, movingOriginY});
//                LOGGER.log(Level.SEVERE, "xxx/yyy: {0},{1}", new Object[]{xxx, yyy});
//
//
//                for (Element e : movingNodes) {
//                    if (e instanceof ElementXY ee) {
//                        ee.setX(movingOriginX + xxx);
//                        ee.setY(-(movingOriginY + yyy));
//                    } else if (e instanceof ElementDualXY ee) {
//                        switch (ee.getSelectedEnd()) {
//                            case ONE -> {
//                                ee.setX1(movingOriginX + xxx);
//                                ee.setY1(-(movingOriginY + yyy));
//                            }
//                            case TWO -> {
//                                ee.setX2(movingOriginX + xxx);
//                                ee.setY2(-(movingOriginY + yyy));
//                            }
//                            default -> {
//                            }
//                        }
//                    }
//                }
//            }
        });

        setOnKeyPressed((ke) -> {
            if (!movingNodes.isEmpty() && ke.getCode() == ESCAPE) {
                for (Element e : movingNodes) {
                    if (e instanceof ElementXY exy) {
                        exy.restoreSnapshot();
                    } else if (e instanceof ElementDualXY exy) {
                        exy.restoreSnapshot();
                    }
                }
                movingNodes.clear(); // End move of node.
                LOGGER.log(Level.SEVERE, "Abandon move.");
                ke.consume();
            }
        });
        workArea.setOnMouseMoved((me) -> {
            // Move any selected node.
            if (!movingNodes.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Work Area: mXY: {0},{1}", new Object[]{me.getX(), me.getY()});
                //LOGGER.log(Level.SEVERE, "Moving the things around.");
                // TODO: Is 'option' key held down? then use altGrid.
                double snap = parentEditor.getDrawing().getGrid().getSizeMM();
                //double xGrids = (int) ((movingOriginX - waX) / snap);
                //double yGrids = (int) ((movingOriginY - waY) / snap);

                double xxx = (int) (me.getX() / snap) * snap; // Snap to grid
                double yyy = (int) (me.getY() / snap) * snap; // Snap to grid
//                double partialX = (movingOriginX / snap) * snap;
//                double partialY = (movingOriginY / snap) * snap;
//
//                double gridsX = (int) ((movingOriginX - me.getX()) / snap) * snap;
//                double gridsY = (int) ((movingOriginY - me.getY()) / snap) * snap;

//                LOGGER.log(Level.SEVERE, "     moving: {0},{1}", new Object[]{movingOriginX, movingOriginY});
                LOGGER.log(Level.SEVERE, "    xxx/yyy: {0},{1}", new Object[]{xxx, yyy});

                for (Element e : movingNodes) {
                    if (e instanceof ElementXY ee) {
                        double[] snapshot = ee.getSnapshot();
                        ee.setX(xxx + snapshot[0] % snap);
                        ee.setY(-(yyy + snapshot[1] % snap));
                    } else if (e instanceof ElementDualXY ee) {
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
            if (!movingNodes.isEmpty()) {
                return;
            }

            LOGGER.log(Level.SEVERE, "Editor Work Area Clicked: " + me.getButton().name());
            //LOGGER.log(Level.SEVERE, "Mouse: {0},{1}  scene: {2},{3}", new Object[]{me.getX(), -me.getY(), me.getSceneX(), -me.getSceneY()});
            ArrayList<Element> picks = new ArrayList<>();
            parentEditor.getSymbol().getElements().forEach((e) -> {
                if (e instanceof ElementXY ee) {
                    if (Math.abs(me.getX() - ee.getX()) < PICK_SIZE
                            && Math.abs(-me.getY() - ee.getY()) < PICK_SIZE) {
                        picks.add(e);
                    }
                } else if (e instanceof ElementDualXY ee) {
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
                        movingNodes.add(pick);
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
                            movingNodes.add(e);
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
                    moveGroupItem.setDisable(true);

                    // Always there.
                    moveGroupItem.setVisible(true);

                    if (!picks.isEmpty()) {
                        copyItem.setVisible(true);
                        deleteItem.setVisible(true);
                        mirrorItem.setVisible(true);
                        moveItem.setVisible(true);
                        nameItem.setVisible(true);
                        rotateItem.setVisible(true);
                        showItem.setVisible(true);
                        sep1Item.setVisible(true);
                        sep2Item.setVisible(true);
                        propertiesItem.setVisible(true);
                    }

                    if (picks.size() > 1) {
                        // TODO: Highlight first item and present options menu with "next" option at top.
                        nextItem.setVisible(true);
                        nextSeparatorItem.setVisible(true);
                    }

                    contextMenu.show(workArea, me.getScreenX(), me.getScreenY());
                }
            }
        });
        setOnMouseClicked((me) -> {
            LOGGER.log(Level.SEVERE, "Editor Window Area Clicked: " + me.getButton().name());
            if (!movingNodes.isEmpty()) {
                if (null != me.getButton()) {
                    switch (me.getButton()) {
                        case PRIMARY -> {
                            movingNodes.clear(); // End move of node.
                            LOGGER.log(Level.SEVERE, "End of move.");
                            me.consume();
                        }
                        case MIDDLE -> { // Mirror
                            // All Nodes should be Rotatable. break if not.
                            if (elementsCanRotate(movingNodes)) {
                                for (Element e : movingNodes) {
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
                            if (elementsCanRotate(movingNodes)) {
                                for (Element e : movingNodes) {
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

    private ViewNode findElement(Element e) {
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
