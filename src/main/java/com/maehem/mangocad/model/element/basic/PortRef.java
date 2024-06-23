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

import com.maehem.mangocad.model.element.Element;

/**
 * portref EMPTY>
   portref
          moduleInst    %String;       #REQUIRED
          port          %String;       #REQUIRED
 * 
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PortRef extends Element {
    
    public static final String ELEMENT_NAME = "portref";

    private String moduleInst;
    private String port;

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the moduleInst
     */
    public String getModuleInst() {
        return moduleInst;
    }

    /**
     * @param moduleInst the moduleInst to set
     */
    public void setModuleInst(String moduleInst) {
        this.moduleInst = moduleInst;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }
    
}
