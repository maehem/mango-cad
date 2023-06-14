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

import java.util.ArrayList;
import java.util.List;
import com.maehem.mangocad.model.element.highlevel.Device;
import com.maehem.mangocad.model.element.basic.Gate;
import com.maehem.mangocad.model.LibraryElement;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class DeviceSet extends LibraryElement {
    private String prefix = "";
    private boolean uservalue = false;
    private List<Gate> gates = new ArrayList<>();
    private List<Device> devices = new ArrayList<>();

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the userValue
     */
    public boolean getUservalue() {
        return uservalue;
    }

    /**
     * @param userValue the userValue to set
     */
    public void setUservalue(boolean userValue) {
        this.uservalue = userValue;
    }

    /**
     * @return the gates
     */
    public List<Gate> getGates() {
        return gates;
    }

    /**
     * @param gates the gates to set
     */
    public void setGates(List<Gate> gates) {
        this.gates = gates;
    }

    /**
     * @return the devices
     */
    public List<Device> getDevices() {
        return devices;
    }

    /**
     * @param devices the devices to set
     */
    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    
}
