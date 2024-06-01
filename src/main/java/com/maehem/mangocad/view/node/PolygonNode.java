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
import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.ElementListener;
import com.maehem.mangocad.model.element.basic.ElementPolygon;
import com.maehem.mangocad.model.element.basic.Vertex;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.enums.ElementPolygonField;
import com.maehem.mangocad.model.element.enums.RotationField;
import com.maehem.mangocad.model.element.enums.VertexField;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.PickListener;
import static com.maehem.mangocad.view.library.LibraryElementNode.distance;
import java.util.List;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PolygonNode extends ViewNode implements ElementListener {

    private final ElementPolygon polygonElement;
    private final Layers layers;
    private final ColorPalette palette;
    private final Path path = new Path();
    private boolean closePath = false;

    public PolygonNode(ElementPolygon er, Layers layers, ColorPalette palette, PickListener pickListener) {
        super(er, pickListener);

        this.polygonElement = er;
        this.layers = layers;
        this.palette = palette;

        path.setStrokeLineJoin(StrokeLineJoin.ROUND);
        path.setStrokeType(StrokeType.CENTERED);
        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setStrokeWidth(0.254);
        path.setStroke(Color.GREEN);
        path.setSmooth(true);
        path.setFill(Color.TRANSPARENT);

        add(path);

        updateLayer();
        updateWidth();
        rebuildPath();

        if (!er.getVertices().isEmpty()) {
            Platform.runLater(() -> {
                for (Vertex v : polygonElement.getVertices()) {
                    v.addListener(this);
                }
            });

        }
        Platform.runLater(() -> {
            polygonElement.addListener(this);
        });
    }

    public void rebuildPath() {
        LOGGER.log(Level.SEVERE, "Rebuild path.");
        path.getElements().clear();

        double lastX = 0.0;
        double lastY = 0.0;
        double arc = 0.0;
        boolean isFirst = true;
        for (Vertex v : polygonElement.getVertices()) {
            if (isFirst) {
                lastX = v.getX();
                lastY = v.getY();
                arc = v.getCurve(); // Arc/Curve is applied to (n+1) element.
                isFirst = false;
                MoveTo mt = new MoveTo(lastX, -lastY);
                path.getElements().add(mt);
                continue;
            }

            double x1 = v.getX();
            double y1 = v.getY();

            addPathEdge(path, arc, lastX, lastY, x1, y1);
            arc = v.getCurve(); // Apply to next leg.
            lastX = x1;
            lastY = y1;
        }

        if (closePath) {
            path.getElements().add(new ClosePath());
        }
        // Close the path using the last curve.
//        addPathEdge(path, arc, lastX, lastY, polygonElement.getVertices().get(0).getX(), polygonElement.getVertices().get(0).getY());
    }

    /**
     * Sync Vertex data with Path segments.
     */
    private void updateVerticesXY() {
//        if (polygonElement.getVertices().size() != path.getElements().size()) {
//            LOGGER.log(Level.SEVERE, "PolygonNode:  Vertices and Path elements are out of sync. But how? Rebuilding.");
//            rebuildVertices();
//        } else {
        List<Vertex> vertices = polygonElement.getVertices();
        ObservableList<PathElement> pathElements = path.getElements();

        // TODO: Bind Path elements to property of Vector
        for (int i = 0; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);
            PathElement pe = pathElements.get(i);
            if (pe instanceof MoveTo mt) {
                mt.setX(v.getX());
                mt.setY(-v.getY());
            } else if (pe instanceof ArcTo mt) {
                mt.setX(v.getX());
                mt.setY(-v.getY());
            } else if (pe instanceof LineTo mt) {
                mt.setX(v.getX());
                mt.setY(-v.getY());
            } else {
                LOGGER.log(Level.SEVERE, "PolygonNode: Path element was not a MoveTo! But how?");
            }
        }
//        }
    }

    public void setClosePath(boolean closePath) {
        this.closePath = closePath;
    }

    private void updateWidth() {
        path.setStrokeWidth(polygonElement.getWidth());
    }

    private void updateLayer() {
        LayerElement layer = layers.get(polygonElement.getLayerNum());
        Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));

        path.setStroke(polygonElement.isSelected() || polygonElement.hasSelections() ? c.brighter().brighter() : c);
        if (closePath) {
            path.setFill(c);
        } else {
            path.setFill(Color.TRANSPARENT);
        }
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

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        LOGGER.log(Level.SEVERE,
                "Polygon properties have changed! {0}: {1} => {2}",
                new Object[]{field, oldVal != null ? oldVal.toString() : "null", newVal != null ? newVal.toString() : "null"});

        if (field instanceof ElementPolygonField erf) {
            switch (erf) {
                case ElementPolygonField.WIDTH -> {
                    updateWidth();
                }
                case ElementPolygonField.LAYER -> {
                    updateLayer();
                }
                case ElementPolygonField.VERTEX -> {
                    LOGGER.log(Level.SEVERE, "Polygon Vertex has changed.");
                    if ((oldVal == null && newVal != null) || (oldVal != null && newVal == null)) {
                        LOGGER.log(Level.SEVERE, "    Something was added or removed. Rebuild path.");
                        // Vertex added or removed.
                        rebuildPath();
                    }
                }
            }
        }
        if (field instanceof RotationField rf) {
            rebuildPath();
        }
        if (field instanceof VertexField vf) {
            switch (vf) {
                case SELECTED -> {
                    updateLayer();
                }
                default -> {
                    LOGGER.log(Level.SEVERE, "    Vertex field has changed. f:" + vf.name());
//                    updateVertices();
                    updateVerticesXY();
                }
            }

        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PolygonNode with " + polygonElement.getVertices().size() + " Vertices:\n          ");
        for (Vertex v : polygonElement.getVertices()) {
            sb.append(v.getX() + "," + v.getY() + "crv(" + v.getCurve() + ")");
            sb.append("   ");
        }

        return sb.toString();
    }
}
