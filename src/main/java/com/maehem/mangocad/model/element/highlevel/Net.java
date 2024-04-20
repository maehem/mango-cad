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
package com.maehem.mangocad.model.element.highlevel;

import com.maehem.mangocad.model.Element;
import java.util.ArrayList;

/**
 * Combined NET and BUS into dual-mode class. Busses don't have netClass set but
 * are still have a list of Segment things.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Net extends Element {

    public static final String ELEMENT_NAME_NET = "net";
    public static final String ELEMENT_NAME_BUS = "bus";
    public static final boolean AS_BUS = true;

    private final ArrayList<Segment> segments = new ArrayList<>();

    private String name;
    private int netClass = 0;  // non-posittive == BUS

    public Net(boolean asBus) {
        if (asBus) {
            netClass = -1;
        }
    }

    public Net() {
        this(!AS_BUS);
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    @Override
    public String getElementName() {
        return isBus() ? ELEMENT_NAME_BUS : ELEMENT_NAME_NET;
    }

    public boolean isBus() {
        return getNetClass() < 0;
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
     * @return the netClass
     */
    public int getNetClass() {
        return netClass;
    }

    /**
     * @param netClass the netClass to set
     */
    public void setNetClass(int netClass) {
        this.netClass = netClass;
    }

}
