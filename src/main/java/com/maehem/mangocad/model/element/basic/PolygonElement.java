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
 /*
 * TODO: Move advanced params to SignalPolygon (isolate,orphans,thermals,rank)
 */
package com.maehem.mangocad.model.element.basic;

import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.ElementListener;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.enums.PolygonPour;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LayerNumberValue;
import com.maehem.mangocad.model.element.property.LockProperty;
import com.maehem.mangocad.model.element.property.LockValue;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.WidthProperty;
import com.maehem.mangocad.model.element.property.WidthValue;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 * ELEMENT polygon
 * Sub-elements:  vertex
 *          The vertices must define a valid polygon; if the last vertex
 *          is the same as the first one, it is ignored.
 * Attribute List
 *          width         %Dimension;    #REQUIRED
 *          layer         %Layer;        #REQUIRED
 *          spacing       %Dimension;    #IMPLIED
 *          pour          %PolygonPour;  "solid"
 *          isolate       %Dimension;    #IMPLIED
 *          orphans       %Bool;         "no"
 *          thermals      %Bool;         "yes"
 *          rank          %Int;          "0"
 *          grouprefs     IDREFS         #IMPLIED
 *
 *      isolate: Only in <signal> or <package> context
 *      orphans: Only in <signal> context
 *      thermals:Only in <signal> context
 *      rank: 1..6 in <signal> context, 0 or 7 in <package> context.
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class PolygonElement extends Element implements
        LayerNumberProperty, ElementListener, SelectableProperty,
        WidthProperty, LockProperty,
        GrouprefsProperty, ElementValueListener {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");
    public static final String ELEMENT_NAME = "polygon";

    public enum Field {
        //WIDTH("width", Double.class),
        //LAYER("layer", Integer.class),
        //SELECTED("selected", Boolean.class),
        POUR("pour", PolygonPour.class),
        SPACING("spacing", Double.class),
        ISOLATE("isolate", Double.class),
        ORPHANS("orphans", Boolean.class),
        THERMAL("thermal", Boolean.class),
        RANK("rank", Integer.class),
        VERTEX("vertex", Vertex.class);

        private final String fName;
        private final Class clazz;

        private Field(String name, Class clazz) {
            this.fName = name;
            this.clazz = clazz;
        }

        public String fName() {
            return fName;
        }

        public Class clazz() {
            return clazz;
        }

    }

    //private int layer;
    public final LayerNumberValue layerValue = new LayerNumberValue(98);

    public final LockValue lockProperty = new LockValue();
    public final WidthValue widthProperty = new WidthValue();

    private PolygonPour pour = PolygonPour.SOLID;
    private double spacing = 1.27;
    private double isolate;  // Not in symbol, footprint?
    private boolean orphans = false;
    private boolean thermals = true;
    private int rank = 1;
    public final List<Vertex> vertices = new ArrayList<>();
    private final ArrayList<String> grouprefs = new ArrayList<>();

    // Ephemaral data
    private PolygonElement snapshot = null;
    private boolean selected = false;
    private boolean picked = false;

    public PolygonElement() {

        layerValue.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public LayerNumberValue getLayerNumberProperty() {
        return layerValue;
    }

    @Override
    public WidthValue getWidthProperty() {
        return widthProperty;
    }

    @Override
    public LockValue getLockProperty() {
        return lockProperty;
    }

    /**
     * @return the width
     */
//    @Override
    public double getWidth() {
        return widthProperty.get();
    }

    /**
     * @param width the width to set
     */
//    @Override
    public void setWidth(double width) {
//        if (this.width != width) {
//            double oldVal = this.width;
        widthProperty.set(width);
//            notifyListeners(WidthProperty.Field.WIDTH, oldVal, this.width);
//        }
    }

    /**
     * @return the pour
     */
    public PolygonPour getPour() {
        return pour;
    }

    /**
     * @param pour the pour to set
     */
    public void setPour(PolygonPour pour) {
        if (this.pour != pour) {
            PolygonPour oldVal = this.pour;
            this.pour = pour;
            notifyListeners(PolygonElement.Field.POUR, oldVal, this.pour);
        }
    }

    /**
     * @param pour the pour to set
     */
    public void setPour(String pour) {
        setPour(PolygonPour.fromCode(pour));
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double val) {
        if (this.spacing != val) {
            double oldVal = this.spacing;
            this.spacing = val;
            notifyListeners(PolygonElement.Field.SPACING, oldVal, this.spacing);
        }
    }

    /**
     * @return the isolate
     */
    public double getIsolate() {
        return isolate;
    }

    /**
     * @param isolate the isolate to set
     */
    public void setIsolate(double isolate) {
        if (this.isolate != isolate) {
            double oldVal = this.isolate;
            this.isolate = isolate;
            notifyListeners(PolygonElement.Field.ISOLATE, oldVal, this.isolate);
        }
    }

    /**
     * @return the orphans
     */
    public boolean isOrphans() {
        return orphans;
    }

    /**
     * @param orphans the orphans to set
     */
    public void setOrphans(boolean orphans) {
        if (this.orphans != orphans) {
            boolean oldVal = this.orphans;
            this.orphans = orphans;
            notifyListeners(PolygonElement.Field.ORPHANS, oldVal, this.orphans);
        }
    }

    /**
     * @return the thermals
     */
    public boolean isThermals() {
        return thermals;
    }

    /**
     * @param thermals the thermals to set
     */
    public void setThermals(boolean thermals) {
        if (this.thermals != thermals) {
            boolean oldVal = this.thermals;
            this.thermals = thermals;
            notifyListeners(PolygonElement.Field.THERMAL, oldVal, this.thermals);
        }
    }

    /**
     * @return the rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * @param rank the rank to set
     */
    public void setRank(int rank) {
        if (this.rank != rank) {
            int oldVal = this.rank;
            // Ingest may try to set rank of zero. Lowest is "1".
            if (rank <= 0) {
                this.rank = 1;
            } else if (rank > 6) {  // Highest rank is 6
                this.rank = 6;
            } else {
                this.rank = rank;
            }
            notifyListeners(PolygonElement.Field.RANK, oldVal, this.rank);
        }
    }

    /**
     * @return the vertices
     */
    public List<Vertex> getVertices() {
        return vertices;
    }

    /**
     * Should only be called by ingest (file load). Not for casual use!
     *
     * @param vertices the vertices to set
     */
    public void setVertices(List<Vertex> vertices) {
        this.vertices.clear();
        for (Vertex v : vertices) {
            this.vertices.add(v);
            v.addListener(this);
        }
    }

    /**
     * Add a new vertex at the end of the list.
     *
     * @param vertNew
     */
    public void addVertex(Vertex vertNew) {
        LOGGER.log(Level.SEVERE, "Polygon added a vertex. {0},{1}  obj:{2}", new Object[]{vertNew.getX(), vertNew.getY(), vertNew.hashCode()});
        getVertices().add(vertNew);
//        StringBuilder sb = new StringBuilder("PolyList:");
//        for (Vertex v : getVertices()) {
//            sb.append("\n    ");
//            sb.append(v.hashCode());
//            if (getVertices().getFirst().equals(v)) {
//                sb.append("(*)");
//            }
//            sb.append(" ===>  ");
//            sb.append("x: ");
//            sb.append(v.getX());
//            sb.append(",  y: ");
//            sb.append(v.getY());
//        }
//        LOGGER.log(Level.SEVERE, sb.toString());
        vertNew.addListener(this);
        notifyListeners(PolygonElement.Field.VERTEX, null, vertNew);
    }

    /**
     * Insert a Vertex in the list, after the specified vertex.
     *
     * @param before adds @Vertex after this one.
     * @param vertNew the new @Vertex to be added.
     */
    public void addVertex(Vertex before, Vertex vertNew) {
        LOGGER.log(Level.SEVERE, "Polygon added a vertex. {0},{1}", new Object[]{vertNew.getX(), vertNew.getY()});
        List<Vertex> verts = getVertices();
        getVertices().add(verts.indexOf(before) + 1, vertNew);

        vertNew.addListener(this);
        notifyListeners(PolygonElement.Field.VERTEX, null, vertNew);
    }

    public void removeVertex(Vertex v) {
        LOGGER.log(Level.SEVERE, "Polygon removed a vertex. {0},{1}", new Object[]{v.getX(), v.getY()});
        if (getVertices().contains(v)) {
            getVertices().remove(v);
            v.removeListener(this);
            notifyListeners(PolygonElement.Field.VERTEX, v, null);
        }
    }

    public int selectVerticesIn(double x, double y, double range) {
        int vCount = 0;
        for (Vertex v : vertices) {
            if ((Math.abs(x - v.getX()) < range && Math.abs(y - v.getY()) < range)) {
                v.setSelected(true);
                vCount++;
            } else {
                v.setSelected(false);
            }
        }
        return vCount;
    }

    public boolean hasSelections() {
        for (Vertex v : vertices) {
            if (v.isSelected()) {
                return true;
            }
        }

        return false;
    }

    public int pickVerticesIn(double x, double y, double range) {
        int vCount = 0;
        for (Vertex v : vertices) {
            if ((Math.abs(x - v.getX()) < range && Math.abs(y - v.getY()) < range)) {
                v.setPicked(true);
                vCount++;
            } else {
                v.setPicked(false);
            }
        }
        return vCount;
    }

    public boolean hasPicks() {
        for (Vertex v : vertices) {
            if (v.isPicked()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isPicked() {
        return picked;
    }

    @Override
    public void setPicked(boolean picked) {
        boolean oldValue = this.picked;
        this.picked = picked;
        vertices.forEach((v) -> {
            v.setPicked(picked);
        });
        notifyListeners(SelectableProperty.Field.PICKED, oldValue, this.picked);
    }


    /**
     * Return a list of vertices that are selected. * given X/Y.
     *
     * @param x
     * @param y
     * @param hitBox
     * @return list of found vertices
     */
    public Vertex[] getSelectedVertices() {
        ArrayList<Vertex> found = new ArrayList<>();

        for (Vertex v : getVertices()) {
            if (v.isSelected()) {
                found.add(v);
            }
        }

        return found.toArray(Vertex[]::new);
    }

    public PolygonElement copy() {
        PolygonElement copy = new PolygonElement();

        copy.setWidth(getWidth());
        copy.setLayerNum(getLayerNum());
        copy.setVertices(getVertices());

        return copy;
    }

    @Override
    public void createSnapshot() {
        snapshot = copy();
    }

    @Override
    public void restoreSnapshot() {
        if (snapshot != null) {
            setWidth(snapshot.getWidth());
            setLayerNum(snapshot.getLayerNum());
            setVertices(snapshot.getVertices());

            snapshot = null;
        }
    }

    @Override
    public Element getSnapshot() {
        if (snapshot == null) {
            createSnapshot();
        }
        return snapshot;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            boolean oldValue = this.selected;
            this.selected = selected;
            notifyListeners(SelectableProperty.Field.SELECTED, oldValue, this.selected);
        }
    }

    /**
     * When vertexes change they come through here. We aggregate and forward.
     *
     * @param e
     * @param field
     * @param oldVal
     * @param newVal
     */
    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        notifyListeners(field, oldVal, newVal);
    }

    @Override
    public int getLayerNum() {
        return layerValue.get();
    }

    @Override
    public void setLayerNum(int layer) {
        layerValue.set(layer);
//        if (this.layer != layer) {
//            int oldVal = this.layer;
//            this.layer = layer;
//            notifyListeners(LayerNumberProperty.Field.LAYER, oldVal, this.layer);
//        }
    }

    @Override
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(layerValue)) {
            notifyListeners(LayerNumberProperty.Field.LAYER, layerValue.getOldValue(), layerValue.get());
        }

    }

    /**
     * <pre>
     * <polygon width="0.254" layer="94" spacing="1.381759375" pour="hatch">
     * <vertex x="50.8" y="-15.24"/>
     * <vertex x="71.12" y="-15.24"/>
     * <vertex x="71.12" y="-5.08"/>
     * <vertex x="50.8" y="-5.08"/>
     * <vertex x="48.26" y="-5.08"/>
     * <vertex x="48.26" y="-10.16"/>
     * <vertex x="40.64" y="-10.16" curve="60"/>
     * <vertex x="40.64" y="-15.24"/>
     * </polygon>
     * </pre>
     *
     * @return
     */
    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<polygon{0}{1}{2}{3}{4}>\n{5}</polygon>");

        StringBuilder vertSb = new StringBuilder();
        vertices.forEach(v -> {
            vertSb.append(v.toXML()).append("\n");
        });

        Object[] args = {
            " width=\"" + widthProperty.get() + "\"", // 0
            " layer=\"" + getLayerNum() + "\"", // 1
            lockProperty.xmlValue(), // 2
            getSpacing() != 1.27 ? " spacing=\"" + getSpacing() + "\"" : "", // 3
            " pour=\"" + pour.code() + "\"", // 4
            vertSb.toString() // 5
        };

        return mf.format(args);
    }
}
