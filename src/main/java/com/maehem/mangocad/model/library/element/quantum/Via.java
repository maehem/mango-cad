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
package com.maehem.mangocad.model.library.element.quantum;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Via extends _AQuantum {
    private double extent;
    private double drill;
    private double diameter = 0.0;
    private String shape = "round";
    private boolean stop = false;

    /**
     * @return the drill
     */
    public double getDrill() {
        return drill;
    }

    /**
     * @param drill the drill to set
     */
    public void setDrill(double drill) {
        this.drill = drill;
    }

    /**
     * @return the extent
     */
    public double getExtent() {
        return extent;
    }

    /**
     * @param extent the extent to set
     */
    public void setExtent(double extent) {
        this.extent = extent;
    }

    /**
     * @return the diameter
     */
    public double getDiameter() {
        return diameter;
    }

    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    /**
     * @return the shape
     */
    public String getShape() {
        return shape;
    }

    /**
     * @param shape the shape to set
     */
    public void setShape(String shape) {
        this.shape = shape;
    }

    /**
     * @return the stop
     */
    public boolean isStop() {
        return stop;
    }

    /**
     * @param stop the stop to set
     */
    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
