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
package com.maehem.mangocad.model.library.element.quantum;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class ElementPolygon extends _AQuantum {
    private double width;
    private double spacing;
    private String pour = "solid";
    private double isolate;
    private boolean orphans = false;
    private boolean thermals = true;
    private int rank = 0;
    private List<Vertex> vertices = new ArrayList<>();

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

    /**
     * @return the spacing
     */
    public double getSpacing() {
        return spacing;
    }

    /**
     * @param spacing the spacing to set
     */
    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    /**
     * @return the pour
     */
    public String getPour() {
        return pour;
    }

    /**
     * @param pour the pour to set
     */
    public void setPour(String pour) {
        this.pour = pour;
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
        this.isolate = isolate;
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
        this.orphans = orphans;
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
        this.thermals = thermals;
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
        this.rank = rank;
    }

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
        this.vertices = vertices;
    }
    
}
