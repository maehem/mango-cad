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

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Connection {
    // 'layer' is not used.
    
    private String gate;
    private String pin;
    private String pad;
    private String route = "all";

    /**
     * @return the gate
     */
    public String getGate() {
        return gate;
    }

    /**
     * @param gate the gate to set
     */
    public void setGate(String gate) {
        this.gate = gate;
    }

    /**
     * @return the pin
     */
    public String getPin() {
        return pin;
    }

    /**
     * @param pin the pin to set
     */
    public void setPin(String pin) {
        this.pin = pin;
    }

    /**
     * @return the pad
     */
    public String getPad() {
        return pad;
    }

    /**
     * @param pad the pad to set
     */
    public void setPad(String pad) {
        this.pad = pad;
    }

    /**
     * @return the route
     */
    public String getRoute() {
        return route;
    }

    /**
     * @param route the route to set
     */
    public void setRoute(String route) {
        this.route = route;
    }


}
