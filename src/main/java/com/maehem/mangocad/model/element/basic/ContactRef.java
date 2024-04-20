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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.enums.ContactRoute;

/**
 * <pre>
 * ELEMENT contactref EMPTY>
 *    ATTLIST contactref
 *      element       %String;       #REQUIRED
 *      pad           %String;       #REQUIRED
 *      route         %ContactRoute; "all"
 *      routetag      %String;       ""
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ContactRef extends Element {

    public static final String ELEMENT_NAME = "contactref";

    private String element;
    private ElementElement elementO;
    private String pad;
    private ContactRoute route = ContactRoute.ALL;
    private String routeTag = "";

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the element
     */
    public String getElement() {
        return element;
    }

    /**
     * @param element the element to set
     */
    public void setElement(String element) {
        this.element = element;
    }

    /**
     * @return the element
     */
    public ElementElement getElementO() {
        return elementO;
    }

    /**
     * @param element the element to set
     */
    public void setElementO(ElementElement element) {
        this.elementO = element;
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
     * @return the routeTag
     */
    public String getRouteTag() {
        return routeTag;
    }

    /**
     * @param routeTag the routeTag to set
     */
    public void setRouteTag(String routeTag) {
        this.routeTag = routeTag;
    }

    /**
     * @return the route
     */
    public ContactRoute getRoute() {
        return route;
    }

    /**
     * @param route the route to set
     */
    public void setRoute(ContactRoute route) {
        this.route = route;
    }

}
