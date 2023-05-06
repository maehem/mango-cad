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
public class PadTHD extends _AQuantum {
    private boolean first;
    private String name;
    private double rotation = 0.0;
    private double drill;
    private String shape = "round";
    private boolean stopmask = true;
    private boolean thermals = true;
    private double diameter = 0.0;

    /**
     * @return the first
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * @param first
     */
    public void setFirst(boolean first) {
        this.first = first;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rotation
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * @param rotation the rotation to set
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /**
     * @return the stopMask
     */
    public boolean isStopmask() {
        return stopmask;
    }

    /**
     * @param stopmask the stopMask to set
     */
    public void setStopmask(boolean stopmask) {
        this.stopmask = stopmask;
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
     * @return the diameter
     */
    public double getDiameter() {
        return diameter;
    }

    public double getDerivedDiameter() {
        if ( getDiameter() == 0.0 ) {
            double min = getDrill() + 0.3;
            double cmp = getDrill() * 1.25;
            if ( cmp > min ) {
                return cmp;
            }
            return min;
        }
        
        return getDiameter();
    }
    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

//    @Override
//    public Rectangle2D getBounds() {
//        double d = getDiameter();
//        if ( d == 0.0 ) {
//            d = getDrill()*1.25;
//        }
//        return new Rectangle2D.Double(getX()-d/2, getY()-d/2, d, d);
//    }
    
}
