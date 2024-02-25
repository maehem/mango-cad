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
package com.maehem.mangocad.model.eaglecad;

import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.basic.ContactRef;
import com.maehem.mangocad.model.element.basic.ElementElement;
import com.maehem.mangocad.model.element.drawing.Board;
import com.maehem.mangocad.model.element.highlevel.Signal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class EagleCADResolve {
    public static final Logger LOGGER = Logger.getLogger("com.maehem.mangocad");

    public static void resolveContactRefs(Board brd) {
        for (Signal s : brd.getSignals()) {
            for (_AQuantum e : s.getElements()) {
                if (e instanceof ContactRef cr) {
                    cr.setElementO(brd.getElement(cr.getElement()));
                    LOGGER.log(Level.SEVERE, "cr: {0}.{1}", new Object[]{cr.getElement(), cr.getPad()});
                }
            }
        }
    }

    /**
     * Configure each element with library obj ref and pkg obj ref.
     *
     * @param brd
     */
    static void resolveElements(Board brd) {
        for (ElementElement e : brd.getElements()) {
            e.setLibraryObj(brd.getLibrary(e.getLibrary()));
            LOGGER.log(Level.SEVERE, "element: {0}  library: {1}", new Object[]{e.getName(), e.getLibrary()});
        }
    }
}
