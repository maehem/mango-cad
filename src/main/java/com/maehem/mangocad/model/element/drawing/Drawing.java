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
package com.maehem.mangocad.model.element.drawing;

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.misc.Grid;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.element.misc.Setting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     drawing (settings?, grid?, filters?, layers, (library | schematic | board))
 * </pre>
 * 
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Drawing extends _AQuantum {
    public static final String ELEMENT_NAME = "drawing";

    private final ColorPalette colorPalette = new ColorPalette(ColorPalette.Style.DARK);

    private final ArrayList<Setting> settings = new ArrayList<>();
    private final Grid grid = new Grid();
    private final ArrayList<Filter> filters = new ArrayList<>();    
    private final LayerElement layers[] = new LayerElement[256];

    // Non-savable and ephemeral things
    // DRAWING_NAME,  SHEET (x of xx), LAST_DATE_TIME
    // Compute these during file load/ingest.
    HashMap<String, String> vars = new HashMap<>();
    
    private DesignObject design;

    public Drawing() {
        layers[0] = new LayerElement();
    }
    
    
    
    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    public ColorPalette getPalette() {
        return colorPalette;
    }

    public Grid getGrid() {
        return grid;
    }
    
    /**
     * @return the settings
     */
    public ArrayList<Setting> getSettings() {
        return settings;
    }

    /**
     * @return the filters
     */
    public ArrayList<Filter> getFilters() {
        return filters;
    }

    /**
     * @return the layers
     */
    public LayerElement[] getLayers() {
        return layers;
    }

    public int getIndexForLayer(int layerNum) {
        LayerElement layer = layers[layerNum];

        if (layer != null) {
            return layer.getColorIndex();
        } else {
            return -1;
        }
    }

    /**
     * @return the design
     */
    public DesignObject getDesign() {
        return design;
    }

    /**
     * @param design the design to set
     */
    public void setDesign(DesignObject design) {
        this.design = design;
    }
  
    public Map getVars() {
        return vars;
    }
}
