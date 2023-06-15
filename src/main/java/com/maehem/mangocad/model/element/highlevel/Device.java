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

import com.maehem.mangocad.model.element.basic.Connection;
import com.maehem.mangocad.model.element.basic.Technology;
import com.maehem.mangocad.model.element.basic.Package3dInstance;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Device {
    // 'layer' is not used.
    
    private String name = "";
    private String footprint;  // Underbar. 'package' is a reserved Java word.
    private List<Connection> connections = new ArrayList<>();
    private List<Technology> technologies = new ArrayList<>();
    private final List<Package3dInstance> package3dinstances = new ArrayList<>();
    
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
     * @return the footprint
     */
    public String getFootprint() {
        return footprint;
    }

    /**
     * @param _package the footprint to set
     */
    public void setFootprint(String _package) {
        this.footprint = _package;
    }

    /**
     * @return the connections
     */
    public List<Connection> getConnections() {
        return connections;
    }

    /**
     * @param connections the connects to set
     */
    public void setConnects(List<Connection> connections) {
        this.connections = connections;
    }

    /**
     * @return the technologies
     */
    public List<Technology> getTechnologies() {
        return technologies;
    }

    /**
     * @param technologies the technologies to set
     */
    public void setTechnologies(List<Technology> technologies) {
        this.technologies = technologies;
    }
    
    public List<Package3dInstance> getPackage3dInstances() {
        return package3dinstances;
    }
}
