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

import com.maehem.mangocad.model.Element;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class ElementPolygon extends Element {

    public static final String ELEMENT_NAME = "polygon";

    private double width;
//    private double spacing;
//    private String pour = "solid"; // TODO: Enum
    //private double isolate;
    //private boolean orphans = false;
    //private boolean thermals = true;
    //private int rank = 1;
    private final List<Vertex> vertices = new ArrayList<>();

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

//    /**
//     * @return the spacing
//     */
//    public double getSpacing() {
//        return spacing;
//    }
//
//    /**
//     * @param spacing the spacing to set
//     */
//    public void setSpacing(double spacing) {
//        this.spacing = spacing;
//    }
//
//    /**
//     * @return the pour
//     */
//    public String getPour() {
//        return pour;
//    }
//
//    /**
//     * @param pour the pour to set
//     */
//    public void setPour(String pour) {
//        this.pour = pour;
//    }
//
//    /**
//     * @return the isolate
//     */
//    public double getIsolate() {
//        return isolate;
//    }
//
//    /**
//     * @param isolate the isolate to set
//     */
//    public void setIsolate(double isolate) {
//        this.isolate = isolate;
//    }
//
//    /**
//     * @return the orphans
//     */
//    public boolean isOrphans() {
//        return orphans;
//    }
//
//    /**
//     * @param orphans the orphans to set
//     */
//    public void setOrphans(boolean orphans) {
//        this.orphans = orphans;
//    }
//
//    /**
//     * @return the thermals
//     */
//    public boolean isThermals() {
//        return thermals;
//    }
//
//    /**
//     * @param thermals the thermals to set
//     */
//    public void setThermals(boolean thermals) {
//        this.thermals = thermals;
//    }
//
//    /**
//     * @return the rank
//     */
//    public int getRank() {
//        return rank;
//    }
//
//    /**
//     * @param rank the rank to set
//     */
//    public void setRank(int rank) {
//        // Ingest may try to set rank of zero. Lowest is "1".
//        if (rank <= 0) {
//            this.rank = 1;
//        } else if (rank > 6) {  // Highest rank is 6
//            this.rank = 6;
//        } else {
//            this.rank = rank;
//        }
//    }

    /**
     * @return the vertices
     */
    public List<Vertex> getVertices() {
        return vertices;
    }

    /**
     * @param vertices the vertices to set
     */
    public void setVertices(List<Vertex> vertices) {
        this.vertices.clear();
        for (Vertex v : vertices) {
            this.vertices.add(v);
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

    /**
     * Return a list of vertices that are selected.     * given X/Y.
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

        return found.toArray(new Vertex[]{});
    }
}
