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
package com.maehem.mangocad.model.element.basic;

import com.maehem.mangocad.model._AQuantum;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Gate extends _AQuantum {
    // 'layer' is not used.
    
    private String name;
    private double x;
    private double y;
    private String symbol;
    private String addlevel = "next";
    private int swapLevel = 0;

    public static final String ELEMENT_NAME = "gate";
    
    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * @return the swapLevel
     */
    public int getSwapLevel() {
        return swapLevel;
    }

    /**
     * @param swapLevel the swapLevel to set
     */
    public void setSwapLevel(int swapLevel) {
        this.swapLevel = swapLevel;
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
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the addlevel
     */
    public String getAddlevel() {
        return addlevel;
    }

    /**
     * @param addlevel the addlevel to set
     */
    public void setAddlevel(String addlevel) {
        this.addlevel = addlevel;
    }
    
    

}
