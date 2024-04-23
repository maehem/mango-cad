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
package com.maehem.mangocad.model;

import java.util.ArrayList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public abstract class Element {
    private int layer;

    private ArrayList<ElementListener> listeners = new ArrayList<>();

    /**
     * XML element name. Used for loading saving XML file.
     *
     * @return XML element name
     */
    public abstract String getElementName();

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

    public boolean addListener(ElementListener el) {
        return this.listeners.add(el);
    }

    public boolean removeListener(ElementListener el) {
        return this.listeners.remove(el);
    }

    public void notifyListeners(Enum field, Object oldVal, Object newVal) {
        for (ElementListener l : listeners) {
            l.elementChanged(this, field, oldVal, newVal);
        }
    }
}