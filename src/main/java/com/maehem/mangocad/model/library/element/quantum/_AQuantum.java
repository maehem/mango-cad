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
public abstract class _AQuantum {
//    private double x;
//    private double y;
    private int layer;

    /**
     * XML element name. Used for loading saving XML file.
     * 
     * @return XML element name
     */
    public abstract String getElementName();
    
//    /**
//     * @return the x
//     */
//    public double getX() {
//        return x;
//    }
//
//    /**
//     * @param x the x to set
//     */
//    public void setX(double x) {
//        this.x = x;
//    }
//
//    /**
//     * @return the y
//     */
//    public double getY() {
//        return y;
//    }
//
//    /**
//     * @param y the y to set
//     */
//    public void setY(double y) {
//        this.y = y;
//    }

    /**
     * @return the layer
     */
    public int getLayerNum() {
        return layer;
    }

    /**
     * @param layer the layer to set
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

}
