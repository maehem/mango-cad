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
package com.maehem.mangocad.model.element.misc;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.enums.VerticalText;


/**
 * <pre>
 * setting EMPTY
 *    ATTLIST setting
        alwaysvectorfont %Bool;         #IMPLIED
        verticaltext     %VerticalText; "up"
        keepoldvectorfont %Bool;        "no"          
 * </pre>
 * 
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Setting extends Element {

    public static final String ELEMENT_NAME = "setting";   
    
    private boolean alwaysVectorFont = false;
    private VerticalText verticalText = VerticalText.UP;
    private boolean keepOldVectorFont = false;
    
    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the alwaysVectorFont
     */
    public boolean isAlwaysVectorFont() {
        return alwaysVectorFont;
    }

    /**
     * @param alwaysVectorFont the alwaysVectorFont to set
     */
    public void setAlwaysVectorFont(boolean alwaysVectorFont) {
        this.alwaysVectorFont = alwaysVectorFont;
    }

    /**
     * @return the verticalText
     */
    public VerticalText getVerticalText() {
        return verticalText;
    }

    /**
     * @param verticalText the verticalText to set
     */
    public void setVerticalText(VerticalText verticalText) {
        this.verticalText = verticalText;
    }

    /**
     * @return the keepOldVectorFont
     */
    public boolean isKeepOldVectorFont() {
        return keepOldVectorFont;
    }

    /**
     * @param keepOldVectorFont the keepOldVectorFont to set
     */
    public void setKeepOldVectorFont(boolean keepOldVectorFont) {
        this.keepOldVectorFont = keepOldVectorFont;
    }

}
